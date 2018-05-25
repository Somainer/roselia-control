package utils
import akka.actor.ActorRef

case class ClientSideActor(ref: ActorRef) {
  def ![T](s: T) = ref ! s.toString

}
