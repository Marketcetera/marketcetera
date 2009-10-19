package org.marketcetera.photon.commons.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

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
     * the error level, a dialog will be presented to the user with the details
     * of the failure, and this method will return false.</li>
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
                    /*
                     * The runnable responded to cancellation. This should only
                     * happen if cancelable is true. Since the exception
                     * occurred on a separate thread where task was forked,
                     * there is no need to do anything here.
                     */
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
     * the error level, a dialog will be presented to the user with the details
     * of the failure, and this method will return false.</li>
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
            List<IStatus> nestedDetails = Lists.newArrayList();
            Throwable current = e;
            while (current != null) {
                String message = current.getLocalizedMessage();
                if (message != null) {
                    nestedDetails.add(new Status(Status.ERROR,
                            CommonsUI.PLUGIN_ID, message));
                }
                current = current.getCause();
            }
            int size = nestedDetails.size();
            IStatus status;
            if (size == 0) {
                status = new Status(Status.ERROR, CommonsUI.PLUGIN_ID,
                        Messages.JFACE_UTILS_SEE_LOG.getText(e.getClass()
                                .getSimpleName()));
            } else if (size == 1) {
                status = nestedDetails.get(0);
            } else {
                status = new MultiStatus(CommonsUI.PLUGIN_ID, IStatus.ERROR,
                        nestedDetails.subList(1, size).toArray(
                                new IStatus[size - 1]), nestedDetails.get(0)
                                .getMessage(), null);
            }
            ErrorDialog.openError(shellProvider.getShell(),
                    Messages.JFACE_UTILS_OPERATION_FAILED__DIALOG_TITLE
                            .getText(), null, status);
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

    /**
     * Convenience utility that creates a {@link SubMonitor}, handles checked
     * exceptions, and calls {@link IProgressMonitor#done()} on the parent
     * progress monitor.
     * 
     * @param runnable
     *            the code that does the actual work
     * @return a runnable that can safely be used
     */
    public static IRunnableWithProgress safeRunnableWithProgress(
            final IUnsafeRunnableWithProgress runnable) {
        Validate.notNull(runnable, "runnable"); //$NON-NLS-1$
        return new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                try {
                    runnable.run(monitor);
                } catch (InterruptedException e) {
                    // propagate InterruptedException since it has special
                    // meaning, i.e. cancellation
                    throw e;
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    if (monitor != null) {
                        monitor.done();
                    }
                }
            }
        };
    }

    /**
     * Interface to be used with
     * {@link JFaceUtils#safeRunnableWithProgress(IUnsafeRunnableWithProgress)}.
     */
    @ClassVersion("$Id$")
    public interface IUnsafeRunnableWithProgress {

        /**
         * Runs this operation. Progress should be reported to the given
         * progress sub monitor. A request to cancel the operation should be
         * honored and acknowledged by throwing
         * <code>InterruptedException</code>.
         * 
         * @param monitor
         *            the progress monitor to use for reporting progress to the
         *            user. It is the caller's responsibility to call done() on
         *            the given monitor. Accepts null, indicating that no
         *            progress should be reported and that the operation cannot
         *            be canceled.
         * @throws InterruptedException
         *             if the operation detects a request to cancel, using
         *             <code>IProgressMonitor.isCanceled()</code>, it should
         *             exit by throwing <code>InterruptedException</code>
         * @throws Exception
         *             if an exception occurs
         */
        void run(IProgressMonitor monitor) throws Exception;
    }

    private JFaceUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
