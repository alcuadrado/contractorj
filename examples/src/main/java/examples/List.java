package examples;

public class List {

    private int size = 0;

    public boolean inv() {
        return size >= 0;
    }

    public void add() {
        size += 1;
    }

    public boolean add_pre() {
        return true;
    }

    public void remove() {
        size -= 1;
    }

    public boolean remove_pre() {
        return size > 0;
    }

}
