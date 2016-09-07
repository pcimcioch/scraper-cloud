package scraper.services.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Service responsible for storing and accessing files.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StorageServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApp.class, args);
    }
}