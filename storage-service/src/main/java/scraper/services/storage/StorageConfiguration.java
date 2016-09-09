package scraper.services.storage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

/**
 * Configuration for storage service.
 */
@Configuration
public class StorageConfiguration {

    @Bean(destroyMethod = "")
    FileSystem filesystem() {
        return FileSystems.getDefault();
    }
}
