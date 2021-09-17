import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets

import org.apache.commons.collections.collection.TypedCollection
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.http.HttpHost
import org.apache.spark.SparkConf
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.JsonToStructs
import org.apache.spark.sql.catalyst.json.{JSONOptions, JacksonParser}
import org.apache.spark.sql.connector.read.streaming.SparkDataStream
import org.apache.spark.sql.{DataFrame, Dataset, Encoders, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Milliseconds, StreamingContext}
import org.apache.spark.streaming.kinesis.{KinesisInputDStream, SparkAWSCredentials}
import org.apache.spark.streaming.kinesis.KinesisInitialPositions.Latest
import org.elasticsearch.client.{RequestOptions, RestClient, RestHighLevelClient}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.Serialization.write
import schemas.Zbx
import utils.Decompress

object Main {
    def main(args: Array[String]) {
        // scp -i /c/Users/ariel/Desktop/key.pem target/scala-2.12/spark_example_kinesis_2.12-1.0.jar ec2-user@<ip>:/mnt1/project_ariel
        StreamingExamples.setStreamingLogLevels()

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("KinesisTests")

        val ss = SparkSession.builder().config(sparkConf).getOrCreate()
        val ssc = new StreamingContext(ss.sparkContext, Milliseconds(1000))

        val messages = getKinesisStreamingInstance(ssc, "kinesis-zabbix-item-float")

        //ss.sparkContext.hadoopRDD(jobConf, classOf[DynamoDBInputFormat], classOf[Text], classOf[DynamoDBItemWritable])

        messages
            .map(Decompress.Gzip(_))
            .flatMap(new String(_, StandardCharsets.UTF_8).split('\n')) // TODO: find a efficient way to convert to string
            .foreachRDD(rdd => {
                val session = SparkSession.builder.getOrCreate()
                import session.implicits._

                val ds: Dataset[String] = rdd.toDS()

                if (!ds.isEmpty) {
                    val result: Dataset[Zbx] = ds
                        .select(from_json($"value", Encoders.product[Zbx].schema) as "json")
                        .select("json.*")
                        .as[Zbx]

                    var host = result.head().host

                    result.show(10, false)
                    result.select(count("*")).show()

                    //result.foreach(row => {
                    //    implicit val formats: Formats = DefaultFormats
                    //
                    //    val client = new RestHighLevelClient(
                    //        RestClient.builder(
                    //            new HttpHost("", 443, "https")
                    //        )
                    //    )
                    //
                    //    client.index(new IndexRequest("zabbix-int").source(write(row), XContentType.JSON), RequestOptions.DEFAULT)
                    //    client.close()
                    //})

                    //df.as[Zbx].rdd
                } else {
                    //session.sparkContext.emptyRDD[Zbx]
                }
            })

        ssc.start()
        ssc.awaitTermination()
    }

    def getKinesisStreamingInstance(ssc: StreamingContext, stream: String) = {
        val credentials = SparkAWSCredentials.builder.basicCredentials(
            "",
            ""
        ).build()

        val region = "us-east-1"

        KinesisInputDStream.builder
            .streamingContext(ssc)
            .endpointUrl(s"https://kinesis.$region.amazonaws.com")
            .regionName(region)
            .streamName(stream)
            .initialPosition(new Latest())
            .checkpointAppName(s"$stream-checkpoint")
            .checkpointInterval(Milliseconds(1000))
            .storageLevel(StorageLevel.MEMORY_ONLY)
            .kinesisCredentials(credentials)
            .build()
    }
}