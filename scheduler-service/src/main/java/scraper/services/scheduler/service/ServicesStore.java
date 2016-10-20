package scraper.services.scheduler.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scraper.common.FuncUtils;
import scraper.services.scheduler.dto.ServiceDescriptorJsonDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Service used to store Runnable Services Descriptors.
 */
@Service
// TODO add tests
public class ServicesStore {

    private final Log logger = LogFactory.getLog(ServicesStore.class);

    private final List<ServiceDescriptorJsonDto> services = new ArrayList<>();

    private final ReadWriteLock servicesLock = new ReentrantReadWriteLock();

    private final DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate;

    @Autowired
    public ServicesStore(RestTemplate restTemplate, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.discoveryClient = discoveryClient;
    }

    /**
     * Gets list of all Runnable Service available in application.
     *
     * @return list of available services represented as json DTOs
     */
    public List<ServiceDescriptorJsonDto> getServices() {
        servicesLock.readLock().lock();
        try {
            return new ArrayList<>(services);
        } finally {
            servicesLock.readLock().unlock();
        }
    }

    @Scheduled(fixedRate = 30000)
    protected void refreshServices() {
        Set<String> actualServiceIds = getWorkerServiceIds();
        Set<String> currentServiceIds = getServiceIds();

        List<ServiceDescriptorJsonDto> newServices = getDescriptors(FuncUtils.filterSet(actualServiceIds, id -> !currentServiceIds.contains(id)));

        servicesLock.writeLock().lock();
        try {
            services.addAll(newServices);
            services.removeIf(s -> !actualServiceIds.contains(s.getServiceId()));
        } finally {
            servicesLock.writeLock().unlock();
        }
    }

    private List<ServiceDescriptorJsonDto> getDescriptors(Set<String> serviceIds) {
        List<ServiceDescriptorJsonDto> descriptors = new ArrayList<>(serviceIds.size());
        for (String serviceId : serviceIds) {
            ServiceDescriptorJsonDto descriptor = fetchDescriptor(serviceId);
            if (descriptor != null) {
                descriptors.add(descriptor);
            }
        }

        return descriptors;
    }

    private ServiceDescriptorJsonDto fetchDescriptor(String serviceId) {
        String url = String.format("http://%s/worker/meta", serviceId);

        try {
            ResponseEntity<ServiceDescriptorJsonDto> response = restTemplate.getForEntity(url, ServiceDescriptorJsonDto.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ServiceDescriptorJsonDto descriptor = response.getBody();
                descriptor.setServiceId(serviceId);
                return descriptor;
            } else {
                logger.warn(String.format("Unable to obtain worker [%s] metadata. Status code: %s", serviceId, response.getStatusCode()));
            }
        } catch (Exception ex) {
            logger.warn(String.format("Unable to obtain worker [%s] metadata", serviceId), ex);
        }

        return null;
    }

    private Set<String> getServiceIds() {
        servicesLock.readLock().lock();
        try {
            return FuncUtils.mapSet(services, ServiceDescriptorJsonDto::getServiceId);
        } finally {
            servicesLock.readLock().unlock();
        }
    }

    private Set<String> getWorkerServiceIds() {
        Set<String> serviceIds = new HashSet<>();
        for (String serviceId : discoveryClient.getServices()) {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
            if (instances.isEmpty() || !"true".equals(instances.get(0).getMetadata().getOrDefault("worker", "false"))) {
                continue;
            }

            serviceIds.add(serviceId);
        }

        return serviceIds;
    }
}
