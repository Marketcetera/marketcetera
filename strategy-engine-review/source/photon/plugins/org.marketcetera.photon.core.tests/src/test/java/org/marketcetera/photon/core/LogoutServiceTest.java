package org.marketcetera.photon.core;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.util.except.ExceptUtils;
import org.mockito.InOrder;

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
    public void testAdd() {
        ILogoutService fixture = new LogoutService();
        Runnable runnable1 = mock(Runnable.class);
        Runnable runnable2 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        verify(runnable1).run();
        verify(runnable2).run();
        reset(runnable1, runnable2);
        fixture.logout();
        // should not be called again
        verify(runnable1, never()).run();
        verify(runnable2, never()).run();
    }

    @Test
    public void testRunnableExceptionIgnored() {
        setLevel(LogoutService.class.getName(), Level.WARN);
        ILogoutService fixture = new LogoutService();
        Runnable runnable1 = mock(Runnable.class);
        doThrow(new RuntimeException()).when(runnable1).run();
        Runnable runnable2 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        verify(runnable1).run();
        // runnable2 still called
        verify(runnable2).run();
        assertSingleEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
    }

    @Test
    public void testSubclassInvoked() {
        final Runnable doLogoutTracker = mock(Runnable.class);
        doThrow(new RuntimeException()).when(doLogoutTracker).run();
        ILogoutService fixture = new LogoutService() {
            @Override
            protected void doLogout() {
                doLogoutTracker.run();
            }
        };
        fixture.logout();
        fixture.logout();
        verify(doLogoutTracker, times(2)).run();
    }

    @Test
    public void testSubclassExceptionIgnored() {
        setLevel(LogoutService.class.getName(), Level.WARN);
        final Runnable doLogoutTracker = mock(Runnable.class);
        doThrow(new RuntimeException()).when(doLogoutTracker).run();
        ILogoutService fixture = new LogoutService() {
            @Override
            protected void doLogout() {
                doLogoutTracker.run();
            }
        };
        Runnable runnable1 = mock(Runnable.class);
        Runnable runnable2 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable2);
        fixture.logout();
        assertSingleEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
        // verify runnable1 is 
        InOrder inOrder = inOrder(doLogoutTracker, runnable1, runnable2);
        inOrder.verify(doLogoutTracker).run();
        inOrder.verify(runnable1).run();
        verify(runnable2).run();
        fixture.logout();
        assertSingleEvent(Level.WARN, LogoutService.class.getName(),
                "Unhandled runtime exception caught in LogoutService.",
                ExceptUtils.class.getName());
        // doLogoutTracker called a second time, but the other runnables still
        // only one invocation since they should have been removed
        verify(doLogoutTracker, times(2)).run();
        verify(runnable1).run();
        verify(runnable2).run();
    }

    @Test
    public void testDoLogoutCalledBeforeRunnable() {
        setLevel(LogoutService.class.getName(), Level.WARN);
        final Runnable doLogoutTracker = mock(Runnable.class);
        ILogoutService fixture = new LogoutService() {
            @Override
            protected void doLogout() {
                doLogoutTracker.run();
            }
        };
        Runnable runnable1 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.logout(); 
        InOrder inOrder = inOrder(doLogoutTracker, runnable1);
        inOrder.verify(doLogoutTracker).run();
        inOrder.verify(runnable1).run();
    }
    
    @Test
    public void testRemove() {
        ILogoutService fixture = new LogoutService();
        Runnable runnable1 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.removeLogoutRunnable(runnable1);
        // do it again, make sure its no-op
        fixture.removeLogoutRunnable(runnable1);
        fixture.logout();
        verify(runnable1, never()).run();
    }
    
    @Test
    public void testNullRunnables() throws Exception {
        final ILogoutService fixture = new LogoutService();
        new ExpectedNullArgumentFailure("runnable") {
            @Override
            protected void run() throws Exception {
                fixture.addLogoutRunnable(null);
            }
        };
        new ExpectedNullArgumentFailure("runnable") {
            @Override
            protected void run() throws Exception {
                fixture.removeLogoutRunnable(null);
            }
        };
    }
    
    @Test
    public void testDuplicateAdd() throws Exception {
        ILogoutService fixture = new LogoutService();
        Runnable runnable1 = mock(Runnable.class);
        fixture.addLogoutRunnable(runnable1);
        fixture.addLogoutRunnable(runnable1);
        fixture.logout();
        // one time
        verify(runnable1).run();
    }
}
