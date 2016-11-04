package examples.ListIterator;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class MyListItr implements ListIterator {

    /**
     * The list on which iteration is performed
     */
    private final MyArrayList list;

    /**
     * Index of element to be returned by subsequent call to next.
     */
    int cursor = 0;

    /**
     * Index of element returned by most recent call to next or previous.
     * Reset to -1 if this element is deleted by a call to remove.
     */
    int lastRet = -1;

    MyListItr(MyArrayList myArrayList, int index) {
        list = myArrayList;
        cursor = index;
    }

    public boolean hasNext() {
        return cursor != list.size();
    }

    public Object next() {
        try {
            Object next = list.get(cursor);
            lastRet = cursor++;
            return next;
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        if (lastRet == -1)
            throw new IllegalStateException();

        try {
            list.remove(lastRet);
            if (lastRet > cursor)
                cursor--;
            lastRet = -1;
        } catch (IndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean hasPrevious() {
        return cursor != 0;
    }

    public Object previous() {
        try {
            int i = cursor - 1;
            Object previous = list.get(i);
            lastRet = cursor = i;
            return previous;
        } catch (IndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    public void set(Object o) {
        if (lastRet == -1)
            throw new IllegalStateException();

        try {
            list.set(lastRet, o);
        } catch (IndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    public void add(Object o) {
        try {
            list.add(cursor++, o);
            lastRet = -1;
        } catch (IndexOutOfBoundsException e) {
            throw new ConcurrentModificationException();
        }
    }

    public int nextIndex() {
        throw new IllegalStateException();
    }

    public int previousIndex() {
        throw new IllegalStateException();
    }

    public boolean inv() {
        return list != null && list.elementData != null &&
                cursor >= 0 && cursor <= list.size && list.size <= list.elementData.length &&
                (lastRet == -1 || (cursor - 1 <= lastRet) && lastRet <= cursor);
    }

    public boolean MyListItr_pre(MyArrayList myArrayList, int index) {
        return myArrayList != null
                && myArrayList.elementData != null
                && 0 <= index && index <= myArrayList.size
                && 0 <= myArrayList.size && myArrayList.size <= myArrayList.elementData.length
                && 10 <= myArrayList.elementData.length;
    }

    public boolean add_pre(Object o) {
        return 0 <= cursor && cursor <= list.size;
    }

    public boolean next_pre() {
        return 0 <= cursor && cursor < list.size;
    }

    public boolean previous_pre() {
        return 0 <= cursor - 1 && cursor - 1 < list.size;
    }

    public boolean remove_pre() {
        return lastRet != -1 && 0 <= lastRet && lastRet < list.size;
    }

}

