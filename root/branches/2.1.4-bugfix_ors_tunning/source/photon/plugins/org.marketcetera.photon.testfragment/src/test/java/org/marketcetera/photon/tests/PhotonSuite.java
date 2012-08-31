package org.marketcetera.photon.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.BrokerManagerTest;
import org.marketcetera.photon.PhotonPositionMarketDataConcurrencyTest;
import org.marketcetera.photon.PhotonPositionMarketDataTest;
import org.marketcetera.photon.TimeOfDayTest;
import org.marketcetera.photon.actions.BrokerNotificationListenerTest;
import org.marketcetera.photon.parser.CommandParserTest;
import org.marketcetera.photon.parser.OrderSingleParserTest;
import org.marketcetera.photon.ui.databinding.EquityObservableTest;
import org.marketcetera.photon.ui.databinding.NewOrReplaceOrderObservableTest;
import org.marketcetera.photon.ui.databinding.OptionObservableTest;
import org.marketcetera.photon.views.AddSymbolActionTest;
import org.marketcetera.photon.views.InstrumentMementoSerializerTest;
import org.marketcetera.photon.views.MarketDataViewItemTest;
import org.marketcetera.photon.views.OptionOrderTicketViewTest;
import org.marketcetera.photon.views.StockOrderTicketViewTest;

/* $License$ */

/**
 * Test suite for this bundle. The tests that require the full Photon
 * application are in {@link PhotonApplicationSuite}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { BrokerManagerTest.class,
        org.marketcetera.photon.MessagesTest.class,
        PhotonPositionMarketDataConcurrencyTest.class,
        PhotonPositionMarketDataTest.class, TimeOfDayTest.class,
        BrokerNotificationListenerTest.class, AddSymbolActionTest.class,
        MarketDataViewItemTest.class, NewOrReplaceOrderObservableTest.class,
        EquityObservableTest.class, OptionObservableTest.class,
        CommandParserTest.class, OrderSingleParserTest.class,
        org.marketcetera.photon.parser.MessagesTest.class,
        org.marketcetera.photon.views.MessagesTest.class,
        InstrumentMementoSerializerTest.class, StockOrderTicketViewTest.class,
        OptionOrderTicketViewTest.class })
public class PhotonSuite {
}
