package cn.itcast.implic.basic

import java.io.File

import cn.itcast.implic.RichFile

object MyPredef {

  implicit def fileToRichFile(f:File)=new RichFile(f)
}
