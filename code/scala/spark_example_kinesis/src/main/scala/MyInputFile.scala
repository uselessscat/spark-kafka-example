import org.apache.parquet.io.{InputFile, SeekableInputStream}

class MyInputFile(array: Array[Byte]) extends InputFile {
    override def getLength: Long = array.length

    override def newStream(): SeekableInputStream = new MySeekableInputStream(array)
}