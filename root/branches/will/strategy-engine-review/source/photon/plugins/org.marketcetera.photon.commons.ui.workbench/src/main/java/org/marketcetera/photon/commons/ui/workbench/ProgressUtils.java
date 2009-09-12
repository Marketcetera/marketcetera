package org.marketcetera.photon.commons.ui.workbench;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities for working with the workbench {@link IProgressService}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ProgressUtils {

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
     * @param shellProvider
     *            provides the shell for the error dialog
     * @param operation
     *            the operation to run
     * @param failureMessage
     *            the localized message for logging failures
     * @return true if the operation succeeded, false if it was canceled or an
     *         error occurred
     * @throws IllegalArgumentException
     *             if any parameter is null
     */
    public static boolean runModalWithErrorDialog(IShellProvider shellProvider,
            final IRunnableWithProgress operation,
            final I18NBoundMessage failureMessage) {
        Validate.notNull(shellProvider, "shellProvider", //$NON-NLS-1$
                operation, "operation", //$NON-NLS-1$ 
                failureMessage, "failureMessage"); //$NON-NLS-1$
        final IProgressService progressService = PlatformUI.getWorkbench()
                .getProgressService();
        /*
         * IProgressService extends IRunnableContext. However, its run method is
         * implemented for backwards compatibility and not as slick as
         * busyCursorWhile (the progress dialog is opened immediately instead of
         * after a short busy delay). So we adapt here to use the desired
         * method.
         */
        IRunnableContext adapter = new IRunnableContext() {
            @Override
            public void run(boolean fork, boolean cancelable,
                    IRunnableWithProgress runnable)
                    throws InvocationTargetException, InterruptedException {
                progressService.busyCursorWhile(runnable);
            }
        };
        return JFaceUtils.runModalWithErrorDialog(adapter, shellProvider,
                operation, false, failureMessage);
    }

    private ProgressUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
