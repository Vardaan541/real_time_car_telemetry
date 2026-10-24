package com.carmetrics.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class TelemetrySparkJob {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "vehicle-telemetry-processed";
    private static final String KEYSPACE = "car_metrics";
    private static final String TABLE = "vehicle_telemetry";
    private static final String CHECKPOINT = "spark-checkpoints/vehicle-telemetry";

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession.builder()
                .appName("car-metrics-telemetry")
                .master("local[*]")
                .config("spark.cassandra.connection.host", "localhost")
                .config("spark.cassandra.connection.port", "9042")
                .config("spark.cassandra.connection.localDC", "datacenter1")
                .config("spark.sql.session.timeZone", "UTC")
                .getOrCreate();

        StructType schema = new StructType()
                .add("vehicleId", DataTypes.StringType)
                .add("latitude", DataTypes.DoubleType)
                .add("longitude", DataTypes.DoubleType)
                .add("speed", DataTypes.DoubleType)
                .add("rpm", DataTypes.IntegerType)
                .add("fuelLevel", DataTypes.DoubleType)
                .add("engineTemperature", DataTypes.DoubleType)
                .add("batteryStatus", DataTypes.DoubleType)
                .add("eventTime", DataTypes.LongType);

        Dataset<Row> parsed = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", BOOTSTRAP_SERVERS)
                .option("subscribe", TOPIC)
                .option("startingOffsets", "earliest")
                .load()
                .select(functions.from_json(functions.col("value").cast(DataTypes.StringType), schema).alias("data"))
                .select("data.*")
                .filter(functions.col("vehicleId").isNotNull().and(functions.col("eventTime").isNotNull()))
                .select(
                        functions.col("vehicleId").alias("vehicle_id"),
                        functions.timestamp_millis(functions.col("eventTime")).alias("event_time"),
                        functions.col("latitude").alias("latitude"),
                        functions.col("longitude").alias("longitude"),
                        functions.col("speed").alias("speed"),
                        functions.col("rpm").alias("rpm"),
                        functions.col("fuelLevel").alias("fuel_level"),
                        functions.col("engineTemperature").alias("engine_temperature"),
                        functions.col("batteryStatus").alias("battery_status"));

        parsed.writeStream()
                .foreachBatch(TelemetrySparkJob::writeBatch)
                .option("checkpointLocation", CHECKPOINT)
                .trigger(Trigger.ProcessingTime("5 seconds"))
                .start()
                .awaitTermination();
    }

    private static void writeBatch(Dataset<Row> batch, long batchId) {
        batch.write()
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", KEYSPACE)
                .option("table", TABLE)
                .mode("append")
                .save();
    }
}
