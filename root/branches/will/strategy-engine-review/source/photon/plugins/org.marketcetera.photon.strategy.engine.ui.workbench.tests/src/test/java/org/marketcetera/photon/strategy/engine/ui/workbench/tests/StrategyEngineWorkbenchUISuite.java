package org.marketcetera.photon.strategy.engine.ui.workbench.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.DeployedStrategyAdapterFactoryTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.DeployedStrategyConfigurationPropertyPageTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.DeployedStrategyPropertyTesterTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.NewPropertyInputDialogTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEngineIdentificationPropertyPageTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEnginePropertyTesterTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEngineAdapterFactoryTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEnginesViewPropertyTesterTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.StrategyEnginesViewTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.DeployHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.EngineRefreshHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StrategyRefreshHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.RestartHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StartAllHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StartHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StopAllHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.StopHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.UndeployHandlerTest;
import org.marketcetera.photon.strategy.engine.ui.workbench.StrategyEngineWorkbenchUITest;

@RunWith(Suite.class)
@SuiteClasses( {
        DeployedStrategyPropertyTesterTest.class,
        StrategyEnginePropertyTesterTest.class,
        StrategyEnginesViewTest.class,
        org.marketcetera.photon.internal.strategy.engine.ui.workbench.handlers.MessagesTest.class,
        org.marketcetera.photon.internal.strategy.engine.ui.workbench.MessagesTest.class,
        StrategyEnginesViewPropertyTesterTest.class,
        StrategyEngineAdapterFactoryTest.class,
        DeployedStrategyAdapterFactoryTest.class,
        StrategyEngineWorkbenchUITest.class,
        StrategyEngineIdentificationPropertyPageTest.class,
        DeployedStrategyConfigurationPropertyPageTest.class,
        NewPropertyInputDialogTest.class, DeployHandlerTest.class,
        UndeployHandlerTest.class, StartHandlerTest.class,
        StopHandlerTest.class, StartAllHandlerTest.class,
        StopAllHandlerTest.class, EngineRefreshHandlerTest.class,
        StrategyRefreshHandlerTest.class, RestartHandlerTest.class })
public final class StrategyEngineWorkbenchUISuite {
}
