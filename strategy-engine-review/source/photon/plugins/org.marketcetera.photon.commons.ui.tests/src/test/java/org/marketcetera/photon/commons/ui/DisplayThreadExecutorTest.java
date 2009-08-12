package org.marketcetera.photon.commons.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.log4j.Level;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.SimpleExecutorService;
import org.marketcetera.photon.commons.SimpleExecutorServiceTestBase;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.SimpleUIRunner;

/* $License$ */

/**
 * Tests {@link DisplayThreadExecutor}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class DisplayThreadExecutorTest extends SimpleExecutorServiceTestBase {

    @Override
    protected ExecutorService createFixture() throws Exception {
        return AbstractUIRunner.syncCall(new Callable<ExecutorService>() {
            @Override
            public ExecutorService call() throws Exception {
                return DisplayThreadExecutor.getInstance(Display.getCurrent());
            }
        });
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
        Display d = new Display();
        final ExecutorService fixture = DisplayThreadExecutor.getInstance(d);
        d.dispose();
        new ExpectedFailure<RejectedExecutionException>("org.eclipse.swt.SWTException: Device is disposed") {
            @Override
            protected void run() throws Exception {
                fixture.submit(mock(Runnable.class));
            }
        };
        setDefaultLevel(Level.ALL);
        fixture.shutdown();
        assertThat(fixture.isShutdown(), is(true));
        assertThat(fixture.isTerminated(), is(false));
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
        new ExpectedFailure<IllegalArgumentException>(
                "'display' must not be null") {
            @Override
            protected void run() throws Exception {
                DisplayThreadExecutor.getInstance(null);
            }
        };
    }

}
