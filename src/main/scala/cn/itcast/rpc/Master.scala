package cn.itcast.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable
//通过主构造器将ip和端口传进来
class Master(val host:String,val port: Int) extends Actor{

  println("constructor invoked")

  //workerId -> WorkerInfo
  val idToWorker=new mutable.HashMap[String,WorkerInfo]()
  //WorkerInfo
  val workers=new mutable.HashSet[WorkerInfo]()

  //超时检查
  val CHECK_INTERVAL: Int=15000

  //构造方法之后，获取消息方法之前
  override def preStart(): Unit = {
    println("preStart invoked")
    //导入隐式转换
    import context.dispatcher //使用timer太low了，可以使用akka的，使用定时器，要导入这个包
    context.system.scheduler.schedule(0 millis,CHECK_INTERVAL millis,self,CheckTimeOutWorker)
  }

  //用于接收消息
  override def receive: Receive = {
    case "connect" =>{
      println("one client connect")
      sender ! "reply"
    }
      //保存worker注册信息
    case RegisterWorker(workId,memory,cores) =>{
      //判断一下，是不是已经注册过
      if(!idToWorker.contains(workId)){
        //把Worker的信息封装起来保存到内存中
        val workerInfo=new WorkerInfo(workId,memory,cores)
        idToWorker(workId)=workerInfo
        workers +=workerInfo
        sender ! RegisteredWorker(s"akka.tcp://MasterSystem@$host:$port/user/Master")//通知worker注册完毕
      }
    }
      //心跳保持
    case Heartbeat(workerid) =>{
      if(idToWorker.contains(workerid)){
        val workerInfo=idToWorker(workerid)
        //报活
        val currentTime=System.currentTimeMillis()
        workerInfo.lastHeartbeatTime=currentTime
      }
    }
      //检测心跳
    case CheckTimeOutWorker =>{
      val currentTime=System.currentTimeMillis()
      //得到超时worker集合
      val toRemove=workers.filter(x => currentTime-x.lastHeartbeatTime > CHECK_INTERVAL)
      //将超时的worker移除
      for(w <- toRemove){
        workers -= w
        idToWorker -= w.workerId
      }
      println(workers.size)
    }
    case "hello" =>{
      println("hello")
    }
  }
}

object Master{
  def main(args: Array[String]): Unit = {
    val host=args(0)
    val port=args(1).toInt
    //准备配置
    val configStr=
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config =ConfigFactory.parseString(configStr)

    //ActorSystem（老大），辅助创建和监控下面的Actor，他是单例的
    val actorSystem=ActorSystem("MasterSystem",config)
    //创建Actor，起个名字
    val master=actorSystem.actorOf(Props(new Master(host,port)),"Master")//Master主构造器会执行

    master ! "hello" //发送信息

    actorSystem.awaitTermination()  //让进程等待着，先别结束
  }
}
