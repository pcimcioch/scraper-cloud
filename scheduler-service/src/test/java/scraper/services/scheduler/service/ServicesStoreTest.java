package scraper.services.scheduler.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import scraper.common.FuncUtils;
import scraper.common.Utils;
import scraper.services.scheduler.dto.ServiceDescriptorJsonDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

@RunWith(MockitoJUnitRunner.class)
public class ServicesStoreTest {

    private ServicesStore store;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DiscoveryClient discoveryClient;

    private ServiceInstance workerService1 = mockService("worker1", "worker", "true");

    private ServiceInstance workerService2 = mockService("worker2", "worker", "true", "something", "12");

    private ServiceInstance workerService3_1 = mockService("worker3", "worker", "true");

    private ServiceInstance workerService3_2 = mockService("worker3", "worker", "true");

    private ServiceInstance standardService1 = mockService("standard1", "worker", "false");

    private ServiceInstance standardService2 = mockService("standard2");

    private ServiceInstance standardService3_1 = mockService("standard3", "something", "13");

    private ServiceInstance standardService3_2 = mockService("standard3", "something", "13");

    private ServiceDescriptorJsonDto workerService1Descriptor = new ServiceDescriptorJsonDto("Worker 1", "worker1", "Worker 1 Description", null);

    private ServiceDescriptorJsonDto workerService2Descriptor = new ServiceDescriptorJsonDto("Worker 2", "worker2", "Worker 2 Description", null);

    private ServiceDescriptorJsonDto workerService3Descriptor = new ServiceDescriptorJsonDto("Worker 3", "worker3", "Worker 3 Description", null);

    @Before
    public void setUp() {
        store = new ServicesStore(restTemplate, discoveryClient);
    }

    @Test
    public void testRefresh_exceptionOnMetadata() {
        // given
        initDiscoveryClient(workerService1, workerService2, workerService3_1, workerService3_2, standardService1, standardService2, standardService3_1, standardService3_2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toThrow(new RuntimeException("Test Exception"));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when
        store.refreshServices();

        // then
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService3Descriptor);
    }

    @Test
    public void testRefresh_wrongStatusCodeOnMetadata() {
        // given
        initDiscoveryClient(workerService1, workerService2, workerService3_1, workerService3_2, standardService1, standardService2, standardService3_1, standardService3_2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when
        store.refreshServices();

        // then
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService3Descriptor);
    }

    @Test
    public void testRefresh_getAll() {
        // given
        initDiscoveryClient(workerService1, workerService2, workerService3_1, workerService3_2, standardService1, standardService2, standardService3_1, standardService3_2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when
        store.refreshServices();

        // then
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor, workerService3Descriptor);
    }

    @Test
    public void testRefresh_noChanges() {
        // given
        initDiscoveryClient(workerService1, workerService2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when then
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor);

        // when then
        initDiscoveryClient(workerService1, workerService2);
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor);
    }

    @Test
    public void testRefresh_someAdded() {
        // given
        initDiscoveryClient(workerService1, workerService2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when then
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor);

        // when then
        initDiscoveryClient(workerService1, workerService2, workerService3_2);
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor, workerService3Descriptor);
    }

    @Test
    public void testRefresh_someRemoved() {
        // given
        initDiscoveryClient(workerService1, workerService2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when then
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor);

        // when then
        initDiscoveryClient(workerService1);
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor);
    }

    @Test
    public void testRefresh_someAddedSomeRemoved() {
        // given
        initDiscoveryClient(workerService1, workerService2);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when then
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor);

        // when then
        initDiscoveryClient(workerService2, workerService3_1);
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService2Descriptor, workerService3Descriptor);
    }

    @Test
    public void testGetServices_listCopy() {
        // given
        initDiscoveryClient(workerService1, workerService2, workerService3_1);
        stub(restTemplate.getForEntity("http://worker1/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService1Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker2/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService2Descriptor, HttpStatus.OK));
        stub(restTemplate.getForEntity("http://worker3/worker/meta", ServiceDescriptorJsonDto.class)).toReturn(new ResponseEntity<>(workerService3Descriptor, HttpStatus.OK));

        // sanity
        assertTrue(store.getServices().isEmpty());

        // when
        store.refreshServices();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor, workerService3Descriptor);
        store.getServices().clear();
        assertDescriptors(store.getServices(), workerService1Descriptor, workerService2Descriptor, workerService3Descriptor);
    }

    private ServiceInstance mockService(String serviceId, String... metadata) {
        ServiceInstance instance = mock(ServiceInstance.class);
        stub(instance.getMetadata()).toReturn(Utils.map((Object[]) metadata));
        stub(instance.getServiceId()).toReturn(serviceId);

        return instance;
    }

    private void initDiscoveryClient(ServiceInstance... instances) {
        for (ServiceInstance instance : instances) {
            String serviceId = instance.getServiceId();
            List<ServiceInstance> serviceInstances = FuncUtils.filter(instances, inst -> inst.getServiceId().equals(serviceId));
            stub(discoveryClient.getInstances(serviceId)).toReturn(serviceInstances);
        }

        Set<String> serviceIds = FuncUtils.mapSet(instances, ServiceInstance::getServiceId);
        stub(discoveryClient.getServices()).toReturn(new ArrayList<>(serviceIds));
    }

    private void assertDescriptors(Collection<? extends ServiceDescriptorJsonDto> actualDescriptors, ServiceDescriptorJsonDto... expectedDescriptors) {
        assertEquals(expectedDescriptors.length, actualDescriptors.size());
        for (ServiceDescriptorJsonDto expected : expectedDescriptors) {
            assertTrue(actualDescriptors.contains(expected));
        }
    }
}