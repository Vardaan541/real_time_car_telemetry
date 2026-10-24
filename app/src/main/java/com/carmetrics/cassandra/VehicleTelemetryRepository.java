package com.carmetrics.cassandra;

import java.util.List;

import org.springframework.data.cassandra.core.mapping.MapId;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

public interface VehicleTelemetryRepository extends CassandraRepository<VehicleTelemetryEntity, MapId> {

    @Query("SELECT * FROM vehicle_telemetry WHERE vehicle_id = ?0 LIMIT ?1")
    List<VehicleTelemetryEntity> findRecent(String vehicleId, int limit);
}
