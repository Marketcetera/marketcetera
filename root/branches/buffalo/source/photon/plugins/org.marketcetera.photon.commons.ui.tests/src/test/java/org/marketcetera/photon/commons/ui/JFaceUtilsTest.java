package org.marketcetera.photon.commons.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
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
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ui.JFaceUtils.IUnsafeRunnableWithProgress;
import org.marketcetera.photon.test.ExpectedFailure;
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
 * @since 2.0.0
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
        testRunModalErrorHelper(
                new Exception(),
                "A problem occurred during the operation (Exception).  See the log for details.");
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
        ErrorDialogFixture fixture = new ErrorDialogFixture();
        fixture.assertError(text);
        fixture.dismiss();
        assertThat(result.get(), is(false));
        verify(failureMessage).error(JFaceUtils.class, exception);
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
        testRunWithErrorDialogErrorHelper(
                new Exception(),
                "A problem occurred during the operation (Exception).  See the log for details.");
    }

    @Test
    public void testRunWithErrorDialogExceptionChain() {
        Exception exception = new Exception("ABC", new Exception("XYZ"));
        testRunWithErrorDialogErrorHelper(exception, "ABC", "XYZ");
    }

    @Test
    public void testRunWithErrorDialogRootCause() {
        /*
         * Used initCause instead of Exception(Throwable) because the latter
         * makes a message from Throwable#toString.
         */
        Exception nested = new Exception();
        nested.initCause(new Exception("ABC"));
        Exception exception = new Exception();
        exception.initCause(new Exception("XYZ", nested));
        testRunWithErrorDialogErrorHelper(exception, "XYZ", "ABC");
    }

    private void testRunWithErrorDialogErrorHelper(final Exception exception,
            String text, String... details) {
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
        ErrorDialogFixture fixture = new ErrorDialogFixture();
        fixture.assertError(text);
        fixture.assertDetails(details);
        fixture.dismiss();
        assertThat(result.get(), is(false));
        verify(failureMessage).error(JFaceUtils.class, exception);
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

    @Test
    public void testSafeRunnableParentMonitorDone() throws Exception {
        IProgressMonitor mockMonitor = mock(IProgressMonitor.class);
        JFaceUtils.safeRunnableWithProgress(new IUnsafeRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, Exception {
                SubMonitor progress = SubMonitor.convert(monitor, "task", 100);
                progress.worked(99);
                progress.worked(2);
            }
        }).run(mockMonitor);
        verify(mockMonitor).beginTask("task", 1000);
        verify(mockMonitor).worked(990);
        // only 10 ticks left
        verify(mockMonitor).worked(10);
        verify(mockMonitor).done();
    }

    @Test
    public void testSafeRunnableInterruptedException() throws Exception {
        final IProgressMonitor mockMonitor = mock(IProgressMonitor.class);
        new ExpectedFailure<InterruptedException>(null) {
            @Override
            protected void run() throws Exception {
                JFaceUtils.safeRunnableWithProgress(
                        new IUnsafeRunnableWithProgress() {
                            @Override
                            public void run(IProgressMonitor monitor)
                                    throws Exception {
                                throw new InterruptedException();
                            }
                        }).run(mockMonitor);
            }
        };
        verify(mockMonitor).done();
    }

    @Test
    public void testSafeRunnableException() throws Exception {
        final IProgressMonitor mockMonitor = mock(IProgressMonitor.class);
        final Exception exception = new Exception();
        ExpectedFailure<InvocationTargetException> failure = new ExpectedFailure<InvocationTargetException>(
                null) {
            @Override
            protected void run() throws Exception {
                JFaceUtils.safeRunnableWithProgress(
                        new IUnsafeRunnableWithProgress() {
                            @Override
                            public void run(IProgressMonitor monitor)
                                    throws Exception {
                                throw exception;
                            }
                        }).run(mockMonitor);
            }
        };
        assertThat(failure.getException().getCause(), is((Throwable) exception));
        verify(mockMonitor).done();
    }

    public static class ErrorDialogFixture {

        private final SWTBot mBot;
        private final SWTBotShell mShell;

        public ErrorDialogFixture() {
            mBot = new SWTBot();
            mShell = mBot.shell("Operation Failed");
        }

        public void assertError(String message) {
            mBot.label(message);
        }

        public void assertDetails(String[] details) {
            if (details.length > 0) {
                mBot.button("Details >>").click();
                assertThat(mBot.list().getItems(), is(details));

            }
        }

        public void dismiss() {
            mBot.button("OK").click();
            mBot.waitUntil(shellCloses(mShell));
        }
    }

}
