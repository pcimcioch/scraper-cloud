package scraper.services.scheduler.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerRunnerTest {

    @Mock
    private Runnable callback;

    private SchedulerRunner runner;

    @Before
    public void setUp() {
        runner = new SchedulerRunner();
    }

    @Test
    public void testRun_callbackThrowsException() {
        // given
        doThrow(new IllegalArgumentException("Test")).when(callback).run();

        // when
        runner.safeRun(callback);

        // then
        verify(callback).run();
    }

    @Test
    public void testRun() {
        // when
        runner.safeRun(callback);

        // then
        verify(callback).run();
    }
}