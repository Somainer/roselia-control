package moe.roselia.roseliacontrol

import moe.roselia.roseliacontrol.CommandParser._

import scala.swing.Swing._
import scala.swing._
import scala.swing.event.ButtonClicked

//noinspection ForwardReference
object RoseliaControl extends SimpleSwingApplication {
  val uri = "ws://localhost:9000/bind"
  //val uri = "wss://rc.roselia.xyz/bind"
  val keyboard = KeyboardSimulator.KeyboardSimulator()
  val mainFont = new Font("Source Sans Pro", 1, 25)

  def top = new MainFrame {
    title = "Roselia Control Client"

//    iconImage = Icon(getClass.getResource("/img/logo.png")).getImage

    val label = new Label("Waiting...") {
      font = mainFont
    }
    val mainLabel = new Label {
      text = "Roselia Control"

      foreground = new Color(0x5869b1)
      font = mainFont
    }
    val button = new Button("Stop/Restart") {
      enabled = false
      reactions += {
        case ButtonClicked(_) =>
          ws.stop
          this.enabled = false
          connection = ws.connection
      }
    }
    contents = new FlowPanel {
      contents += mainLabel
      contents += label
      contents += button
      border = Swing.EmptyBorder(50, 50, 50, 50)
    }
    setLocationRelativeTo(this)


    val ws: WebSocketClient = new WebSocketClient(uri) {
      override def onReceive = {
        case Commands("bind", "success", code) => label.text = code
        case Commands("link", "success") =>
          label.text = "Controller Linked!"
          button.enabled = true
        case Commands("keyboard", NeedInt(keyCode)) => keyboard.sendKey(keyCode)
        case Commands("keyboard", keyChar) => keyboard.sendKey(keyChar)
        case Commands("shortcut", keyChar) =>
          println("Pressing:" + keyChar)
          keyboard sendShortCut keyChar
        case Commands("mouse", "left") => keyboard.leftClick
        case Commands("mouse", "right") => keyboard.rightClick
        case Commands("shutdown", _) =>
          label.text = "Disconnected"
          button.enabled = true
      }
    }
    var connection = ws.connection
    listenTo(button)
    println("APP ran...")
  }
}
