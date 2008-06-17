package org.marketcetera.photon.scripting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class Classpath implements List<IPath> {
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	List<IPath> innerList = new ArrayList<IPath>();

	public boolean add(String path){
		if (path == null){
			return false;
		}
		IPath iPath = Path.fromOSString(path);
		if (iPath == null)
			throw new NullPointerException();
		return innerList.add(iPath);
	}

	public void add(int index, String path){
		if (path != null){
			IPath iPath = Path.fromOSString(path);
			if (iPath == null)
				throw new NullPointerException();
			innerList.add(index, iPath);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		int size = innerList.size();
		int i = 0;
		StringBuffer sb = new StringBuffer();
		for (IPath path : innerList) {
			sb.append(path.toOSString());
			if (++i < size){
				sb.append(PATH_SEPARATOR);
			}
		}
		return sb.toString();
	}

	/**
	 * @param index
	 * @param iPath
	 * @see java.util.List#add(int, java.lang.Object)
	 */
	public void add(int index, IPath iPath) {
		if (iPath == null)
			throw new NullPointerException();
		innerList.add(index, iPath);
	}

	/**
	 * @param iPath
	 * @return whether the path was successfully added.
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(IPath iPath) {
		if (iPath == null)
			throw new NullPointerException();
		return innerList.add(iPath);
	}

	/**
	 * @param c
	 * @return whether the elements were successfully added
	 * @see java.util.List#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends IPath> c) {
		return innerList.addAll(c);
	}

	/**
	 * @param index
	 * @param c
	 * @return whether the elements of the collection were successfully added.
	 * @see java.util.List#addAll(int, java.util.Collection)
	 */
	public boolean addAll(int index, Collection<? extends IPath> c) {
		return innerList.addAll(index, c);
	}

	/**
	 * 
	 * @see java.util.List#clear()
	 */
	public void clear() {
		innerList.clear();
	}

	/**
	 * @param o
	 * @return true if the classpath contans element o
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return innerList.contains(o);
	}

	/**
	 * @param c
	 * @return true if the classpath contains all of the elements in c
	 * @see java.util.List#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return innerList.containsAll(c);
	}

	/**
	 * @param o
	 * @return true if this classpath equals o
	 * @see java.util.List#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return innerList.equals(o);
	}

	/**
	 * @param index
	 * @return the path element at index
	 * @see java.util.List#get(int)
	 */
	public IPath get(int index) {
		return innerList.get(index);
	}

	/**
	 * @return the hashcode for this Classpath
	 * @see java.util.List#hashCode()
	 */
	public int hashCode() {
		return innerList.hashCode();
	}

	/**
	 * @param o
	 * @return the first index at which o is found.
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object o) {
		return innerList.indexOf(o);
	}

	/**
	 * @return true if the classpath is empty
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return innerList.isEmpty();
	}

	/**
	 * @return an iterator for the elements of this classpath
	 * @see java.util.List#iterator()
	 */
	public Iterator<IPath> iterator() {
		return innerList.iterator();
	}

	/**
	 * @param o
	 * @return the last index at which o is found.
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object o) {
		return innerList.lastIndexOf(o);
	}

	/**
	 * @return a ListIterator for this classpath
	 * @see java.util.List#listIterator()
	 */
	public ListIterator<IPath> listIterator() {
		return innerList.listIterator();
	}

	/**
	 * @param index
	 * @return a ListIterator for this classpath starting at the element
	 * specified by index.
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator<IPath> listIterator(int index) {
		return innerList.listIterator(index);
	}

	/**
	 * @param index
	 * @return the element found at index
	 * @see java.util.List#remove(int)
	 */
	public IPath remove(int index) {
		return innerList.remove(index);
	}

	/**
	 * @param o
	 * @return true if the element was successfully removed
	 * @see java.util.List#remove(java.lang.Object)
	 */
	public boolean remove(Object o) {
		return innerList.remove(o);
	}

	/**
	 * @param c
	 * @return true if the list was changed as a result of this call
	 * @see java.util.List#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		return innerList.removeAll(c);
	}

	/**
	 * @param c
	 * @return true if the list was changed as a result of this call
	 * @see java.util.List#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		return innerList.retainAll(c);
	}

	/**
	 * @param index
	 * @param iPath
	 * @return the element previously at the specified position.
	 * @see java.util.List#set(int, java.lang.Object)
	 */
	public IPath set(int index, IPath iPath) {
		if (iPath == null)
			throw new NullPointerException();
		return innerList.set(index, iPath);
	}

	/**
	 * @return the size of this classpath in list elements
	 * @see java.util.List#size()
	 */
	public int size() {
		return innerList.size();
	}

	/**
	 * @param fromIndex
	 * @param toIndex
	 * @return the sub list
	 * @see java.util.List#subList(int, int)
	 */
	public List<IPath> subList(int fromIndex, int toIndex) {
		return innerList.subList(fromIndex, toIndex);
	}

	/**
	 * @return an array representation of this classpath
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		return innerList.toArray();
	}

	/**
	 * @param <T>
	 * @param a
	 * @return an array representation of this classpath
	 */
	public <T> T[] toArray(T[] a) {
		return innerList.toArray(a);
	}
	

}
