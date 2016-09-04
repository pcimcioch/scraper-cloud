package scraper.properties;

import org.junit.Test;
import scraper.exception.IllegalAnnotationException;
import scraper.exception.ValidationException;
import scraper.properties.bool.BoolPropertyDescriptor;
import scraper.properties.string.StringPropertyDescriptor;
import scraper.properties.testclasses.InapplicablePropertyTypeTestClass;
import scraper.properties.testclasses.MultiplePropertiesTestClass;
import scraper.properties.testclasses.NoPropertiesTestClass;
import scraper.properties.testclasses.OnlyPropertiesTestClass;
import scraper.properties.testclasses.SomePropertiesTestClass;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ClassPropertyDescriptorFactoryTest {

    @Test
    public void testBuildClassPropertyDescriptor() {
        assertBuild(NoPropertiesTestClass.class, new NoPropertiesTestClass(1, "a"));
        assertBuild(OnlyPropertiesTestClass.class, new OnlyPropertiesTestClass("a", "b", true), new StringPropertyDescriptor("field1", "view", "description", 12, 34, "v.*", true),
                new StringPropertyDescriptor("field2", "view2", "description2", 10, 12, ".*b", true), new BoolPropertyDescriptor("field3", "view3", "description3"));
        assertBuild(SomePropertiesTestClass.class, new SomePropertiesTestClass(1, "a", "b"), new StringPropertyDescriptor("field2", "view", "description", 10, 12, "", true));
    }

    @Test
    public void testBuildClassPropertyDescriptor_multipleDescriptors() {
        assertBuildFailed(MultiplePropertiesTestClass.class, new MultiplePropertiesTestClass("a"),
                "Field [scraper.properties.testclasses.MultiplePropertiesTestClass.field1] has more than one Property Descriptor");
    }

    @Test
    public void testBuildClassPropertyDescriptor_inapplicableType() {
        assertBuildFailed(InapplicablePropertyTypeTestClass.class, new InapplicablePropertyTypeTestClass(1),
                "Annotation StringProperty cannot be applied to field [field1] with type [int]");
    }

    @Test
    public void testValidate_multipleDescriptors() {
        assertValidateAnnotationFailed(new MultiplePropertiesTestClass("value1"),
                "Field [scraper.properties.testclasses.MultiplePropertiesTestClass.field1] has more than one Property Descriptor");
    }

    @Test
    public void testValidate_inapplicableType() {
        assertValidateAnnotationFailed(new InapplicablePropertyTypeTestClass(10), "Value must be a String");
    }

    @Test
    public void testValidate_validationFailed() {
        assertValidateFailed(new SomePropertiesTestClass(12, "01", "value"),
                "Field [scraper.properties.testclasses.SomePropertiesTestClass.field2] validation exception: Value [01] must be at least 10 characters long");
    }

    @Test
    public void testValidate() {
        assertValidate(new NoPropertiesTestClass(12, "value2"));
        assertValidate(new OnlyPropertiesTestClass("valueeeeeeeeeeeeeeeee", "123456789b", true));
        assertValidate(new SomePropertiesTestClass(12, "0123456789", "value"));
    }

    private void assertValidate(Object obj) {
        ClassPropertyDescriptorFactory.validate(obj);
    }

    private void assertValidateFailed(Object obj, String exceptionMsg) {
        try {
            ClassPropertyDescriptorFactory.validate(obj);
            fail();
        } catch (ValidationException ex) {
            assertEquals(exceptionMsg, ex.getMessage());
        }
    }

    private void assertValidateAnnotationFailed(Object obj, String exceptionMsg) {
        try {
            ClassPropertyDescriptorFactory.validate(obj);
            fail();
        } catch (IllegalAnnotationException ex) {
            assertEquals(exceptionMsg, ex.getMessage());
        }
    }

    private <T> void assertBuild(Class<T> type, T defaultObject, PropertyDescriptor... descriptors) {
        ClassPropertyDescriptor descriptor = ClassPropertyDescriptorFactory.buildClassPropertyDescriptor(type, defaultObject);
        assertEquals(new ClassPropertyDescriptor(Arrays.asList(descriptors), defaultObject), descriptor);
    }

    private <T> void assertBuildFailed(Class<T> type, T defaultObject, String exceptionMsg) {
        try {
            ClassPropertyDescriptorFactory.buildClassPropertyDescriptor(type, defaultObject);
            fail();
        } catch (IllegalAnnotationException ex) {
            assertEquals(exceptionMsg, ex.getMessage());
        }
    }
}