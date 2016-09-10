package scraper.services.chan.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scraper.services.chan.processor.BoardCollector;
import scraper.services.chan.processor.Settings;

import java.io.IOException;

@RestController
public class ChanCollectorController {

    private final BoardCollector collector;

    @Autowired
    public ChanCollectorController(BoardCollector collector) {
        this.collector = collector;
    }

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public String testRun() throws IOException {
        collector.collectBoard(new Settings("gif", 3));
        return "Run";
    }
}
