package org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench;

import org.eclipse.core.runtime.IProgressMonitor;
import org.marketcetera.photon.commons.ui.workbench.AbstractSelectionHandler;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.base.Predicate;

/**
 * Handler for the {@code
 * org.marketcetera.photon.strategy.engine.ui.workbench.connect} command that
 * connects the currently selected strategy agent.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ConnectHandler extends
        AbstractSelectionHandler<StrategyAgentEngine> {

    /**
     * Constructor.
     */
    public ConnectHandler() {
        super(StrategyAgentEngine.class, Messages.CONNECT_HANDLER_FAILED,
                new Predicate<StrategyAgentEngine>() {
                    @Override
                    public boolean apply(StrategyAgentEngine input) {
                        return input.getConnectionState() == ConnectionState.DISCONNECTED;
                    }
                });
    }

    @Override
    protected void process(StrategyAgentEngine item, IProgressMonitor monitor)
            throws Exception {
        monitor.setTaskName(Messages.CONNECT_HANDLER__TASK_NAME.getText(item
                .getName()));
        item.connect();
    }
}
