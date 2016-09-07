package scraper.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Main application gateway
 */
@SpringBootApplication
@EnableZuulProxy
public class MainGateway {

    public static void main(String[] args) {
        SpringApplication.run(MainGateway.class, args);
    }
}