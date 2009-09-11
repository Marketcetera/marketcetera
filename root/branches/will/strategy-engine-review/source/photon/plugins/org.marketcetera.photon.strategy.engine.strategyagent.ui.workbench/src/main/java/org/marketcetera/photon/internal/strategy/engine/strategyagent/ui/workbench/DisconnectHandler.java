package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import java.util.concurrent.Callable;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtils;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.disconnect} command that
 * disconnects the currently selected strategy agent.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class DisconnectHandler extends SafeHandler {

    @Override
    public void executeSafely(ExecutionEvent event) throws ExecutionException {
        final StrategyAgentEngine engine = (StrategyAgentEngine) ((StructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event)).getFirstElement();
        String name = engine.getName();
        final IRunnableWithProgress operation = JFaceUtils.wrap(
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        engine.disconnect();
                        return null;
                    }
                }, Messages.DISCONNECT_HANDLER__TASK_NAME.getText(name));
        ProgressUtils
                .runModalWithErrorDialog(HandlerUtil
                        .getActiveWorkbenchWindowChecked(event), operation,
                        new I18NBoundMessage1P(
                                Messages.DISCONNECT_HANDLER_FAILED, name));
    }
}
