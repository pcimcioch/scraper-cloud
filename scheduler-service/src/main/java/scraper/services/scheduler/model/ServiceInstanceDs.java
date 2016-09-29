package scraper.services.scheduler.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import scraper.common.Utils;

/**
 * Neo4j entity representing Runnable Service Instance.
 */
@NodeEntity
public class ServiceInstanceDs {

    @GraphId
    private Long id;

    @Property(name = "serviceId")
    private String serviceId;

    @Property(name = "instanceName")
    private String instanceName;

    @Property(name = "settings")
    private String settings;

    @Property(name = "schedule")
    private String schedule;

    public ServiceInstanceDs() {
    }

    public ServiceInstanceDs(String serviceId, String instanceName, String settings, String schedule) {
        this(null, serviceId, instanceName, settings, schedule);
    }

    public ServiceInstanceDs(Long id, String serviceId, String instanceName, String settings, String schedule) {
        this.id = id;
        this.serviceId = serviceId;
        this.instanceName = instanceName;
        this.settings = settings;
        this.schedule = schedule;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
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

        ServiceInstanceDs other = (ServiceInstanceDs) obj;

        return Utils.computeEq(id, other.id, serviceId, other.serviceId, instanceName, other.instanceName, settings, other.settings, schedule, other.schedule);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(id, serviceId, instanceName, settings, schedule);
    }
}
