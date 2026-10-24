CREATE KEYSPACE IF NOT EXISTS car_metrics
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS car_metrics.vehicle_telemetry (
    vehicle_id text,
    event_time timestamp,
    latitude double,
    longitude double,
    speed double,
    rpm int,
    fuel_level double,
    engine_temperature double,
    battery_status double,
    PRIMARY KEY (vehicle_id, event_time)
) WITH CLUSTERING ORDER BY (event_time DESC);
