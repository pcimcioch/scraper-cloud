package scraper.services.scheduler.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scraper.services.common.StatusMessage;
import scraper.services.scheduler.dto.ServiceDescriptorJsonDto;
import scraper.services.scheduler.dto.ServiceInstanceJsonReadDto;
import scraper.services.scheduler.dto.ServiceInstanceJsonWriteDto;
import scraper.services.scheduler.service.SchedulerService;

import java.util.List;

/**
 * REST Controller used to manage Runnable Services instances in the application.
 */
// TODO go through all classes and add javadocs
@RestController
public class SchedulerController {

    private final SchedulerService schedulerService;

    @Autowired
    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Returns all available services.
     *
     * @return list of services described as json DTOs
     */
    @RequestMapping(path = "/service", method = RequestMethod.GET)
    public List<ServiceDescriptorJsonDto> getServices() {
        return schedulerService.getServices();
    }

    /**
     * Returns all Runnable Service instances.
     *
     * @return list of service instances json DTOs
     */
    @RequestMapping(path = "/service/instance", method = RequestMethod.GET)
    public List<ServiceInstanceJsonReadDto> getServiceInstances() {
        return schedulerService.getServiceInstances();
    }

    /**
     * Runs Runnable Service instance.
     *
     * @param instanceId service instance id
     * @return status message
     */
    @RequestMapping(path = "/service/instance/{instanceId}/run", method = RequestMethod.GET)
    public StatusMessage runServiceInstance(@PathVariable("instanceId") long instanceId) {
        schedulerService.runServiceInstance(instanceId);
        return new StatusMessage("Started");
    }

    /**
     * Deletes Runnable Service instance.
     *
     * @param instanceId service instance id
     * @return status message
     */
    @RequestMapping(path = "/service/instance/{instanceId}", method = RequestMethod.DELETE)
    public StatusMessage deleteServiceInstance(@PathVariable("instanceId") long instanceId) {
        schedulerService.deleteServiceInstance(instanceId);
        return new StatusMessage("Deleted");
    }

    /**
     * Updates Runnable Service instance settings.
     *
     * @param instanceId service instance id
     * @param settings   new settings
     * @return status message
     */
    @RequestMapping(path = "/service/instance/{instanceId}/settings", method = RequestMethod.PUT, consumes = "application/json")
    public StatusMessage updateServiceInstanceSettings(@PathVariable("instanceId") long instanceId, @RequestBody String settings) {
        schedulerService.updateServiceInstanceSettings(instanceId, settings);
        return new StatusMessage("Settings Updated");
    }

    /**
     * Updates Runnable Service instance schedule.
     *
     * @param instanceId service instance id
     * @param schedule   new schedule
     * @return status message
     */
    @RequestMapping(path = "/service/instance/{instanceId}/schedule", method = RequestMethod.PUT, consumes = "text/plain")
    public StatusMessage updateServiceInstanceSchedule(@PathVariable("instanceId") long instanceId, @RequestBody(required = false) String schedule) {
        schedulerService.updateServiceInstanceSchedule(instanceId, schedule);
        return new StatusMessage("Schedule Updated");
    }

    /**
     * Creates new Runnable Service instance.
     *
     * @param serviceInstance new service instance json DTO.
     * @return status message
     */
    @RequestMapping(path = "/service/instance/", method = RequestMethod.POST, consumes = "application/json")
    public StatusMessage createServiceInstance(@RequestBody ServiceInstanceJsonWriteDto serviceInstance) {
        schedulerService.addServiceInstance(serviceInstance);
        return new StatusMessage("Added");
    }
}
