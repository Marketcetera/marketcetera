package org.marketcetera.photon.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.marketcetera.util.misc.NamedThreadFactory;

/* $License$ */

/**
 * Test runner that starts a separate UI thread. The UI thread initializes a
 * {@link Display} and delegates to subclasses to run the event loop.
 * <p>
 * Currently, a single static thread is shared by all test.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractUIRunner extends BlockJUnit4ClassRunner {

    /**
     * The <code>UI</code> annotation specifies that a test method should be
     * invoked on the UI thread.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target( { ElementType.METHOD })
    public @interface UI {
    }

    private final Set<FrameworkMethod> mUIMethods;
    private final AtomicReference<Throwable> mAsyncThrowable = new AtomicReference<Throwable>();
    public static final ExecutorService sTestUIThreadExecutor;
    private static final Display sDisplay;
    private Future<?> mEventLoopFuture;

    static {
        sTestUIThreadExecutor = Executors
                .newSingleThreadExecutor(new NamedThreadFactory(
                        "Test UI Thread"));
        Display d = null;
        try {
            d = sTestUIThreadExecutor.submit(new Callable<Display>() {
                @Override
                public Display call() throws Exception {
                    return new Display();
                }
            }).get();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            sDisplay = d;
        }
    }

    /**
     * Constructor. Should only be called by the JUnit framework.
     * 
     * @param klass
     *            test class
     * @throws InitializationError
     *             if the test class is malformed.
     */
    public AbstractUIRunner(Class<?> klass) throws InitializationError {
        super(klass);
        mUIMethods = new HashSet<FrameworkMethod>(getTestClass()
                .getAnnotatedMethods(UI.class));
    }

    @Override
    public void run(RunNotifier notifier) {
        RunListener failureSpy = new ScreenshotCaptureListener();
        notifier.addListener(failureSpy);
        try {
            super.run(notifier);
        } finally {
            notifier.removeListener(failureSpy);
        }
    }

    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        final List<FrameworkMethod> beforeClasses = getTestClass()
                .getAnnotatedMethods(BeforeClass.class);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final CountDownLatch ready = new CountDownLatch(1);
                mEventLoopFuture = sTestUIThreadExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        runEventLoop(sDisplay, ready);
                    }
                });
                ready.await();
                runFrameworkMethods(beforeClasses, null, false);
                statement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfterClasses(final Statement statement) {
        final List<FrameworkMethod> afterClasses = getTestClass()
                .getAnnotatedMethods(AfterClass.class);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable throwable = null;
                try {
                    statement.evaluate();
                } catch (Throwable t) {
                    throwable = t;
                }
                try {
                    runFrameworkMethods(afterClasses, null, true);
                } catch (Throwable t) {
                    if (throwable == null) {
                        throwable = t;
                    }
                }
                try {
                    shutDownUI(sDisplay);
                    mEventLoopFuture.get();
                } catch (Throwable t) {
                    if (throwable == null) {
                        throwable = t;
                    }
                }
                if (throwable != null) {
                    throw throwable;
                }
            }
        };
    }

    @Override
    protected Statement withBefores(FrameworkMethod method,
            final Object target, final Statement statement) {
        final List<FrameworkMethod> befores = getTestClass()
                .getAnnotatedMethods(Before.class);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                runFrameworkMethods(befores, target, false);
                statement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfters(FrameworkMethod method, final Object target,
            final Statement statement) {
        final List<FrameworkMethod> afters = getTestClass()
                .getAnnotatedMethods(After.class);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable throwable = null;
                try {
                    statement.evaluate();
                } catch (Throwable t) {
                    throwable = t;
                }
                try {
                    runFrameworkMethods(afters, target, true);
                } catch (Throwable t) {
                    if (throwable == null) {
                        throwable = t;
                    }
                }
                if (throwable != null) {
                    throw throwable;
                }
                throwable = mAsyncThrowable.getAndSet(null);
                if (throwable != null) {
                    throw throwable;
                }
            }
        };
    }

    @Override
    protected Statement methodInvoker(final FrameworkMethod method,
            final Object test) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                runFrameworkMethods(Collections.singletonList(method), test,
                        false);
            }
        };
    }

    /*
     * Runs the given methods in sequence. If they are annotated as UI methods,
     * they are synchronously run on the UI thread.
     */
    private void runFrameworkMethods(List<FrameworkMethod> methods,
            Object target, boolean runAll) throws Throwable {
        Throwable throwable = null;
        for (FrameworkMethod method : methods) {
            try {
                FrameworkMethodRunner runner = new FrameworkMethodRunner(
                        method, target);
                if (mUIMethods.contains(method)) {
                    syncRun(runner);
                } else {
                    method.invokeExplosively(target);
                }
            } catch (Throwable t) {
                if (!runAll) {
                    throw t;
                } else if (throwable == null) {
                    throwable = t;
                }
            }
        }
        if (throwable != null) {
            throw throwable;
        }
    }

    /**
     * Hook for subclasses to run the event loop. This is called at the
     * beginning of the test in the UI thread and must not return until
     * {@link #shutDownUI()} is called.
     * 
     * @param display
     *            the display to run the event loop
     * @param ready
     *            indicates that the display is ready for events
     */
    protected abstract void runEventLoop(Display display, CountDownLatch ready);

    /**
     * Hook for subclasses to stop the event loop.
     * 
     * @param display
     *            the display the event loop is running on
     */
    protected abstract void shutDownUI(Display display);

    /**
     * Subclasses can call to save a throwable called from another thread. The
     * throwable will be rethrown after the current test run (if no other
     * exceptions occurred). Note that only the first throwable captured in this
     * way will be rethrown. Successive calls in the same test will have no
     * effect other than printing the throwable's stack trace.
     * 
     * @param throwable
     *            throwable to rethrow after the test run
     */
    protected final void setAsyncThrowable(Throwable throwable) {
        throwable.printStackTrace();
        mAsyncThrowable.compareAndSet(null, throwable);
    }

    /**
     * A runnable that can throw checked throwables.
     */
    public interface ThrowableRunnable {
        void run() throws Throwable;
    }

    private static class CaptureRunnable implements Runnable {
        private volatile Throwable mThrowable;
        private final ThrowableRunnable mRunnable;

        public CaptureRunnable(ThrowableRunnable runnable) {
            mRunnable = runnable;
        }

        @Override
        public final void run() {
            try {
                mRunnable.run();
            } catch (Throwable t) {
                mThrowable = t;
            }
        }

        public void rethrow() throws Exception {
            if (mThrowable != null) {
                if (mThrowable instanceof Error) {
                    throw (Error) mThrowable;
                } else if (mThrowable instanceof Exception) {
                    throw (Exception) mThrowable;
                } else {
                    throw new RuntimeException(mThrowable);
                }
            }
        }
    }

    /**
     * Runs a FrameworkMethod and captures any Throwables to be rethrown. This
     * is used to run the FrameworkMethod on the UI thread and capture failures
     * on the main JUnit thread.
     */
    private static class FrameworkMethodRunner implements ThrowableRunnable {
        private final FrameworkMethod mMethod;
        private final Object mTarget;

        public FrameworkMethodRunner(FrameworkMethod method, Object target) {
            mMethod = method;
            mTarget = target;
        }

        @Override
        public void run() throws Throwable {
            mMethod.invokeExplosively(mTarget);
        }
    }

    /**
     * Executes the runnable on the UI thread. This is only valid during tests
     * being run with {@link AbstractUIRunner}.
     * 
     * @param runnable
     *            the runnable to run
     * @throws Exception
     *             if the runnable throws it
     */
    public static void syncRun(final ThrowableRunnable runnable)
            throws Exception {
        final CaptureRunnable capture = new CaptureRunnable(runnable);
        sDisplay.syncExec(capture);
        capture.rethrow();
    }

    /**
     * Executes the callable on the UI thread. This is only valid during tests
     * being run with {@link AbstractUIRunner}.
     * 
     * @param callable
     *            the callable to run
     * @return the result of the callable
     * @throws Throwable
     *             if the callable throws it
     */
    public static <T> T syncCall(final Callable<T> callable) throws Exception {
        final AtomicReference<T> ref = new AtomicReference<T>();
        syncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Exception {
                ref.set(callable.call());
            }
        });
        return ref.get();
    }

    private static RuntimeException launderThrowable(Throwable throwable) {
        if (throwable instanceof RuntimeException)
            return (RuntimeException) throwable;
        else if (throwable instanceof Error)
            throw (Error) throwable;
        else
            throw new IllegalStateException("Not unchecked", throwable); //$NON-NLS-1$
    }

}