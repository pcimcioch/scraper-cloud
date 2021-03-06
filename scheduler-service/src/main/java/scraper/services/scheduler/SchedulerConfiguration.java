package scraper.services.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SchedulerConfiguration {

    @Bean
    public static TaskScheduler getTaskScheduler(@Value("${scraper.service.scheduler.pool-size:5}") int poolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);

        return taskScheduler;
    }

    @Bean
    @LoadBalanced
    public static RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
