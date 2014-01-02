package org.marketcetera.photon.commons.ui.workbench;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Convenience command handler base class that wraps unchecked exceptions in an
 * {@link ExecutionException}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public abstract class SafeHandler extends AbstractHandler {

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            executeSafely(event);
            return null;
        } catch (RuntimeException e) {
            throw new ExecutionException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * Execute the command. If this method throws a {@link RuntimeException}, it
     * will be wrapped in an {@link ExecutionException}.
     * 
     * @param event
     *            An event containing all the information about the current
     *            state of the application, must not be null
     * @throws ExecutionException
     *             if an exception occurred during execution
     */
    protected abstract void executeSafely(ExecutionEvent event)
            throws ExecutionException;

}
