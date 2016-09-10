package scraper.services.chan;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

/**
 * Configuration of neo4j database.
 */
@Configuration
@EnableNeo4jRepositories
public class Neo4JDatabaseConfiguration {

    public static final String NEO4J_DRIVER = "org.neo4j.ogm.drivers.http.driver.HttpDriver";

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration(@Value("${scraper.data.neo4j.user}") String username, @Value("${scraper.data.neo4j.password}") String password,
            @Value("${scraper.data.neo4j.host}") String host) {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config.driverConfiguration().setDriverClassName(NEO4J_DRIVER).setCredentials(username, password).setURI(String.format("http://%s:7474", host));

        return config;
    }

    @Bean
    public SessionFactory getSessionFactory(org.neo4j.ogm.config.Configuration configuration) {
        return new SessionFactory(configuration);
    }
}
