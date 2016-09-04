package scraper.properties.testclasses;

import scraper.properties.bool.BoolProperty;
import scraper.properties.string.StringProperty;

public class MultiplePropertiesTestClass {

    @StringProperty(description = "description", viewName = "view")
    @BoolProperty(description = "description 2", viewName = "view 2")
    public String field1;

    public MultiplePropertiesTestClass(String field1) {
        this.field1 = field1;
    }
}
