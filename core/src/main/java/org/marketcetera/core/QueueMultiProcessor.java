package org.marketcetera.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/* $License$ */

/**
 * Provides a {@link QueueProcessor} implementation that works with as many data elements as possible at once.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class QueueMultiProcessor<Clazz>
        extends QueueProcessor<Clazz>
{
    /**
     * Create a new QueueMultiProcessor instance.
     */
    protected QueueMultiProcessor()
    {
        super();
    }
    /**
     * Create a new QueueMultiProcessor instance.
     *
     * @param inThreadDescriptor a <code>String</code> value describing the processor
     * @param inQueue a <code>BlockingQueue&lt;Clazz&gt;</code> value
     */
    protected QueueMultiProcessor(String inThreadDescriptor,
                                  BlockingQueue<Clazz> inQueue)
    {
        super(inThreadDescriptor,
              inQueue);
    }
    /**
     * Create a new QueueMultiProcessor instance.
     *
     * @param inThreadDescriptor a <code>String</code> value describing the processor
     */
    protected QueueMultiProcessor(String inThreadDescriptor)
    {
        super(inThreadDescriptor);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
     */
    @Override
    protected void processData(Clazz inData)
            throws Exception
    {
        List<Clazz> data = new ArrayList<>();
        data.add(inData);
        getQueue().drainTo(data,
                           getBatchSize());
        processData(data);
    }
    /**
     * Gets the maximum batch size to process at once.
     * 
     * <p>Subclasses may override this value to customize the batch size.
     * The default value is {@link Integer#MAX_VALUE}.
     *
     * @return an <code>int</cod> value
     */
    protected int getBatchSize()
    {
        return Integer.MAX_VALUE;
    }
    /**
     * Processes as much data as is available at any one time.
     *
     * @param inData a <code>List&lt;Clazz&gt;</code> value
     */
    protected abstract void processData(List<Clazz> inData);
}
