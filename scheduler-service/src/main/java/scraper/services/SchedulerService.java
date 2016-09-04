package scraper.services;

import java.util.Arrays;
import java.util.List;

public class SchedulerService {

    public List<ScheduleJsonDto> getSchedules() {
        return Arrays.asList(new ScheduleJsonDto("test name 1", "test schedule 1"), new ScheduleJsonDto("test name 2", "test schedule 2"));
    }
}
