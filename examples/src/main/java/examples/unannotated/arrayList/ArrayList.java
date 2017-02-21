/*
 * Copyright (c) 1997, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package examples.unannotated.arrayList;

import java.util.Arrays;

public class ArrayList {

  /**
   * The array buffer into which the elements of the ArrayList are stored. The capacity of the
   * ArrayList is the length of this array buffer.
   */
  public transient Object[] elementData;

  /**
   * The size of the ArrayList (the number of elements it contains).
   *
   * @serial
   */
  public int size;

  /**
   * The number of times this list has been <i>structurally modified</i>. Structural modifications
   * are those that change the size of the list, or otherwise perturb it in such a fashion that
   * iterations in progress may yield incorrect results.
   *
   * <p>This field is used by the iterator and list iterator implementation returned by the {@code
   * iterator} and {@code listIterator} methods. If the value of this field changes unexpectedly,
   * the iterator (or list iterator) will throw a {@code ConcurrentModificationException} in
   * response to the {@code next}, {@code remove}, {@code previous}, {@code set} or {@code add}
   * operations. This provides <i>fail-fast</i> behavior, rather than non-deterministic behavior in
   * the face of concurrent modification during iteration.
   *
   * <p><b>Use of this field by subclasses is optional.</b> If a subclass wishes to provide
   * fail-fast iterators (and list iterators), then it merely has to increment this field in its
   * {@code add(int, E)} and {@code remove(int)} methods (and any other methods that it overrides
   * that result in structural modifications to the list). A single call to {@code add(int, E)} or
   * {@code remove(int)} must add no more than one to this field, or the iterators (and list
   * iterators) will throw bogus {@code ConcurrentModificationExceptions}. If an implementation does
   * not wish to provide fail-fast iterators, this field may be ignored.
   */
  public transient int modCount = 0;

  /**
   * Constructs an empty list with the specified initial capacity.
   *
   * @param initialCapacity the initial capacity of the list
   * @exception IllegalArgumentException if the specified initial capacity is negative
   */
  public ArrayList(int initialCapacity) {
    super();
    if (initialCapacity < 0)
      throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    this.elementData = new Object[initialCapacity];
  }

  /** Constructs an empty list with an initial capacity of ten. */
  public ArrayList() {
    this(10);
  }

  /**
   * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary, to ensure that it can
   * hold at least the number of elements specified by the minimum capacity argument.
   *
   * @param minCapacity the desired minimum capacity
   */
  public void ensureCapacity(int minCapacity) {
    modCount++;
    int oldCapacity = elementData.length;
    if (minCapacity > oldCapacity) {
      Object oldData[] = elementData;
      int newCapacity = (oldCapacity * 3) / 2 + 1;
      if (newCapacity < minCapacity) newCapacity = minCapacity;
      // minCapacity is usually close to size, so this is a win:
      elementData = Arrays.copyOf(elementData, newCapacity);
    }
  }

  // Positional Access Operations

  @SuppressWarnings("unchecked")
  Object elementData(int index) {
    return elementData[index];
  }

  /**
   * Replaces the element at the specified position in this list with the specified element.
   *
   * @param index index of the element to replace
   * @param element element to be stored at the specified position
   * @return the element previously at the specified position
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public Object set(int index, Object element) {
    rangeCheck(index);

    Object oldValue = elementData(index);
    elementData[index] = element;
    return oldValue;
  }

  /**
   * Inserts the specified element at the specified position in this list. Shifts the element
   * currently at that position (if any) and any subsequent elements to the right (adds one to their
   * indices).
   *
   * @param index index at which the specified element is to be inserted
   * @param element element to be inserted
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public void add(int index, Object element) {
    rangeCheckForAdd(index);

    ensureCapacity(size + 1); // Increments modCount!!
    System.arraycopy(elementData, index, elementData, index + 1, size - index);
    elementData[index] = element;
    size++;
  }

  /**
   * Removes the element at the specified position in this list. Shifts any subsequent elements to
   * the left (subtracts one from their indices).
   *
   * @param index the index of the element to be removed
   * @return the element that was removed from the list
   * @throws IndexOutOfBoundsException {@inheritDoc}
   */
  public Object remove(int index) {
    rangeCheck(index);

    modCount++;
    Object oldValue = elementData(index);

    int numMoved = size - index - 1;
    if (numMoved > 0) System.arraycopy(elementData, index + 1, elementData, index, numMoved);
    elementData[--size] = null; // Let gc do its work

    return oldValue;
  }

  /**
   * Checks if the given index is in range. If not, throws an appropriate runtime exception. This
   * method does *not* check if the index is negative: It is always used immediately prior to an
   * array access, which throws an ArrayIndexOutOfBoundsException if index is negative.
   */
  private void rangeCheck(int index) {
    if (index >= size) throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
  }

  /** A version of rangeCheck used by add and addAll. */
  private void rangeCheckForAdd(int index) {
    if (index > size || index < 0) throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
  }

  /**
   * Constructs an IndexOutOfBoundsException detail message. Of the many possible refactorings of
   * the error handling code, this "outlining" performs best with both server and client VMs.
   */
  private String outOfBoundsMsg(int index) {
    return "Index: " + index + ", Size: " + size;
  }

  /**
   * Returns a list iterator over the elements in this list (in proper sequence).
   *
   * <p>The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
   */
  public ListIterator listIterator() {
    return new ListIterator(this);
  }
}
