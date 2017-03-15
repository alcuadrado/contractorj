package examples.unannotated;

public class FiniteStack {

  private final int[] data;
  private int size = 0;

  public FiniteStack() {
    this(5);
  }

  public FiniteStack(final int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }
    data = new int[capacity];
  }

  public void Push(int item) {
    data[size++] = item;
  }

  public int Pop() {
    return data[--size];
  }
}
