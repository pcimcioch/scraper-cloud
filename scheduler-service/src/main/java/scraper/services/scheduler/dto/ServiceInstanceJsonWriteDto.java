package scraper.services.scheduler.dto;

import scraper.services.scheduler.model.ServiceInstanceDs;

/**
 * Json DTO representing Runnable Service instance.
 * <p>
 * This DTO is only accepted by a server.
 */
public class ServiceInstanceJsonWriteDto {

    private String serviceId;

    private String instanceName;

    private String settings;

    private String schedule;

    public ServiceInstanceJsonWriteDto() {
    }

    public ServiceInstanceJsonWriteDto(String serviceId, String instanceName, String settings, String schedule) {
        this.serviceId = serviceId;
        this.instanceName = instanceName;
        this.settings = settings;
        this.schedule = schedule;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public ServiceInstanceDs convertToEntity() {
        return new ServiceInstanceDs(serviceId, instanceName, settings, schedule);
    }
}
