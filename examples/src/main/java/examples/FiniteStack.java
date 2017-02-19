package examples;

public class FiniteStack {

  private int max;
  private int next;

  public static boolean FiniteStack_pre() {
    return true;
  }

  public FiniteStack() {
    max = 5;
    next = -1;
  }

  public static boolean FiniteStack_pre(int size) {
    return size > 2;
  }

  public FiniteStack(int size) {
    max = size;
    next = -1;
  }

  public void Pop() {
    next = next - 1;
  }

  public void Push() {
    next = next + 1;
  }

  public boolean inv() {
    return max > 2 && next >= -1 && max >= next;
  }

  public boolean Pop_pre() {
    return next > -1;
  }

  public boolean Push_pre() {
    return next < max;
  }
}
