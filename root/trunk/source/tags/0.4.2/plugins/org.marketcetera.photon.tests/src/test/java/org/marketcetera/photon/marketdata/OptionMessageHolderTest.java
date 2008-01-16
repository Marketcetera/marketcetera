package org.marketcetera.photon.marketdata;

import java.text.ParseException;

import junit.framework.TestCase;

import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.StringField;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix44.DerivativeSecurityList;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.MarketDataRequest.NoRelatedSym;

public class OptionMessageHolderTest extends TestCase {

	public void testGetKey() throws ParseException, FieldNotFound {
		MSymbol symbol = new MSymbol("IBM");
		FieldMap strikeInfo = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StringField(StrikePrice.FIELD, "72.5"));
		strikeInfo.setField(new MaturityMonthYear("200711"));

		FieldMap callMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+AE"));

		FieldMap putMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+RE"));

		OptionMessageHolder holder = new OptionMessageHolder("IBM", strikeInfo, callMessage, putMessage);
		OptionPairKey key = holder.getKey();
		assertEquals(0, key.getExpirationDay());
		assertEquals(11, key.getExpirationMonth());
		assertEquals(2007, key.getExpirationYear());
		assertEquals("IBM", key.getOptionRoot());
		assertEquals("72.5", key.getStrikePrice().toPlainString());
	}

	public void testGetMarketDataForSymbol() throws ParseException, FieldNotFound {
		MSymbol symbol = new MSymbol("IBM");
		FieldMap strikeInfo = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StringField(StrikePrice.FIELD, "72.5"));
		strikeInfo.setField(new MaturityMonthYear("200711"));

		FieldMap callMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+AE"));

		FieldMap putMessage = new MarketDataSnapshotFullRefresh();
		putMessage.setField(new Symbol("IBM+RE"));

		FieldMap callExtraInfo = new DerivativeSecurityList.NoRelatedSym();
		callExtraInfo.setField(new Symbol("IBM+AE"));
		
		FieldMap putExtraInfo = new DerivativeSecurityList.NoRelatedSym();
		putExtraInfo.setField(new Symbol("IBM+RE"));

		OptionMessageHolder holder = new OptionMessageHolder("IBM", strikeInfo, callExtraInfo, putExtraInfo);
		holder.setMarketData(PutOrCall.CALL, callMessage);
		holder.setMarketData(PutOrCall.PUT, putMessage);
		assertTrue(callMessage == holder.getMarketDataForSymbol("IBM+AE"));
		assertTrue(putMessage == holder.getMarketDataForSymbol("IBM+RE"));
	}

}
