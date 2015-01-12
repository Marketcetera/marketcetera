package org.marketcetera.photon.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.marketcetera.photon.commons.tests.CommonsSuite;
import org.marketcetera.photon.commons.ui.tests.CommonsUISuite;
import org.marketcetera.photon.commons.ui.workbench.tests.CommonsWorkbenchSuite;
import org.marketcetera.photon.core.tests.CoreSuite;
import org.marketcetera.photon.marketdata.tests.MarketDataCoreSuite;
import org.marketcetera.photon.marketdata.ui.tests.MarketDataUISuite;
import org.marketcetera.photon.module.ui.tests.ModuleUISuite;
import org.marketcetera.photon.notification.tests.NotificationSuite;
import org.marketcetera.photon.positions.ui.tests.PositionsUISuite;
import org.marketcetera.photon.tests.PhotonApplicationSuite;
import org.marketcetera.photon.tests.PhotonSuite;

@RunWith(Suite.class)
@SuiteClasses( { CommonsSuite.class, CommonsUISuite.class,
        CommonsWorkbenchSuite.class, CoreSuite.class,
        ModuleUISuite.class, MarketDataCoreSuite.class,
        MarketDataUISuite.class, NotificationSuite.class,
        PositionsUISuite.class, PhotonSuite.class, PhotonApplicationSuite.class })
public class AllTestsSuite {
}
