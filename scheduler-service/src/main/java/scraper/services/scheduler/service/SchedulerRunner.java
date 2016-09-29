package scraper.services.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Simple service responsible for safely running scheduled callback.
 */
@Service
public class SchedulerRunner {

    private final Log logger = LogFactory.getLog(SchedulerRunner.class);

    /**
     * Runs given {@code callback}.
     * <p>
     * Handles all exceptions thrown by {@code callback}.
     *
     * @param callback callback method
     */
    @Async
    public void safeRun(Runnable callback) {
        logger.info("Starting scheduled task");
        try {
            callback.run();
        } catch (Exception ex) {
            logger.error("Scheduled task failure: " + ex.getMessage(), ex);
        } finally {
            logger.info("Finished scheduled task");
        }
    }
}
