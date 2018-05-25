package services

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import utils.NestedMessages._

import scala.annotation.tailrec
import scala.collection.mutable

class UserManager extends Actor with ActorLogging {
  val userMap = mutable.Map.empty[Int, ActorRef]
  val rng = new util.Random

  @tailrec private def nextRandom: Int = {
    val res = rng.nextInt(90000) + 100000
    if (userMap.contains(res)) nextRandom else res
  }

  def receive: Receive = {
    case SetClient(userId, actor) => userMap.put(userId, actor)
      sender() ! 'ok
    case RequireNew =>
      val res = nextRandom
      val realSender = sender()
      userMap.put(res, realSender)
      realSender ! BindSuccess(res)
    case BindServer(idx) => sender() ! (userMap remove idx map BindSuccess[ActorRef] getOrElse BindFailed)
    case Disconnected(idx) =>
      userMap.remove(idx)
      sender() ! 'ok
    case _ => new ClassNotFoundException("Command Not Found")
  }
}

object UserManager {
  def props = Props(new UserManager)
}