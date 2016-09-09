package scraper.services.chan.processor;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scraper.services.chan.model.CollectorProcessedCheckpointDs;
import scraper.services.chan.repository.CollectorProcessedCheckpointDsRepository;
import scraper.services.chan.web.WebService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BoardCollectorTest {

    @Mock
    private PageCollector pageParser;

    @Mock
    private WebService webService;

    @Mock
    private CollectorProcessedCheckpointDsRepository checkpointRepository;

    private BoardCollector boardCollector;

    @Before
    public void setUp() {
        boardCollector = new BoardCollector(pageParser, webService, checkpointRepository);
    }

    @Test
    public void testCollectBoard_firstCollect() throws IOException {
        // given
        Settings settings = new Settings("wg", null);
        List<Document> pageDoms = mockPages(1, 5, settings);
        long checkpointId = mockCheckpoint(settings, null);

        // when
        boardCollector.collectBoard(settings);

        // then
        assertCollected(pageDoms, settings);
        assertCheckpoints(1, 5, checkpointId, settings);
    }

    @Test
    public void testCollectBoard_notFirstCollect() throws IOException {
        // given
        Settings settings = new Settings("wg", null);
        List<Document> pageDoms = mockPages(5, 12, settings);
        long checkpointId = mockCheckpoint(settings, new CollectorProcessedCheckpointDs(15L, settings.getBoardName(), 5));

        // when
        boardCollector.collectBoard(settings);

        // then
        assertCollected(pageDoms, settings);
        assertCheckpoints(5, 12, checkpointId, settings);
    }

    @Test
    public void testCollectBoard_limit() throws IOException {
        // given
        Settings settings = new Settings("wg", 5);
        List<Document> pageDoms = mockPages(1, 10, settings);
        long checkpointId = mockCheckpoint(settings, null);

        // when
        boardCollector.collectBoard(settings);

        // then
        assertCollected(pageDoms.subList(0, 5), settings);
        assertCheckpoints(1, 5, checkpointId, settings);
    }

    @Test
    public void testCollectBoard_ioFailed() throws IOException {
        // given
        Settings settings = new Settings("wg", null);
        List<Document> pageDoms = mockPages(1, 5, settings);
        String url = String.format("https://yuki.la/%s/page/%d", settings.getBoardName(), 4);
        IOException testException = new IOException("test");
        stub(webService.getDocument(url)).toThrow(testException);

        long checkpointId = mockCheckpoint(settings, null);

        // when
        try {
            boardCollector.collectBoard(settings);
            fail();
        } catch (IOException ex) {
            // then
            assertSame(testException, ex);
        }

        // then
        assertCollected(pageDoms.subList(0, 3), settings);
        assertCheckpoints(1, 3, checkpointId, settings);
    }

    private List<Document> mockPages(int firstPage, int lastPage, Settings settings) throws IOException {
        List<Document> pageDoms = new ArrayList<>(lastPage - firstPage + 1);
        for (int i = firstPage; i <= lastPage; i++) {
            Document pageDom = mock(Document.class);
            String url = String.format("https://yuki.la/%s/page/%d", settings.getBoardName(), i);

            stub(webService.getDocument(url)).toReturn(pageDom);
            pageDoms.add(pageDom);
        }

        String lastUrl = String.format("https://yuki.la/%s/page/%d", settings.getBoardName(), lastPage + 1);
        stub(webService.getDocument(lastUrl)).toThrow(new HttpStatusException("Not found", 404, lastUrl));

        return pageDoms;
    }

    private long mockCheckpoint(Settings settings, CollectorProcessedCheckpointDs checkpoint) {
        if (checkpoint == null) {
            stub(checkpointRepository.findByBoardName(settings.getBoardName())).toReturn(null);
            CollectorProcessedCheckpointDs newCheckpoint = new CollectorProcessedCheckpointDs(12L, settings.getBoardName(), 1);
            stub(checkpointRepository.save(new CollectorProcessedCheckpointDs(settings.getBoardName(), 1))).toReturn(newCheckpoint);
            return newCheckpoint.getId();
        }

        stub(checkpointRepository.findByBoardName(settings.getBoardName())).toReturn(checkpoint);
        return checkpoint.getId();
    }

    private void assertCollected(List<Document> pageDoms, Settings settings) throws IOException {
        verify(pageParser, times(pageDoms.size())).parsePage(any(Document.class), eq(settings));
        for (Document pageDom : pageDoms) {
            verify(pageParser).parsePage(pageDom, settings);
        }
    }

    private void assertCheckpoints(int firstPage, int lastPage, long checkpointId, Settings settings) {
        int expectedCallTimes = firstPage == 1 ? lastPage - firstPage + 1 : lastPage - firstPage;
        verify(checkpointRepository, times(expectedCallTimes)).save(any(CollectorProcessedCheckpointDs.class));
    }
}