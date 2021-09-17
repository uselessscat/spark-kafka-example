import java.io.EOFException
import java.nio.ByteBuffer

import org.apache.parquet.io.SeekableInputStream

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