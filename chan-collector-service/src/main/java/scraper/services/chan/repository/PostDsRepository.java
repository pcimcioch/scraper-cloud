package scraper.services.chan.repository;

import org.springframework.data.repository.CrudRepository;
import scraper.services.chan.model.PostDs;

/**
 * Neo4j repository for {@link PostDs}.
 */
public interface PostDsRepository extends CrudRepository<PostDs, Long> {

}
