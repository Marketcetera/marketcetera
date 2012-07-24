/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.util.concurrent;

import java.io.ObjectStreamException;
import java.util.HashMap;

import ca.odell.glazedlists.impl.SerializedReadWriteLock;

/**
 * An implementation of {@link ca.odell.glazedlists.util.concurrent.LockFactory} that has been derived from
 * <a href="http://dcl.mathcs.emory.edu/util/backport-util-concurrent/">backport-util-concurrent</a>.
 *
 * <p>An implementation of {@link ca.odell.glazedlists.util.concurrent.ReadWriteLock} supporting similar
 * semantics to {@link java.util.concurrent.locks.ReentrantLock}.
 * <p>This class has the following properties:
 *
 * <ul>
 * <li><b>Acquisition order</b>
 *
 * <p>The order of entry
 * to the read and write lock is unspecified, subject to reentrancy
 * constraints.  A nonfair lock that is continously contended may
 * indefinitely postpone one or more reader or writer threads, but
 * will normally have higher throughput than a fair lock.
 * <p>
 *
 * DEPARTURE FROM java.util.concurrent: this implementation impose
 * a writer-preferrence and thus its acquisition order may be different
 * than in java.util.concurrent.
 *
 * <li><b>Reentrancy</b>
 *
 * <p>This lock allows both readers and writers to reacquire read or
 * write locks in the style of a {@code ReentrantLock}. Non-reentrant
 * readers are not allowed until all write locks held by the writing
 * thread have been released.
 *
 * <p>Additionally, a writer can acquire the read lock, but not
 * vice-versa.  Among other applications, reentrancy can be useful
 * when write locks are held during calls or callbacks to methods that
 * perform reads under read locks.  If a reader tries to acquire the
 * write lock it will never succeed.
 *
 * <li><b>Lock downgrading</b>
 * <p>Reentrancy also allows downgrading from the write lock to a read lock,
 * by acquiring the write lock, then the read lock and then releasing the
 * write lock. However, upgrading from a read lock to the write lock is
 * <b>not</b> possible.
 *
 * <li><b>Interruption of lock acquisition</b>
 * <p>The read lock and write lock both support interruption during lock
 * acquisition.
 *
 * <li><b>{@code Condition} support</b>
 * <p>The write lock provides a {@code Condition} implementation that
 * behaves in the same way, with respect to the write lock, as the
 * {@code Condition} implementation provided by
 * {@code ReentrantLock#newCondition} does for {@code ReentrantLock}.
 * This {@code Condition} can, of course, only be used with the write lock.
 *
 * <p>The read lock does not support a {@code Condition} and
 * {@code readLock().newCondition()} throws
 * {@code UnsupportedOperationException}.
 *
 * <li><b>Instrumentation</b>
 * <p>This class supports methods to determine whether locks
 * are held or contended. These methods are designed for monitoring
 * system state, not for synchronization control.
 * </ul>
 *
 * <p>Serialization of this class behaves in the same way as built-in
 * locks: a deserialized lock is in the unlocked state, regardless of
 * its state when serialized.
 *
 * <p><b>Sample usages</b>. Here is a code sketch showing how to exploit
 * reentrancy to perform lock downgrading after updating a cache (exception
 * handling is elided for simplicity):
 * <pre>
 * class CachedData {
 *   Object data;
 *   volatile boolean cacheValid;
 *   ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *
 *   void processCachedData() {
 *     rwl.readLock().lock();
 *     if (!cacheValid) {
 *        // Must release read lock before acquiring write lock
 *        rwl.readLock().unlock();
 *        rwl.writeLock().lock();
 *        // Recheck state because another thread might have acquired
 *        //   write lock and changed state before we did.
 *        if (!cacheValid) {
 *          data = ...
 *          cacheValid = true;
 *        }
 *        // Downgrade by acquiring read lock before releasing write lock
 *        rwl.readLock().lock();
 *        rwl.writeLock().unlock(); // Unlock write, still hold read
 *     }
 *
 *     use(data);
 *     rwl.readLock().unlock();
 *   }
 * }
 * </pre>
 *
 * ReentrantReadWriteLocks can be used to improve concurrency in some
 * uses of some kinds of Collections. This is typically worthwhile
 * only when the collections are expected to be large, accessed by
 * more reader threads than writer threads, and entail operations with
 * overhead that outweighs synchronization overhead. For example, here
 * is a class using a TreeMap that is expected to be large and
 * concurrently accessed.
 *
 * <pre>{@code
 * class RWDictionary {
 *    private final Map<String, Data> m = new TreeMap<String, Data>();
 *    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
 *    private final Lock r = rwl.readLock();
 *    private final Lock w = rwl.writeLock();
 *
 *    public Data get(String key) {
 *        r.lock();
 *        try { return m.get(key); }
 *        finally { r.unlock(); }
 *    }
 *    public String[] allKeys() {
 *        r.lock();
 *        try { return m.keySet().toArray(); }
 *        finally { r.unlock(); }
 *    }
 *    public Data put(String key, Data value) {
 *        w.lock();
 *        try { return m.put(key, value); }
 *        finally { w.unlock(); }
 *    }
 *    public void clear() {
 *        w.lock();
 *        try { m.clear(); }
 *        finally { w.unlock(); }
 *    }
 * }}</pre>
 *
 * <h3>Implementation Notes</h3>
 *
 * <p>This lock supports a maximum of 65535 recursive write locks
 * and 65535 read locks. Attempts to exceed these limits result in
 * {@link Error} throws from locking methods.
 *
 * @since 1.5
 * @author Doug Lea
 */
