package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
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
 * any selected engines or strategies. The selection can contain both strategies
 * and strategy engines, but the strategy will be ignored if its containing
 * engine is also selected (since it will be refreshed with the engine). If one
 * refresh fails, the operation will be aborted at that point.
 * <p>
 * Selected engines are assumed to be connected so this handler should only be
 * enabled in that case.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
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
        if (engines.isEmpty() && strategies.isEmpty()) {
            return;
        }
        final IRunnableWithProgress operation = JFaceUtils
                .safeRunnableWithProgress(new IUnsafeRunnableWithProgress() {
                    @Override
                    public void run(IProgressMonitor monitor) throws Exception {
                        /*
                         * Guess that refreshing an engine will take roughly 3
                         * times as long as refreshing a single strategy.
                         */
                        int engineWork = 3;
                        SubMonitor progress = SubMonitor.convert(monitor, 3
                                * engines.size() + strategies.size());
                        for (StrategyEngine engine : engines) {
                            ModalContext.checkCanceled(progress);
                            progress
                                    .setTaskName(Messages.REFRESH_HANDLER_REFRESH_ENGINE__TASK_NAME
                                            .getText(engine.getName()));
                            engine.getConnection().refresh();
                            progress.worked(engineWork);
                        }
                        for (DeployedStrategy strategy : strategies) {
                            ModalContext.checkCanceled(progress);
                            progress
                                    .setTaskName(Messages.REFRESH_HANDLER_REFRESH_STRATEGY__TASK_NAME
                                            .getText(
                                                    strategy.getInstanceName(),
                                                    strategy.getEngine()
                                                            .getName()));
                            strategy.getEngine().getConnection().refresh(
                                    strategy);
                            progress.worked(1);
                        }
                    }
                });
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                Messages.REFRESH_HANDLER_FAILED);
    }
}
