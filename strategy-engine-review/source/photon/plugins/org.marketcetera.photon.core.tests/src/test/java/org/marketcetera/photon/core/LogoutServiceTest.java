package org.marketcetera.photon.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.except.ExceptUtils;

/* $License$ */

/**
 * Tests {@link LogoutService}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class LogoutServiceTest extends PhotonTestBase {

    @Test
    public void testLogout() {
        LogoutService fixture = new LogoutService();
        TrackingRunnable runnable1 = new TrackingRunnable();
        TrackingRunnable runnable2 = new TrackingRunnable();
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        assertThat(runnable1.getCalled(), is(1));
        assertThat(runnable2.getCalled(), is(1));
        fixture.logout();
        // should not be called again
        assertThat(runnable1.getCalled(), is(1));
        assertThat(runnable2.getCalled(), is(1));
    }

    @Test
    public void testRunnableExceptionIgnored() {
        setLevel(LogoutService.class.getName(), Level.WARN);
        LogoutService fixture = new LogoutService();
        TrackingRunnable runnable1 = new ThrowingRunnable();
        TrackingRunnable runnable2 = new TrackingRunnable();
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        assertThat(runnable1.getCalled(), is(1));
        // runnable2 still called
        assertThat(runnable2.getCalled(), is(1));
        assertSingleEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
    }

    @Test
    public void testSubclassInvoked() {
        final TrackingRunnable doLogoutTracker = new ThrowingRunnable();
        LogoutService fixture = new LogoutService() {
            @Override
            protected void doLogout() {
                doLogoutTracker.run();
            }
        };
        fixture.logout();
        assertThat(doLogoutTracker.getCalled(), is(1));
        fixture.logout();
        // called again
        assertThat(doLogoutTracker.getCalled(), is(2));
    }

    @Test
    public void testSubclassExceptionIgnored() {
        setLevel(LogoutService.class.getName(), Level.WARN);
        final TrackingRunnable doLogoutTracker = new ThrowingRunnable();
        LogoutService fixture = new LogoutService() {
            @Override
            protected void doLogout() {
                doLogoutTracker.run();
            }
        };
        TrackingRunnable runnable1 = new TrackingRunnable();
        TrackingRunnable runnable2 = new TrackingRunnable();
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        assertThat(doLogoutTracker.getCalled(), is(1));
        assertThat(runnable1.getCalled(), is(1));
        assertThat(runnable2.getCalled(), is(1));
        assertSingleEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
        fixture.logout();
        assertThat(runnable1.getCalled(), is(1));
        assertThat(runnable2.getCalled(), is(1));
        assertThat(doLogoutTracker.getCalled(), is(2));
        assertLastEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
    }

    private class TrackingRunnable implements Runnable {
        AtomicInteger mCalled = new AtomicInteger();

        @Override
        public void run() {
            mCalled.incrementAndGet();
        }

        int getCalled() {
            return mCalled.get();
        }
    }

    private class ThrowingRunnable extends TrackingRunnable {
        @Override
        public void run() {
            super.run();
            throw new RuntimeException();
        }
    }
}
