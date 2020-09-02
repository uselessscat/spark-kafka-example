import org.apache.spark.sql.SparkSession

object Main {
    def main(args: Array[String]) {
        val session = SparkSession.builder.appName("SparkTest").getOrCreate()
        
        val textFile = session.read.textFile("hola.txt")

        println(s"$textFile")

        session.stop()
    }
}