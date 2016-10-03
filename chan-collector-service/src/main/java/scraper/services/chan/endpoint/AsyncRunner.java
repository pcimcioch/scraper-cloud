package scraper.services.chan.endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import scraper.services.chan.processor.BoardCollector;
import scraper.services.chan.processor.Settings;

/**
 * Service used to run board collection asynchronously.
 */
@Component
public class AsyncRunner {

    private final Log logger = LogFactory.getLog(AsyncRunner.class);

    private final BoardCollector collector;

    @Autowired
    public AsyncRunner(BoardCollector collector) {
        this.collector = collector;
    }

    /**
     * Runs board collection asynchronously. Will handle all exceptions.
     *
     * @param settings settings for the run
     */
    @Async
    public void runAsync(Settings settings) {
        try {
            collector.collectBoard(settings);
        } catch (Exception ex) {
            logger.error("Unexpected error during board collection", ex);
        }
    }
}
