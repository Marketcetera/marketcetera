package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.ModalContext;
import org.marketcetera.photon.commons.ui.workbench.AbstractSelectionHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Collections2;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.stopAll} command that
 * stops all running strategies deployed on the selected {@link StrategyEngine}
 * objects. If one fails, the operation will be aborted at that point.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public final class StopAllHandler extends
        AbstractSelectionHandler<StrategyEngine> {

    /**
     * Constructor.
     */
    public StopAllHandler() {
        super(StrategyEngine.class, Messages.STOP_HANDLER_FAILED);
    }

    @Override
    protected void process(StrategyEngine item, IProgressMonitor monitor)
            throws Exception {
        Collection<DeployedStrategy> strategies = Collections2.filter(item
                .getDeployedStrategies(), StopHandler.runningStrategies());
        SubMonitor progress = SubMonitor.convert(monitor, strategies.size());
        for (DeployedStrategy strategy : strategies) {
            ModalContext.checkCanceled(progress);
            progress.setTaskName(Messages.STOP_HANDLER__TASK_NAME.getText(
                    strategy.getInstanceName(), item.getName()));
            strategy.getEngine().getConnection().stop(strategy);
            progress.worked(1);
        }
    }
}
