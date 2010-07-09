package org.marketcetera.photon.commons.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Level;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.commons.SimpleExecutorService;
import org.marketcetera.photon.commons.SimpleExecutorServiceTestBase;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.ExpectedFailure;

/* $License$ */

/**
 * Tests {@link DisplayThreadExecutor}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class DisplayThreadExecutorTest extends SimpleExecutorServiceTestBase {

    private volatile Display mDisplay;
    private ExecutorService mHelperExecutor;

    @Before
    public void before() throws Exception {
        mHelperExecutor = Executors.newSingleThreadExecutor();
        final CountDownLatch latch = new CountDownLatch(1);
        mHelperExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDisplay = new Display();
                latch.countDown();
                while (!mDisplay.isDisposed() && !Thread.currentThread().isInterrupted()) {
                    mDisplay.readAndDispatch();
                    Thread.yield();
                }
            }
        });
        latch.await();
    }

    @After
    public void after() {
        mHelperExecutor.shutdownNow();
    }

    @Override
    protected ExecutorService createFixture() throws Exception {
        return DisplayThreadExecutor.getInstance(mDisplay);
    }

    @Test
    public void testExecute() throws Exception {
        new ExpectedFailure<SWTException>("Invalid thread access") {
            @Override
            protected void run() throws Exception {
                new Shell().dispose(); // should throw here
            }
        };
        createFixture().submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                new Shell().dispose(); // should not throw here
                return null;
            }
        });
    }

    @Test
    public void testShutdownWhenDisplayIsDisposed() throws Exception {
        final ExecutorService fixture = createFixture();
        mDisplay.syncExec(new Runnable() { 
            @Override
            public void run() {
                mDisplay.dispose();
            }
        });
        new ExpectedFailure<RejectedExecutionException>(
                "org.eclipse.swt.SWTException: Device is disposed") {
            @Override
            protected void run() throws Exception {
                fixture.submit(mock(Runnable.class));
            }
        };
        setDefaultLevel(Level.ALL);
        fixture.shutdown();
        assertThat(fixture.isShutdown(), is(true));
        assertThat(fixture.isTerminated(), is(false));
        assertThat(fixture.awaitTermination(300, TimeUnit.MILLISECONDS),
                is(false));
        assertSingleEvent(
                Level.ERROR,
                DisplayThreadExecutor.class.getName(),
                "An abnormal shutdown occurred. The executor service will not terminate.",
                SimpleExecutorService.class.getName());
    }

    @Test
    public void testSharedInstance() throws Exception {
        assertThat(createFixture(), sameInstance(createFixture()));
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("display") {
            @Override
            protected void run() throws Exception {
                DisplayThreadExecutor.getInstance(null);
            }
        };
    }

    @Test
    public void testSynchronousExecuteOnDisplayThread() throws Exception {
        final ExecutorService fixture = createFixture();
        mDisplay.syncExec(new Runnable() {
            @Override
            public void run() {
                Runnable mock = mock(Runnable.class);
                fixture.execute(mock);
                verify(mock).run();
            }
        });
    }

}
