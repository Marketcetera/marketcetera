package org.marketcetera.core.util.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides a framework for processing data in a separate thread.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public abstract class QueueProcessor<Clazz>
        implements Runnable, Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            return;
        }
        try {
            onStart();
        } catch (Exception e) {
            Messages.UNABLE_TO_START.warn(this,
                                          e,
                                          threadDescriptor);
            throw new RuntimeException(e);
        }
        keepAlive.set(true);
        thread = new Thread(this,
                            threadDescriptor);
        thread.start();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            return;
        }
        try {
            onStop();
        } catch (Exception e) {
            Messages.ERROR_DURING_STOP.warn(this,
                                            e,
                                            threadDescriptor);
        }
        keepAlive.set(false);
        if(thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try {
            running.set(true);
            lastException = null;
            interrupted.set(false);
            Messages.STARTED.info(this,
                                  threadDescriptor);
            while(keepAlive.get()) {
                try {
                    Clazz dataObject = queue.take();
                    SLF4JLoggerProxy.trace(this,
                                           "Queue processor {} processing {}",
                                           threadDescriptor,
                                           dataObject);
                    processData(dataObject);
                } catch (InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    lastException = e;
                    if(shutdownOnException(e)) {
                        throw e;
                    }
                    Messages.IGNORING_EXCEPTION.warn(this,
                                                     e,
                                                     threadDescriptor);
                }
            }
        } catch (InterruptedException e) {
            interrupted.set(true);
            Messages.INTERRUPTED.info(this,
                                      threadDescriptor);
        } catch (Exception e) {
            lastException = e;
            Messages.SHUTTING_DOWN_FROM_ERROR.warn(this,
                                                   e,
                                                   threadDescriptor);
            stop();
        } finally {
            keepAlive.set(false);
            running.set(false);
            Messages.STOPPED.info(this,
                                  threadDescriptor);
        }
    }
    /**
     * Processes the given data.
     * 
     * <p>This method is invoked when data is available in the processing queue. Data
     * is guaranteed to be processed in the order mandated by the queue. No other data
     * will be processed until this method returns.
     *
     * @param inData a <code>Clazz</code> value
     * @throws Exception an <code>Exception</code> value
     */
    protected abstract void processData(Clazz inData)
            throws Exception;
    /**
     * Called when the processor starts.
     * 
     * <p>Any exception thrown will prevent the processor from starting.
     *
     * @throws Exception if an error occurs
     */
    protected void onStart()
            throws Exception {}
    /**
     * Called when the processor stops.
     *
     * <p>Any exception thrown will not prevent the processor from stopping.
     *
     * @throws Exception if an error occurs
     */
    protected void onStop()
            throws Exception {}
    /**
     * Gets the last exception thrown by the processor during data processing.
     *
     * @return an <code>Exception</code> value or <code>null</code>
     */
    protected Exception getLastException()
    {
        return lastException;
    }
    /**
     * Indicates if the queue processor was interrupted during data processing.
     *
     * @return a <code>boolean</code> value
     */
    protected boolean wasInterrupted()
    {
        return interrupted.get();
    }
    /**
     * Indicates if the queue processor should shutdown as a result of the given exception.
     *
     * @param inException an <code>Exception</code> value
     * @return a <code>boolean</code> value
     */
    protected abstract boolean shutdownOnException(Exception inException);
    /**
     * Create a new QueueProcessor instance.
     *
     * @param inQueue a <code>BlockingQueue&lt;Clazz&gt;</code> value
     */
    protected QueueProcessor(BlockingQueue<Clazz> inQueue)
    {
        this(inQueue,
             "Unknown Queue Processor");
    }
    /**
     * Create a new QueueProcessor instance.
     *
     * @param inQueue a <code>BlockingQueue&lt;Clazz&gt;</code> value
     * @param inThreadDescriptor a <code>String</code> value describing the processor
     */
    protected QueueProcessor(BlockingQueue<Clazz> inQueue,
                             String inThreadDescriptor)
    {
        if(inQueue == null) {
            throw new NullPointerException();
        }
        if(inThreadDescriptor == null) {
            throw new NullPointerException();
        }
        queue = inQueue;
        threadDescriptor = inThreadDescriptor;
    }
    /**
     * last exception thrown during data processing or <code>null</code>
     */
    private volatile Exception lastException;
    /**
     * thread value used to process data
     */
    private volatile Thread thread;
    /**
     * indicates if the processor was interrupted during data processing
     */
    private final AtomicBoolean interrupted = new AtomicBoolean(false);
    /**
     * indicates if the processor is running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * indicates if the processor should continue to process data
     */
    private final AtomicBoolean keepAlive = new AtomicBoolean(true);
    /**
     * queue used to hold data to be processed
     */
    private final BlockingQueue<Clazz> queue;
    /**
     * describes the thread
     */
    private final String threadDescriptor;
}
