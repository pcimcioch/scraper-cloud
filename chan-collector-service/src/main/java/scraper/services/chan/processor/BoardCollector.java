package scraper.services.chan.processor;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scraper.services.chan.model.CollectorProcessedCheckpointDs;
import scraper.services.chan.repository.CollectorProcessedCheckpointDsRepository;
import scraper.services.chan.web.WebService;

import java.io.IOException;

/**
 * Service responsible for collecting / scraping data from 4chan archive.
 */
@Service
public class BoardCollector {

    private final PageCollector pageParser;

    private final WebService webService;

    private final CollectorProcessedCheckpointDsRepository checkpointRepository;

    @Autowired
    public BoardCollector(PageCollector pageParser, WebService webService, CollectorProcessedCheckpointDsRepository checkpointRepository) {
        this.pageParser = pageParser;
        this.webService = webService;
        this.checkpointRepository = checkpointRepository;
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
                pageParser.parsePage(page, settings);
                if (currentPageIndx != startPageIndx) {
                    checkpoint.setLastPageIndx(currentPageIndx);
                    checkpointRepository.save(checkpoint);
                }
            }
            ++currentPageIndx;
        } while (page != null && (maxPages == null || currentPageIndx < startPageIndx + settings.getMaxPages()));
    }

    private CollectorProcessedCheckpointDs getCheckpoint(Settings settings) {
        CollectorProcessedCheckpointDs checkpoint = checkpointRepository.findByBoardName(settings.getBoardName());
        return checkpoint == null ? checkpointRepository.save(new CollectorProcessedCheckpointDs(settings.getBoardName(), 1)) : checkpoint;
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
}
