package examples;

public class Switch {

    private boolean isOn = false;

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
