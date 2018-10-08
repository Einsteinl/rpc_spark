package cn.itcast.implic.genericity

/**
  * 视图界定 ViewBound
  * 必须导入隐式函数，将T转换为Ordered[T]
  * 其实就是比较两个对象，java中一般是让对象自己实现comparable接口，之后重新compareTo方法
  * 之后，当两个对象做比较是，直接A.compareTo(B)
  *
  * 而scala提供隐式转换这种机制，亮点是，不用把比较规则写死到对象中，可以自由切换。具体实现如下：
  * 先在比较类MissLeft中将T声明为Ordered[T],此处的Ordered[T]可以理解为，T对象实现了Ordered接口
  * 之后，直接用Ordered[T]进行比较，再将比较结果返回
  * 而这种方式的关键点是：将T转换为Ordered[T]，所有需要利用隐式方法或者函数，将其转换 import cn.itcast.implic.genericity.MyPreDef._
  */
//class MissLeft[T <% Ordered[T]] {
//
//  def choose(first:T,second:T):T={
//    if (first>second) first else second
//  }
//}

/**
  * 上下文界定  ContextBound
  *
  * 与上边所说类似，只不过这个的关键之处是
  * （通过隐式的方式）要传一个Ordering[T],个人理解为类似于java中的比较器
  */
class MissLeft[T : Ordering]{
  def choose(first: T,second: T): T={
    val ord=implicitly[Ordering[T]]
    if(ord.gt(first,second)) first else second
  }
}

object MissLeft{
  def main(args: Array[String]): Unit = {
    import cn.itcast.implic.genericity.MyPreDef._
    val m1=new MissLeft[Girl]
    val g1=new Girl("hatanao",98,28)
    val g2=new Girl("sora",95,33)
    val g=m1.choose(g1,g2)
    println(g.name)
  }
}
