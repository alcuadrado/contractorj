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

    if (size == capacity) {
      throw new RuntimeException("Stack is full");
    }

    data[size++] = item;
  }

  @SuppressWarnings("unchecked")
  public T Pop() {
    if (size == 0) {
      throw new RuntimeException("Stack is empty");
    }

    return (T) data[--size];
  }

  public int getSize() {
    return size;
  }
}