public class J2SE14ReadWriteLock implements ReadWriteLock, java.io.Serializable  {
    private static final long serialVersionUID = -3463448656717690166L;

    final ReadLock readerLock_ = new ReadLock(this);
    final WriteLock writerLock_ = new WriteLock(this);

    final Sync sync;

    /**
     * Creates a new {@code ReentrantReadWriteLock} with
     * default (nonfair) ordering properties.
     */
    public J2SE14ReadWriteLock() {
        this.sync = new NonfairSync();
    }

    public Lock writeLock() { return writerLock_; }
    public Lock readLock()  { return readerLock_; }

    /** Use a {@link ca.odell.glazedlists.impl.SerializedReadWriteLock} as placeholder in the serialization stream. */
    private Object writeReplace() throws ObjectStreamException {
        return new SerializedReadWriteLock();
    }

    /**
     * Synchronization implementation for ReentrantReadWriteLock.
     * Subclassed into fair and nonfair versions.
     */
    private abstract static class Sync implements java.io.Serializable {

        private static final int NONE   = 0;
        private static final int READER = 1;
        private static final int WRITER = 2;

        transient int activeReaders_ = 0;
        transient Thread activeWriter_ = null;
        transient int waitingReaders_ = 0;
        transient int waitingWriters_ = 0;

        /** Number of acquires on write lock by activeWriter_ thread **/
        transient int writeHolds_ = 0;

        /** Number of acquires on read lock by any reader thread **/
        transient HashMap readers_ = new HashMap();

        /** cache/reuse the special Integer value one to speed up readlocks **/
        static final Integer IONE = new Integer(1);

        Sync() {}

        /*
           Each of these variants is needed to maintain atomicity
           of wait counts during wait loops. They could be
           made faster by manually inlining each other. We hope that
           compilers do this for us though.
        */

        synchronized boolean startReadFromNewReader() {
            boolean pass = startRead();
            if (!pass) ++waitingReaders_;
            return pass;
        }

        synchronized boolean startWriteFromNewWriter() {
            boolean pass = startWrite();
            if (!pass) ++waitingWriters_;
            return pass;
        }

        synchronized boolean startReadFromWaitingReader() {
            boolean pass = startRead();
            if (pass) --waitingReaders_;
            return pass;
        }

