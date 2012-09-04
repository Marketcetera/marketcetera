/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists;

import java.util.List;

import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.event.ListEventPublisher;
import ca.odell.glazedlists.util.concurrent.ReadWriteLock;

/**
 * An observable {@link java.util.List}. {@link ca.odell.glazedlists.event.ListEventListener}s can register to be
 * notified when this list changes.
 *
 * <p>{@link EventList}s may be writable or read-only. Consult the Javadoc for
 * your {@link EventList} if you are unsure.
 *
 * <p><strong><font color="#FF0000">Warning:</font></strong> {@link EventList}s
 * are thread ready but not thread safe. If you are sharing an {@link EventList}
 * between multiple threads, you can add thread safety by using the built-in
 * locks:
 * <pre>
 * EventList myList = ...
 * myList.getReadWriteLock().writeLock().lock();
 * try {
 *    // access myList here
 *    if(myList.size() > 3) {
 *       System.out.println(myList.get(3));
 *       myList.remove(3);
 *    }
 * } finally {
 *    myList.getReadWriteLock().writeLock().unlock();
 * }
 * </pre>
 *
 * Note that you are also required to acquire and hold the lock during the
 * construction of an EventList if concurrent modifications are possible in
 * your environment, like so:
 *
 * <pre>
 * EventList source = ...
 * SortedList sorted;
 * source.getReadWriteLock().readLock().lock();
 * try {
 *    sorted = new SortedList(source);
 * } finally {
 *    source.getReadWriteLock().readLock().unlock();
 * }
 * </pre>
 *
 * <p><strong><font color="#FF0000">Warning:</font></strong> {@link EventList}s
 * may break the contract required by {@link java.util.List}. For example, when
 * you {@link #add(int,Object) add()} on a {@link ca.odell.glazedlists.SortedList}, it will ignore the specified
 * index so that the element will be inserted in sorted order.
 * *
 */
public interface EventList<E> extends List<E> {

    /**
     * Registers the specified listener to receive change updates for this list.
     */
    public void addListEventListener(ListEventListener<? super E> listChangeListener);

    /**
     * Removes the specified listener from receiving change updates for this list.
     */
    public void removeListEventListener(ListEventListener<? super E> listChangeListener);

    /**
     * Gets the lock required to share this list between multiple threads.
     *
     * @return a re-entrant {@link ca.odell.glazedlists.util.concurrent.ReadWriteLock} that guarantees thread safe
     *      access to this list.
     */
    public ReadWriteLock getReadWriteLock();

    /**
     * Get the publisher used to distribute {@link ca.odell.glazedlists.event.ListEvent}s.
     */
    public ListEventPublisher getPublisher();

    /**
     * Disposing an EventList will make it eligible for garbage collection.
     * Some EventLists install themselves as listeners to related objects so
     * disposing them is necessary.
     *
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on an {@link EventList} after it has been disposed.
     */
    public void dispose();
}