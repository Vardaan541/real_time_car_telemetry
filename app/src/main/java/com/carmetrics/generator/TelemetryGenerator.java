package com.carmetrics.generator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.carmetrics.config.GeneratorProperties;
import com.carmetrics.model.VehicleTelemetry;
import com.carmetrics.producer.TelemetryProducer;

import jakarta.annotation.PostConstruct;

@Component
public class TelemetryGenerator {

    private final TelemetryProducer producer;
    private final GeneratorProperties properties;
    private final List<VehicleState> vehicles = new ArrayList<>();

    public TelemetryGenerator(TelemetryProducer producer, GeneratorProperties properties) {
        this.producer = producer;
        this.properties = properties;
    }

    @PostConstruct
    void initializeVehicles() {
        int count = properties.getVehicleCount();
        for (int index = 0; index < count; index++) {
            vehicles.add(new VehicleState(index, count));
        }
    }

    @Scheduled(fixedRateString = "${app.generator.interval-ms}")
    public void generate() {
        if (!properties.isEnabled()) {
            return;
        }
        for (VehicleState vehicle : vehicles) {
            VehicleTelemetry telemetry = vehicle.next(properties.getIntervalMs());
            producer.send(telemetry);
        }
    }
}
