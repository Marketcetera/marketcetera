package org.marketcetera.core;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.commons.lang.Validate;

/* $License$ */

/**
 * Provides an auto-closeable implementation of {@link ReadWriteLock}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class CloseableLock
        implements Closeable
{
    /**
     * Creates a <code>CloseableLock</code> around the given <code>Lock</code>.
     *
     * @param inLock a <code>Lock</code> object to wrap in a closeable interface
     * @return a <code>CloseableLock</code> value
     */
    public static CloseableLock create(Lock inLock)
    {
        return new CloseableLock(inLock);
    }
    /**
     * Locks the wrapped lock.
     */
    public void lock()
    {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException ignored) {
            // ignore this exception, it almost certainly occurs on shutdown or manual interruption and it's annoying
        }
    }
    /**
     * Tries to lock the wrapped lock only if it is available at the time of the call.
     *
     * @return a <code>boolean</code> value
     */
    public boolean tryLock()
    {
        return lock.tryLock();
    }
    /**
     * Tries to lock the wrapped lock and waits the given amount of time until the lock can be acquired.
     *
     * @param inTimeout a <code>long</code> value
     * @param inTimeUnit a <code>TimeUnit</code> value
     * @return a <code>boolean</code> value
     * @throws InterruptedException if the call is interrupted before acquiring the lock
     */
    public boolean tryLock(long inTimeout,
                           TimeUnit inTimeUnit)
            throws InterruptedException
    {
        return lock.tryLock(inTimeout,
                            inTimeUnit);
    }
    /**
     * Unlocks the wrapped lock.
     */
    public void unlock()
    {
        close();
    }
    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    public void close()
    {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException ignored) {} 
    }
    /**
     * Create a new CloseableLock instance.
     *
     * @param inLock a <code>Lock</code> value
     */
    private CloseableLock(Lock inLock)
    {
        Validate.notNull(inLock);
        lock = inLock;
    }
    /**
     * inner lock value
     */
    private final Lock lock;
}
