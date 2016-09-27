package scraper.services.chan.endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import scraper.services.chan.processor.BoardCollector;
import scraper.services.chan.processor.Settings;

@Component
public class AsyncRunner {

    private final Log logger = LogFactory.getLog(AsyncRunner.class);

    private final BoardCollector collector;

    @Autowired
    public AsyncRunner(BoardCollector collector) {
        this.collector = collector;
    }

    @Async
    public void runAsync(Settings settings) {
        try {
            collector.collectBoard(settings);
        } catch (Exception ex) {
            logger.error("Unexpected error during board collection", ex);
        }
    }
}
