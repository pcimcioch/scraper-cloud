package scraper.properties.option;

import scraper.common.Utils;
import scraper.properties.PropertyDescriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static scraper.common.FuncUtils.mapSet;

/**
 * Descriptor for {@link EnumProperty}.
 */
public class EnumPropertyDescriptor extends PropertyDescriptor {

    private static final String TYPE = "option";

    private Set<String> options;

    public <T extends Enum<T>> EnumPropertyDescriptor(String propertyName, Class<T> enumType, EnumProperty property) {
        this(propertyName, enumType, property.viewName(), property.description(), property.required());
    }

    public <T extends Enum<T>> EnumPropertyDescriptor(String propertyName, Set<String> options, EnumProperty property) {
        this(propertyName, options, property.viewName(), property.description(), property.required());
    }

    public <T extends Enum<T>> EnumPropertyDescriptor(String propertyName, Class<T> enumType, String viewName, String description, boolean required) {
        this(propertyName, mapSet(enumType.getEnumConstants(), T::name), viewName, description, required);
    }

    public <T extends Enum<T>> EnumPropertyDescriptor(String propertyName, Set<String> options, String viewName, String description, boolean required) {
        super(propertyName, viewName, description, required);
        setOptions(options);
    }

    public Set<String> getOptions() {
        return Collections.unmodifiableSet(options);
    }

    public void setOptions(Set<String> options) {
        this.options = new HashSet<>(options);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(super.hashCode(), options);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || getClass() != obj.getClass()) {
            return false;
        }
        EnumPropertyDescriptor other = (EnumPropertyDescriptor) obj;

        return Utils.computeEq(options, other.options);
    }
}
