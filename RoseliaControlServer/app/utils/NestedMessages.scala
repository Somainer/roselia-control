package utils

import akka.actor.ActorRef

object NestedMessages {

  case class SetClient(clientId: Int, service: ActorRef)

  case object RequireNew {
    override def toString: String = "new"
  }

  case class KeyBoardCommand(code: Int) {
    override def toString: String = s"keyboard|$code"
  }

  case class MouseCommand(button: String) {
    override def toString: String = s"mouse|$button"
  }

  case class ShortCutCommand(codes: String) {
    override def toString: String = s"shortcut|$codes"
  }

  case class BoundSuccess(clientId: Int) {
    override def toString: String = s"bindsuccess|$clientId"
  }

  case class BindServer(clientId: Int) {
    override def toString: String = s"bind|$clientId"
  }

  abstract class BindResult[+T] {
    override def toString: String = "bind|"
  }

  case class BindSuccess[+T](ref: T) extends BindResult[T] {
    override def toString: String = super.toString + s"success|$ref"
  }

  case object BindFailed extends BindResult[Nothing] {
    override def toString: String = super.toString + "failed"
  }

  case class ShutDown(reason: String) {
    override def toString: String = s"shutdown|$reason"
  }

  case class Disconnected(clientId: Int) {
    override def toString: String = s"disconnect|$clientId"
  }

  case object Ping {
    override def toString: String = "ping"
  }

  case object Pong {
    override def toString: String = "pong"
  }

}
