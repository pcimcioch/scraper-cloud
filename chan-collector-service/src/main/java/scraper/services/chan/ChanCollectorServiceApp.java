package scraper.services.chan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Service responsible collecting 4chan threads and save their graph representation.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ChanCollectorServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ChanCollectorServiceApp.class, args);
    }
}