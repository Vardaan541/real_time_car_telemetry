package com.carmetrics.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic rawTelemetryTopic(@Value("${app.kafka.raw-topic}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic processedTelemetryTopic(@Value("${app.kafka.processed-topic}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
