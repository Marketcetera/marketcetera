package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Table;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.MockEventTranslator;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.osgi.framework.BundleContext;

import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryTime;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import ca.odell.glazedlists.EventList;


public class MarketDataViewTest extends ViewTestBase {


	private MarketDataFeedTracker marketDataFeedTracker;

	public MarketDataViewTest(String name) {
		super(name);

		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		marketDataFeedTracker = new MarketDataFeedTracker(bundleContext);
		marketDataFeedTracker.open();
	}

	public void testShowQuote() throws Exception {
		MarketDataSnapshotFullRefresh fixMessage = new MarketDataSnapshotFullRefresh();
		fixMessage.set(new Symbol("MRKT"));
		
		addGroup(fixMessage, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		addGroup(fixMessage, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		fixMessage.setString(LastPx.FIELD,"123.4");
		
		MockEventTranslator.setMessageToReturn(fixMessage);

		MarketDataView view = (MarketDataView) getTestView();
		view.addSymbol(new MSymbol("MRKT"));
		
        final EventList<MessageHolder> input = view.getInput();     

        doDelay(new Callable<Boolean>() {
            public Boolean call() 
                throws Exception
            {
                try {
                    input.get(0).getMessage().getInt(NoMDEntries.FIELD);
                    return true;
                } catch (Throwable t) {
                    return false;
                }
            }		    
        });
		
		assertEquals(1, input.size());		
		MessageHolder messageHolder = input.get(0);
		Message message = messageHolder.getMessage();
		assertEquals("MRKT", message.getString(Symbol.FIELD));
		int noEntries = message.getInt(NoMDEntries.FIELD);
		for (int i = 1; i < noEntries+1; i++){
			MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			message.getGroup(i, group);
			if (i == 1){
				assertEquals(MDEntryType.BID, group.getChar(MDEntryType.FIELD));
				assertEquals(1, group.getInt(MDEntryPx.FIELD));
			} else if (i == 2) {
				assertEquals(MDEntryType.OFFER, group.getChar(MDEntryType.FIELD));
				assertEquals(10, group.getInt(MDEntryPx.FIELD));
			} else {
				assertTrue(false);
			}
		}
	}

	public void testLastPxValue() throws Exception {
		
		MarketDataSnapshotFullRefresh fixMessage = new MarketDataSnapshotFullRefresh();
		fixMessage.set(new Symbol("MRKT"));
		
		final String priceWithZeroes = "123.400";
		addGroup(fixMessage, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		addGroup(fixMessage, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		addGroup(fixMessage, MDEntryType.TRADE, new BigDecimal(priceWithZeroes), BigDecimal.TEN, new Date(), "BGUS");
		
		MockEventTranslator.setMessageToReturn(fixMessage);
		
		final MarketDataView view = (MarketDataView)getTestView();
		view.addSymbol(new MSymbol("MRKT"));
		final Table tableView = (Table)view.getMessagesViewer().getControl();
		doDelay(new Callable<Boolean>() {
            public Boolean call() 
                throws Exception
            {
                return tableView.getItem(0).getText(2).equals(priceWithZeroes);
            }		    
		});
        String text = tableView.getItem(0).getText(2);
		assertEquals(priceWithZeroes, text);
	}

	public static void addGroup(Message message, char side, BigDecimal price, BigDecimal quantity, Date time, String mkt) {
        MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
        group.set(new MDEntryType(side));
        group.set(new MDEntryTime(time));
        group.set(new MDMkt(mkt));
		group.set(new MDEntryPx(price));
		group.set(new MDEntrySize(quantity));
        message.addGroup(group);
    }

	@Override
	protected String getViewID() {
		return MarketDataView.ID;
	}
}
