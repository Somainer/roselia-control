package controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import services.{ClientActor, UserManager, UserStateActor}


class WebSocketController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {
  val mangerActor: ActorRef = system.actorOf(UserManager.props)

  def socketClient = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(ClientActor.props(_, mangerActor))
  }

  def socketServer(clientId: Int) = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(UserStateActor.props(_, clientId, mangerActor))
  }

}
