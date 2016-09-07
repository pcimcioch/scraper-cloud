package scraper.services.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchedulerController {

    private final SchedulerService service;

    @Autowired
    public SchedulerController(SchedulerService service) {
        this.service = service;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public List<ScheduleJsonDto> getSchedules() {
        return service.getSchedules();
    }
}
