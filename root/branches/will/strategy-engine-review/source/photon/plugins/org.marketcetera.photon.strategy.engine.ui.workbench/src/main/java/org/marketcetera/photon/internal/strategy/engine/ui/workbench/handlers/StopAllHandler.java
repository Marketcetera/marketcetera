package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

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
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.stopAll} command that
 * stops all running strategies deployed on the selected engines.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StopAllHandler extends SafeHandler {

    @Override
    protected void executeSafely(ExecutionEvent event)
            throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil
                .getCurrentSelectionChecked(event);
        Iterable<Parameter<DeployedStrategy>> strategies = Collections
                .emptyList();
        for (Object item : selection.toList()) {
            StrategyEngine engine = (StrategyEngine) item;
            strategies = Iterables.concat(strategies, Parameter.build(engine
                    .getDeployedStrategies(),
                    new Predicate<DeployedStrategy>() {
                        @Override
                        public boolean apply(DeployedStrategy input) {
                            return input.getState() == StrategyState.RUNNING;
                        }
                    }));
        }
        final ImmutableList<Parameter<DeployedStrategy>> finalStrategies = ImmutableList
                .copyOf(strategies);
        final IRunnableWithProgress operation = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {
                SubMonitor progress = SubMonitor.convert(monitor,
                        finalStrategies.size());
                try {
                    for (Parameter<DeployedStrategy> parameter : finalStrategies) {
                        ModalContext.checkCanceled(progress);
                        progress
                                .setTaskName(Messages.STOP_ALL_HANDLER__TASK_NAME
                                        .getText(parameter.getName()));
                        parameter.getConnection().stop(parameter.getObject());
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
                Messages.STOP_ALL_HANDLER_FAILED);
    }
}
