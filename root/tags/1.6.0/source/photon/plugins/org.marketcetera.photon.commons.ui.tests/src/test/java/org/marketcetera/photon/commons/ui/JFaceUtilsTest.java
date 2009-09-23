package org.marketcetera.photon.commons.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Tests {@link JFaceUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class JFaceUtilsTest extends PhotonTestBase {

    private class MockContext implements IRunnableContext, IShellProvider {

        @Override
        public void run(boolean fork, boolean cancelable,
                IRunnableWithProgress runnable)
                throws InvocationTargetException, InterruptedException {
            runnable.run(new NullProgressMonitor());
        }

        @Override
        public Shell getShell() {
            return mShell;
        }

    }

    private Shell mShell;
    private final MockContext mMockContext = new MockContext();

    @Before
    @UI
    public void beforeUI() {
        setLevel(JFaceUtils.class.getName(), Level.ALL);
        mShell = new Shell();
    }

    @After
    @UI
    public void afterUI() {
        mShell.dispose();
    }

    @Test
    @UI
    public void testRunModalSuccess() {
        assertThat(JFaceUtils.runModalWithErrorDialog(mMockContext,
                mMockContext, new IRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException,
                            InterruptedException {
                    }
                }, false, mock(I18NBoundMessage.class)), is(true));
    }

    @Test
    @UI
    public void testRunModalCancel() {
        final InterruptedException toThrow = new InterruptedException();
        final I18NBoundMessage failureMessage = mock(I18NBoundMessage.class);
        assertThat(JFaceUtils.runModalWithErrorDialog(mMockContext,
                mMockContext, new IRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor)
                            throws InvocationTargetException,
                            InterruptedException {
                        throw toThrow;
                    }
                }, false, failureMessage), is(false));
        verify(failureMessage).info(JFaceUtils.class, toThrow);
    }

    @Test
    public void testRunModalError() {
        testRunModalErrorHelper(new Exception("Exception Text"),
                "Exception Text");
    }

    @Test
    public void testRunModalErrorNoMessage() {
        testRunModalErrorHelper(new Exception(),
                "A Java exception occurred during the operation.  See the log for details.");
    }

    private void testRunModalErrorHelper(Exception exception, String text) {
        final AtomicBoolean result = new AtomicBoolean(true);
        final InvocationTargetException toThrow = new InvocationTargetException(
                exception);
        final I18NBoundMessage failureMessage = mock(I18NBoundMessage.class);
        mShell.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                result.set(JFaceUtils.runModalWithErrorDialog(mMockContext,
                        mMockContext, new IRunnableWithProgress() {
                            @Override
                            public void run(IProgressMonitor monitor)
                                    throws InvocationTargetException,
                                    InterruptedException {
                                throw toThrow;
                            }
                        }, false, failureMessage));
            }
        });
        SWTBot bot = new SWTBot();
        bot.shell("Operation Failed");
        bot.label(text);
        bot.button("OK").click();
        assertThat(result.get(), is(false));
    }

    @Test
    @UI
    public void testRunWithErrorDialogSuccess() {
        assertThat(JFaceUtils.runWithErrorDialog(mMockContext,
                new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return true;
                    }
                }, mock(I18NBoundMessage.class)), is(true));
    }

    @Test
    public void testRunWithErrorDialogError() {
        testRunWithErrorDialogErrorHelper(new Exception("Exception Text"),
                "Exception Text");
    }

    @Test
    public void testRunWithErrorDialogErrorNoMessage() {
        testRunWithErrorDialogErrorHelper(new Exception(),
                "A Java exception occurred during the operation.  See the log for details.");
    }

    private void testRunWithErrorDialogErrorHelper(final Exception exception,
            String text) {
        final AtomicBoolean result = new AtomicBoolean(true);
        final I18NBoundMessage failureMessage = mock(I18NBoundMessage.class);
        mShell.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                result.set(JFaceUtils.runWithErrorDialog(mMockContext,
                        new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                throw exception;
                            }
                        }, failureMessage));
            }
        });
        SWTBot bot = new SWTBot();
        bot.shell("Operation Failed");
        bot.label(text);
        bot.button("OK").click();
        assertThat(result.get(), is(false));
    }

    @Test
    public void testValidation() throws Exception {
        final IRunnableWithProgress op = mock(IRunnableWithProgress.class);
        final I18NBoundMessage message = mock(I18NBoundMessage.class);
        new ExpectedNullArgumentFailure("container") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(null, op, false, message);
            }
        };
        new ExpectedNullArgumentFailure("context") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(null, mMockContext, op,
                        false, message);
            }
        };
        new ExpectedNullArgumentFailure("shellProvider") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, null, op,
                        false, message);
            }
        };
        new ExpectedNullArgumentFailure("operation") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext,
                        null, false, message);
            }
        };
        new ExpectedNullArgumentFailure("failureMessage") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext,
                        mock(IRunnableWithProgress.class), false, null);
            }
        };
    }

    @Test
    public void testRunWithErrorDialogValidation() throws Exception {
        new ExpectedNullArgumentFailure("shellProvider") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runWithErrorDialog(null, null, null);
            }
        };
        new ExpectedNullArgumentFailure("operation") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runWithErrorDialog(mMockContext, null, null);
            }
        };
        new ExpectedNullArgumentFailure("failureMessage") {
            @SuppressWarnings("unchecked")
            @Override
            protected void run() throws Exception {
                JFaceUtils.runWithErrorDialog(mMockContext,
                        mock(Callable.class), null);
            }
        };
    }

}
