package org.marketcetera.photon.test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

/* $License$ */

/**
 * Utilities for testing commands.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class CommandTestUtil {

    /**
     * Runs the provided command against the provided selection.
     * 
     * @param commandId
     *            the command id
     * @param selection
     *            the selection
     * @throws Exception if the command throws one
     */
    public static void runCommand(String commandId,
            IStructuredSelection selection) throws Exception {
        ICommandService commandService = (ICommandService) PlatformUI
                .getWorkbench().getService(ICommandService.class);
        Command cmd = commandService.getCommand(commandId);
        assertThat(cmd.isDefined(), is(true));
    
        IHandlerService handlerService = (IHandlerService) PlatformUI
                .getWorkbench().getService(IHandlerService.class);
        EvaluationContext c = null;
        if (selection != null) {
            c = new EvaluationContext(handlerService
                    .createContextSnapshot(false), selection.toList());
            c.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, selection);
        }
        if (c != null) {
            handlerService.executeCommandInContext(new ParameterizedCommand(
                    cmd, null), null, c);
        } else {
            handlerService.executeCommand(commandId, null);
        }
    }

    private CommandTestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
