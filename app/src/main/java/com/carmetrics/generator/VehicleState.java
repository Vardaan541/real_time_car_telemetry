package com.carmetrics.generator;

import java.util.Random;

import com.carmetrics.model.VehicleTelemetry;

final class VehicleState {

    private static final double START_LATITUDE = 37.7749;
    private static final double START_LONGITUDE = -122.4194;
    private static final double KM_PER_DEGREE_LATITUDE = 111.0;

    private final String vehicleId;
    private final double headingRadians;
    private final Random random;

    private double latitude;
    private double longitude;
    private double speed;
    private double fuelLevel;
    private double engineTemperature;
    private double batteryStatus;

    VehicleState(int index, int vehicleCount) {
        this.vehicleId = String.format("vehicle-%03d", index + 1);
        this.random = new Random();
        this.latitude = START_LATITUDE + index * 0.01;
        this.longitude = START_LONGITUDE + index * 0.01;
        this.headingRadians = vehicleCount == 0 ? 0 : index * (2 * Math.PI / vehicleCount);
        this.speed = 40 + random.nextDouble() * 40;
        this.fuelLevel = 60 + random.nextDouble() * 40;
        this.batteryStatus = 70 + random.nextDouble() * 30;
        this.engineTemperature = clamp(70 + speed * 0.25, 60, 120);
    }

    VehicleTelemetry next(long intervalMs) {
        speed = clamp(speed + (random.nextDouble() * 10 - 5), 0, 140);

        int rpm;
        if (speed < 1) {
            rpm = 700 + random.nextInt(201);
        } else {
            double jitter = random.nextDouble() * 100 - 50;
            rpm = (int) Math.round(clamp(800 + speed * 35 + jitter, 700, 6500));
        }

        double distanceKm = speed * (intervalMs / 3_600_000.0);
        latitude += distanceKm * Math.cos(headingRadians) / KM_PER_DEGREE_LATITUDE;
        double lonScale = KM_PER_DEGREE_LATITUDE * Math.cos(Math.toRadians(latitude));
        if (Math.abs(lonScale) < 1e-6) {
            lonScale = lonScale < 0 ? -1e-6 : 1e-6;
        }
        longitude += distanceKm * Math.sin(headingRadians) / lonScale;

        fuelLevel = Math.max(0, fuelLevel - (0.01 + random.nextDouble() * 0.02));

        double targetTemperature = 70 + speed * 0.25;
        engineTemperature = clamp(engineTemperature + (targetTemperature - engineTemperature) * 0.1, 60, 120);

        batteryStatus = Math.max(10, batteryStatus - 0.01);

        VehicleTelemetry telemetry = new VehicleTelemetry();
        telemetry.setVehicleId(vehicleId);
        telemetry.setLatitude(latitude);
        telemetry.setLongitude(longitude);
        telemetry.setSpeed(speed);
        telemetry.setRpm(rpm);
        telemetry.setFuelLevel(fuelLevel);
        telemetry.setEngineTemperature(engineTemperature);
        telemetry.setBatteryStatus(batteryStatus);
        telemetry.setEventTime(System.currentTimeMillis());
        return telemetry;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
