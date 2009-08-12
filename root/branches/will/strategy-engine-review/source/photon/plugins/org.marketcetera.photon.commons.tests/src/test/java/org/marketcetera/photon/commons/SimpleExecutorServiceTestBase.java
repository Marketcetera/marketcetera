package org.marketcetera.photon.commons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.util.test.TestCaseBase;


/* $License$ */

/**
 * Test base for subclasses of {@link SimpleExecutorService}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class SimpleExecutorServiceTestBase extends TestCaseBase {

    /**
     * Returns the {@link SimpleExecutorService} fixture to test.
     * 
     * @return
     */
    protected abstract ExecutorService createFixture() throws Exception;

    @Test
    public void testTermination() throws Exception {
        ExecutorService fixture = createFixture();
        final CountDownLatch latch = new CountDownLatch(1);
        fixture.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    fail();
                }
            }
        });
        fixture.shutdown();
        assertThat(fixture.isShutdown(), is(true));
        assertThat(fixture.isTerminated(), is(false));
        assertThat(fixture.awaitTermination(300, TimeUnit.MILLISECONDS),
                is(false));
        latch.countDown();
        assertThat(fixture.awaitTermination(1000, TimeUnit.MILLISECONDS),
                is(true));
        assertThat(fixture.isTerminated(), is(true));
    }
    
    @Test
    public void testBaseValidation() throws Exception {
        new ExpectedFailure<Exception>("command must not be null") {
            @Override
            protected void run() throws Exception {
                createFixture().execute(null);
            }
        };
        new ExpectedFailure<Exception>("The executor service has been shutdown.") {
            @Override
            protected void run() throws Exception {
                ExecutorService fixture = createFixture();
                fixture.shutdown();
                fixture.execute(mock(Runnable.class));
            }
        };
    }
}
