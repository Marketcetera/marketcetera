package org.marketcetera.core;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang.Validate;

/* $License$ */

/**
 * Provides an auto-closeable implementation of {@link ReadWriteLock}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
     *
     * @throws RuntimeException if the lock request was interrupted
     */
    public void lock()
    {
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        lock.unlock();
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
