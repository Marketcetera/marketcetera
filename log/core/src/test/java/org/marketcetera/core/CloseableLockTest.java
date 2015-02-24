package org.marketcetera.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link CloseableLock}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class CloseableLockTest
{
    /**
     * Tests {@link CloseableLock#create(java.util.concurrent.locks.Lock)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreate()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                CloseableLock.create(null);
            }
        };
        assertNotNull(CloseableLock.create(new ReentrantReadWriteLock().readLock()));
    }
    /**
     * Tests {@link CloseableLock#lock()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testLock()
            throws Exception
    {
        // try conventional first
        ReadWriteLock myLock = new ReentrantReadWriteLock();
        // can lock/unlock
        Lock writeLock = myLock.writeLock();
        writeLock.lock();
        writeLock.unlock();
        // verify that we can lock the wrapped lock
        try(CloseableLock testLock = CloseableLock.create(myLock.writeLock())) {
            testLock.lock();
        }
        // verify that we can lock the original lock again (wouldn't be possible if the try-with didn't unlock)
        writeLock = myLock.writeLock();
        writeLock.lock();
        writeLock.unlock();
        // repeat test with convention lock/unlock of the wrapped lock
        CloseableLock wrappedLock = CloseableLock.create(myLock.writeLock());
        wrappedLock.lock();
        wrappedLock.unlock();
        // verify that we can lock the original lock again
        writeLock = myLock.writeLock();
        writeLock.lock();
        writeLock.unlock();
    }
    /**
     * Tests {@link CloseableLock#lock()} with contention.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlreadyLocked()
            throws Exception
    {
        final ReadWriteLock myLock = new ReentrantReadWriteLock();
        final AtomicBoolean keepLocked = new AtomicBoolean(true);
        final AtomicBoolean isLocked = new AtomicBoolean(false);
        Runnable locked = new Runnable() {
            @Override
            public void run()
            {
                // make this a read lock for flexibility
                Lock lock = myLock.readLock();
                lock.lock();
                isLocked.set(true);
                synchronized(isLocked) {
                    isLocked.notifyAll();
                }
                while(keepLocked.get()) {
                    try {
                        synchronized(keepLocked) {
                            keepLocked.wait();
                        }
                    } catch (InterruptedException e) {
                        SLF4JLoggerProxy.debug(CloseableLockTest.this,
                                               e);
                    }
                }
                SLF4JLoggerProxy.debug(CloseableLockTest.this,
                                       "Unlocking test lock");
                lock.unlock();
                isLocked.set(false);
                synchronized(isLocked) {
                    isLocked.notifyAll();
                }
            }
        };
        Thread locker = new Thread(locked);
        locker.start();
        while(!isLocked.get()) {
            synchronized(isLocked) {
                isLocked.wait(250);
            }
        }
        // lock is held in the other thread, try to lock with CloseableLock - first with read lock, which should be fine
        assertTrue(isLocked.get());
        try(CloseableLock testLock = CloseableLock.create(myLock.readLock())) {
            testLock.lock();
        }
        // check that the other lock is still locked
        assertTrue(isLocked.get());
        // now, try a write lock (in yet another thread), which should not be possible until we unlock the thread held in the other lock
        final AtomicBoolean otherLockSucceeded = new AtomicBoolean(false);
        Runnable otherLocked = new Runnable() {
            @Override
            public void run()
            {
                try(CloseableLock testLock = CloseableLock.create(myLock.writeLock())) {
                    testLock.lock();
                    SLF4JLoggerProxy.debug(CloseableLockTest.this,
                                           "Successfully locked other lock");
                    otherLockSucceeded.set(true);
                    synchronized(otherLockSucceeded) {
                        otherLockSucceeded.notifyAll();
                    }
                }
            }
        };
        Thread otherLocker = new Thread(otherLocked);
        otherLocker.start();
        // wait a little bit
        Thread.sleep(1000);
        assertFalse(otherLockSucceeded.get());
        // interrupt the other thread
        otherLocker.interrupt();
        otherLocker.join();
        // restart the other locker
        otherLocker = new Thread(otherLocked);
        otherLocker.start();
        Thread.sleep(1000);
        // release the first lock
        keepLocked.set(false);
        synchronized(keepLocked) {
            keepLocked.notifyAll();
        }
        // wait for the second lock to succeed
        while(!otherLockSucceeded.get()) {
            synchronized(otherLockSucceeded) {
                otherLockSucceeded.wait(250);
            }
        }
        assertTrue(otherLockSucceeded.get());
        while(isLocked.get()) {
            synchronized(isLocked) {
                isLocked.wait(250);
            }
        }
        assertFalse(isLocked.get());
        // clean up
        locker.interrupt();
        locker.join();
        otherLocker.interrupt();
        otherLocker.join();
    }
}
