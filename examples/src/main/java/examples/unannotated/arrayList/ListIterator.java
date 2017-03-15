package examples.unannotated.arrayList;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/** An optimized version of AbstractList.ListItr */
public class ListIterator {

  public ArrayList arrayList;
  public int cursor; // index of next element to return
  public int lastRet = -1; // index of last element returned; -1 if no such
  public int expectedModCount;

  public boolean hasNext() {
    return cursor != arrayList.size;
  }

  @SuppressWarnings("unchecked")
  public Object next() {
    checkForComodification();
    int i = cursor;
    if (i >= arrayList.size) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i + 1;
    return elementData[lastRet = i];
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

  public ListIterator(int index, int i, int i0) {
    this(index, 0);
  }

  public ListIterator(int index, int i, int i0, int i1) {
    this(index, 1);
  }

  public ListIterator(int index, int i, int i0, int i1, int i3) {
    this(index, 3);
  }

  public ListIterator(int index, int i, int i0, int i1, int i3, int i8) {
    this(index, 8);
  }

  public ListIterator(int index, int count) {
    this(new ArrayList(), index);
    fillList(count);
  }

  private void fillList(int count) {
    if (count > 0) {
      arrayList.add(0, null);
      fillList(--count);
    }
  }

  public ListIterator(ArrayList arrayList, int index) {
    this.arrayList = arrayList;
    cursor = index;
    expectedModCount = arrayList.modCount;
  }

  public ListIterator(ArrayList arrayList) {
    this(arrayList, 0);
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
  public Object previous() {
    checkForComodification();
    int i = cursor - 1;
    if (i < 0) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i;
    return elementData[lastRet = i];
  }

  public void set(Object e) {
    if (lastRet < 0) throw new IllegalStateException();
    checkForComodification();

    try {
      arrayList.set(lastRet, e);
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }

  public void add(Object e) {
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
