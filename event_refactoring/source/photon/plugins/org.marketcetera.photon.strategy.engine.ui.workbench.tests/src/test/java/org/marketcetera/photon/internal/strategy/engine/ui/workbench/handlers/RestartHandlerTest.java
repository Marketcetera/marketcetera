package org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.marketcetera.photon.commons.ui.workbench.ProgressUtilsTest.ProgressDialogFixture;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.WorkbenchRunner;

/* $License$ */

/**
 * Tests {@link RestartHandler}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class RestartHandlerTest extends ChangeStrategyHandlerTestBase {

    public RestartHandlerTest() {
        super("Restart", "ignored");
        mStrategy1.setState(StrategyState.RUNNING);
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
        return Arrays.asList(mStrategy1, mStrategy2);
    }

    @Override
    protected void validateProgress(ProgressDialogFixture fixture,
            DeployedStrategy strategy) throws Exception {
        fixture.assertTask(MessageFormat.format(
                "Stopping strategy ''{0}'' on ''My Engine''...", strategy
                        .getInstanceName()));
        mConnection.acceptStop(strategy);
        fixture.assertTask(MessageFormat.format(
                "Starting strategy ''{0}'' on ''My Engine''...", strategy
                        .getInstanceName()));
    }

}