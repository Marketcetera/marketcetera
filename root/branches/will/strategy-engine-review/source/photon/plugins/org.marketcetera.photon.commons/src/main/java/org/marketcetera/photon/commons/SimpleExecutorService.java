package org.marketcetera.photon.commons;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An abstract ExecutorService that provides simple shutdown and termination
 * semantics, assuming that {@link #doExecute(Runnable)} ensures tasks are run
 * in order. It does not support {@link #shutdownNow()}, which will simply call
 * {@link #shutdown()} and return null.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public abstract class SimpleExecutorService extends AbstractExecutorService {

    private final AtomicBoolean mIsShutdown = new AtomicBoolean();
    private volatile boolean mTerminated;
    private final CountDownLatch mTerminatedLatch = new CountDownLatch(1);

    @Override
    public final boolean awaitTermination(long timeout, TimeUnit unit)
            throws InterruptedException {
        mTerminatedLatch.await(timeout, unit);
        return mTerminated;
    }

    @Override
    public final boolean isShutdown() {
        return mIsShutdown.get();
    }

    @Override
    public final boolean isTerminated() {
        return mTerminated;
    }

    @Override
    public final void shutdown() {
        if (mIsShutdown.compareAndSet(false, true)) {
            synchronized (mIsShutdown) {
                // queue shutdown command
                try {
                    doExecute(new Runnable() {
                        @Override
                        public void run() {
                            mTerminated = true;
                            mTerminatedLatch.countDown();
                        }
                    });
                } catch (Exception e) {
                    Messages.SIMPLE_EXECUTOR_SERVICE_ABNORMAL_SHUTDOWN.error(
                            this, e);
                }
            }
        }
    }

    @Override
    public final List<Runnable> shutdownNow() {
        // unable to determine tasks that haven't executed so just shutdown
        shutdown();
        return null;
    }

    @Override
    public final void execute(Runnable command) {
        if (command == null) {
            // per specification
            throw new NullPointerException("command must not be null"); //$NON-NLS-1$
        }
        synchronized (mIsShutdown) {
            /*
             * The value of the shutdown flag may change before doExecute
             * completes, but the lock ensures that the current command will
             * still be queued before the shutdown command.
             */
            if (mIsShutdown.get()) {
                throw new RejectedExecutionException(
                        Messages.SIMPLE_EXECUTOR_SERVICE_REJECT_SHUTDOWN
                                .getText());
            }
            try {
                doExecute(command);
            } catch (RejectedExecutionException e) {
                // pass through if it is already the declared type
                throw e;
            } catch (Exception e) {
                throw new RejectedExecutionException(e);
            }
        }
    }

    /**
     * Hook for subclasses to execute the given command. Subclasses must ensure
     * that commands are executed in order. After {@link #shutdown()} completes,
     * it is guaranteed that this method will no longer be called.
     * 
     * Implementation note: this method is called while holding a lock that
     * ensures it will not be called concurrently. Subclass implementations
     * should not call any other method on this class.
     * 
     * @param command
     *            the runnable task, guaranteed to be non-null
     * @throws Exception
     *             if this task cannot be accepted for execution
     */
    protected abstract void doExecute(Runnable command) throws Exception;

}