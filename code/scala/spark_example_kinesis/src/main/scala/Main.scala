import org.apache.spark.SparkConf

import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kinesis.KinesisInputDStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kinesis.KinesisInitialPositions.Latest
import org.apache.spark.streaming.kinesis.SparkAWSCredentials  

object Main {
    def main(args: Array[String]) {
        StreamingExamples.setStreamingLogLevels()

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("DirectKafkaWordCount")
        val ssc = new StreamingContext(sparkConf, Seconds(2))

        val cred = SparkAWSCredentials.builder.basicCredentials(
            "",
            ""
        ).build()

        val messages = KinesisInputDStream.builder
            .streamingContext(ssc)
            .endpointUrl("https://kinesis.us-east-1.amazonaws.com")
            .regionName("us-east-1")
            .streamName("NIFI")
            .initialPosition(new Latest())
            .checkpointAppName("kinesis-checkpoint")
            .checkpointInterval(Seconds(60 * 10))
            .storageLevel(StorageLevel.MEMORY_ONLY)
            .kinesisCredentials(cred)
            .build()

        val words = messages.flatMap(byteArray => new String(byteArray))
        val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
        wordCounts.print()

        ssc.start()
        ssc.awaitTermination()
    }
}