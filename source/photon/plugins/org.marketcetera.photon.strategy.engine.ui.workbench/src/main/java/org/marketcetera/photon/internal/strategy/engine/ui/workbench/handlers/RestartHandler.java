package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.marketcetera.photon.commons.ui.workbench.AbstractSelectionHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.restart} command that
 * restarts selected {@link DeployedStrategy} objects. If one fails, the
 * operation will be aborted at that point.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class RestartHandler extends
        AbstractSelectionHandler<DeployedStrategy> {

    /**
     * Constructor.
     */
    public RestartHandler() {
        super(DeployedStrategy.class, Messages.RESTART_HANDLER_FAILED,
                StopHandler.runningStrategies());
    }

    @Override
    protected void process(DeployedStrategy item, IProgressMonitor monitor)
            throws Exception {
        SubMonitor progress = SubMonitor.convert(monitor, 2);
        progress.setTaskName(Messages.STOP_HANDLER__TASK_NAME.getText(item
                .getInstanceName(), item.getEngine().getName()));
        item.getEngine().getConnection().stop(item);
        progress.worked(1);
        progress.setTaskName(Messages.START_HANDLER__TASK_NAME.getText(item
                .getInstanceName(), item.getEngine().getName()));
        item.getEngine().getConnection().start(item);
        progress.worked(1);
    }
}
