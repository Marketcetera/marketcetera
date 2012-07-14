package org.marketcetera.photon.marketdata.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.marketdata.DepthOfBookKeyTest;
import org.marketcetera.photon.internal.marketdata.DepthOfBookManagerTest;
import org.marketcetera.photon.internal.marketdata.KeyTest;
import org.marketcetera.photon.internal.marketdata.LatestTickKeyTest;
import org.marketcetera.photon.internal.marketdata.LatestTickManagerTest;
import org.marketcetera.photon.internal.marketdata.MarketDataFeedTest;
import org.marketcetera.photon.internal.marketdata.MarketDataReceiverModuleTest;
import org.marketcetera.photon.internal.marketdata.MarketDataTest;
import org.marketcetera.photon.internal.marketdata.MarketstatKeyTest;
import org.marketcetera.photon.internal.marketdata.MarketstatManagerTest;
import org.marketcetera.photon.internal.marketdata.MessagesTest;
import org.marketcetera.photon.internal.marketdata.SharedOptionLatestTickKeyTest;
import org.marketcetera.photon.internal.marketdata.SharedOptionLatestTickManagerTest;
import org.marketcetera.photon.internal.marketdata.SharedOptionMarketstatKeyTest;
import org.marketcetera.photon.internal.marketdata.SharedOptionMarketstatManagerTest;
import org.marketcetera.photon.internal.marketdata.TopOfBookKeyTest;
import org.marketcetera.photon.internal.marketdata.TopOfBookManagerTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { MessagesTest.class, KeyTest.class,
        LatestTickKeyTest.class, TopOfBookKeyTest.class,
        MarketstatKeyTest.class, DepthOfBookKeyTest.class,
        SharedOptionLatestTickKeyTest.class,
        SharedOptionMarketstatKeyTest.class, MarketDataTest.class,
        MarketDataFeedTest.class, MarketDataReceiverModuleTest.class,
        LatestTickManagerTest.class, TopOfBookManagerTest.class,
        MarketstatManagerTest.class, DepthOfBookManagerTest.class,
        SharedOptionLatestTickManagerTest.class,
        SharedOptionMarketstatManagerTest.class })
public class MarketDataCoreSuite {
}
