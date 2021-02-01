package org.marketcetera.core.queue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/* $License$ */

/**
 * Provides an asynchronous set of serial processing queues based on some functionally distinct characteristic, like an order number or instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class FunctionalQueueProcessor<MessagePackageClazz extends Serializable,KeyClazz>
        extends QueueProcessor<MessagePackageClazz>
{
    /**
     * Get the distinctive functional characteristic from the given message package.
     *
     * @param inPackage a <code>MessagePackageClazz</code> value
     * @return a <code>KeyClazz</code> value
     */
    protected abstract KeyClazz getKey(MessagePackageClazz inPackage);
    /**
     * Process one message.
     *
     * @param inPackage a <code>MessagePackageClazz</code> value
     */
    protected abstract void processMessage(MessagePackageClazz inPackage);
    /**
     * Provide a human-readable description of the work to be done.
     *
     * <p>Examples include: <code>OrderProcessor</code>, <code>InstrumentProcessor</code>.</p>
     *
     * @return a <code>String</code> value
     */
    protected abstract String getWorkDescriptor();
    /**
     * Get the maximum number of simultaneous tasks to work at once.
     *
     * @return an <code>int</code> value
     */
    protected int getMaxSimultaneousTasks()
    {
        return defaultMaxSimultaneousTasks;
    }
    /**
     * Get the number of milliseconds to wait before checking to see if a sub-queue is available to work.
     *
     * @return a <code>long</code> value
     */
    protected long getExecutionDelay()
    {
        return defaultExecutionDelay;
    }
    /**
     * Get the sub-queue time-to-live value in milliseconds.
     *
     *<p>This value is the number of milliseconds to leave an empty sub-queue alive before retiring it.</p>
     *
     * @return a <code>long</code> value
     */
    protected long getExecutionTtl()
    {
        return defaultExecutionTtl;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
     */
    @Override
    protected void processData(MessagePackageClazz inData)
            throws Exception
    {
        // take the message package and split it into one of the subordinate worker queues
        KeyClazz messageKey = getKey(inData);
        SubQueueProcessor subQueue = null;
        boolean warned = false;
        long delayStarted = 0;
        while(subQueue == null) {
            synchronized(subQueues) {
                subQueue = subQueues.get(messageKey);
                if(subQueue == null) {
                    // no sub queue for this task category yet
                    // TODO size might be expensive?
                    int size = subQueues.size();
                    if(size >= getMaxSimultaneousTasks()) {
                        // already at max sub queues, have to wait for a slot to become available
                        if(!warned) {
                            delayStarted = System.currentTimeMillis();
                            SLF4JLoggerProxy.info(this,
                                                  "Cannot process {} yet because max {} sub queues ({}) in use, consider increasing the value",
                                                  inData,
                                                  getWorkDescriptor(),
                                                  size);
                            warned = true;
                        }
                    } else {
                        subQueue = new SubQueueProcessor(messageKey);
                        subQueue.start();
                        subQueues.put(messageKey,
                                      subQueue);
                    }
                }
            }
            if(subQueue == null) {
                Thread.sleep(getExecutionDelay());
            }
        }
        if(warned) {
            SLF4JLoggerProxy.info(getClass(),
                                  "Resuming work on {} after {}ms delay",
                                  inData,
                                  System.currentTimeMillis() - delayStarted);
        }
        subQueue.add(inData);
    }
    /**
     * Processes one vein of tasks to be processed, e.g., all tasks for a given order or instrument.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    protected class SubQueueProcessor
            extends QueueProcessor<MessagePackageClazz>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(MessagePackageClazz inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#size()
         */
        @Override
        protected int size()
        {
            return super.size();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(MessagePackageClazz inData)
                throws Exception
        {
            try {
                timestamp = System.currentTimeMillis();
                workingOnMessage.set(true);
                processMessage(inData);
            } finally {
                timestamp = System.currentTimeMillis();
                workingOnMessage.set(false);
            }
        }
        /**
         * Create a new SubQueueProcessor instance.
         *
         * @param inMessageKey a <code>KeyClazz</code> value
         */
        private SubQueueProcessor(KeyClazz inMessageKey)
        {
            super(StringUtils.trim(getWorkDescriptor() + "-" + inMessageKey));
            key = inMessageKey;
            timeoutTask = new SubProcessorQueueTimeoutTask(this);
            scheduledService.schedule(timeoutTask,
                                      getExecutionTtl(),
                                      TimeUnit.MILLISECONDS);
            timestamp = System.currentTimeMillis();
        }
        /**
         * indicates if this message is currently being worked on or not
         */
        private final AtomicBoolean workingOnMessage = new AtomicBoolean(false);
        /**
         * holds the last time this sub queue was touched
         */
        private volatile long timestamp;
        /**
         * key of this queue
         */
        private final Object key;
        /**
         * task used to time out this queue
         */
        private final SubProcessorQueueTimeoutTask timeoutTask;
    }
    /**
     * Times out a sub queue if it has been unused for a period of time.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: QuickFIXApplication.java 17799 2018-11-21 15:06:07Z colin $
     * @since $Release$
     */
    private class SubProcessorQueueTimeoutTask
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                long currentTime = System.currentTimeMillis();
                SLF4JLoggerProxy.debug(FunctionalQueueProcessor.this,
                                       "Testing {} for timeout at {}",
                                       queue,
                                       currentTime);
                // TODO how sure are we that a new task couldn't be added? maybe synchronize on queue instead?
                synchronized(subQueues) {
                    long timestamp = queue.timestamp;
                    if(!queue.workingOnMessage.get() && currentTime > timestamp+getExecutionTtl()) {
                        if(queue.size() != 0) {
                            SLF4JLoggerProxy.debug(FunctionalQueueProcessor.this,
                                                   "Not timing out {} because size {} is not zero",
                                                   queue,
                                                   queue.size());
                        } else {
                            SLF4JLoggerProxy.debug(FunctionalQueueProcessor.this,
                                                   "Timing out {} of size {}",
                                                   queue,
                                                   queue.size());
                            subQueues.remove(queue.key);
                            queue.stop();
                            return;
                        }
                    }
                }
                scheduledService.schedule(queue.timeoutTask,
                                          getExecutionTtl(),
                                          TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    SLF4JLoggerProxy.warn(this,
                                          e,
                                          "{} on execution process: {}",
                                          queue.key,
                                          ExceptionUtils.getRootCauseMessage(e));
                } else {
                    SLF4JLoggerProxy.warn(this,
                                          "{} on execution process: {}",
                                          queue.key,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
        /**
         * Create a new SubProcessorQueueTimeoutTask instance.
         *
         * @param inSubQueueProcessor a <code>SubQueueProcessor</code> value
         */
        private SubProcessorQueueTimeoutTask(SubQueueProcessor inSubQueueProcessor)
        {
            queue = inSubQueueProcessor;
        }
        /**
         * queue for processing tasks
         */
        private final SubQueueProcessor queue;
    }
    /**
     * holds sub queues by functional key
     */
    private final Map<KeyClazz,SubQueueProcessor> subQueues = new HashMap<>();
    /**
     * default maximum number of simultaneous tasks to have active at once
     */
    private int defaultMaxSimultaneousTasks = 5;
    /**
     * interval to wait between checks for available sub queues
     */
    private long defaultExecutionDelay = 10;
    /**
     * number of milliseconds to leave a sub queue alive before retiring it
     */
    private long defaultExecutionTtl = 1000;
    /**
     * executes jobs at scheduled times
     */
    private static final ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(1,
                                                                                                      new ThreadFactoryBuilder().setNameFormat(FunctionalQueueProcessor.class.getSimpleName()+"Scheduler").build());
}
