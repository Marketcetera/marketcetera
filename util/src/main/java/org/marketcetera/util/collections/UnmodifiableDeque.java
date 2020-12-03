package org.marketcetera.util.collections;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Provides a <code>Deque</code> implementation that cannot be modified.
 * 
 * <p>This class is not thread-safe: if thread-safety is required external
 * synchronization is necessary.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UnmodifiableDeque.java 16154 2012-07-14 16:34:05Z colin $
 * @since 2.1.4
 */
@NotThreadSafe
@ClassVersion("$Id: UnmodifiableDeque.java 16154 2012-07-14 16:34:05Z colin $")
public class UnmodifiableDeque<T>
        implements Deque<T>
{
    /**
     * Create a new UnmodifiableDeque instance.
     *
     * @param inCollection a <code>Deque&lt;T&gt;</code> value
     */
    public UnmodifiableDeque(Deque<T> inCollection)
    {
        if(inCollection == null) {
            throw new NullPointerException();
        }
        innerCollection = inCollection;
    }
    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> inC)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#clear()
     */
    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> inC)
    {
        return innerCollection.containsAll(inC);
    }
    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return innerCollection.isEmpty();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> inC)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> inC)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    @Override
    public Object[] toArray()
    {
        return innerCollection.toArray();
    }
    /* (non-Javadoc)
     * @see java.util.Collection#toArray(T[])
     */
    @Override
    public <C> C[] toArray(C[] inA)
    {
        return innerCollection.toArray(inA);
    }
    /* (non-Javadoc)
     * @see java.util.Deque#add(java.lang.Object)
     */
    @Override
    public boolean add(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#addFirst(java.lang.Object)
     */
    @Override
    public void addFirst(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#addLast(java.lang.Object)
     */
    @Override
    public void addLast(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object inO)
    {
        return innerCollection.contains(inO);
    }
    /* (non-Javadoc)
     * @see java.util.Deque#descendingIterator()
     */
    @Override
    public Iterator<T> descendingIterator()
    {
        return new UnmodifiableIterator(innerCollection,
                                        true);
    }
    /* (non-Javadoc)
     * @see java.util.Deque#element()
     */
    @Override
    public T element()
    {
        return innerCollection.element();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#getFirst()
     */
    @Override
    public T getFirst()
    {
        return innerCollection.getFirst();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#getLast()
     */
    @Override
    public T getLast()
    {
        return innerCollection.getLast();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#iterator()
     */
    @Override
    public Iterator<T> iterator()
    {
        return new UnmodifiableIterator(innerCollection,
                                        false);
    }
    /* (non-Javadoc)
     * @see java.util.Deque#offer(java.lang.Object)
     */
    @Override
    public boolean offer(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#offerFirst(java.lang.Object)
     */
    @Override
    public boolean offerFirst(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#offerLast(java.lang.Object)
     */
    @Override
    public boolean offerLast(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#peek()
     */
    @Override
    public T peek()
    {
        return innerCollection.peek();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#peekFirst()
     */
    @Override
    public T peekFirst()
    {
        return innerCollection.peekFirst();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#peekLast()
     */
    @Override
    public T peekLast()
    {
        return innerCollection.peekLast();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#poll()
     */
    @Override
    public T poll()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#pollFirst()
     */
    @Override
    public T pollFirst()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#pollLast()
     */
    @Override
    public T pollLast()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#pop()
     */
    @Override
    public T pop()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#push(java.lang.Object)
     */
    @Override
    public void push(T inE)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#remove()
     */
    @Override
    public T remove()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object inO)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#removeFirst()
     */
    @Override
    public T removeFirst()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#removeFirstOccurrence(java.lang.Object)
     */
    @Override
    public boolean removeFirstOccurrence(Object inO)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#removeLast()
     */
    @Override
    public T removeLast()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#removeLastOccurrence(java.lang.Object)
     */
    @Override
    public boolean removeLastOccurrence(Object inO)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.util.Deque#size()
     */
    @Override
    public int size()
    {
        return innerCollection.size();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return innerCollection.hashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        return innerCollection.equals(obj);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return innerCollection.toString();
    }
    /**
     * Provides an <code>Iterator</code> implementation that cannot be used to modify
     * the underlying <code>Collection</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: UnmodifiableDeque.java 16154 2012-07-14 16:34:05Z colin $
     * @since 2.1.4
     */
    @ThreadSafe
    @ClassVersion("$Id: UnmodifiableDeque.java 16154 2012-07-14 16:34:05Z colin $")
    private class UnmodifiableIterator
            implements Iterator<T>
    {
        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }
        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public T next()
        {
            return iterator.next();
        }
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Create a new UnmodifiableIterator instance.
         *
         * @param inCollection a <code>Deque&lt;T&gt;</code> value
         * @param inIsDescending a <code>boolean</code> value indicating if the iterator should be ascending
         *  or descending
         */
        private UnmodifiableIterator(Deque<T> inCollection,
                                     boolean inIsDescending)
        {
            if(inIsDescending) {
                iterator = inCollection.descendingIterator();
            } else {
                iterator = inCollection.iterator();
            }
        }
        /**
         * underlying iterator used to track the cursor location
         */
        private final Iterator<T> iterator;
    }
    /**
     * underlying collection used to store managed data
     */
    private final Deque<T> innerCollection;
}
