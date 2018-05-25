package services

import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import utils.CommandParser.Commands
import utils.NestedMessages._


class ClientActor(responseRef: ActorRef, serviceRef: ActorRef) extends Actor {
  var clientId: Int = _
  var server: ActorRef = _

  override def receive: Receive = {
    case kb: KeyBoardCommand => response(kb)
    case kbs: ShortCutCommand => response(kbs)
    case ms: MouseCommand => response(ms)
    case BindSuccess(kid: Int) =>
      clientId = kid
      response(BindSuccess(kid))
    case BindSuccess(ref: ActorRef) =>
      server = ref
      response(Commands("link", "success"))
    case Ping => responseRef ! Pong
    case "ping" => response(Pong)
    case st@ShutDown(reason) =>
      response(st)
      self ! PoisonPill
  }

  def response[T](t: T): Unit = {
    responseRef ! t.toString
  }

  override def preStart(): Unit = serviceRef ! RequireNew

  override def postStop(): Unit = {
    serviceRef ! Disconnected(clientId)
    response(ShutDown("bye"))
    if(server != null) server ! ShutDown("bye")
  }

}

object ClientActor {
  def props(rr: ActorRef, sr: ActorRef) = Props(new ClientActor(rr, sr))
}
