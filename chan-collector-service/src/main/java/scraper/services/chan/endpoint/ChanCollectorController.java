package scraper.services.chan.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scraper.properties.ClassPropertyDescriptorFactory;
import scraper.services.chan.processor.Settings;

import java.io.IOException;

@RestController
public class ChanCollectorController {

    private final AsyncRunner runner;

    @Autowired
    public ChanCollectorController(AsyncRunner runner) {
        this.runner = runner;
    }

    @RequestMapping(path = "/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public String run(@RequestBody Settings settings) throws IOException {
        ClassPropertyDescriptorFactory.validate(settings);
        runner.runAsync(settings);

        return "Run";
    }
}
