package scraper.services.scheduler.dto;

import com.fasterxml.jackson.databind.JsonNode;
import scraper.common.Utils;

/**
 * Json DTO representing Runnable Service.
 */
public class ServiceDescriptorJsonDto {

    private String name;

    private String serviceId;

    private String description;

    private JsonNode propertyDescriptor;

    public ServiceDescriptorJsonDto() {
    }

    public ServiceDescriptorJsonDto(String name, String serviceId, String description, JsonNode propertyDescriptor) {
        setName(name);
        setServiceId(serviceId);
        setDescription(description);
        setPropertyDescriptor(propertyDescriptor);
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

    public JsonNode getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public void setPropertyDescriptor(JsonNode propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
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

        return Utils.computeEq(name, other.name, serviceId, other.serviceId, description, other.description, propertyDescriptor, other.propertyDescriptor);

    }

    @Override
    public int hashCode() {
        return Utils.computeHash(name, serviceId, description, propertyDescriptor);
    }
}
