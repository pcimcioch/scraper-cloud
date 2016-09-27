package scraper.services.chan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Service responsible collecting 4chan threads and save their graph representation.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class ChanCollectorServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ChanCollectorServiceApp.class, args);
    }
}