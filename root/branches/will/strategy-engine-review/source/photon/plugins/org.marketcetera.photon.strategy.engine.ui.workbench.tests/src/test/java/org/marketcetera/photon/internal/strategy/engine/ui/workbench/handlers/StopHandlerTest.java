package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.WorkbenchRunner;

/* $License$ */

/**
 * Tests {@link StopHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class StopHandlerTest extends ChangeStrategyHandlerTestBase {

    public StopHandlerTest() {
        super("Stop", "Stopping");
        mStrategy1.setState(StrategyState.RUNNING);
        mStrategy2.setState(StrategyState.STOPPED);
        mStrategy3.setState(StrategyState.RUNNING);
    }

    @Override
    protected void acceptChange(BlockingConnection connection, Object object)
            throws Exception {
        connection.acceptStop(object);
    }

    @Override
    protected List<DeployedStrategy> getMultipleAffected() {
        return Arrays.asList(mStrategy1, mStrategy3);
    }
}
