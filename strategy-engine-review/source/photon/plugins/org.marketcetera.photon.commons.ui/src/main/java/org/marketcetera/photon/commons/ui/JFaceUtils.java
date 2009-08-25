package org.marketcetera.photon.commons.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * JFace utility methods.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class JFaceUtils {

    /**
     * Runs the operation in a wizard container. This is equivalent to:
     * 
     * <pre>
     * runModalWithErrorDialog(container, new SameShellProvider(container.getShell()),
     *         operation, cancelable, failureMessage)
     * </pre>
     * 
     * @param container
     *            the wizard container that provides the context and shell
     * @param operation
     *            the operation to run
     * @param cancelable
     *            whether the operation should be cancelable
     * @param failureMessage
     *            the localized message for logging failures
     * @return true if the operation succeeded, false if it was canceled or an
     *         error occurred
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static boolean runModalWithErrorDialog(IWizardContainer container,
            IRunnableWithProgress operation, boolean cancelable,
            I18NBoundMessage failureMessage) {
        Validate.notNull(container, "container"); //$NON-NLS-1$
        return runModalWithErrorDialog(container, new SameShellProvider(
                container.getShell()), operation, cancelable, failureMessage);
    }

    /**
     * Runs the operation on the given context. There are three possible
     * outcomes:
     * <ol>
     * <li>The operation is successful - this method will return true.</li>
     * <li>The operation was canceled - failureMessage will be logged at the
     * info level and this method will return false.</li>
     * <li>The operation threw an exception - failureMessage will be logged at
     * the error level, a MessageDialog will be presented to the user with the
     * details of the failure, and this method will return false.</li>
     * </ol>
     * 
     * @param context
     *            the context in which to run the operation
     * @param shellProvider
     *            provides the shell for the error dialog
     * @param operation
     *            the operation to run
     * @param cancelable
     *            whether the operation should be cancelable
     * @param failureMessage
     *            the localized message for logging failures
     * @return true if the operation succeeded, false if it was canceled or an
     *         error occurred
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static boolean runModalWithErrorDialog(
            final IRunnableContext context, IShellProvider shellProvider,
            final IRunnableWithProgress operation, final boolean cancelable,
            final I18NBoundMessage failureMessage) {
        Validate.notNull(context, "context", //$NON-NLS-1$ 
                shellProvider, "shellProvider", //$NON-NLS-1$
                operation, "operation", //$NON-NLS-1$ 
                failureMessage, "failureMessage"); //$NON-NLS-1$
        return runWithErrorDialog(shellProvider, new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    context.run(true, cancelable, operation);
                    return true;
                } catch (InterruptedException e) {
                    // The runnable responded to cancellation. This should only
                    // happen
                    // if cancelable is true. Since the exception occurred on a
                    // separate
                    // thread where task was forked, there is no need to do
                    // anything
                    // here.
                    failureMessage.info(JFaceUtils.class, e);
                    return false;
                } catch (InvocationTargetException e) {
                    Throwable realException = e.getCause();
                    if (realException instanceof Error) {
                        throw (Error) realException;
                    } else if (realException instanceof Exception) {
                        throw (Exception) realException;
                    } else {
                        throw e;
                    }
                }
            }
        }, failureMessage);
    }

    /**
     * Runs the operation. There are two possible outcomes:
     * <ol>
     * <li>The operation is successful - this method will return true.</li>
     * <li>The operation threw an exception - failureMessage will be logged at
     * the error level, a MessageDialog will be presented to the user with the
     * details of the failure, and this method will return false.</li>
     * </ol>
     * 
     * @param shellProvider
     *            provides the shell for the error dialog
     * @param operation
     *            the operation to run
     * @param failureMessage
     *            the localized message for logging failures
     * @return true if the operation succeeded, false if an error occurred
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static boolean runWithErrorDialog(IShellProvider shellProvider,
            Callable<Boolean> operation, I18NBoundMessage failureMessage) {
        Validate.notNull(shellProvider, "shellProvider", //$NON-NLS-1$
                operation, "operation", //$NON-NLS-1$ 
                failureMessage, "failureMessage"); //$NON-NLS-1$
        try {
            return operation.call();
        } catch (Exception e) {
            failureMessage.error(JFaceUtils.class, e);
            String message = e.getLocalizedMessage();
            if (message == null) {
                message = Messages.JFACE_UTILS_GENERIC_EXCEPTION_OCCURRED
                        .getText();
            }
            MessageDialog.openError(shellProvider.getShell(),
                    Messages.JFACE_UTILS_OPERATION_FAILED__DIALOG_TITLE
                            .getText(), message);
            return false;
        }
    }

    /**
     * Utility method to wrap a Callable in an {@link IRunnableWithProgress}.
     * Use this when you have a top level task that does not support the
     * progress monitor.
     * 
     * @param callable
     *            the task to run
     * @param taskName
     *            the name for the task, may be null
     * @return an {@link IRunnableWithProgress} wrapping the task
     * @throws IllegalArgumentException
     *             if callable is null
     */
    public static IRunnableWithProgress wrap(final Callable<Void> callable,
            final String taskName) {
        Validate.notNull(callable, "callable"); //$NON-NLS-1$
        return new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                monitor.beginTask(taskName, IProgressMonitor.UNKNOWN);
                try {
                    callable.call();
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    monitor.done();
                }
            }
        };
    }

    private JFaceUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
