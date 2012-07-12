package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;

/* $License$ */

/**
 * Tests {@link StartHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StartHandlerTest extends ChangeStrategyHandlerTestBase {

    public StartHandlerTest() {
        super("Start", "Starting");
        mStrategy1.setState(StrategyState.STOPPED);
        mStrategy2.setState(StrategyState.RUNNING);
        mStrategy3.setState(StrategyState.STOPPED);
    }

    @Override
    protected void acceptChange(BlockingConnection connection, Object object)
            throws Exception {
        connection.acceptStart(object);
    }

    @Override
    protected List<DeployedStrategy> getMultipleAffected() {
        return Arrays.asList(mStrategy1, mStrategy3);
    }
}
