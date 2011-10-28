package ptrl.ui;

import java.awt.event.KeyEvent;

/**
 * Author: Yuri Buyanov
 * Date: 10/27/11 22:33
 */
public class Controls {
  public static boolean isSkip(KeyEvent ke) {
    return 'h' == ke.getKeyChar();
  }

  public static boolean isUp(KeyEvent ke) {
    return isUpStrict(ke) || isUpRight(ke) || isUpLeft(ke);
  }

  public static boolean isDown(KeyEvent ke) {
    return isDownStrict(ke) || isDownRight(ke) || isDownLeft(ke);
  }

  public static boolean isLeft(KeyEvent ke) {
    return isLeftStrict(ke) || isUpLeft(ke) || isDownLeft(ke);
  }

  public static boolean isRight(KeyEvent ke) {
    return isRightStrict(ke) || isUpRight(ke) || isDownRight(ke);
  }

  public static boolean isUpStrict(KeyEvent ke) {
    return 'y' == ke.getKeyChar() || KeyEvent.VK_UP == ke.getKeyCode();
  }

  public static boolean isDownStrict(KeyEvent ke) {
    return 'n' == ke.getKeyChar() || KeyEvent.VK_DOWN == ke.getKeyCode();
  }

  public static boolean isLeftStrict(KeyEvent ke) {
    return 'g' == ke.getKeyChar() || KeyEvent.VK_LEFT == ke.getKeyCode();
  }

  public static boolean isRightStrict(KeyEvent ke) {
    return 'j' == ke.getKeyChar() || KeyEvent.VK_RIGHT == ke.getKeyCode();
  }

  public static boolean isUpRight(KeyEvent ke) {
    return 'u' == ke.getKeyChar();
  }

  public static boolean isUpLeft(KeyEvent ke) {
    return 't' == ke.getKeyChar();
  }

  public static boolean isDownRight(KeyEvent ke) {
    return 'm' == ke.getKeyChar();
  }

  public static boolean isDownLeft(KeyEvent ke) {
    return 'b' == ke.getKeyChar();
  }


}
