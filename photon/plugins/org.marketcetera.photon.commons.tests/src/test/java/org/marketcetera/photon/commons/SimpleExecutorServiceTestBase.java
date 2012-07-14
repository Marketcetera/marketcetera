package org.marketcetera.photon.commons;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.PhotonTestBase;


/* $License$ */

/**
 * Test base for subclasses of {@link SimpleExecutorService}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public abstract class SimpleExecutorServiceTestBase extends PhotonTestBase {

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
        new ExpectedFailure<Exception>("The executor service has been shut down.") {
            @Override
            protected void run() throws Exception {
                ExecutorService fixture = createFixture();
                fixture.shutdown();
                fixture.execute(mock(Runnable.class));
            }
        };
    }
    
    @Test
    public void testSequential() throws Exception {
        Random r = new Random(System.nanoTime());
        ExecutorService fixture = createFixture();
        class TestTask implements Runnable {
            long timeStamp = Long.MIN_VALUE;
            final int mSleepMillis;
            
            public TestTask(int sleepMillis) {
                mSleepMillis = sleepMillis;
            }
            @Override
            public void run() {
                try {
                    Thread.sleep(mSleepMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                timeStamp = System.nanoTime();
            }
        }
        TestTask[] tasks = new TestTask[10];
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = new TestTask(r.nextInt(300));
        }
        for (int i = 0; i < tasks.length; i++) {
            fixture.execute(tasks[i]);
        }
        fixture.shutdown();
        assertThat(fixture.awaitTermination(5, TimeUnit.SECONDS), is(true));
        long currentTime = 0;
        for (int i = 0; i < tasks.length; i++) {
            long taskTime = tasks[i].timeStamp;
            assertThat("task " + i + " completed out of order", taskTime, greaterThan(currentTime));
            currentTime = taskTime;
        }
    }
    
    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }
}
