package scraper.services.chan.repository;

import org.springframework.data.repository.CrudRepository;
import scraper.services.chan.model.CollectorProcessedCheckpointDs;

/**
 * Neo4j repository for {@link CollectorProcessedCheckpointDs}.
 */
public interface CollectorProcessedCheckpointDsRepository extends CrudRepository<CollectorProcessedCheckpointDs, Long> {

    CollectorProcessedCheckpointDs findByBoardName(String boardName);
}
