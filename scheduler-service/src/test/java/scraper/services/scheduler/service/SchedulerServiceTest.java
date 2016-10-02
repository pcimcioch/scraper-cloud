package scraper.services.scheduler.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.scheduling.support.CronTrigger;
import scraper.exception.ResourceNotFoundException;
import scraper.exception.ValidationException;
import scraper.services.scheduler.dto.ServiceInstanceJsonReadDto;
import scraper.services.scheduler.dto.ServiceInstanceJsonWriteDto;
import scraper.services.scheduler.model.ServiceInstanceDs;
import scraper.services.scheduler.repository.ServiceInstanceDsRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerServiceTest {

    @Mock
    private ServiceInstanceDsRepository instanceRepository;

    @Mock
    private ServiceRunner serviceRunner;

    @Mock
    private Scheduler scheduler;

    private SchedulerService service;

    @Before
    public void setUp() {
        service = new SchedulerService(instanceRepository, serviceRunner, scheduler);
    }

    @Test
    public void testInitScheduler_noInstances() {
        // given
        when(instanceRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        service.initScheduler();

        // then
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testInitScheduler() {
        // given
        ServiceInstanceDs instanceDs1 = new ServiceInstanceDs(12L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        ServiceInstanceDs instanceDs2 = new ServiceInstanceDs(13L, "chan-collector-service", "ins2", "settings2", null);
        when(instanceRepository.findAll()).thenReturn(Arrays.asList(instanceDs1, instanceDs2));

        // when
        service.initScheduler();

        // then
        verify(scheduler).schedule(eq(12L), eq(new CronTrigger("0 15 9-17 * * MON-FRI")), any(Runnable.class));
        verify(scheduler).cancel(13L);
    }

    @Test
    public void testGetServiceInstances_noInstances() {
        // given
        when(instanceRepository.findAll()).thenReturn(Collections.emptyList());

        // when
        List<ServiceInstanceJsonReadDto> instances = service.getServiceInstances();

        // then
        assertTrue(instances.isEmpty());
    }

    @Test
    public void testGetServiceInstances() {
        // given
        ServiceInstanceDs instanceDs1 = new ServiceInstanceDs(12L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        ServiceInstanceDs instanceDs2 = new ServiceInstanceDs(13L, "chan-collector-service", "ins2", "settings2", null);
        when(instanceRepository.findAll()).thenReturn(Arrays.asList(instanceDs1, instanceDs2));

        // when
        List<ServiceInstanceJsonReadDto> instances = service.getServiceInstances();

        // then
        assertEquals(Arrays.asList(new ServiceInstanceJsonReadDto(12L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI"),
                new ServiceInstanceJsonReadDto(13L, "chan-collector-service", "ins2", "settings2", null)), instances);
    }

    @Test
    public void testRunServiceInstance_missingInstance() {
        // given
        when(instanceRepository.findOne(13L)).thenReturn(null);

        // when
        try {
            service.runServiceInstance(13L);
            fail();
        } catch (ResourceNotFoundException ex) {
            // expected
        }

        // then
        verify(serviceRunner, never()).runService(any());
    }

    @Test
    public void testRunServiceInstance() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        service.runServiceInstance(13L);

        // then
        verify(serviceRunner).runService(instanceDs);
    }

    @Test
    public void testAddServiceInstance_incorrectSchedule() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "ins1", "settings1", "incorrect");

        // when
        try {
            service.addServiceInstance(instance);
            fail();
        } catch (ValidationException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testAddServiceInstance_nullInstanceName() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", null, "settings1", "0 15 9-17 * * MON-FRI");

        // when
        try {
            service.addServiceInstance(instance);
            fail();
        } catch (ValidationException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testAddServiceInstance_emptyInstanceName() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "", "settings1", "0 15 9-17 * * MON-FRI");

        // when
        try {
            service.addServiceInstance(instance);
            fail();
        } catch (ValidationException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testAddServiceInstance_incorrectInstanceName() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "!%", "settings1", "0 15 9-17 * * MON-FRI");

        // when
        try {
            service.addServiceInstance(instance);
            fail();
        } catch (ValidationException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testAddServiceInstance_duplicatedInstanceName() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "ins1", "settings1", "0 15 9-17 * * MON-FRI");
        ServiceInstanceDs existingInstance = new ServiceInstanceDs(10L, "chan-collector-service", "ins1", "settings2", "0 14 10-16 * * MON-FRI");
        when(instanceRepository.findByServiceIdAndInstanceName("chan-collector-service", "ins1")).thenReturn(existingInstance);

        // when
        try {
            service.addServiceInstance(instance);
            fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testAddServiceInstance() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "ins1", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", "0 15 9-17 * * MON-FRI"))).thenReturn(
                new ServiceInstanceDs(13L, "chan-collector-service", "ins1", "settings1", "0 15 9-17 * * MON-FRI"));

        // when
        service.addServiceInstance(instance);

        // then
        verify(instanceRepository).save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", "0 15 9-17 * * MON-FRI"));
        verify(scheduler).schedule(eq(13L), eq(new CronTrigger("0 15 9-17 * * MON-FRI")), any(Runnable.class));
    }

    @Test
    public void testAddServiceInstance_nullSchedule() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "ins1", "settings1", null);
        when(instanceRepository.save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", null))).thenReturn(
                new ServiceInstanceDs(13L, "chan-collector-service", "ins1", "settings1", null));

        // when
        service.addServiceInstance(instance);

        // then
        verify(instanceRepository).save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", null));
        verify(scheduler).cancel(13L);
    }

    @Test
    public void testAddServiceInstance_emptySchedule() {
        // given
        ServiceInstanceJsonWriteDto instance = new ServiceInstanceJsonWriteDto("chan-collector-service", "ins1", "settings1", "");
        when(instanceRepository.save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", ""))).thenReturn(
                new ServiceInstanceDs(13L, "chan-collector-service", "ins1", "settings1", ""));

        // when
        service.addServiceInstance(instance);

        // then
        verify(instanceRepository).save(new ServiceInstanceDs("chan-collector-service", "ins1", "settings1", ""));
        verify(scheduler).cancel(13L);
    }

    @Test
    public void testDeleteServiceInstance() {
        // when
        service.deleteServiceInstance(13L);

        // then
        verify(instanceRepository).delete(13L);
        verify(scheduler).cancel(13L);
    }

    @Test
    public void testUpdateServiceInstanceSettings_noInstance() {
        // given
        when(instanceRepository.findOne(13L)).thenReturn(null);

        // when
        try {
            service.updateServiceInstanceSettings(13L, "newSettings");
            fail();
        } catch (ResourceNotFoundException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
    }

    @Test
    public void testUpdateServiceInstanceSettings() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        service.updateServiceInstanceSettings(13L, "newSettings");

        // then
        ServiceInstanceDs expectedInstanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "newSettings", "0 15 9-17 * * MON-FRI");
        verify(instanceRepository).save(expectedInstanceDs);
    }

    @Test
    public void testUpdateServiceInstanceSchedule_noInstance() {
        // given
        when(instanceRepository.findOne(13L)).thenReturn(null);

        // when
        try {
            service.updateServiceInstanceSchedule(13L, "0 15 10-12 * * MON-FRI");
            fail();
        } catch (ResourceNotFoundException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testUpdateServiceInstanceSchedule_incorrectSchedule() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        try {
            service.updateServiceInstanceSchedule(13L, "incorrect");
            fail();
        } catch (ValidationException ex) {
            // expected
        }

        // then
        verify(instanceRepository, never()).save(any(ServiceInstanceDs.class));
        verifyNoMoreInteractions(scheduler);
    }

    @Test
    public void testUpdateServiceInstanceSchedule() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        service.updateServiceInstanceSchedule(13L, "0 15 10-12 * * MON-FRI");

        // then
        ServiceInstanceDs expectedInstanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 10-12 * * MON-FRI");
        verify(instanceRepository).save(expectedInstanceDs);
        verify(scheduler).schedule(eq(13L), eq(new CronTrigger("0 15 10-12 * * MON-FRI")), any(Runnable.class));
    }

    @Test
    public void testUpdateServiceInstanceSchedule_nullSchedule() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        service.updateServiceInstanceSchedule(13L, null);

        // then
        ServiceInstanceDs expectedInstanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", null);
        verify(instanceRepository).save(expectedInstanceDs);
        verify(scheduler).cancel(13L);
    }

    @Test
    public void testUpdateServiceInstanceSchedule_emptySchedule() {
        // given
        ServiceInstanceDs instanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "0 15 9-17 * * MON-FRI");
        when(instanceRepository.findOne(13L)).thenReturn(instanceDs);

        // when
        service.updateServiceInstanceSchedule(13L, "");

        // then
        ServiceInstanceDs expectedInstanceDs = new ServiceInstanceDs(13L, "chan-collector-service", "ins", "settings1", "");
        verify(instanceRepository).save(expectedInstanceDs);
        verify(scheduler).cancel(13L);
    }
}