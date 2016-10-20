package scraper.services.chan.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scraper.properties.ClassPropertyDescriptorFactory;
import scraper.services.chan.dto.WorkerDescriptorJsonDto;
import scraper.services.chan.processor.Settings;
import scraper.services.common.StatusMessage;

import java.io.IOException;

/**
 * Controller for managing whole service.
 */
@RestController
public class ChanCollectorController {

    private final WorkerDescriptorJsonDto metadata =
            new WorkerDescriptorJsonDto("Chan Collector", "4Chan boards Collector", ClassPropertyDescriptorFactory.buildClassPropertyDescriptor(Settings.class, new Settings()));

    private final AsyncRunner runner;

    @Autowired
    public ChanCollectorController(AsyncRunner runner) {
        this.runner = runner;
    }

    /**
     * Runs the worker.
     *
     * @param settings settings for the run
     * @return status message
     */
    @RequestMapping(path = "/worker/run", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public StatusMessage run(@RequestBody Settings settings) throws IOException {
        ClassPropertyDescriptorFactory.validate(settings);
        runner.runAsync(settings);

        return new StatusMessage("Run");
    }

    /**
     * Gets worker metadata.
     *
     * @return worker description
     */
    @RequestMapping(path = "/worker/meta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkerDescriptorJsonDto getMetadata() {
        return metadata;
    }
}
