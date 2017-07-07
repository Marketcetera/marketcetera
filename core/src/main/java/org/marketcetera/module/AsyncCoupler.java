package org.marketcetera.module;

import org.marketcetera.core.QueueProcessor;

/* $License$ */

/**
 * Manages asynchronous data flow coupling.
 * 
 * <p>Note that this class lacks a needed policy for thread management. Right now, a new thread is allocated for
 * each data flow. It is probably better to have a maximum number of threads set aside for async data flows. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AsyncCoupler
        extends AbstractDataCoupler
{
    /**
     * Create a new AsyncCoupler instance.
     *
     * @param inManager a <code>ModuleManager</code> value
     * @param inEmitter a <code>Module</code> value
     * @param inReceiver a <code>Module</code> value
     * @param inFlowID a <code>DataFlowID</code> value
     * @param inExceptionHandler a <code>DataFlowExceptionHandler</code> value
     */
    AsyncCoupler(ModuleManager inManager,
                 Module inEmitter,
                 Module inReceiver,
                 DataFlowID inFlowID,
                 DataFlowExceptionHandler inExceptionHandler)
    {
        super(inManager,
              inEmitter,
              inReceiver,
              inFlowID,
              inExceptionHandler);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataCoupler#process(java.lang.Object)
     */
    @Override
    protected void process(Object inData)
    {
        processor.add(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataCoupler#preInitiate()
     */
    @Override
    protected void preInitiate()
    {
        processor = new Processor();
        processor.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.AbstractDataCoupler#onCancel()
     */
    @Override
    protected void postCancel()
    {
        if(processor != null) {
            try {
                processor.stop();
            } catch (Exception ignored) {}
            processor = null;
        }
    }
    /**
     * Processes incoming data.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class Processor
            extends QueueProcessor<Object>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(Object inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(Object inData)
                throws Exception
        {
            AsyncCoupler.this.receive(inData);
        }
    }
    /**
     * processes async data objects
     */
    private Processor processor;
}
