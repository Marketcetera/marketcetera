package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.marketdata.MarketceteraSubscription;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.SecurityReqID;
import quickfix.field.Symbol;
import quickfix.fix44.DerivativeSecurityList;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

public class OptionOrderTicketControllerTest extends TestCase {
	private OptionOrderTicketController controller;

    @Override
    protected void setUp() throws Exception {
    	super.setUp();
		controller = PhotonPlugin.getDefault().getOptionOrderTicketController();

    }
	public void testShowStockQuote() throws Exception {
		final String optionRoot = "MRK";
		final String callContractSpecifier = "GA";
		final String putContractSpecifier = "RA";

		controller.requestOptionRootInfo(optionRoot);

		DerivativeSecurityList securityList = OptionOrderTicketViewTest.createDummySecurityList(optionRoot, new String[] { callContractSpecifier }, new String[] { putContractSpecifier }, new BigDecimal[] { BigDecimal.TEN });
		securityList.setField(new SecurityReqID(((MarketceteraSubscription)controller.getDerivativeSecurityListSubscription()).getCorrelationFieldValue()));

		controller.onMessage(securityList);

		controller.listenMarketData(optionRoot);
		
		MarketDataSnapshotFullRefresh quoteMessageToSend = new MarketDataSnapshotFullRefresh();
		quoteMessageToSend.set(new Symbol(optionRoot));

		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.BID, BigDecimal.ONE, BigDecimal.TEN, new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OFFER, new BigDecimal(93), new BigDecimal(88), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADE, new BigDecimal(89), new BigDecimal(103), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADE_VOLUME, new BigDecimal(1000), new BigDecimal(2000), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.CLOSING_PRICE, new BigDecimal(13), new BigDecimal(14), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.OPENING_PRICE, new BigDecimal(15), new BigDecimal(16), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADING_SESSION_HIGH_PRICE, new BigDecimal(66), new BigDecimal(67), new Date(), "BGUS");
		MarketDataViewTest.addGroup(quoteMessageToSend, MDEntryType.TRADING_SESSION_LOW_PRICE, new BigDecimal(61), new BigDecimal(62), new Date(), "BGUS");
		quoteMessageToSend.setField(new MDReqID(((MarketceteraSubscription)controller.getCurrentSubscription()).getCorrelationFieldValue()));

		controller.onMessage(quoteMessageToSend);

		OptionOrderTicketModel model = PhotonPlugin.getDefault().getOptionOrderTicketModel();

		assertEquals("MRK", model.getUnderlyingSymbol().getValue());
		assertEquals(new BigDecimal(89), model.getUnderlyingLastPrice().getValue());
		assertEquals(BigDecimal.ONE, model.getUnderlyingBidPrice().getValue());
		assertEquals(BigDecimal.TEN, model.getUnderlyingBidSize().getValue());
		assertEquals(new BigDecimal(93), model.getUnderlyingOfferPrice().getValue());
		assertEquals(new BigDecimal(88), model.getUnderlyingOfferSize().getValue());
		assertEquals(new BigDecimal(2000), model.getUnderlyingVolume().getValue());
		assertEquals(new BigDecimal(15), model.getUnderlyingOpenPrice().getValue());
		assertEquals(new BigDecimal(66), model.getUnderlyingHighPrice().getValue());
		assertEquals(new BigDecimal(61), model.getUnderlyingLowPrice().getValue());
		assertEquals(new BigDecimal(13), model.getUnderlyingPreviousClosePrice().getValue());


		// TODO: fix these
//		assertEquals(, model.getUnderlyingTickIndicator().getValue());
//		assertEquals(, model.getUnderlyingLastPriceChange().getValue());

	}

}
