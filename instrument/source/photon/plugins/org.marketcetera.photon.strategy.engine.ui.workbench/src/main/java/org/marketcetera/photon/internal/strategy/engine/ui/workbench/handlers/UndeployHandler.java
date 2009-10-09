package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.marketcetera.photon.commons.ui.workbench.AbstractSelectionHandler;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.undeploy} command that
 * undeploys currently selected {@link DeployedStrategy} objects. If one fails,
 * the operation will be aborted at that point.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class UndeployHandler extends
        AbstractSelectionHandler<DeployedStrategy> {

    /**
     * Constructor.
     */
    public UndeployHandler() {
        super(DeployedStrategy.class, Messages.UNDEPLOY_HANDLER_FAILED);
    }

    @Override
    protected void process(DeployedStrategy item, IProgressMonitor monitor)
            throws Exception {
        monitor.setTaskName(Messages.UNDEPLOY_HANDLER__TASK_NAME.getText(item
                .getInstanceName(), item.getEngine().getName()));
        item.getEngine().getConnection().undeploy(item);
    }
}
