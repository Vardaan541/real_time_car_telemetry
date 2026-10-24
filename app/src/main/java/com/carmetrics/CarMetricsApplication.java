package com.carmetrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.carmetrics.config.GeneratorProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(GeneratorProperties.class)
public class CarMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarMetricsApplication.class, args);
    }
}
