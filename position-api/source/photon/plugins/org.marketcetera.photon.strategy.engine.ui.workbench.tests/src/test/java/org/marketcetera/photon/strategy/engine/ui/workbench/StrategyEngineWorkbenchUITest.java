package org.marketcetera.photon.strategy.engine.ui.workbench;

import static org.junit.Assert.assertTrue;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.CONNECT_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.DEPLOY_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.DISCONNECT_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.PLUGIN_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.RESTART_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.START_ALL_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.START_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.STOP_ALL_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.STOP_COMMAND_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.STRATEGY_ENGINES_COMMAND_CATEGORY_ID;
import static org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUI.UNDEPLOY_COMMAND_ID;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.photon.test.OSGITestUtil;
import org.marketcetera.photon.test.WorkbenchRunner;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link StrategyEngineWorkbenchUI}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(WorkbenchRunner.class)
public class StrategyEngineWorkbenchUITest {

    @Test
    public void testPluginId() {
        OSGITestUtil.assertBundle(PLUGIN_ID);
    }

    @Test
    public void verifyCommands() {
        ImmutableList<String> commands = ImmutableList.of(CONNECT_COMMAND_ID,
                DISCONNECT_COMMAND_ID, DEPLOY_COMMAND_ID, UNDEPLOY_COMMAND_ID,
                START_COMMAND_ID, STOP_COMMAND_ID, RESTART_COMMAND_ID,
                START_ALL_COMMAND_ID, STOP_ALL_COMMAND_ID);
        ICommandService service = (ICommandService) PlatformUI.getWorkbench()
                .getService(ICommandService.class);
        for (String commandId : commands) {
            assertTrue(service.getCommand(commandId).isDefined());
        }
        assertTrue(service.getCategory(STRATEGY_ENGINES_COMMAND_CATEGORY_ID)
                .isDefined());
    }
}
