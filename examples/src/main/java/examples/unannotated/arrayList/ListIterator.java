package examples.unannotated.arrayList;

import examples.List;

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

  public ListIterator(int index, int elements) {
    ArrayList arrayList = new ArrayList();

    for (int i = 0; i < Math.min(elements, 15); i++) {
      arrayList.add(0, null);
    }

    this.arrayList = arrayList;
    cursor = index;
    expectedModCount = this.arrayList.modCount;
  }

  public ListIterator(ArrayList arrayList, int index) {
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
