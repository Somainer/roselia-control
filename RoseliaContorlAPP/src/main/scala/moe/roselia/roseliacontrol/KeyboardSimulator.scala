package moe.roselia.roseliacontrol

import java.awt._
import java.awt.event.{InputEvent, KeyEvent}

import scala.util.Try

object KeyboardSimulator {

  trait BaseKeyboardSimulator {
    val robot = new Robot()
    robot.setAutoDelay(100)

    def sendKey(code: Int) {
      robot.keyPress(code)
      robot.keyRelease(code)
    }

    def sendKey(code: String): Unit = translateCode(code) foreach sendKey

    def sendSeq(cs: Seq[Int]) = cs foreach sendKey

    def sendShortCutCodes(cs: Seq[Int]) = {
      cs foreach robot.keyPress
      cs.reverse foreach robot.keyRelease
    }

    def sendShortCuts(cs: Seq[String]) = sendShortCutCodes(cs map (_.toLowerCase) flatMap translateCode)

    def sendShortCut(s: String) = sendShortCuts(s split '+')

    val specialKeys: Map[String, Int] = Map(
      "enter" -> Keys.Enter,
      "down" -> Keys.Down,
      "up" -> Keys.Up,
      "left" -> Keys.Left,
      "right" -> Keys.Right,
      "pageup" -> Keys.PageUp,
      "pagedown" -> Keys.PageDown,
      "control" -> Keys.Control,
      "alt" -> Keys.Alt,
      "backspace" -> Keys.BackSpace,
      "tab" -> Keys.Tab,
      "cancel" -> Keys.Cancel,
      "cleat" -> Keys.Cleat,
      "shift" -> Keys.Shift,
      "pause" -> Keys.Pause,
      "capslock" -> Keys.CapsLock,
      "escape" -> Keys.Escape,
      "space" -> Keys.Space,
      "end" -> Keys.End,
      "home" -> Keys.Home,
      "delete" -> Keys.Delete
    ) ++
      Map((0 to 9) map (i => i.toString -> (i + '0'.toInt)): _*) ++
      Map(('A' to 'Z').map(l => l.toString.toLowerCase -> l.toInt): _*) ++
      Map((1 until 12) map (i => s"f$i" -> (111 + i)): _*)


    def translateCode(s: String): Option[Int] = specialKeys.get(s)
      .orElse(if(s.length == 1) Some(s.charAt(0).toInt) else None)
      .orElse(Try{s.toInt}.toOption)

    def mouseClick(code: Int): Unit = {
      robot.mousePress(code)
      robot.mouseRelease(code)
    }

    def mouseMove = robot.mouseMove _

    def scrollWheel = robot.mouseWheel _

    def leftClick = mouseClick(InputEvent.BUTTON1_MASK)

    def rightClick = mouseClick(InputEvent.BUTTON3_MASK)

    def sendEnter = sendKey(KeyEvent.VK_ENTER)

    def capsLock = sendKey(KeyEvent.VK_CAPS_LOCK)

    def sendUp = sendKey(Keys.Up)

    def sendDown = sendKey(Keys.Down)

    def sendLeft = sendKey(Keys.Left)

    def sendRight = sendKey(Keys.Right)
  }

  object Keys {
    val Enter = KeyEvent.VK_ENTER
    val Down = KeyEvent.VK_DOWN
    val Up = KeyEvent.VK_UP
    val Left = KeyEvent.VK_LEFT
    val Right = KeyEvent.VK_RIGHT
    val PageUp = KeyEvent.VK_PAGE_UP
    val PageDown = KeyEvent.VK_PAGE_DOWN
    val Control = KeyEvent.VK_CONTROL
    val Alt = KeyEvent.VK_ALT
    val BackSpace = KeyEvent.VK_BACK_SPACE
    val Tab = KeyEvent.VK_TAB
    val Cancel = KeyEvent.VK_CANCEL
    val Cleat = KeyEvent.VK_CLEAR
    val Shift = KeyEvent.VK_SHIFT
    val Pause = KeyEvent.VK_PAUSE
    val CapsLock = KeyEvent.VK_CAPS_LOCK
    val Escape = KeyEvent.VK_ESCAPE
    val Space = KeyEvent.VK_SPACE
    val End = KeyEvent.VK_END
    val Home = KeyEvent.VK_HOME
    val Delete = KeyEvent.VK_DELETE
  }

  case class KeyboardSimulator() extends BaseKeyboardSimulator

  case class PowerPointKeys() extends BaseKeyboardSimulator {
    def startPlay = sendKey(KeyEvent.VK_F5)

    def exitPlat = sendKey(KeyEvent.VK_ESCAPE)

    def nextPage = sendKey(KeyEvent.VK_N)

    def prevPage = sendKey(KeyEvent.VK_P)

    def whiteScreen = sendKey(KeyEvent.VK_W)

    def blackScreen = sendKey(KeyEvent.VK_B)
  }

}


