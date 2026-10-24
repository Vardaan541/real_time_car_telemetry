# Connected Car Analytics Platform

Local pipeline for simulated vehicle telemetry. Prerequisites are installed on the machine. This project does not start them.

Prerequisites: JDK 17, Maven, Apache Kafka in KRaft mode on `localhost:9092`, and Cassandra on `localhost:9042` with datacenter name `datacenter1`.

Run Maven from the repository root. `.mvn/jvm.config` opens the Java 17 modules Spark local mode needs. The `exec:java` goal runs in that Maven process.

1. Start Cassandra. Start Kafka and confirm the broker is listening on port 9092.
2. Apply the schema:

```
cqlsh -f app/src/main/resources/schema.cql
```

3. From the repository root, start the Spring Boot application:

```
mvn -pl app -am spring-boot:run
```

4. In a second terminal, from the repository root, start the Spark job:

```
mvn -pl spark-processor -am exec:java -Dexec.mainClass=com.carmetrics.spark.TelemetrySparkJob
```

5. Wait for a few generator ticks and one Spark micro-batch.

Checks:

- A Kafka console consumer on `vehicle-telemetry` shows JSON with the telemetry fields and `eventTime`.
- A Kafka console consumer on `vehicle-telemetry-processed` shows the same records after validation.
- In `cqlsh`:

```
SELECT * FROM car_metrics.vehicle_telemetry WHERE vehicle_id = 'vehicle-001' LIMIT 5;
```

- Open `http://localhost:8080/api/telemetry/vehicle-001/latest` in a browser to see the labeled reading and the pipeline steps. The page refreshes every 5 seconds.
- `curl http://localhost:8080/api/telemetry/vehicle-001?limit=5`
- `curl http://localhost:8080/api/telemetry/vehicle-001/latest`

Stop Spark and Spring Boot with Ctrl+C. Leave Cassandra and Kafka running until you stop them locally.
