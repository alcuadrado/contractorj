package examples;

public class Switch {

  private boolean isOn;

  public static boolean Switch_pre() {
    return true;
  }

  public Switch() {
    isOn = false;
  }

  public static boolean Switch_pre(boolean isOn) {
    return true;
  }

  public Switch(boolean isOn) {
    this.isOn = isOn;
  }

  public boolean inv() {
    return true;
  }

  public void on() {
    isOn = true;
  }

  public boolean on_pre() {
    return !isOn;
  }

  public void off() {
    isOn = false;
  }

  public boolean off_pre() {
    return isOn;
  }
}
