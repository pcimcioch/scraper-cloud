package scraper.services.chan.dto;

import scraper.common.Utils;
import scraper.properties.ClassPropertyDescriptor;

public class WorkerDescriptorJsonDto {

    private final String name;

    private final String description;

    private final ClassPropertyDescriptor propertyDescriptor;

    public WorkerDescriptorJsonDto(String name, String description, ClassPropertyDescriptor propertyDescriptor) {
        this.name = name;
        this.description = description;
        this.propertyDescriptor = propertyDescriptor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ClassPropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkerDescriptorJsonDto other = (WorkerDescriptorJsonDto) o;

        return Utils.computeEq(name, other.name, description, other.description, propertyDescriptor, other.propertyDescriptor);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(name, description, propertyDescriptor);
    }
}
