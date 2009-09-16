package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Handler for the {@code org.eclipse.ui.edit.delete} command that removes the
 * currently selected strategy agent, disconnecting if necessary.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class DeleteHandler extends SafeHandler {

    @Override
    protected void executeSafely(ExecutionEvent event)
            throws ExecutionException {
        final StrategyAgentEngine engine = (StrategyAgentEngine) ((StructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event)).getFirstElement();
        String name = engine.getName();
        if (!MessageDialog.openConfirm(
                HandlerUtil.getActiveShellChecked(event),
                Messages.DELETE_HANDLER_CONFIRMATION__TITLE.getText(),
                Messages.DELETE_HANDLER_CONFIRMATION.getText(name))) {
            return;
        }
        if (engine.getConnectionState() == ConnectionState.CONNECTED) {
            new DisconnectHandler().execute(event);
        }
        IStrategyEngines part = (IStrategyEngines) HandlerUtil
                .getActivePartChecked(event);
        part.removeEngine(engine);
    }

}
