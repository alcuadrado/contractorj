package examples.unannotated;

public class GenericStack<T> {

  private final int capacity;

  private final Object[] data;

  private int size = 0;

  public GenericStack(final int capacity) {

    if (capacity <= 0) {
      throw new IllegalArgumentException();
    }

    this.capacity = capacity;
    data = new Object[capacity];
  }

  public GenericStack() {
    this(5);
  }

  public void Push(T item) {
    data[size++] = item;
  }

  @SuppressWarnings("unchecked")
  public T Pop() {
    return (T) data[--size];
  }

  public int getSize() {
    return size;
  }
}
