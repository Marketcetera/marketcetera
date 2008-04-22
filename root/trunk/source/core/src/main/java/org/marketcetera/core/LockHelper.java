package org.marketcetera.core;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A wrapper around {@link ReentrantReadWriteLock} guaranteeing
 * acquisition and release of the lock.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class LockHelper
{
    /**
     * object which controls the locking behavior
     */
    private final ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
    /**
     * Executes the given {@link Callable} block within the context of a
     * write lock.
     *
     * @param inBlock a <code>Callable&lt;T&gt;</code> value
     * @return a <code>T</code> value
     * @throws Exception if an error occurs during block execution
     */
    public <T> T executeWrite(Callable<T> inBlock)
        throws Exception
    {
        try {
            mLock.writeLock().lock();
            return inBlock.call();
        } finally {
            mLock.writeLock().unlock();
        }
    }
    
    public <T> T executeRead(Callable<T> inBlock)
        throws Exception
    {
        try {
            mLock.readLock().lock();
            return inBlock.call();
        } finally {
            mLock.readLock().unlock();
        }
    }
}