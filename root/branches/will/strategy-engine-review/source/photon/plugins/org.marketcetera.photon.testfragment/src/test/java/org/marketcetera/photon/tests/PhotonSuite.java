package org.marketcetera.photon.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.BrokerManagerTest;
import org.marketcetera.photon.PhotonPositionMarketDataConcurrencyTest;
import org.marketcetera.photon.PhotonPositionMarketDataTest;
import org.marketcetera.photon.TimeOfDayTest;
import org.marketcetera.photon.actions.BrokerNotificationListenerTest;
import org.marketcetera.photon.marketdata.MessagesTest;
import org.marketcetera.photon.views.AddSymbolActionTest;
import org.marketcetera.photon.views.MarketDataViewItemTest;

/* $License$ */

/**
 * Test suite for this bundle. The tests that require the full Photon
 * application are in {@link PhotonApplicationSuite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { BrokerManagerTest.class, MessagesTest.class,
        PhotonPositionMarketDataConcurrencyTest.class,
        PhotonPositionMarketDataTest.class, TimeOfDayTest.class,
        BrokerNotificationListenerTest.class, AddSymbolActionTest.class,
        MarketDataViewItemTest.class })
public class PhotonSuite {
}
