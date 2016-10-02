package scraper.services.scheduler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import scraper.services.scheduler.dto.ServiceDescriptorJsonDto;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Service used to store Runnable Services Descriptors.
 */
@Service
// TODO add tests
public class ServicesStore {

    /**
     * Gets list of all Runnable Service available in application.
     *
     * @return list of available services represented as json DTOs
     */
    public List<ServiceDescriptorJsonDto> getServices() {
        // TODO implement
        JsonNode descriptor = fromJson("{\"descriptors\":[{\"propertyName"
                + "\":\"boardName\",\"viewName\":\"Board Name\",\"description\":\"Board Name\",\"required\":true,\"minLength\":2,\"maxLength\":255,\"pattern\":\"\","
                + "\"type\":\"text\"},{\"propertyName\":\"maxPages\",\"viewName\":\"Maximum Pages\",\"description\":\"Maximum Pages to process during one collection\","
                + "\"required\":false,\"type\":\"number\"}],\"defaultObject\":{\"boardName\":null,\"maxPages\":5}}");
        return Collections.singletonList(new ServiceDescriptorJsonDto("Chan Collector", "chan-collector-service", "4Chan Collector Service", descriptor));
    }

    // TODO remove
    private JsonNode fromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            return null;
        }
    }
}
