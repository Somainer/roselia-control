import utils.CommandParser._

object TestCommands {
  def testCommand: Unit = {

    "bind" match {
      case GetID() => println("BindCommand!")
    }
    "fuck|you" match {
      case GetID() => println("BindCommand!")
      case Commands(h, t) => println(h + " + " + t)
    }
    "div|233" match {
      case Commands("div", NeedInt(x)) => println("Divide by " + x)
      case Commands(h, NeedInt(t)) => println(h + "int:" + t)
      case Commands(h, t) => println("Not an integer.")
    }
    "it|is | an | test" match {
      case Commands(a, b, c) => println("tri")
      case Commands(a, b, c, d) => println("4:" + a + b + c + d)
    }
  }

  def main(args: Array[String]): Unit = {
    testCommand
  }
}