package examples.arrayList;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/** An optimized version of AbstractList.ListItr */
public class ListIterator<E> {

  public ArrayList<E> arrayList;
  public int cursor; // index of next element to return
  public int lastRet = -1; // index of last element returned; -1 if no such
  public int expectedModCount;

  //public boolean hasNext() {
  //  return cursor != arrayList.size;
  //}

  @SuppressWarnings("unchecked")
  public E next() {
    //checkForComodification();
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();
    int i = cursor;
    if (i >= arrayList.size) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i + 1;
    return (E) elementData[lastRet = i];
  }

  public void remove() {
    if (lastRet < 0) throw new IllegalStateException();
    //checkForComodification();
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();

    try {
      arrayList.remove(lastRet);
      cursor = lastRet;
      lastRet = -1;
      expectedModCount = arrayList.modCount;
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }

  //final void checkForComodification() {
  //  if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();
  //}

  public ListIterator(ArrayList<E> arrayList, int index) {
    super();
    this.arrayList = arrayList;
    cursor = index;
    expectedModCount = arrayList.modCount;
  }

  //public boolean hasPrevious() {
  //  return cursor != 0;
  //}

  /*public int nextIndex() {
    return cursor;
  }

  public int previousIndex() {
    return cursor - 1;
  }*/

  public boolean previous_pre() {
    //return hasPrevious();
    return cursor != 0;
  }

  @SuppressWarnings("unchecked")
  public E previous() {
    //checkForComodification();
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();
    int i = cursor - 1;
    if (i < 0) throw new NoSuchElementException();
    Object[] elementData = arrayList.elementData;
    if (i >= elementData.length) throw new ConcurrentModificationException();
    cursor = i;
    return (E) elementData[lastRet = i];
  }


  public void set(E e) {
    if (lastRet < 0) throw new IllegalStateException();
    //checkForComodification();
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();

    try {
      arrayList.set(lastRet, e);
    } catch (IndexOutOfBoundsException ex) {
      throw new ConcurrentModificationException();
    }
  }

  public void add(E e) {
    //checkForComodification();
    if (arrayList.modCount != expectedModCount) throw new ConcurrentModificationException();

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

  // Contracts

  public static boolean ListIterator_pre(ArrayList arrayList, int index) {
    return arrayList != null
        && arrayList.inv()
        && index <= arrayList.size
        && arrayList.listiterator_pre(index);
  }

 /* public boolean inv_1(){
    return 	(

            LI_cursor <= AL_size &&  0<= LI_cursor
            && (-1 == LI_lastRet
                    || (LI_cursor < AL_size && LI_lastRet == LI_cursor)
                    || (LI_cursor > 0 && LI_lastRet == LI_cursor - 1)
            )

            && 0 <= LI_expectedModCount && 0 <= AL_modCount

            && 0 <= AL_size && AL_size <= AL_elementData_length
            && 10 <= AL_elementData_length;
  }*/

  public boolean inv() {
    return arrayList != null
        && arrayList.inv()
        && expectedModCount >= 0
        && expectedModCount <= arrayList.modCount
        && cursor >= 0
        && cursor <= arrayList.size
        && (lastRet == -1
            || (cursor < arrayList.size /* == 0? */ && lastRet == cursor)
            || (cursor > 0 && lastRet == cursor - 1));
  }


  public boolean next_pre() {
    if (arrayList.modCount != expectedModCount)
      return false;

    // hasNext()
    if (!(cursor != arrayList.size))
      return false;

    return true;

  }

  public boolean add_pre(){
    if (arrayList.modCount != expectedModCount)
      return false;

    return true;
  }
  public boolean set_pre() {
    return lastRet >= 0 && lastRet < arrayList.size; // Inline part of ArrayList#set_pre
  }

  public boolean remove_pre() {
    return lastRet >= 0 && lastRet < arrayList.size; // Inline part of ArrayList#remove_pre
  }
}
