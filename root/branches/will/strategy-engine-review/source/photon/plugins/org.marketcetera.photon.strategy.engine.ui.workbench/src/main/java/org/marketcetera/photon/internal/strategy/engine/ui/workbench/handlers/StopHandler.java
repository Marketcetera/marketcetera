package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.marketcetera.photon.commons.ui.workbench.AbstractSelectionHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Predicate;

/* $License$ */

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.stop} command that stops
 * the currently selected {@link DeployedStrategy} objects. If one fails, the
 * operation will be aborted at that point.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StopHandler extends
        AbstractSelectionHandler<DeployedStrategy> {

    private static final Predicate<DeployedStrategy> sRunningStrategies = new Predicate<DeployedStrategy>() {
        @Override
        public boolean apply(DeployedStrategy input) {
            return input.getState() == StrategyState.RUNNING;
        }
    };

    /**
     * Returns a predicate that filters stopped strategies.
     * 
     * @return the predicate
     */
    static Predicate<DeployedStrategy> runningStrategies() {
        return sRunningStrategies;
    }

    /**
     * Constructor.
     */
    public StopHandler() {
        super(DeployedStrategy.class, Messages.STOP_HANDLER_FAILED,
                runningStrategies());
    }

    @Override
    protected void process(DeployedStrategy item, IProgressMonitor monitor)
            throws Exception {
        monitor.setTaskName(Messages.STOP_HANDLER__TASK_NAME.getText(item
                .getInstanceName(), item.getEngine().getName()));
        item.getEngine().getConnection().stop(item);
    }
}
