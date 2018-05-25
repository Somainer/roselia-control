package services

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import akka.event.LoggingReceive
import utils.CommandParser._
import utils.NestedMessages._

case class UserStateActor(responseRef: ActorRef, userId: Int, serviceRef: ActorRef) extends Actor {
  var boundClient: ActorRef = _

  def receive = LoggingReceive {
    case BindFailed =>
      responseRef ! Commands("bind", "failed")
      response(Commands("bye", "Invalid UserID"))
      stopService()
    case BindSuccess(ref: ActorRef) =>
      boundClient = ref
      responseRef ! Commands("bind", "success", ref.path.toStringWithoutAddress)
      boundClient ! BindSuccess(self)
    case Commands("shortcut", keyCodes) =>
      boundClient ! ShortCutCommand(keyCodes)
      response('ok)
    case Commands("keyboard", NeedInt(keyCode)) =>
      boundClient ! KeyBoardCommand(keyCode)
      response('ok)
    case Commands("mouse", btn) =>
      boundClient ! MouseCommand(btn)
      response('ok)
    case Ping => sender() ! Pong
    case "ping" => response(Pong)
    case ShutDown(reason) =>
      response(Commands("bye", reason))
      stopService()
  }

  def stopService(): Unit = {
    //responseRef ! PoisonPill
    self ! PoisonPill

  }

  def response[T](t: T): Unit = {
    responseRef ! t.toString
  }

  override def preStart(): Unit = serviceRef ! BindServer(userId)

  override def postStop(): Unit = {
    if (boundClient != null) boundClient ! ShutDown("bye")
  }

}

object UserStateActor {
  def props(rr: ActorRef, uid: Int, sr: ActorRef) = Props(new UserStateActor(rr, uid, sr))
}