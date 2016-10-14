package scraper.services.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scraper.services.scheduler.model.ServiceInstanceDs;

import java.util.List;

/**
 * Class responsible for running Runnable Service instances.
 */
@Service
// TODO add tests
public class ServiceRunner {

    private static final HttpHeaders JSON_CONTENT_HEADERS = new HttpHeaders();

    static {
        JSON_CONTENT_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    private final Log logger = LogFactory.getLog(ServiceRunner.class);

    // TODO load balance it
    private final RestTemplate restTemplate;

    private final DiscoveryClient discoveryClient;

    @Autowired
    public ServiceRunner(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Runs Runnable Service instance.
     *
     * @param instance instance to run
     */
    public void runService(ServiceInstanceDs instance) {
        logger.info(String.format("Running: %s %s", instance.getInstanceName(), instance.getServiceId()));

        List<ServiceInstance> instances = discoveryClient.getInstances("chan-collector-service");

        String url = String.format("http://%s:8081/worker/run", instance.getServiceId());
        restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(instance.getSettings(), JSON_CONTENT_HEADERS), String.class);
    }
}
