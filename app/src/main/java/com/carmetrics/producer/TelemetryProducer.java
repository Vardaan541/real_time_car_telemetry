package com.carmetrics.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.carmetrics.model.VehicleTelemetry;

@Component
public class TelemetryProducer {

    private final KafkaTemplate<String, VehicleTelemetry> kafkaTemplate;
    private final String topic;

    public TelemetryProducer(KafkaTemplate<String, VehicleTelemetry> kafkaTemplate,
                             @Value("${app.kafka.raw-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(VehicleTelemetry telemetry) {
        kafkaTemplate.send(topic, telemetry.getVehicleId(), telemetry);
    }
}
