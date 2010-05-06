package org.marketcetera.photon.commons.ui.workbench;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.test.AbstractUIRunner;
import org.marketcetera.photon.test.WorkbenchRunner;
import org.marketcetera.photon.test.AbstractUIRunner.ThrowableRunnable;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Tests {@link ProgressUtils}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(WorkbenchRunner.class)
public class ProgressUtilsTest {

    @Test
    public void testRunModalWithErrorDialog() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean();
        AbstractUIRunner.asyncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                result.set(ProgressUtils.runModalWithErrorDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow(), JFaceUtils
                        .wrap(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                latch.await();
                                return null;
                            }
                        }, "Test operation"), mock(I18NBoundMessage.class)));
            }
        });
        ProgressDialogFixture fixture = new ProgressDialogFixture();
        fixture.assertTask("Test operation");
        latch.countDown();
        fixture.waitForClose();
        assertThat(result.get(), is(true));
    }

    @Test
    public void testRunModalWithErrorDialogThrowsException() {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean result = new AtomicBoolean();
        final I18NBoundMessage mockFailureMessage = mock(I18NBoundMessage.class);
        final Exception exception = new Exception("Failure");
        AbstractUIRunner.asyncRun(new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                result.set(ProgressUtils.runModalWithErrorDialog(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow(), JFaceUtils
                        .wrap(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                latch.await();
                                throw exception;
                            }
                        }, "Test operation"), mockFailureMessage));
            }
        });
        ProgressDialogFixture fixture = new ProgressDialogFixture();
        fixture.assertTask("Test operation");
        latch.countDown();
        fixture.waitForClose();
        verify(mockFailureMessage).error(JFaceUtils.class, exception);
        assertThat(result.get(), is(false));
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("shellProvider") {
            @Override
            protected void run() throws Exception {
                ProgressUtils.runModalWithErrorDialog(null,
                        mock(IRunnableWithProgress.class),
                        mock(I18NBoundMessage.class));
            }
        };
        new ExpectedNullArgumentFailure("operation") {
            @Override
            protected void run() throws Exception {
                ProgressUtils.runModalWithErrorDialog(
                        mock(IShellProvider.class), null,
                        mock(I18NBoundMessage.class));
            }
        };
        new ExpectedNullArgumentFailure("failureMessage") {
            @Override
            protected void run() throws Exception {
                ProgressUtils.runModalWithErrorDialog(
                        mock(IShellProvider.class),
                        mock(IRunnableWithProgress.class), null);
            }
        };
    }

    public static class ProgressDialogFixture {

        private final SWTBot mBot = new SWTBot();
        private final SWTBotShell mShell;

        public ProgressDialogFixture() {
            mShell = mBot.shell("Progress Information");
        }

        public void assertTask(String taskName) {
            mBot.label(taskName);
        }

        public void cancel() {
            mBot.button("Cancel").click();
        }

        public void waitForClose() {
            mBot.waitUntil(shellCloses(mShell));
        }
    }
}
