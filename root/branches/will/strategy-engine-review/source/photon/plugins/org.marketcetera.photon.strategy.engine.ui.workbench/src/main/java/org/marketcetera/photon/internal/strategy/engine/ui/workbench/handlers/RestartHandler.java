package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.JFaceUtils;
import org.marketcetera.photon.commons.ui.JFaceUtils.IUnsafeRunnableWithProgress;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtils;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.restart} command that
 * restarted selected strategies.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class RestartHandler extends SafeHandler {

    @Override
    protected void executeSafely(ExecutionEvent event)
            throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        final List<DeployedStrategy> strategies = Collections
                .synchronizedList(Lists.<DeployedStrategy> newArrayList());
        for (Object item : selection.toList()) {
            DeployedStrategy strategy = (DeployedStrategy) item;
            if (strategy.getState() == StrategyState.RUNNING) {
                strategies.add(strategy);
            }
        }
        final IRunnableWithProgress operation = JFaceUtils
                .safeRunnableWithProgress(new IUnsafeRunnableWithProgress() {
                    @Override
                    public void run(SubMonitor monitor) throws Exception {
                        for (DeployedStrategy strategy : strategies) {
                            ModalContext.checkCanceled(monitor);
                            monitor
                                    .setTaskName(Messages.STOP_HANDLER__TASK_NAME
                                            .getText(strategy.getInstanceName()));
                            strategy.getEngine().getConnection().stop(strategy);
                            monitor.worked(1);
                            ModalContext.checkCanceled(monitor);
                            monitor
                                    .setTaskName(Messages.START_HANDLER__TASK_NAME
                                            .getText(strategy.getInstanceName()));
                            strategy.getEngine().getConnection().start(strategy);
                            monitor.worked(1);
                        }
                    }
                }, strategies.size());
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                Messages.STOP_ALL_HANDLER_FAILED);
    }

}
