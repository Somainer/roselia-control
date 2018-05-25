package services

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import javax.inject.Inject
import play.api.Logger
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

class WebSocketActor(out: ActorRef) extends Actor {
  def receive: Receive = {
    case msg => {
      Logger.info(s"Received $msg from ${sender()}")
      out ! s"Received $msg"
    }
  }
}

object WebSocketActor {
  def props(out: ActorRef) = Props(new WebSocketActor(out))
}