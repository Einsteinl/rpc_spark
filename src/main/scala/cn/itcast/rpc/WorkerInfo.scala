package cn.itcast.rpc

class WorkerInfo(val workerId:String,val memory: Int,val cores: Int) {

  //上一次心跳
  var lastHeartbeatTime: Long = _
}
