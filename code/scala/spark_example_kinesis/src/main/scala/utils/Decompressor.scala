package utils

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

object Decompress {
    def Gzip(bytes: Array[Byte]): Array[Byte] = {
        val gzIn = new GzipCompressorInputStream(new ByteArrayInputStream(bytes))

        // actually our decompression grows the data by 8x for batches of ~5000 json rows
        val out = new ByteArrayOutputStream(bytes.length * 8)
        val buffer = new Array[Byte](bytes.length / 10)

        var n = -1
        while (-1 != {
            n = gzIn.read(buffer)
            n
        }) out.write(buffer, 0, n)

        // TODO: IMPROVE, this clones the array
        val uncompressed: Array[Byte] = out.toByteArray()

        uncompressed
    }
}
