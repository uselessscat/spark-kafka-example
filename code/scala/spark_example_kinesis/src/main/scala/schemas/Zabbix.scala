package schemas

case class ZbxHost(host: String, name: String)

case class Zbx(host: ZbxHost = null,
               groups: Array[String],
               applications: Array[String],
               itemid: Int,
               name: String,
               clock: Int,
               ns: Int,
               value: Float,
               `type`: Int)
