package examples.unannotated.arrayList;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/** An optimized version of AbstractList.ListItr */
public class ListIterator<E> {

  private ArrayList<E> arrayList;
  int cursor; // index of next element to return
  int lastRet = -1; // index of last element returned; -1 if no such
  int expectedModCount;

  public boolean hasNext() {
    return cursor != arrayList.size;
  }

  @SuppressWarnings("unchecked")
  public E next() {
    checkForComodification();
    int i = cursor;
    if (i >= arrayList.size) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i + 1;
    return (E) elementData[lastRet = i];
  }

  public void remove() {
    if (lastRet < 0) throw new IllegalStateException();
    checkForComodification();

    try {
      arrayList.remove(lastRet);
      cursor = lastRet;
      lastRet = -1;
      expectedModCount = arrayList.modCount;
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }

  final void checkForComodification() {
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();
  }

  public ListIterator(ArrayList<E> arrayList, int index) {
    super();
    this.arrayList = arrayList;
    cursor = index;
    expectedModCount = arrayList.modCount;
  }

  public boolean hasPrevious() {
    return cursor != 0;
  }

  public int nextIndex() {
    return cursor;
  }

  public int previousIndex() {
    return cursor - 1;
  }

  @SuppressWarnings("unchecked")
  public E previous() {
    checkForComodification();
    int i = cursor - 1;
    if (i < 0) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i;
    return (E) elementData[lastRet = i];
  }

  public void set(E e) {
    if (lastRet < 0) throw new IllegalStateException();
    checkForComodification();

    try {
      arrayList.set(lastRet, e);
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }

  public void add(E e) {
    checkForComodification();

    try {
      int i = cursor;
      arrayList.add(i, e);
      cursor = i + 1;
      lastRet = -1;
      expectedModCount = arrayList.modCount;
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }
  
}
