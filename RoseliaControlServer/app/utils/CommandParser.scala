package utils

import scala.util.matching.Regex
import scala.util.Try

object CommandParser {
  final val delim = '|'
  implicit def toInteger(s: String) = s.toInt
  implicit def convert[T](s: String)(implicit f: String => T) = f(s)
  object GetID {
    def apply = "bind"

    def unapply(arg: String): Boolean = arg == "bind"

    def unapply(arg: Symbol): Boolean = arg == 'bind
  }
  
  object Commands {
    def apply[A](string: A*): String = string.map(_.toString).mkString(delim.toString)

    def unapplySeq(arg: String): Option[Seq[String]] = Some(arg split delim map {_.trim})
  }

  object CaseInsensitiveCommands {
    def apply[A](string: A*): String = string.map(_.toString).mkString(delim.toString).toLowerCase

    def unapplySeq(arg: String): Option[Seq[String]] = Some(arg.toLowerCase split delim map {_.trim})
  }

  object AtomCommand {
    def apply(c: String) = Symbol(c)

    def apply(c: Symbol) = c

    def unapply(arg: String): Option[String] = Some(arg)

    def unapply(arg: Symbol): Option[String] = Some(arg.name)
  }
  
  object NeedInt {
    def apply(i: Int) = i.toString

    def unapply(arg: String): Option[Int] = Try{ convert[Int](arg) }.toOption

    def unapply(arg: Int): Option[Int] = Some(arg)
  }


}
