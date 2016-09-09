package scraper.services.chan.processor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import scraper.common.Utils;
import scraper.services.chan.model.ThreadDs;
import scraper.services.chan.repository.ThreadDsRepository;
import scraper.services.chan.web.WebService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PageCollectorTest {

    @Mock
    private WebService webService;

    @Mock
    private ThreadParser threadParser;

    @Mock
    private ThreadDsRepository threadRepository;

    private PageCollector pageCollector;

    @Before
    public void setUp() {
        pageCollector = new PageCollector(webService, threadParser, threadRepository);
    }

    @Test
    public void testCollectPage() throws IOException {
        // given
        Document pageDom = getDocument("/scraper/services/chan/processor/page1.html");
        Settings settings = new Settings("wg", null);
        Set<String> threadIds =
                Utils.set("6353085", "6353115", "6353173", "6353329", "6353331", "6353368", "6353396", "6353422", "6353597", "6353667", "6353679", "6353698", "6353748", "6353772",
                        "6353877", "6354028", "6354123", "6354152", "6354241", "6354383", "6354388", "6354420", "6354553", "6354571", "6354575");

        List<ThreadDs> threads = mockDocuments(settings, threadIds);

        // when
        pageCollector.parsePage(pageDom, settings);

        // then
        assertSaved(threads);
    }

    @Test
    public void testCollectPage_threadsAlreadyCollected() throws IOException {
        // given
        Document pageDom = getDocument("/scraper/services/chan/processor/page1.html");
        Settings settings = new Settings("wg", null);
        Set<String> threadIds =
                Utils.set("6353085", "6353115", "6353173", "6353331", "6353368", "6353396", "6353597", "6353667", "6353679", "6353698", "6353748", "6353772", "6353877", "6354028",
                        "6354152", "6354241", "6354383", "6354388", "6354420", "6354553", "6354571", "6354575");
        Set<String> collectedThreads = Utils.set("6354123", "6353329", "6353422");

        List<ThreadDs> threads = mockDocuments(settings, threadIds);
        mockAlreadyCollected(collectedThreads);

        // when
        pageCollector.parsePage(pageDom, settings);

        // then
        assertSaved(threads);
    }

    private void assertSaved(List<ThreadDs> threads) {
        verify(threadRepository, times(threads.size())).save(any(ThreadDs.class));
        for (ThreadDs thread : threads) {
            verify(threadRepository).save(thread);
        }
    }

    private List<ThreadDs> mockDocuments(Settings settings, Set<String> threadIds) throws IOException {
        List<ThreadDs> threads = new ArrayList<>(threadIds.size());
        for (String threadId : threadIds) {
            Document threadDom = mock(Document.class);
            ThreadDs threadDs = mock(ThreadDs.class);

            stub(webService.getDocument(String.format("https://yuki.la/%s/%s", settings.getBoardName(), threadId))).toReturn(threadDom);
            stub(threadParser.parseThread(threadDom, settings)).toReturn(threadDs);
        }

        return threads;
    }

    private void mockAlreadyCollected(Set<String> threadIds) {
        for (String threadId : threadIds) {
            stub(threadRepository.findIdByThreadId(threadId)).toReturn(12L);
        }
    }

    private Document getDocument(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        return Jsoup.parse(in, StandardCharsets.UTF_8.name(), "http://boards.4chan.org/");
    }
}