package scraper.services.scheduler.service;

import fr.zebasto.spring.post.initialize.PostInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import scraper.common.StringUtils;
import scraper.exception.ResourceNotFoundException;
import scraper.exception.ValidationException;
import scraper.services.scheduler.dto.ServiceDescriptorJsonDto;
import scraper.services.scheduler.dto.ServiceInstanceJsonReadDto;
import scraper.services.scheduler.dto.ServiceInstanceJsonWriteDto;
import scraper.services.scheduler.model.ServiceInstanceDs;
import scraper.services.scheduler.repository.ServiceInstanceDsRepository;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static scraper.common.FuncUtils.map;

/**
 * Service used to manage Runnable Services and their instances.
 */
@Service
// TODO add tests
public class SchedulerService {

    private static final String INSTANCE_PATTERN = "[a-zA-Z0-9\\.]+";

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final ServicesStore servicesStore;

    private final ServiceInstanceDsRepository instanceRepository;

    private final ServiceRunner serviceRunner;

    private final Scheduler scheduler;

    @Autowired
    public SchedulerService(ServicesStore servicesStore, ServiceInstanceDsRepository instanceRepository, ServiceRunner serviceRunner, Scheduler scheduler) {
        this.servicesStore = servicesStore;
        this.instanceRepository = instanceRepository;
        this.serviceRunner = serviceRunner;
        this.scheduler = scheduler;
    }

    @PostInitialize
    public void initScheduler() {
        for (ServiceInstanceJsonReadDto instance : getServiceInstances()) {
            reschedule(instance.getId(), instance.getSchedule());
        }
    }

    /**
     * Gets list of all Runnable Service available in application.
     *
     * @return list of available services represented as json DTOs
     */
    public List<ServiceDescriptorJsonDto> getServices() {
        return servicesStore.getServices();
    }

    /**
     * Gets list of all Runnable Service instances.
     *
     * @return list of all service instances
     */
    public List<ServiceInstanceJsonReadDto> getServiceInstances() {
        lock.readLock().lock();
        try {
            return map(instanceRepository.findAll(), ServiceInstanceJsonReadDto::new);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Runs Runnable Service instance.
     *
     * @param instanceId service instance id
     * @throws ResourceNotFoundException if service instance can not be found
     */
    public void runServiceInstance(long instanceId) {
        ServiceInstanceDs instance = getServiceInstance(instanceId);
        if (instance == null) {
            throw new ResourceNotFoundException("Instance [id=%d] not found", instanceId);
        }

        serviceRunner.runService(instance);
    }

    /**
     * Gets Runnable Service instance.
     *
     * @param instanceId service instance id
     * @return service instance, or <tt>null</tt> if instance with such id can not be found
     */
    public ServiceInstanceDs getServiceInstance(long instanceId) {
        lock.readLock().lock();
        try {
            return instanceRepository.findOne(instanceId);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Creates new Runnable Service instance.
     *
     * @param instance json DTO representing new service instance
     * @throws ValidationException      if schedule or instance name have incorrect values
     * @throws IllegalArgumentException if instance name is already taken
     */
    public void addServiceInstance(ServiceInstanceJsonWriteDto instance) {
        lock.writeLock().lock();
        try {
            validateServiceInstance(instance);
            validateSchedule(instance.getSchedule());

            ServiceInstanceDs instanceDs = instance.convertToEntity();
            instanceDs = instanceRepository.save(instanceDs);
            reschedule(instanceDs.getId(), instanceDs.getSchedule());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Deletes Runnable Service instance.
     * <p>
     * If instance can not be found, nothing will happen
     *
     * @param instanceId service instance id
     */
    public void deleteServiceInstance(long instanceId) {
        lock.writeLock().lock();
        try {
            instanceRepository.delete(instanceId);
            scheduler.cancel(instanceId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates settings in existing Runnable Service instance.
     *
     * @param instanceId  service instance id
     * @param newSettings new settings
     * @throws ResourceNotFoundException if service instance can not be found
     */
    public void updateServiceInstanceSettings(long instanceId, String newSettings) {
        lock.writeLock().lock();
        try {
            ServiceInstanceDs instanceDs = instanceRepository.findOne(instanceId);
            if (instanceDs == null) {
                throw new ResourceNotFoundException("Instance [id=%d] not found", instanceId);
            }

            instanceDs.setSettings(newSettings);
            instanceRepository.save(instanceDs);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates schedule in existing Runnable Service instance.
     *
     * @param instanceId  service instance id
     * @param newSchedule new schedule
     * @throws ResourceNotFoundException if service instance can not be found
     * @throws ValidationException       if schedule have incorrect values
     */
    public void updateServiceInstanceSchedule(long instanceId, String newSchedule) {
        lock.writeLock().lock();
        try {
            ServiceInstanceDs instanceDs = instanceRepository.findOne(instanceId);
            if (instanceDs == null) {
                throw new ResourceNotFoundException("Instance [id=%d] not found", instanceId);
            }
            validateSchedule(newSchedule);

            instanceDs.setSchedule(newSchedule);
            instanceRepository.save(instanceDs);

            reschedule(instanceDs.getId(), instanceDs.getSchedule());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void validateServiceInstance(ServiceInstanceJsonWriteDto instance) {
        String instanceName = instance.getInstanceName();
        String serviceId = instance.getServiceId();

        if (instanceName != null && !instanceName.matches(INSTANCE_PATTERN)) {
            throw new ValidationException("Instance name [%s] doesn't match allowed pattern: %s", instanceName, INSTANCE_PATTERN);
        }

        if (instanceRepository.findByServiceIdAndInstanceName(serviceId, instanceName) != null) {
            throw new IllegalArgumentException("Instance [" + instanceName + "] of service [" + serviceId + "] already exists");
        }
    }

    private static void validateSchedule(String schedule) {
        if (StringUtils.isBlank(schedule)) {
            return;
        }

        try {
            new CronTrigger(schedule);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Schedule expression [%s] is incorrect. %s", ex, schedule, ex.getMessage());
        }
    }

    private void reschedule(Long instanceId, String schedule) {
        if (StringUtils.isBlank(schedule)) {
            scheduler.cancel(instanceId);
        } else {
            scheduler.schedule(instanceId, new CronTrigger(schedule), () -> runServiceInstance(instanceId));
        }
    }
}
