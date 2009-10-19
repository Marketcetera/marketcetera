package org.marketcetera.photon.internal.strategy.engine.sa.ui.workbench;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.IStrategyEngines;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

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
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        List<StrategyAgentEngine> engines = Lists.newArrayList();
        boolean someConnected = false;
        for (Object item : selection.toList()) {
            StrategyAgentEngine engine = (StrategyAgentEngine) item;
            engines.add(engine);
            if (engine.getConnectionState() == ConnectionState.CONNECTED) {
                someConnected = true;
            }
        }
        String confirmationMessage;
        if (engines.size() == 1) {
            confirmationMessage = Messages.DELETE_HANDLER_CONFIRMATION_SINGLE
                    .getText(engines.get(0).getName());
        } else {
            confirmationMessage = Messages.DELETE_HANDLER_CONFIRMATION_MULTIPLE
                    .getText();
        }
        if (!MessageDialog.openConfirm(
                HandlerUtil.getActiveShellChecked(event),
                Messages.DELETE_HANDLER_CONFIRMATION__TITLE.getText(),
                confirmationMessage)) {
            return;
        }
        if (someConnected) {
            new DisconnectHandler().execute(event);
        }
        IStrategyEngines part = (IStrategyEngines) HandlerUtil
                .getActivePartChecked(event);
        for (StrategyAgentEngine engine : engines) {
            if (engine.getConnectionState() == ConnectionState.CONNECTED) {
                // skip, disconnect must have failed
            } else {
                part.removeEngine(engine);
            }
        }
    }
}
