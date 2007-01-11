package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Text;
import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.quotefeed.QuoteFeedService;
import org.marketcetera.photon.ui.BookComposite;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.osgi.framework.BundleContext;
import org.springframework.jms.core.JmsOperations;

import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntryType;
import quickfix.field.NoMDEntries;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class StockOrderTicketViewTest extends ViewTestBase {

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
	}

	public StockOrderTicketViewTest(String name) {
		super(name);
	}

	public void testShowOrder() throws NoSuchFieldException, IllegalAccessException {
		StockOrderTicket ticket = (StockOrderTicket) getTestView();
		Message message = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),
				Side.BUY, BigDecimal.TEN, new MSymbol("QWER"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		ticket.showOrder(message);
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);
		assertEquals("10", ((Text)violator.getField("quantityText", ticket)).getText());
		assertEquals("B", ((CCombo)violator.getField("sideCCombo", ticket)).getText());
		assertEquals("1", ((Text)violator.getField("priceText", ticket)).getText());
		assertEquals("QWER", ((Text)violator.getField("symbolText", ticket)).getText());
		assertEquals("DAY", ((CCombo)violator.getField("tifCCombo", ticket)).getText());
	}
	
	public void testShowQuote() throws Exception {
		BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
		QuoteFeedService quoteFeed = MarketDataViewTest.getNullQuoteFeedService();
		bundleContext.registerService(QuoteFeedService.class.getName(), quoteFeed, null);
		
		
		StockOrderTicket view = (StockOrderTicket) getTestView();
		Message orderMessage = FIXMessageUtil.newLimitOrder(new InternalID("asdf"),
				Side.BUY, BigDecimal.TEN, new MSymbol("MRKT"), BigDecimal.ONE,
				TimeInForce.DAY, null);
		view.showOrder(orderMessage);

		
		JmsOperations jmsOperations = PhotonPlugin.getDefault().getQuoteJmsOperations();
		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol("MRKT"));
		
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, BigDecimal.TEN, BigDecimal.TEN, new Date(), "BGUS");
		quoteMessageToSend.setString(LastPx.FIELD,"123.4");
		
		jmsOperations.convertAndSend(quoteMessageToSend);
		
		// TODO: fix me...
		delay(10000);
		
		AccessViolator violator = new AccessViolator(StockOrderTicket.class);

		BookComposite bookComposite = (BookComposite) violator.getField("bookComposite", view);
		Message returnedMessage = bookComposite.getInput();

		assertEquals("MRKT", returnedMessage.getString(Symbol.FIELD));
		int noEntries = returnedMessage.getInt(NoMDEntries.FIELD);
		for (int i = 1; i < noEntries+1; i++){
			MarketDataSnapshotFullRefresh.NoMDEntries group = new MarketDataSnapshotFullRefresh.NoMDEntries();
			returnedMessage.getGroup(i, group);
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

	@Override
	protected String getViewID() {
		return StockOrderTicket.ID;
	}

}
