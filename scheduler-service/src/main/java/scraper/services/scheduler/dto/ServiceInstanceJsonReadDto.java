package scraper.services.scheduler.dto;

import scraper.common.Utils;
import scraper.services.scheduler.model.ServiceInstanceDs;

/**
 * Json DTO representing Runnable Service instance.
 * <p>
 * This DTO is only returned by a server.
 */
public class ServiceInstanceJsonReadDto {

    private long id;

    private String serviceId;

    private String instanceName;

    private String settings;

    private String schedule;

    public ServiceInstanceJsonReadDto() {
    }

    public ServiceInstanceJsonReadDto(ServiceInstanceDs instance) {
        this(instance.getId(), instance.getServiceId(), instance.getInstanceName(), instance.getSettings(), instance.getSchedule());
    }

    public ServiceInstanceJsonReadDto(long id, String serviceId, String instanceName, String settings, String schedule) {
        this.id = id;
        this.serviceId = serviceId;
        this.instanceName = instanceName;
        this.settings = settings;
        this.schedule = schedule;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ServiceInstanceJsonReadDto other = (ServiceInstanceJsonReadDto) obj;

        return Utils.computeEq(id, other.id, serviceId, other.serviceId, instanceName, other.instanceName, settings, other.settings, schedule, other.schedule);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(id, serviceId, instanceName, settings, schedule);
    }
}
