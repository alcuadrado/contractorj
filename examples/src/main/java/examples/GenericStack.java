package examples;

public class GenericStack<T> {

    private static final int capacity = 5;
    private int size = 0;
    private T[] data = (T[]) new Object[capacity];

    public boolean inv() {
//        return size >= 0 && size <= capacity && data.length == capacity; ANDA MAL POR NO CHEQUEAR NULL
        return size >= 0 && size <= capacity && data.length == capacity && data != null;
    }

    public boolean Push_pre(T item) {
        return size < capacity;
    }

    public void Push(T item) {
        data[size++] = item;
    }

    public boolean Pop_pre() {
        return size > 0;
    }

    public T Pop() {
        return data[--size];
    }

}
