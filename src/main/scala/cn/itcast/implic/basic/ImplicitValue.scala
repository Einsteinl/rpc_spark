package cn.itcast.implic

//所有的隐式值和隐式方法必须放到object
object Context{
  implicit val aa="laozhao"
  implicit val i=1
}

/**
  * 隐式转换之隐式值
  */
object ImplicitValue {

  /**
    * 如果没有导入 与当前声明的隐式参数类型一样 的隐式参数，则用当前隐式参数
    */
  def sayHi()(implicit name:String ="laoduan"):Unit={
    println(s"hi~$name")
  }

  def main(args: Array[String]): Unit = {
    import Context._
    sayHi()("liusir")
  }

}
