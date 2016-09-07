package scraper.services.scheduler;

import scraper.common.Utils;

public class ScheduleJsonDto {

    private String workerName;

    private String schedule;

    public ScheduleJsonDto() {
    }

    public ScheduleJsonDto(String workerName, String schedule) {
        this.workerName = workerName;
        this.schedule = schedule;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ScheduleJsonDto other = (ScheduleJsonDto) obj;

        return Utils.computeEq(workerName, other.workerName, schedule, other.schedule);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(workerName, schedule);
    }
}
