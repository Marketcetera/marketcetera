package org.marketcetera.photon.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.marketcetera.photon.commons.tests.CommonsSuite;
import org.marketcetera.photon.commons.ui.tests.CommonsUISuite;
import org.marketcetera.photon.commons.ui.workbench.tests.CommonsWorkbenchSuite;
import org.marketcetera.photon.core.tests.CoreSuite;
import org.marketcetera.photon.internal.strategy.engine.embedded.tests.EmbeddedEngineSuite;
import org.marketcetera.photon.marketdata.tests.MarketDataCoreSuite;
import org.marketcetera.photon.marketdata.ui.tests.MarketDataUISuite;
import org.marketcetera.photon.module.tests.ModuleCoreSuite;
import org.marketcetera.photon.module.ui.tests.ModuleUISuite;
import org.marketcetera.photon.notification.tests.NotificationSuite;
import org.marketcetera.photon.strategy.engine.model.core.tests.StrategyEngineCoreSuite;
import org.marketcetera.photon.strategy.engine.sa.tests.StrategyAgentEngineSuite;
import org.marketcetera.photon.strategy.engine.sa.ui.tests.StrategyAgentEngineUISuite;
import org.marketcetera.photon.strategy.engine.sa.ui.workbench.tests.StrategyAgentEngineWorkbenchUISuite;
import org.marketcetera.photon.strategy.engine.ui.tests.StrategyEngineUISuite;
import org.marketcetera.photon.strategy.engine.ui.workbench.tests.StrategyEngineWorkbenchUISuite;
import org.marketcetera.photon.strategy.engine.ui.workbench.ws.tests.StrategyEngineWorkspaceSuite;
import org.marketcetera.photon.strategy.tests.StrategySuite;
import org.marketcetera.photon.tests.PhotonApplicationSuite;
import org.marketcetera.photon.tests.PhotonSuite;

@RunWith(Suite.class)
@SuiteClasses( { CommonsSuite.class, CommonsUISuite.class,
        CommonsWorkbenchSuite.class, CoreSuite.class,
        StrategyEngineCoreSuite.class, StrategyEngineUISuite.class,
        StrategyEngineWorkbenchUISuite.class, StrategyAgentEngineSuite.class,
        StrategyAgentEngineUISuite.class,
        StrategyAgentEngineWorkbenchUISuite.class,
        StrategyEngineWorkspaceSuite.class, EmbeddedEngineSuite.class,
        StrategyAgentEngineSuite.class, ModuleCoreSuite.class,
        ModuleUISuite.class, MarketDataCoreSuite.class,
        MarketDataUISuite.class, NotificationSuite.class, StrategySuite.class,
        PhotonSuite.class, PhotonApplicationSuite.class })
public class AllTestsSuite {
}
