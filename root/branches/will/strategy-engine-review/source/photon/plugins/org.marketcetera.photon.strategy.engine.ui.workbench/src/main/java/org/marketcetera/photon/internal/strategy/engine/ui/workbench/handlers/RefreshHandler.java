package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtils;
import org.marketcetera.photon.commons.ui.workbench.SafeHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
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
        List<Parameter<StrategyEngine>> engines = Lists.newArrayList();
        List<Parameter<DeployedStrategy>> strategies = Lists.newArrayList();
        for (Object item : selection.toList()) {
            if (item instanceof StrategyEngine) {
                StrategyEngine engine = (StrategyEngine) item;
                engines.add(new Parameter<StrategyEngine>(engine
                        .getConnection(), engine, engine.getName()));
            } else if (item instanceof DeployedStrategy) {
                DeployedStrategy strategy = (DeployedStrategy) item;
                /*
                 * Optimization to not refresh strategies that are going to be
                 * refreshed with the engine.
                 */
                if (!engines.contains(strategy.getEngine())) {
                    strategies.add(new Parameter<DeployedStrategy>(strategy
                            .getEngine().getConnection(), strategy, strategy
                            .getInstanceName()));
                }
            }
        }
        final ImmutableList<Parameter<StrategyEngine>> finalEngines = ImmutableList
                .copyOf(engines);
        final ImmutableList<Parameter<DeployedStrategy>> finalStrategies = ImmutableList
                .copyOf(strategies);
        final IRunnableWithProgress operation = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                /*
                 * Guess that refreshing an engine will take roughly 3 times as
                 * long as refreshing a single strategy.
                 */
                int engineWork = 3;
                SubMonitor progress = SubMonitor.convert(monitor,
                        (engineWork * finalEngines.size())
                                + finalStrategies.size());
                try {
                    for (Parameter<StrategyEngine> parameter : finalEngines) {
                        ModalContext.checkCanceled(progress);
                        progress
                                .setTaskName(Messages.REFRESH_HANDLER_REFRESH_ENGINE__TASK_NAME
                                        .getText(parameter.getName()));
                        parameter.getConnection().refresh();
                        progress.worked(engineWork);
                    }
                    for (Parameter<DeployedStrategy> parameter : finalStrategies) {
                        ModalContext.checkCanceled(progress);
                        progress
                                .setTaskName(Messages.REFRESH_HANDLER_REFRESH_STRATEGY__TASK_NAME
                                        .getText(parameter.getName()));
                        parameter.getConnection().refresh(parameter.getObject());
                        progress.worked(1);
                    }
                } catch (InterruptedException e) {
                    // propagate InterruptedException since it has special
                    // meaning, i.e. cancellation
                    throw e;
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                } finally {
                    if (monitor != null) {
                        monitor.done();
                    }
                }
            }
        };
        ProgressUtils.runModalWithErrorDialog(HandlerUtil
                .getActiveWorkbenchWindowChecked(event), operation,
                Messages.REFRESH_HANDLER_FAILED);
    }
}
