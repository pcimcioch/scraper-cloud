package scraper.services.scheduler.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import scraper.services.scheduler.model.ServiceInstanceDs;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ServiceRunnerTest {

    private ServiceRunner runner;

    @Mock
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        runner = new ServiceRunner(restTemplate);
    }

    @Test
    public void testRunService() {
        // given
        ServiceInstanceDs instance = new ServiceInstanceDs("service-id", "instance", "settings", "schedule");

        // when
        runner.runService(instance);

        // then
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> expectedEntity = new HttpEntity<>(instance.getSettings(), expectedHeaders);
        verify(restTemplate).exchange("http://service-id/worker/run", HttpMethod.POST, expectedEntity, String.class);
    }
}