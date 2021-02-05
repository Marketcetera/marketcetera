package org.marketcetera.core.queue;

import java.io.Serializable;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.core.HasUuid;
import org.marketcetera.event.BaseUuid;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AbstractMessagePackage
        extends BaseUuid
        implements HasUuid, Serializable, MessagePackageCallback
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.queue.MessagePackageCallback#onComplete()
     */
    @Override
    public void onComplete()
    {
        synchronized(complete) {
            complete.set(true);
            complete.notifyAll();
        }
    }
    public boolean waitForComplete(long inTimeout)
            throws InterruptedException, TimeoutException
    {
        synchronized(complete) {
            if(!complete.get()) {
                complete.wait(inTimeout);
            }
        }
        if(!complete.get() ) {
            throw new TimeoutException();
        }
        return exception == null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.queue.MessagePackageCallback#onError(java.lang.Exception)
     */
    @Override
    public void onError(Exception inException)
    {
        exception = inException;
    }
    /**
     * Get the exception value.
     *
     * @return an <code>Exception</code> value
     */
    public Exception getException()
    {
        return exception;
    }
    /**
     * Sets the exception value.
     *
     * @param inException a <code>Exception</code> value
     */
    public void setException(Exception inException)
    {
        exception = inException;
    }
    private final AtomicBoolean complete = new AtomicBoolean(false);
    private Exception exception;
    private static final long serialVersionUID = -1153703931175298017L;
}
