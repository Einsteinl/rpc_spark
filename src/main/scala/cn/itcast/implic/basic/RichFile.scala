package cn.itcast.implic

import java.io.File

import scala.io.Source

/**
  *
  * 隐式的增强之隐式转换对象
  *
  * 调用File对象的read方法后，首先会从File类中寻找read方法 ，如果没有，就会在当前隐式库里
  * 寻找有没有增强File对象的方法，如果有，查看增强对象下是否有read方法，有则调用
  */

class RichFile(val f:File) {

  def read()=Source.fromFile(f).mkString
}

object RichFile{
  def main(args: Array[String]): Unit = {
    val f=new File("c://words.txt")
    //装饰，显示的增强
   // val contents=new RichFile(f).read()
    import cn.itcast.implic.basic.MyPredef._
    val contents=f.read()
    println(contents)
  }
}


