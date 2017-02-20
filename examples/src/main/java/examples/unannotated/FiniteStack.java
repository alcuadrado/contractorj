package examples.unannotated;

public class FiniteStack<T> {

  private final Object[] data;

  private int size = 0;

  public FiniteStack(final int capacity) {

    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }

    data = new Object[capacity];
  }

  public FiniteStack() {
    this(5);
  }

  public void Push(T item) {
    data[size++] = item;
  }

  public T Pop() {
    return (T) data[--size];
  }

  public int getSize() {
    return size;
  }
}
