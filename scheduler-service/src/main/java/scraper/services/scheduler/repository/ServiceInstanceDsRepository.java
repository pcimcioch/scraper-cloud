package scraper.services.scheduler.repository;

import org.springframework.data.repository.CrudRepository;
import scraper.services.scheduler.model.ServiceInstanceDs;

/**
 * Neo4j repository for {@link ServiceInstanceDs}.
 */
public interface ServiceInstanceDsRepository extends CrudRepository<ServiceInstanceDs, Long> {

    ServiceInstanceDs findByServiceIdAndInstanceName(String serviceId, String instanceName);
}
