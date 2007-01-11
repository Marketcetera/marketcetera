package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.core.IFeedComponentListener;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.quotefeed.QuoteFeedService;
import org.marketcetera.quotefeed.IQuoteFeed;
import org.osgi.framework.BundleContext;
import org.springframework.jms.core.JmsOperations;

import ca.odell.glazedlists.EventList;

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


public class MarketDataViewTest extends ViewTestBase {

	public MarketDataViewTest(String name) {
		super(name);
	}

	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		QuoteFeedService quoteFeed = getNullQuoteFeedService();
		bundleContext.registerService(QuoteFeedService.class.getName(), quoteFeed, null);
		
		
		MarketDataView view = (MarketDataView) getTestView();
		view.addSymbol(new MSymbol("MRKT"));
		EventList<MessageHolder> input = view.getInput();
		assertEquals(1, input.size());

		JmsOperations jmsOperations = PhotonPlugin.getDefault().getQuoteJmsOperations();
		MarketDataSnapshotFullRefresh fixMessage = new MarketDataSnapshotFullRefresh();
		fixMessage.set(new Symbol("MRKT"));
		
		addGroup(fixMessage, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		addGroup(fixMessage, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		fixMessage.setString(LastPx.FIELD,"123.4");
		
		jmsOperations.convertAndSend(fixMessage);
		
		// TODO: fix me...
		delay(10000);
		
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
	
	public static QuoteFeedService getNullQuoteFeedService() {
		QuoteFeedService feedService = new QuoteFeedService();
		feedService.setQuoteFeed(new IQuoteFeed() {

			public JmsOperations getQuoteJmsOperations() {
				return null;
			}

			public JmsOperations getTradeJmsOperations() {
				return null;
			}

			public void listenLevel2(MSymbol symbol) {
			}

			public void listenQuotes(MSymbol symbol) {
			}

			public void listenTrades(MSymbol symbol) {
			}

			public void setQuoteJmsOperations(JmsOperations tradeOperations) {
			}

			public void setTradeJmsOperations(JmsOperations tradeOperations) {
			}

			public void unlistenLevel2(MSymbol symbol) {
			}

			public void unlistenQuotes(MSymbol symbol) {
			}

			public void unlistenTrades(MSymbol symbol) {
			}

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
			
		});
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

}
