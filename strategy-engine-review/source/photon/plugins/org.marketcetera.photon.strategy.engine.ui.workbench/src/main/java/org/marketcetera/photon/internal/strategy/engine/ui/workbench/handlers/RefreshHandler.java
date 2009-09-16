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
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Handler for the {@code org.eclipse.ui.file.refresh} command that refreshes
 * any selected engines or strategies.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class RefreshHandler extends SafeHandler {

    @Override
    protected void executeSafely(ExecutionEvent event)
            throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        final List<StrategyEngine> engines = Collections.synchronizedList(Lists
                .<StrategyEngine> newArrayList());
        final List<DeployedStrategy> strategies = Collections
                .synchronizedList(Lists.<DeployedStrategy> newArrayList());
        for (Object item : selection.toList()) {
            if (item instanceof StrategyEngine) {
                engines.add((StrategyEngine) item);
            } else if (item instanceof DeployedStrategy) {
                DeployedStrategy strategy = (DeployedStrategy) item;
                /*
                 * Optimization to not refresh strategies that are going to be
                 * refreshed with the engine.
                 */
                if (!engines.contains(strategy.getEngine())) {
                    strategies.add(strategy);
                }
            }
        }
        /*
         * Guess that refreshing an engine will take roughly 3 times as long as
         * refreshing a single strategy.
         */
        final int engineWork = 3;
        final IRunnableWithProgress operation = JFaceUtils
                .safeRunnableWithProgress(new IUnsafeRunnableWithProgress() {
                    @Override
                    public void run(SubMonitor monitor) throws Exception {
                        for (StrategyEngine engine : engines) {
                            ModalContext.checkCanceled(monitor);
                            monitor
                                    .setTaskName(Messages.REFRESH_HANDLER_REFRESH_ENGINE__TASK_NAME
                                            .getText(engine.getName()));
                            engine.getConnection().refresh();
                            monitor.worked(engineWork);
                        }
                        for (DeployedStrategy strategy : strategies) {
                            ModalContext.checkCanceled(monitor);
                            monitor
                                    .setTaskName(Messages.REFRESH_HANDLER_REFRESH_STRATEGY__TASK_NAME
                                            .getText(strategy.getInstanceName()));
                            strategy.getEngine().getConnection().refresh(
                                    strategy);
                            monitor.worked(1);
                        }
                    }
                }, engineWork * engines.size() + strategies.size());
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                Messages.REFRESH_HANDLER_FAILED);
    }
}
