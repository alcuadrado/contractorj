package examples;

public class GenericStack<T> {

  private final int capacity;

  private final Object[] data;

  private int size = 0;

  public GenericStack(final int capacity) {
    this.capacity = capacity;
    data = new Object[capacity];
  }

  public GenericStack() {
    this(5);
  }

  public static boolean GenericStack_pre(final int capacity) {
    return capacity > 0;
  }

  public boolean inv() {
    return size >= 0 && size <= capacity && data.length == capacity && data != null;
  }

  public boolean Push_pre() {

    return size < capacity;
  }

  public void Push(T item) {
    data[size++] = item;
  }

  public boolean Pop_pre() {
    return size > 0;
  }

  @SuppressWarnings("unchecked")
  public T Pop() {
    return (T) data[--size];
  }

  public int getSize() {
    return size;
  }
}
