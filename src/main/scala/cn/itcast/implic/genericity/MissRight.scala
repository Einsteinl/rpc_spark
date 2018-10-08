package cn.itcast.implic.genericity

class MissRight[T]{

  //柯理化，隐式的引入函数
  def choose(first:T,second:T)(implicit ord: T => Ordered[T]): T={
    if(first >second) first else second
  }
  //隐式引入实例
  def select(first:T,second:T)(implicit ord:Ordering[T]):T={
    if(ord.gt(first,second)) first else second
  }
  def random(first: T,second: T)(implicit ord:Ordering[T]):T={
    //将Orering转换成Ordered的隐式转换引入进来
    import Ordered.orderingToOrdered
    if(first>second) first else second

  }
}

object MissRight {
  def main(args: Array[String]): Unit = {
    val mr=new MissRight[Girl]
    val g1=new Girl("hatanao",98,28)
    val g2=new Girl("sora",95,33)

    import MyPreDef.girlOrdering
    val g=mr.random(g1,g2)
    println(g.name)
  }

}
