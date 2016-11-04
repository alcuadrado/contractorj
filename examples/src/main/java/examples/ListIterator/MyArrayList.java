package examples.ListIterator;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("unchecked")
public class MyArrayList extends AbstractCollection implements List {
	/**
	 * The array buffer into which the elements of the ArrayList are stored. The
	 * capacity of the ArrayList is the length of this array buffer.
	 */
	public transient Object elementData[];

	/**
	 * The size of the ArrayList (the number of elements it contains).
	 * 
	 * @serial
	 */
	public int size;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the list.
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	public MyArrayList(int initialCapacity) {
		super();
		if (initialCapacity < 0)
			throw new IllegalArgumentException();
		this.elementData = new Object[initialCapacity];
	}

	/**
	 * Constructs an empty list with an initial capacity of ten.
	 */
	public MyArrayList() {
		this(10);
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if necessary,
	 * to ensure that it can hold at least the number of elements specified by
	 * the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity)
				newCapacity = minCapacity;
			elementData = new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 * <p>
	 * 
	 * @return an iterator over the elements in this list in proper sequence.
	 */
	public Iterator iterator() {
		return new MyListItr(this, 0);
	}

	/**
	 * Returns an iterator of the elements in this list (in proper sequence).
	 * This implementation returns <tt>listIterator(0)</tt>.
	 * 
	 * @return an iterator of the elements in this list (in proper sequence).
	 */
	public ListIterator listIterator() {
		return listIterator(0);
	}
	
	/**
	 * Returns a list iterator of the elements in this list (in proper
	 * sequence), starting at the specified position in the list. The specified
	 * index indicates the first element that would be returned by an initial
	 * call to the <tt>next</tt> method. An initial call to the
	 * <tt>previous</tt> method would return the element with the specified
	 * index minus one.
	 * 
	 * @param index
	 *            index of the first element to be returned from the list
	 *            iterator (by a call to the <tt>next</tt> method).
	 * 
	 * @return a list iterator of the elements in this list (in proper
	 *         sequence), starting at the specified position in the list.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is out of range (
	 *             <tt>index &lt; 0 || index &gt; size()</tt>).
	 */
	public ListIterator listIterator(final int index) {
		if (index < 0 || index >= size())
			throw new IndexOutOfBoundsException();

		return new MyListItr(this, index);
	}
	
	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list.
	 */
	public int size() {
		return size;
	}

	/**
	 * Tests if this list has no elements.
	 * 
	 * @return <tt>true</tt> if this list has no elements; <tt>false</tt>
	 *         otherwise.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index
	 *            index of element to return.
	 * @return the element at the specified position in this list.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object get(int index) {
		RangeCheck(index);

		return elementData[index];
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 * 
	 * @param index
	 *            index of element to replace.
	 * @param element
	 *            element to be stored at the specified position.
	 * @return the element previously at the specified position.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range
	 *             <tt>(index &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object set(int index, Object element) {
		RangeCheck(index);

		Object oldValue = elementData[index];
		elementData[index] = element;
		return oldValue;
	}

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param o
	 *            element to be appended to this list.
	 * @return <tt>true</tt> (as per the general contract of Collection.add).
	 */
	public boolean add(Object o) {
		ensureCapacity(size + 1); 
		elementData[size++] = o;
		return true;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * Shifts the element currently at that position (if any) and any subsequent
	 * elements to the right (adds one to their indices).
	 * 
	 * @param index
	 *            index at which the specified element is to be inserted.
	 * @param element
	 *            element to be inserted.
	 * @throws IndexOutOfBoundsException
	 *             if index is out of range
	 *             <tt>(index &lt; 0 || index &gt; size())</tt>.
	 */
	public void add(int index, Object element) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException();

		ensureCapacity(size + 1); 
		System.arraycopy(elementData, index, elementData, index + 1, size
				- index);
		elementData[index] = element;
		size++;
	}

	/**
	 * Removes the element at the specified position in this list. Shifts any
	 * subsequent elements to the left (subtracts one from their indices).
	 * 
	 * @param index
	 *            the index of the element to removed.
	 * @return the element that was removed from the list.
	 * @throws IndexOutOfBoundsException
	 *             if index out of range <tt>(index
	 * 		  &lt; 0 || index &gt;= size())</tt>.
	 */
	public Object remove(int index) {
		RangeCheck(index);

		Object oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		elementData[--size] = null; // Let gc do its work

		return oldValue;
	}

	/**
	 * Check if the given index is in range. If not, throw an appropriate
	 * runtime exception. This method does *not* check if the index is negative:
	 * It is always used immediately prior to an array access, which throws an
	 * ArrayIndexOutOfBoundsException if index is negative.
	 */
	private void RangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException();
	}

	@Override
	public boolean addAll(int arg0, Collection arg1) {
		throw new IllegalStateException();
	}

	@Override
	public int indexOf(Object arg0) {
		throw new IllegalStateException();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		throw new IllegalStateException();
	}

	@Override
	public List subList(int arg0, int arg1) {
		throw new IllegalStateException();
	}

}
