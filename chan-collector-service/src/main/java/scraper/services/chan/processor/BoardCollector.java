package scraper.services.chan.processor;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scraper.services.chan.model.CollectorProcessedCheckpointDs;
import scraper.services.chan.model.ThreadDs;
import scraper.services.chan.repository.CollectorProcessedCheckpointDsRepository;
import scraper.services.chan.repository.ThreadDsRepository;
import scraper.services.chan.web.WebService;

import java.io.IOException;
import java.util.List;

/**
 * Service responsible for collecting / scraping data from 4chan archive.
 */
// TODO add tests
@Service
public class BoardCollector {

    private final PageParser pageParser;

    private final WebService webService;

    private final CollectorProcessedCheckpointDsRepository checkpointRepository;

    private final ThreadDsRepository threadRepository;

    @Autowired
    public BoardCollector(PageParser pageParser, WebService webService, CollectorProcessedCheckpointDsRepository checkpointRepository, ThreadDsRepository threadRepository) {
        this.pageParser = pageParser;
        this.webService = webService;
        this.checkpointRepository = checkpointRepository;
        this.threadRepository = threadRepository;
    }

    /**
     * Scraps uncollected data from board.
     *
     * @param settings collection settings
     * @throws IOException if io failed
     */
    public void collectBoard(Settings settings) throws IOException {
        Integer maxPages = settings.getMaxPages();
        CollectorProcessedCheckpointDs checkpoint = getCheckpoint(settings);

        int startPageIndx = checkpoint.getLastPageIndx();
        int currentPageIndx = startPageIndx;
        Document page;

        do {
            page = getPage(currentPageIndx, settings);
            if (page != null) {
                saveThreads(pageParser.parsePage(page, settings));
                ++currentPageIndx;
                checkpoint.setLastPageIndx(currentPageIndx);
                checkpointRepository.save(checkpoint);
            }
        } while (page != null && (maxPages == null || currentPageIndx < startPageIndx + settings.getMaxPages()));
    }

    private CollectorProcessedCheckpointDs getCheckpoint(Settings settings) {
        CollectorProcessedCheckpointDs checkpoint = checkpointRepository.findByBoardName(settings.getBoardName());
        return checkpoint == null ? checkpointRepository.save(new CollectorProcessedCheckpointDs(settings.getBoardName(), 1)) : checkpoint;
    }

    private void saveStartingPageIndex(int lastPageIndx, Settings settings) {
        CollectorProcessedCheckpointDs checkpoint = checkpointRepository.findByBoardName(settings.getBoardName());
        if (checkpoint == null) {
            checkpointRepository.save(new CollectorProcessedCheckpointDs(settings.getBoardName(), lastPageIndx));
        } else {
            checkpoint.setLastPageIndx(lastPageIndx);
            checkpointRepository.save(checkpoint);
        }
    }

    private Document getPage(int pageIndx, Settings settings) throws IOException {
        String url = String.format("https://yuki.la/%s/page/%d", settings.getBoardName(), pageIndx);
        try {
            return webService.getDocument(url);
        } catch (HttpStatusException ex) {
            // response with error status - just finish
            return null;
        }
    }

    private void saveThreads(List<ThreadDs> threads) {
        threadRepository.save(threads);
    }
}
