package com.carmetrics.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carmetrics.cassandra.VehicleTelemetryEntity;
import com.carmetrics.cassandra.VehicleTelemetryRepository;
import com.carmetrics.model.VehicleTelemetry;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final VehicleTelemetryRepository repository;
    private final String page;

    public TelemetryController(VehicleTelemetryRepository repository) throws IOException {
        this.repository = repository;
        try (InputStream input = TelemetryController.class.getResourceAsStream("/static/index.html")) {
            if (input == null) {
                throw new IOException("Missing static/index.html");
            }
            this.page = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<?> recent(@PathVariable("vehicleId") String vehicleId,
                                    @RequestParam(name = "limit", defaultValue = "20") int limit,
                                    HttpServletRequest request) {
        if (wantsHtml(request)) {
            return htmlPage();
        }
        int appliedLimit = Math.min(Math.max(limit, 1), 100);
        List<VehicleTelemetry> rows = repository.findRecent(vehicleId, appliedLimit).stream()
                .map(this::toModel)
                .toList();
        return ResponseEntity.ok(rows);
    }

    @GetMapping("/{vehicleId}/latest")
    public ResponseEntity<?> latest(@PathVariable("vehicleId") String vehicleId, HttpServletRequest request) {
        if (wantsHtml(request)) {
            return htmlPage();
        }
        List<VehicleTelemetryEntity> rows = repository.findRecent(vehicleId, 1);
        if (rows.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toModel(rows.get(0)));
    }

    private static boolean wantsHtml(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html") && !accept.contains("application/json");
    }

    private ResponseEntity<String> htmlPage() {
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(page);
    }

    private VehicleTelemetry toModel(VehicleTelemetryEntity entity) {
        VehicleTelemetry telemetry = new VehicleTelemetry();
        telemetry.setVehicleId(entity.getVehicleId());
        telemetry.setLatitude(entity.getLatitude());
        telemetry.setLongitude(entity.getLongitude());
        telemetry.setSpeed(entity.getSpeed());
        telemetry.setRpm(entity.getRpm());
        telemetry.setFuelLevel(entity.getFuelLevel());
        telemetry.setEngineTemperature(entity.getEngineTemperature());
        telemetry.setBatteryStatus(entity.getBatteryStatus());
        telemetry.setEventTime(entity.getEventTime().toEpochMilli());
        return telemetry;
    }
}
