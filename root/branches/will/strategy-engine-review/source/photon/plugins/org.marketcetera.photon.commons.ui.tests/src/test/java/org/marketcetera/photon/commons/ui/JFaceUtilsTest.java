package org.marketcetera.photon.commons.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
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
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.SimpleUIRunner;
import org.marketcetera.photon.test.AbstractUIRunner.UI;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.test.TestCaseBase;

/* $License$ */

/**
 * Tests {@link JFaceUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SimpleUIRunner.class)
public class JFaceUtilsTest extends TestCaseBase {

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
    public void before() {
        setDefaultLevel(Level.ALL);
    }

    @Before
    @UI
    public void beforeUI() {
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
        assertThat(JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {
            }
        }, false, mock(I18NBoundMessage.class)), is(true));
    }
    
    @Test
    @UI
    public void testRunModalCancel() {
        final InterruptedException toThrow = new InterruptedException();
        final I18NBoundMessage failureMessage = mock(I18NBoundMessage.class);
        assertThat(JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext, new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException,
                    InterruptedException {
                throw toThrow;
            }
        }, false, failureMessage), is(false));
        verify(failureMessage).info(JFaceUtils.class, toThrow);
    }

    @Test
    public void testRunModalError() {
        testRunModalErrorHelper(new Exception("Exception Text"), "Exception Text");
    }

    @Test
    public void testRunModalErrorNoMessage() {
        testRunModalErrorHelper(new Exception(), "A Java exception occurred during the operation.  See the log for details.");
    }

    private void testRunModalErrorHelper(Exception exception, String text) {
        final AtomicBoolean result = new AtomicBoolean(true);
        final InvocationTargetException toThrow = new InvocationTargetException(exception);
        final I18NBoundMessage failureMessage = mock(I18NBoundMessage.class);
        mShell.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                result.set(JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext, new IRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor) throws InvocationTargetException,
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
    public void testValidation() throws Exception {
        new ExpectedFailure<IllegalArgumentException>("'container' must not be null") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(null, null, false, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>("'context' must not be null") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(null, null, null, false, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>("'shellProvider' must not be null") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, null, null, false, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>("'operation' must not be null") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext, null, false, null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>("'failureMessage' must not be null") {
            @Override
            protected void run() throws Exception {
                JFaceUtils.runModalWithErrorDialog(mMockContext, mMockContext, mock(IRunnableWithProgress.class), false, null);
            }
        };
    }

}
