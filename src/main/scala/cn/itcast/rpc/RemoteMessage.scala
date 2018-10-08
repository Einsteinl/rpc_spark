package cn.itcast.rpc

//实现序列化接口 使RemoteMessage可以进行网络传输
trait RemoteMessage extends Serializable

//Worker -> Master
case class RegisterWorker(id: String,memory: Int,cores: Int) extends RemoteMessage

case class Heartbeat(workerid:String)


//Master -> Worker
case class RegisteredWorker(masterUrl:String)


//Worker -> self
case object SendHeartbeat


//Master -> self
case object CheckTimeOutWorker