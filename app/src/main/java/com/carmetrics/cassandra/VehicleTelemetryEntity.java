package com.carmetrics.cassandra;

import java.time.Instant;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("vehicle_telemetry")
public class VehicleTelemetryEntity {

    @PrimaryKeyColumn(name = "vehicle_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String vehicleId;

    @PrimaryKeyColumn(name = "event_time", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant eventTime;

    @Column("latitude")
    private double latitude;

    @Column("longitude")
    private double longitude;

    @Column("speed")
    private double speed;

    @Column("rpm")
    private int rpm;

    @Column("fuel_level")
    private double fuelLevel;

    @Column("engine_temperature")
    private double engineTemperature;

    @Column("battery_status")
    private double batteryStatus;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getRpm() {
        return rpm;
    }

    public void setRpm(int rpm) {
        this.rpm = rpm;
    }

    public double getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public double getEngineTemperature() {
        return engineTemperature;
    }

    public void setEngineTemperature(double engineTemperature) {
        this.engineTemperature = engineTemperature;
    }

    public double getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(double batteryStatus) {
        this.batteryStatus = batteryStatus;
    }
}
