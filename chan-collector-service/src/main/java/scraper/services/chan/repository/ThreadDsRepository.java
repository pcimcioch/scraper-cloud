package scraper.services.chan.repository;

import org.springframework.data.repository.CrudRepository;
import scraper.services.chan.model.ThreadDs;

/**
 * Neo4j repository for {@link ThreadDs}.
 */
public interface ThreadDsRepository extends CrudRepository<ThreadDs, Long> {

    ThreadDs findByThreadId(String threadId);
}
