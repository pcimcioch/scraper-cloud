package scraper.services.scheduler.dto;

import scraper.common.Utils;

/**
 * Json DTO representing Runnable Service.
 */
public class ServiceDescriptorJsonDto {

    private String name;

    private String serviceId;

    private String description;

    private String propertyDescriptorJson;

    public ServiceDescriptorJsonDto(String name, String serviceId, String description, String propertyDescriptorJson) {
        setName(name);
        setServiceId(serviceId);
        setDescription(description);
        setPropertyDescriptorJson(propertyDescriptorJson);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPropertyDescriptorJson() {
        return propertyDescriptorJson;
    }

    public void setPropertyDescriptorJson(String propertyDescriptorJson) {
        this.propertyDescriptorJson = propertyDescriptorJson;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ServiceDescriptorJsonDto other = (ServiceDescriptorJsonDto) obj;

        return Utils.computeEq(name, other.name, serviceId, other.serviceId, description, other.description, propertyDescriptorJson, other.propertyDescriptorJson);

    }

    @Override
    public int hashCode() {
        return Utils.computeHash(name, serviceId, description, propertyDescriptorJson);
    }
}
