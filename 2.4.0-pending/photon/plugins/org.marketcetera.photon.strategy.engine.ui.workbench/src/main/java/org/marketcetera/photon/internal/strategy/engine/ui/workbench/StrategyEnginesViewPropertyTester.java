package org.marketcetera.photon.internal.strategy.engine.ui.workbench;

import java.text.MessageFormat;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.commands.ICommandService;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Allows {@link StrategyEnginesView} properties to be queried declaratively.
 * This is a hack/workaround to enable the connect and disconnect commands to be
 * visible only when they are handled. These properties should not be accessed
 * by clients.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class StrategyEnginesViewPropertyTester extends PropertyTester {

    private static final String DISCONNECT_COMMAND_HANDLED_PROPERTY = "disconnectCommandHandled"; //$NON-NLS-1$
    private static final String CONNECT_COMMAND_HANDLED_PROPERTY = "connectCommandHandled"; //$NON-NLS-1$
    private static final String DELETE_COMMAND_HANDLED_PROPERTY = "deleteCommandHandled"; //$NON-NLS-1$
    private static final String DELETE_COMMAND_ID = "org.eclipse.ui.edit.delete"; //$NON-NLS-1$

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        StrategyEnginesView view = (StrategyEnginesView) receiver;
        if (CONNECT_COMMAND_HANDLED_PROPERTY.equals(property)) {
            return isHandled(view,
                    StrategyEngineWorkbenchUI.CONNECT_COMMAND_ID, expectedValue);
        } else if (DISCONNECT_COMMAND_HANDLED_PROPERTY.equals(property)) {
            return isHandled(view,
                    StrategyEngineWorkbenchUI.DISCONNECT_COMMAND_ID,
                    expectedValue);
        } else if (DELETE_COMMAND_HANDLED_PROPERTY.equals(property)) {
            return isHandled(view, DELETE_COMMAND_ID, expectedValue);
        } else {
            throw new IllegalArgumentException(MessageFormat.format(
                    "unknown property [{0}]", property)); //$NON-NLS-1$
        }
    }

    private static boolean isHandled(StrategyEnginesView view,
            String commandId, Object expectedValue) {
        ICommandService service = (ICommandService) view.getViewSite()
                .getService(ICommandService.class);
        return Boolean.valueOf(service.getCommand(commandId).isHandled())
                .equals(expectedValue);
    }
}