        synchronized boolean startWriteFromWaitingWriter() {
            boolean pass = startWrite();
            if (pass) --waitingWriters_;
            return pass;
        }

        /*
           A bunch of small synchronized methods are needed
           to allow communication from the Lock objects
           back to this object, that serves as controller
         */

        synchronized void cancelledWaitingReader() { --waitingReaders_; }
        synchronized void cancelledWaitingWriter() { --waitingWriters_; }

        boolean allowReader() {
            return (activeWriter_ == null && waitingWriters_ == 0) ||
                activeWriter_ == Thread.currentThread();
        }

        synchronized boolean startRead() {
            Thread t = Thread.currentThread();
            Integer c = (Integer) readers_.get(t);
            if (c != null) { // already held -- just increment hold count
                readers_.put(t, new Integer(c.intValue() + 1));
                ++activeReaders_;
                return true;
            }
            else if (allowReader()) {
                readers_.put(t, IONE);
                ++activeReaders_;
                return true;
            }
            else
                return false;
        }

        synchronized boolean startWrite() {
            if (activeWriter_ == Thread.currentThread()) { // already held; re-acquire
                ++writeHolds_;
                return true;
            }
            else if (writeHolds_ == 0) {
                if (activeReaders_ == 0 ||
                    (readers_.size() == 1 &&
                     readers_.get(Thread.currentThread()) != null)) {
                    activeWriter_ = Thread.currentThread();
                    writeHolds_ = 1;
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }

        synchronized int endRead() {
            Thread t = Thread.currentThread();
            Integer c = (Integer) readers_.get(t);
            if (c == null)
                throw new IllegalMonitorStateException();
            --activeReaders_;
            if (c != IONE) { // more than one hold; decrement count
                int h = c.intValue() - 1;
                Integer ih = (h == 1) ? IONE : new Integer(h);
                readers_.put(t, ih);
                return NONE;
            }
            else {
                readers_.remove(t);

                if (writeHolds_ > 0) // a write lock is still held by current thread
                    return NONE;
                else if (activeReaders_ == 0 && waitingWriters_ > 0)
                    return WRITER;
                else
                    return NONE;
            }
        }

        synchronized int endWrite() {
            if (activeWriter_ != Thread.currentThread()) {
                throw new IllegalMonitorStateException();
            }
            --writeHolds_;
            if (writeHolds_ > 0) // still being held
                return NONE;
            else {
                activeWriter_ = null;
                if (waitingReaders_ > 0 && allowReader())
                    return READER;
                else if (waitingWriters_ > 0)
                    return WRITER;
                else
                    return NONE;
            }
        }

        synchronized Thread getOwner() {
            return activeWriter_;
        }

        synchronized int getReadLockCount() {
            return activeReaders_;
        }

        synchronized boolean isWriteLocked() {
            return activeWriter_ != null;
        }

        synchronized boolean isWriteLockedByCurrentThread() {
            return activeWriter_ == Thread.currentThread();
        }

        synchronized int getWriteHoldCount() {
            return isWriteLockedByCurrentThread() ? writeHolds_ : 0;
        }

        synchronized int getReadHoldCount() {
            if (activeReaders_ == 0) return 0;
            Thread t = Thread.currentThread();
            Integer i = (Integer)readers_.get(t);
            return (i == null) ? 0 : i.intValue();
        }

        final synchronized boolean hasQueuedThreads() {
            return waitingWriters_ > 0 || waitingReaders_ > 0;
        }

        final synchronized int getQueueLength() {
            return waitingWriters_ + waitingReaders_;
        }

        private void readObject(java.io.ObjectInputStream in)
                throws java.io.IOException, ClassNotFoundException {
            in.defaultReadObject();
            // readers_ is transient, need to reinitialize. Let's flush the memory
            // and ensure visibility by synchronizing (all other accesses to
            // readers_ are also synchronized on "this")
            synchronized (this) {
                readers_ = new HashMap();
            }
        }
    }

    /**
     * Nonfair version of Sync
     */
    private static class NonfairSync extends Sync {
        NonfairSync() {}
    }

    /**
     * The lock returned by method {@link J2SE14ReadWriteLock#readLock}.
     */
    public static class ReadLock implements Lock, java.io.Serializable {

        private static final long serialVersionUID = -5992448646407690164L;

        final J2SE14ReadWriteLock lock;

        /**
         * Constructor for use by subclasses
         *
         * @param lock the outer lock object
         * @throws NullPointerException if the lock is null
         */
        protected ReadLock(J2SE14ReadWriteLock lock) {
            if (lock == null) throw new NullPointerException();
            this.lock = lock;
        }

        /**
         * Acquires the read lock.
         *
         * <p>Acquires the read lock if the write lock is not held by
         * another thread and returns immediately.
         *
         * <p>If the write lock is held by another thread then
         * the current thread becomes disabled for thread scheduling
         * purposes and lies dormant until the read lock has been acquired.
         */
        public void lock() {
            synchronized (this) {
                if (lock.sync.startReadFromNewReader()) return;
                boolean wasInterrupted = Thread.interrupted();
                try {
                    while (true) {
                        try {
                            ReadLock.this.wait();
                        }
                        catch (InterruptedException ex) {
                            wasInterrupted = true;
                            // no need to propagate the potentially masked
                            // signal, since readers are always notified all
                        }
                        if (lock.sync.startReadFromWaitingReader()) return;
                    }
                }
                finally {
                    if (wasInterrupted) Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * Acquires the read lock unless the current thread is
         * {@linkplain Thread#interrupt interrupted}.
         *
         * <p>Acquires the read lock if the write lock is not held
         * by another thread and returns immediately.
         *
         * <p>If the write lock is held by another thread then the
         * current thread becomes disabled for thread scheduling
         * purposes and lies dormant until one of two things happens:
         *
         * <ul>
         *
         * <li>The read lock is acquired by the current thread; or
         *
         * <li>Some other thread {@linkplain Thread#interrupt interrupts}
         * the current thread.
         *
         * </ul>
         *
         * <p>If the current thread:
         *
         * <ul>
         *
         * <li>has its interrupted status set on entry to this method; or
         *
         * <li>is {@linkplain Thread#interrupt interrupted} while
         * acquiring the read lock,
         *
         * </ul>
         *
         * then {@link InterruptedException} is thrown and the current
         * thread's interrupted status is cleared.
         *
         * <p>In this implementation, as this method is an explicit
         * interruption point, preference is given to responding to
         * the interrupt over normal or reentrant acquisition of the
         * lock.
         *
         * @throws InterruptedException if the current thread is interrupted
         */
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) throw new InterruptedException();
            InterruptedException ie = null;
            synchronized (this) {
                if (!lock.sync.startReadFromNewReader()) {
                    for (; ; ) {
                        try {
                            ReadLock.this.wait();
                            if (lock.sync.startReadFromWaitingReader())
                                return;
                        }
                        catch (InterruptedException ex) {
                            lock.sync.cancelledWaitingReader();
                            ie = ex;
                            break;
                        }
                    }
                }
            }
            if (ie != null) {
                // fall through outside synch on interrupt.
                // This notification is not really needed here,
                //   but may be in plausible subclasses
                lock.writerLock_.signalWaiters();
                throw ie;
            }
        }

        /**
         * Acquires the read lock only if the write lock is not held by
         * another thread at the time of invocation.
         *
         * <p>Acquires the read lock if the write lock is not held by
         * another thread and returns immediately with the value
         * {@code true}. Even when this lock has been set to use a
         * fair ordering policy, a call to {@code tryLock()}
         * <em>will</em> immediately acquire the read lock if it is
         * available, whether or not other threads are currently
         * waiting for the read lock.  This &quot;barging&quot; behavior
         * can be useful in certain circumstances, even though it
         * breaks fairness. If you want to honor the fairness setting
         * for this lock, then use {@link #tryLock
         * tryLock(0, TimeUnit.SECONDS) } which is almost equivalent
         * (it also detects interruption).
         *
         * <p>If the write lock is held by another thread then
         * this method will return immediately with the value
         * {@code false}.
         *
         * @return {@code true} if the read lock was acquired
         */
        public boolean tryLock() {
            return lock.sync.startRead();
        }

        /**
         * Attempts to release this lock.
         *
         * <p> If the number of readers is now zero then the lock
         * is made available for write lock attempts.
         */
        public void unlock() {
            switch (lock.sync.endRead()) {
                case Sync.NONE: return;
                case Sync.READER: lock.readerLock_.signalWaiters(); return;
                case Sync.WRITER: lock.writerLock_.signalWaiters(); return;
            }
        }

        synchronized void signalWaiters() {
            notifyAll();
        }

        /**
         * Returns a string identifying this lock, as well as its lock state.
         * The state, in brackets, includes the String {@code "Read locks ="}
         * followed by the number of held read locks.
         *
         * @return a string identifying this lock, as well as its lock state
         */
        public String toString() {
            int r = lock.getReadLockCount();
            return super.toString() +
                "[Read locks = " + r + "]";
        }

    }

    /**
     * The lock returned by method {@link J2SE14ReadWriteLock#writeLock}.
     */
    public static class WriteLock implements Lock, java.io.Serializable {

        private static final long serialVersionUID = -4992448646407690164L;
        final J2SE14ReadWriteLock lock;

        /**
         * Constructor for use by subclasses
         *
         * @param lock the outer lock object
         * @throws NullPointerException if the lock is null
         */
        protected WriteLock(J2SE14ReadWriteLock lock) {
            if (lock == null) throw new NullPointerException();
            this.lock = lock;
        }

        /**
         * Acquires the write lock.
         *
         * <p>Acquires the write lock if neither the read nor write lock
         * are held by another thread
         * and returns immediately, setting the write lock hold count to
         * one.
         *
         * <p>If the current thread already holds the write lock then the
         * hold count is incremented by one and the method returns
         * immediately.
         *
         * <p>If the lock is held by another thread then the current
         * thread becomes disabled for thread scheduling purposes and
         * lies dormant until the write lock has been acquired, at which
         * time the write lock hold count is set to one.
         */
        public void lock() {
            synchronized (this) {
                if (lock.sync.startWriteFromNewWriter()) return;
                boolean wasInterrupted = Thread.interrupted();
                try {
                    while (true) {
                        try {
                            WriteLock.this.wait();
                        }
                        catch (InterruptedException ex) {
                            wasInterrupted = true;
                            // no need to notify; if we were notified,
                            // we will act as notified, and succeed in
                            // startWrite and return
                        }
                        if (lock.sync.startWriteFromWaitingWriter()) return;
                    }
                }
                finally {
                    if (wasInterrupted) Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * Acquires the write lock unless the current thread is
         * {@linkplain Thread#interrupt interrupted}.
         *
         * <p>Acquires the write lock if neither the read nor write lock
         * are held by another thread
         * and returns immediately, setting the write lock hold count to
         * one.
         *
         * <p>If the current thread already holds this lock then the
         * hold count is incremented by one and the method returns
         * immediately.
         *
         * <p>If the lock is held by another thread then the current
         * thread becomes disabled for thread scheduling purposes and
         * lies dormant until one of two things happens:
         *
         * <ul>
         *
         * <li>The write lock is acquired by the current thread; or
         *
         * <li>Some other thread {@linkplain Thread#interrupt interrupts}
         * the current thread.
         *
         * </ul>
         *
         * <p>If the write lock is acquired by the current thread then the
         * lock hold count is set to one.
         *
         * <p>If the current thread:
         *
         * <ul>
         *
         * <li>has its interrupted status set on entry to this method;
         * or
         *
         * <li>is {@linkplain Thread#interrupt interrupted} while
         * acquiring the write lock,
         *
         * </ul>
         *
         * then {@link InterruptedException} is thrown and the current
         * thread's interrupted status is cleared.
         *
         * <p>In this implementation, as this method is an explicit
         * interruption point, preference is given to responding to
         * the interrupt over normal or reentrant acquisition of the
         * lock.
         *
         * @throws InterruptedException if the current thread is interrupted
         */
        public void lockInterruptibly() throws InterruptedException {
            if (Thread.interrupted()) throw new InterruptedException();
            InterruptedException ie = null;
            synchronized (this) {
                if (!lock.sync.startWriteFromNewWriter()) {
                    for (; ; ) {
                        try {
                            WriteLock.this.wait();
                            if (lock.sync.startWriteFromWaitingWriter())
                                return;
                        }
                        catch (InterruptedException ex) {
                            lock.sync.cancelledWaitingWriter();
                            WriteLock.this.notify();
                            ie = ex;
                            break;
                        }
                    }
                }
            }
            if (ie != null) {
                // Fall through outside synch on interrupt.
                //  On exception, we may need to signal readers.
                //  It is not worth checking here whether it is strictly necessary.
                lock.readerLock_.signalWaiters();
                throw ie;
            }
        }

        /**
         * Acquires the write lock only if it is not held by another thread
         * at the time of invocation.
         *
         * <p>Acquires the write lock if neither the read nor write lock
         * are held by another thread
         * and returns immediately with the value {@code true},
         * setting the write lock hold count to one. Even when this lock has
         * been set to use a fair ordering policy, a call to
         * {@code tryLock()} <em>will</em> immediately acquire the
         * lock if it is available, whether or not other threads are
         * currently waiting for the write lock.  This &quot;barging&quot;
         * behavior can be useful in certain circumstances, even
         * though it breaks fairness.
         *
         * <p> If the current thread already holds this lock then the
         * hold count is incremented by one and the method returns
         * {@code true}.
         *
         * <p>If the lock is held by another thread then this method
         * will return immediately with the value {@code false}.
         *
         * @return {@code true} if the lock was free and was acquired
         * by the current thread, or the write lock was already held
         * by the current thread; and {@code false} otherwise.
         */
        public boolean tryLock() {
            return lock.sync.startWrite();
        }

        /**
         * Attempts to release this lock.
         *
         * <p>If the current thread is the holder of this lock then
         * the hold count is decremented. If the hold count is now
         * zero then the lock is released.  If the current thread is
         * not the holder of this lock then {@link
         * IllegalMonitorStateException} is thrown.
         *
         * @throws IllegalMonitorStateException if the current thread does not
         * hold this lock.
         */
        public void unlock() {
            switch (lock.sync.endWrite()) {
                case Sync.NONE: return;
                case Sync.READER: lock.readerLock_.signalWaiters(); return;
                case Sync.WRITER: lock.writerLock_.signalWaiters(); return;
            }
        }

        synchronized void signalWaiters() {
            notify();
        }

        /**
         * Returns a string identifying this lock, as well as its lock
         * state.  The state, in brackets includes either the String
         * {@code "Unlocked"} or the String {@code "Locked by"}
         * followed by the {@linkplain Thread#getName name} of the owning thread.
         *
         * @return a string identifying this lock, as well as its lock state
         */
        public String toString() {
            Thread o = lock.getOwner();
            return super.toString() + ((o == null) ?
                                       "[Unlocked]" :
                                       "[Locked by thread " + o.getName() + "]");
        }

        /**
         * Queries if this write lock is held by the current thread.
         * Identical in effect to {@link
         * java.util.concurrent.locks.ReentrantReadWriteLock#isWriteLockedByCurrentThread}.
         *
         * @return {@code true} if the current thread holds this lock and
         *	   {@code false} otherwise
         * @since 1.6
         */
        public boolean isHeldByCurrentThread() {
            return lock.sync.isWriteLockedByCurrentThread();
        }

        /**
         * Queries the number of holds on this write lock by the current
         * thread.  A thread has a hold on a lock for each lock action
         * that is not matched by an unlock action.  Identical in effect
         * to {@code ReentrantReadWriteLock#getWriteHoldCount}.
         *
         * @return the number of holds on this lock by the current thread,
         *	   or zero if this lock is not held by the current thread
         * @since 1.6
         */
        public int getHoldCount() {
            return lock.sync.getWriteHoldCount();
        }

    }

    // Instrumentation and status

    /**
     * Returns {@code true} if this lock has fairness set true.
     *
     * @return {@code true} if this lock has fairness set true
     */
    public final boolean isFair() {
        return false;
    }

    /**
     * Returns the thread that currently owns the write lock, or
     * {@code null} if not owned. When this method is called by a
     * thread that is not the owner, the return value reflects a
     * best-effort approximation of current lock status. For example,
     * the owner may be momentarily {@code null} even if there are
     * threads trying to acquire the lock but have not yet done so.
     * This method is designed to facilitate construction of
     * subclasses that provide more extensive lock monitoring
     * facilities.
     *
     * @return the owner, or {@code null} if not owned
     */
    protected Thread getOwner() {
        return sync.getOwner();
    }

    /**
     * Queries the number of read locks held for this lock. This
     * method is designed for use in monitoring system state, not for
     * synchronization control.
     * @return the number of read locks held.
     */
    public int getReadLockCount() {
        return sync.getReadLockCount();
    }

    /**
     * Queries if the write lock is held by any thread. This method is
     * designed for use in monitoring system state, not for
     * synchronization control.
     *
     * @return {@code true} if any thread holds the write lock and
     *         {@code false} otherwise
     */
    public boolean isWriteLocked() {
        return sync.isWriteLocked();
    }

    /**
     * Queries if the write lock is held by the current thread.
     *
     * @return {@code true} if the current thread holds the write lock and
     *         {@code false} otherwise
     */
    public boolean isWriteLockedByCurrentThread() {
        return sync.isWriteLockedByCurrentThread();
    }

    /**
     * Queries the number of reentrant write holds on this lock by the
     * current thread.  A writer thread has a hold on a lock for
     * each lock action that is not matched by an unlock action.
     *
     * @return the number of holds on the write lock by the current thread,
     *         or zero if the write lock is not held by the current thread
     */
    public int getWriteHoldCount() {
        return sync.getWriteHoldCount();
    }

    /**
     * Queries the number of reentrant read holds on this lock by the
     * current thread.  A reader thread has a hold on a lock for
     * each lock action that is not matched by an unlock action.
     *
     * @return the number of holds on the read lock by the current thread,
     *         or zero if the read lock is not held by the current thread
     * @since 1.6
     */
    public int getReadHoldCount() {
        return sync.getReadHoldCount();
    }

    /**
     * Queries whether any threads are waiting to acquire the read or
     * write lock. Note that because cancellations may occur at any
     * time, a {@code true} return does not guarantee that any other
     * thread will ever acquire a lock.  This method is designed
     * primarily for use in monitoring of the system state.
     *
     * @return {@code true} if there may be other threads waiting to
     *         acquire the lock
     */
    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    /**
     * Returns an estimate of the number of threads waiting to acquire
     * either the read or write lock.  The value is only an estimate
     * because the number of threads may change dynamically while this
     * method traverses internal data structures.  This method is
     * designed for use in monitoring of the system state, not for
     * synchronization control.
     *
     * @return the estimated number of threads waiting for this lock
     */
    public final int getQueueLength() {
        return sync.getQueueLength();
    }


    /**
     * Returns a string identifying this lock, as well as its lock state.
     * The state, in brackets, includes the String {@code "Write locks ="}
     * followed by the number of reentrantly held write locks, and the
     * String {@code "Read locks ="} followed by the number of held
     * read locks.
     *
     * @return a string identifying this lock, as well as its lock state
     */
    public String toString() {
        return super.toString() +
            "[Write locks = " + getWriteHoldCount() +
            ", Read locks = " + getReadLockCount() + "]";
    }
}