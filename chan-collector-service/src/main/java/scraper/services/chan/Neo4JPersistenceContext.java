package scraper.services.chan;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Configuration of neo4j database.
 */
@Configuration
@EnableNeo4jRepositories("scraper.services.chan.repository")
public class Neo4JPersistenceContext extends Neo4jConfiguration {

    public static final String NEO4J_DRIVER = "org.neo4j.ogm.drivers.http.driver.HttpDriver";

    private final String username;

    private final String password;

    private final String host;

    public Neo4JPersistenceContext(@Value("${scraper.data.neo4j.user}") String username, @Value("${scraper.data.neo4j.password}") String password,
            @Value("${scraper.data.neo4j.host}") String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    private org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config.driverConfiguration().setDriverClassName(NEO4J_DRIVER).setCredentials(username, password).setURI(String.format("http://%s:7474", host));

        return config;
    }

    @Override
    public SessionFactory getSessionFactory() {
        SessionFactory sessionFactory = new SessionFactory(getConfiguration(), "scraper.services.chan.model", "BOOT-INF.classes.scraper.services.chan.model");

        return sessionFactory;
    }
}
