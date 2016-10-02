package scraper.services.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import scraper.services.scheduler.model.ServiceInstanceDs;

/**
 * Class responsible for running Runnable Service instances.
 */
@Service
// TODO add tests
public class ServiceRunner {

    private final Log logger = LogFactory.getLog(ServiceRunner.class);

    /**
     * Runs Runnable Service instance.
     *
     * @param instance instance to run
     */
    public void runService(ServiceInstanceDs instance) {
        // TODO implement
        String msg = String.format("Running: %s %s", instance.getInstanceName(), instance.getServiceId());
        logger.info(msg);
    }
}
