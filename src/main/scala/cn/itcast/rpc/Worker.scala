package cn.itcast.rpc

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

//利用主构造方法将master的ip和端口传进来，便于连接
class Worker(val masterHost:String,val masterPort: Int,val memory: Int,val cores: Int) extends Actor{

  val workerId=UUID.randomUUID().toString

  var master : ActorSelection = _
  //心跳间隔
  val HEART_INTERVAL=10000

  //建立连接
  override def preStart(): Unit = {
    //在master启动时会打印下面那个协议，可以先用这个做一个标志，连接那个master
    //继承actor后会有一个context，可以通过它来连接
    master =context.actorSelection(s"akka.tcp://MasterSystem@$masterHost:$masterPort/user/Master")
    master ! RegisterWorker(workerId,memory,cores)
  }

  override def receive: Receive = {

    case RegisteredWorker(masterUrl) =>{
      println(masterUrl)
      //启动定时器发送心跳
      import context.dispatcher
      //多长时间后执行 单位，多长时间执行一次 单位，消息的接收者（直接给master发不好，先给自己发送消息，以后可以做判断，什么情况下再发送消息）, 信息
      context.system.scheduler.schedule(0 millis,HEART_INTERVAL millis,self,SendHeartbeat)
    }
    case SendHeartbeat =>{
      println("send heartbeat to master")
      master ! Heartbeat(workerId)
    }
  }
}

object Worker{
  def main(args: Array[String]): Unit = {

    //通过actorSystem创建actor
    val host=args(0)
    val port=args(1)
    val masterHost=args(2)
    val masterPort=args(3).toInt

    val memory=args(4).toInt
    val cores=args(5).toInt

    //准备配置
    val configStr=
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
       """.stripMargin
    val config=ConfigFactory.parseString(configStr)
    //ActorSystem老大，辅助创建和 监控下面的Actor，它是单例的
    val actorSystem=ActorSystem("WorkerSystem",config)
    actorSystem.actorOf(Props(new Worker(masterHost,masterPort,memory,cores)),"Worker")

    actorSystem.awaitTermination()

  }
}
