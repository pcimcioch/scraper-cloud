package scraper.services.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scraper.services.scheduler.model.ServiceInstanceDs;

/**
 * Class responsible for running Runnable Service instances.
 */
@Service
public class ServiceRunner {

    private static final HttpHeaders JSON_CONTENT_HEADERS = new HttpHeaders();

    static {
        JSON_CONTENT_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    private final Log logger = LogFactory.getLog(ServiceRunner.class);

    private final RestTemplate restTemplate;

    @Autowired
    public ServiceRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Runs Runnable Service instance.
     *
     * @param instance instance to run
     */
    public void runService(ServiceInstanceDs instance) {
        logger.info(String.format("Running: [%s] [%s]", instance.getInstanceName(), instance.getServiceId()));

        String url = String.format("http://%s/worker/run", instance.getServiceId());
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(instance.getSettings(), JSON_CONTENT_HEADERS), String.class);
    }
}
