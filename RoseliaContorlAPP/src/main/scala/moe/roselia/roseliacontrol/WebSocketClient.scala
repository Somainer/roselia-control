package moe.roselia.roseliacontrol

import com.github.andyglow.websocket._

abstract class WebSocketClient(uri: String) {
  def onReceive: PartialFunction[String, Unit]

  def cli: WebsocketClient[String] = WebsocketClient(uri)(onReceive)

  def connection = cli.open()

  def stop = cli.shutdownSync()
}
