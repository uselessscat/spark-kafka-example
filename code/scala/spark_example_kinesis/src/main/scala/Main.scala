import java.io.EOFException
import java.nio.ByteBuffer

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kinesis.{KinesisInputDStream, SparkAWSCredentials}
import org.apache.spark.streaming.kinesis.KinesisInitialPositions.Latest
import org.apache.avro.generic.GenericRecord
import org.apache.parquet.avro.AvroParquetReader
import org.apache.parquet.io.{InputFile, SeekableInputStream}
import org.xerial.snappy.SnappyInputStream


class MySeekableInputStream(byteArray: Array[Byte]) extends SeekableInputStream {
    private var position: Int = 0

    override def getPos: Long = position

    override def seek(newPos: Long): Unit = {
        position = newPos.toInt
    }

    override def readFully(bytes: Array[Byte]): Unit = {
        readFully(bytes, 0, bytes.length)
    }

    override def readFully(bytes: Array[Byte], start: Int, len: Int): Unit = {
        val bytesLeft = byteArray.length - position

        if (bytesLeft >= len) {
            // if the array provided is pos + len > array.length this might lose bytes
            byteArray.slice(position, position + len).copyToArray(bytes, start, len)
            position += len
        } else {
            byteArray.slice(position, bytesLeft.toInt).copyToArray(bytes, start, bytesLeft)
            position += bytesLeft

            throw new EOFException()
        }
    }

    override def read(buf: ByteBuffer): Int = {
        val bufferRemaining = buf.remaining()
        val bytesAvailable = byteArray.length - position
        val bytesToWrite = math.min(bufferRemaining, bytesAvailable)

        buf.put(byteArray.slice(position, position + bytesToWrite))

        position += bytesToWrite
        bytesToWrite
    }

    override def readFully(buf: ByteBuffer): Unit = {
        val remaining = buf.remaining()
        val bytesRead = read(buf)

        if (bytesRead < remaining) {
            throw new EOFException()
        }
    }

    override def read(): Int = {
        if (position < byteArray.length) {
            val returnInt = 0xff & byteArray(position).toInt
            position += 1
            returnInt
        } else {
            -1
        }
    }
}

class MyInputFile(array: Array[Byte]) extends InputFile {
    override def getLength: Long = array.length

    override def newStream(): SeekableInputStream = new MySeekableInputStream(array)
}


object Main {
    def myfun(bytes: Array[Byte]): GenericRecord = {
        val inputFile = new MyInputFile(bytes)
        // val reader = ParquetFileReader.open(inputFile)

        val parquetReader = AvroParquetReader.builder[GenericRecord](inputFile).build

        parquetReader.read
    }

    def main(args: Array[String]) {
        // val session = SparkSession.builder().getOrCreate()
        StreamingExamples.setStreamingLogLevels()

        val sparkConf = new SparkConf().setMaster("local[*]").setAppName("KinesisTests")
        val sc = new SparkContext(sparkConf)
        val ssc = new StreamingContext(sc, Seconds(2))

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
            .checkpointAppName("kinesis-NIFI-checkpoint")
            .checkpointInterval(Seconds(60 * 10))
            .storageLevel(StorageLevel.MEMORY_ONLY)
            .kinesisCredentials(cred)
            .build()

        messages.map(bytes => myfun(bytes).toString).print()

        ssc.start()
        ssc.awaitTermination()
    }
}