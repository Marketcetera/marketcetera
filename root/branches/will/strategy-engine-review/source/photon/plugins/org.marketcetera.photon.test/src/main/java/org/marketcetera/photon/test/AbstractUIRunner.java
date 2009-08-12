package org.marketcetera.photon.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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

/* $License$ */

/**
 * Test runner that starts a separate UI thread. The UI thread initializes a
 * {@link Display} and delegates to subclasses to run the event loop.
 * <p>
 * Currently, a new thread is created for each test.
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

    private static volatile UIThread sUIThread;
    private final Set<FrameworkMethod> mUIMethods;

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
                sUIThread = new UIThread();
                sUIThread.start();
                sUIThread.await();
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
                    sUIThread.dispose();
                    sUIThread.join();
                    sUIThread = null;
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
     * Hook for subclasses to run the event loop. This method should not return
     * until the UI thread is done ({@link #shutDownUI()} has been called and/or
     * the display is disposed.
     * 
     * @param display
     *            the display to run the event loop
     * @param ready
     *            indicates that the display is ready for events
     */
    protected abstract void runEventLoop(Display display, CountDownLatch ready);

    /**
     * Subclasses can override to do something before the display is disposed.
     */
    protected void shutDownUI() {
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

        public void rethrow() throws Throwable {
            if (mThrowable != null) {
                throw mThrowable;
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
     * UI thread implementation. Spins an event loop in the default realm and
     * allows Runnables to be executed synchronously.
     */
    private class UIThread extends Thread {

        private final CountDownLatch mReady = new CountDownLatch(1);
        private volatile Display mDisplay;

        public UIThread() {
            super("Test UI Thread");
        }

        @Override
        public void run() {
            mDisplay = new Display();
            runEventLoop(mDisplay, mReady);
        }

        public void await() throws InterruptedException {
            mReady.await();
        }

        public void syncExec(ThrowableRunnable r) throws Throwable {
            await();
            final CaptureRunnable capture = new CaptureRunnable(r);
            mDisplay.syncExec(capture);
            capture.rethrow();
        }

        public void dispose() {
            try {
                mDisplay.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            shutDownUI();
                        } finally {
                            mDisplay.dispose();
                        }
                    }
                });
            } catch (SWTException e) {
                // ignore device disposed since the display is already dead
                if (e.code != SWT.ERROR_DEVICE_DISPOSED) {
                    throw e;
                }
            }
        }
    }

    /**
     * Executes the runnable on the UI thread. This is only valid during tests
     * being run with {@link AbstractUIRunner}.
     * 
     * @param runnable
     *            the runnable to run
     * @throws Throwable
     *             if the runnable throws it
     */
    public static void syncRun(final ThrowableRunnable runnable)
            throws Throwable {
        sUIThread.syncExec(runnable);
    }

}