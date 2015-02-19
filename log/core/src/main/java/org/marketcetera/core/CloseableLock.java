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
