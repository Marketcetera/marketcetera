package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.IMarketDataFeed;
import org.marketcetera.marketdata.IMarketDataListener;
import org.marketcetera.marketdata.IMessageSelector;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.marketdata.MarketDataFeedService;
import org.osgi.framework.BundleContext;
import org.springframework.jms.core.JmsOperations;

import quickfix.Message;
import quickfix.StringField;
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


	public MarketDataViewTest(String name) {
		super(name);
	}

	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		MarketDataFeedService marketDataFeedService = getNullQuoteFeedService();
		bundleContext.registerService(MarketDataFeedService.class.getName(), marketDataFeedService, null);
		
		
		MarketDataView view = (MarketDataView) getTestView();
		view.addSymbol(new MSymbol("MRKT"));
		EventList<MessageHolder> input = view.getInput();
		assertEquals(1, input.size());

		MarketDataSnapshotFullRefresh fixMessage = new MarketDataSnapshotFullRefresh();
		fixMessage.set(new Symbol("MRKT"));
		
		addGroup(fixMessage, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		addGroup(fixMessage, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		fixMessage.setString(LastPx.FIELD,"123.4");
		
		((MyMarketDataFeed)marketDataFeedService.getMarketDataFeed()).sendMessage(fixMessage);
		
		// TODO: fix me...
		//delay(10000);
		
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
	
	public static MarketDataFeedService getNullQuoteFeedService() {
		MarketDataFeedService feedService = new MarketDataFeedService(new MyMarketDataFeed());
		return feedService;
	}

	public static void addGroup(Message message, char side, BigDecimal price, BigDecimal quantity, Date time, String mkt) {
		MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
		group.set(new MDEntryType(side));
		group.set(new MDEntryTime(time));
		group.set(new MDMkt(mkt));
		group.setField(new StringField(MDEntryPx.FIELD, price.toPlainString()));
		group.setField(new StringField(MDEntrySize.FIELD, quantity.toPlainString()));
		message.addGroup(group);
	}

	@Override
	protected String getViewID() {
		return MarketDataView.ID;
	}

	public static final class MyMarketDataFeed implements IMarketDataFeed {
		private IMarketDataListener marketDataListener;

		public void addFeedComponentListener(IFeedComponentListener listener) {
		}

		public FeedStatus getFeedStatus() {
			return FeedStatus.AVAILABLE;
		}

		public FeedType getFeedType() {
			return FeedType.SIMULATED;
		}

		public String getID() {
			return "";
		}

		public void removeFeedComponentListener(IFeedComponentListener listener) {
		}

		public boolean isRunning() {
			return true;
		}

		public void start() {
		}

		public void stop() {
		}

		public IMarketDataListener getMarketDataListener() {
			return marketDataListener;
		}

		public void setMarketDataListener(IMarketDataListener listener) {
			marketDataListener = listener;
		}

		public void subscribe(IMessageSelector selector) {
			// TODO Auto-generated method stub
			
		}

		public MSymbol symbolFromString(String symbolString) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean unsubscribe(IMessageSelector selector) {
			// TODO Auto-generated method stub
			return false;
		}

		public void sendMessage(Message aMessage) {
			marketDataListener.onMessage(aMessage);
		}
	}
}
