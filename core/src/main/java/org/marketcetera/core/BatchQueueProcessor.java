package org.marketcetera.core;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import javax.annotation.concurrent.GuardedBy;

/* $License$ */

/**
 * Provides a framework for processing a batch of elements in a separate thread.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class BatchQueueProcessor<Clazz>
        extends QueueProcessor<Clazz>
{
    /**
     * Create a new BatchQueueProcessor instance.
     */
    public BatchQueueProcessor()
    {
        super();
    }
    /**
     * Create a new BatchQueueProcessor instance.
     *
     * @param inThreadDescription a <code>String</code> value
     */
    public BatchQueueProcessor(String inThreadDescription)
    {
        super(inThreadDescription);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
     */
    @Override
    protected final void processData(Clazz inData)
            throws Exception
    {
        synchronized(queueLock) {
            // add the current event to the queue before draining the queue of other events
            objectQueue.add(inData);
            super.getQueue().drainTo(objectQueue);
        }
        synchronized(objectQueue) {
            // use size-1 because the parent has already incremented the marker
            int size = objectQueue.size()-1;
            processQueueMetric.mark(size);
            queueCounterMetric.dec(size);
            processData(objectQueue);
            objectQueue.clear();
        }
    }
    /**
     * Adds the given data to the processing queue.
     *
     * @param inData a <code>Clazz</code> value
     */
    protected void add(Clazz inData)
    {
        synchronized(queueLock) {
            super.add(inData);
        }
    }
    /**
     * Adds the given data to the processing queue in the order implied by the collection.
     *
     * @param inData a <code>Collection&lt;Clazz&gt;</code> value
     */
    protected void addAll(Collection<Clazz> inData)
    {
        synchronized(queueLock) {
            super.addAll(inData);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#getQueue()
     */
    @Override
    protected final BlockingQueue<Clazz> getQueue()
    {
        // don't allow subclasses to affect the queue directly so we can control synchronization
        throw new UnsupportedOperationException();
    }
    /**
     * Processes the given data according to the nature of the subclass.
     * 
     * <p>The given collection is synchronized by the caller.</p>
     * 
     * <p>The method should not access this collection outside of the scope of this method call. Its contents
     * are not guaranteed except in the scope of this method call.
     *
     * @param inData a <code>Deque&lt;Clazz&gt;</code> value
     * @throws Exception if an error occurs processing the data
     */
    protected abstract void processData(Deque<Clazz> inData)
            throws Exception;
    /**
     * provides a mechanism for locking the processing queue
     */
    private final Object queueLock = new Object();
    /**
     * work queue of events to process
     */
    @GuardedBy("queueLock")
    private final Deque<Clazz> objectQueue = new LinkedList<>();
}
