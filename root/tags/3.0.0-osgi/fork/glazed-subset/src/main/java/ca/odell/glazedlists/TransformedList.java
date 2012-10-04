/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists;

// the Glazed Lists' change objects

import java.util.Collection;

import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/**
 * A convenience class for {@link ca.odell.glazedlists.EventList}s that decorate another {@link ca.odell.glazedlists.EventList}.
 * Extending classes transform their source {@link ca.odell.glazedlists.EventList} by modifying the
 * order, visibility and value of its elements.
 *
 * <p>Extending classes may implement the method {@link #getSourceIndex(int)} to
 * translate between indices of this and indices of the source.
 *
 * <p>Extending classes may implement the method {@link #isWritable()} to make the
 * source writable via this API.
 *
 * <p>Extending classes must explicitly call {@link #addListEventListener(ca.odell.glazedlists.event.ListEventListener)}
 * to receive change notifications from the source {@link ca.odell.glazedlists.EventList}.
 *
 * <p><strong><font color="#FF0000">Warning:</font></strong> This class is
 * thread ready but not thread safe. See {@link ca.odell.glazedlists.EventList} for an example
 * of thread safe code.
 *
 */
public abstract class TransformedList<S, E> extends AbstractEventList<E> implements ListEventListener<S> {

    /** the event list to transform */
    protected EventList<S> source;

    /**
     * Creates a {@link TransformedList} to transform the specified {@link ca.odell.glazedlists.EventList}.
     *
     * @param source the {@link ca.odell.glazedlists.EventList} to transform
     */
    protected TransformedList(EventList<S> source) {
        super(source.getPublisher());
        this.source = source;
        readWriteLock = source.getReadWriteLock();
    }

    /**
     * Gets the index in the source {@link ca.odell.glazedlists.EventList} that corresponds to the
     * specified index. More formally, returns the index such that
     * <br><code>this.get(i) == source.get(getSourceIndex(i))</code> for all
     * legal values of <code>i</code>.
     */
    protected int getSourceIndex(int mutationIndex) {
        return mutationIndex;
    }

    /**
     * Gets whether the source {@link ca.odell.glazedlists.EventList} is writable via this API.
     *
     * <p>Extending classes must override this method in order to make themselves
     * writable.
     */
    protected abstract boolean isWritable();

    /** {@inheritDoc} */
    public abstract void listChanged(ListEvent<S> listChanges);

    /** {@inheritDoc} */
    public void add(int index, E value) {
        if(!isWritable()) throw new IllegalStateException("Non-writable List cannot be modified");
        if(index < 0 || index > size()) throw new IndexOutOfBoundsException("Cannot add at " + index + " on list of size " + size());
        final int sourceIndex = index < size() ? getSourceIndex(index) : source.size();
        source.add(sourceIndex, (S) value);
    }

    /** {@inheritDoc} */
    public boolean addAll(int index, Collection<? extends E> values) {
        // nest changes and let the other methods compose the event
        updates.beginEvent(true);
        try {
            return super.addAll(index, values);
        } finally {
            updates.commitEvent();
        }
    }

    /** {@inheritDoc} */
    public void clear() {
        // nest changes and let the other methods compose the event
        updates.beginEvent(true);
        try {
            super.clear();
        } finally {
            updates.commitEvent();
        }
    }

    /** {@inheritDoc} */
    public E get(int index) {
        if(index < 0 || index >= size()) throw new IndexOutOfBoundsException("Cannot get at " + index + " on list of size " + size());
        return (E) source.get(getSourceIndex(index));
    }

    /** {@inheritDoc} */
    public E remove(int index) {
        if(!isWritable()) throw new IllegalStateException("Non-writable List cannot be modified");
        if(index < 0 || index >= size()) throw new IndexOutOfBoundsException("Cannot remove at " + index + " on list of size " + size());
        return (E) source.remove(getSourceIndex(index));
    }

    /** {@inheritDoc} */
    public boolean removeAll(Collection<?> collection) {
        // nest changes and let the other methods compose the event
        updates.beginEvent(true);
        try {
            return super.removeAll(collection);
        } finally {
            updates.commitEvent();
        }
    }

    /** {@inheritDoc} */
    public boolean retainAll(Collection<?> values) {
        // nest changes and let the other methods compose the event
        updates.beginEvent(true);
        try {
            return super.retainAll(values);
        } finally {
            updates.commitEvent();
        }
    }

    /** {@inheritDoc} */
    public E set(int index, E value) {
        if(!isWritable()) throw new IllegalStateException("List " + this.getClass().getName() + " cannot be modified in the current state");
        if(index < 0 || index >= size()) throw new IndexOutOfBoundsException("Cannot set at " + index + " on list of size " + size());
        return (E) source.set(getSourceIndex(index), (S) value);
    }

    /** {@inheritDoc} */
    public int size() {
        return source.size();
    }

    /**
     * Releases the resources consumed by this {@link TransformedList} so that it
     * may eventually be garbage collected.
     *
     * <p>A {@link TransformedList} will be garbage collected without a call to
     * {@link #dispose()}, but not before its source {@link ca.odell.glazedlists.EventList} is garbage
     * collected. By calling {@link #dispose()}, you allow the {@link TransformedList}
     * to be garbage collected before its source {@link ca.odell.glazedlists.EventList}. This is
     * necessary for situations where a {@link TransformedList} is short-lived but
     * its source {@link ca.odell.glazedlists.EventList} is long-lived.
     *
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on a {@link TransformedList} after it has been disposed.
     */
    public void dispose() {
        source.removeListEventListener(this);
    }
}