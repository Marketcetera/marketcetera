package org.marketcetera.photon.marketdata;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.TestCase;

import org.marketcetera.core.AccessViolator;
import org.marketcetera.photon.marketdata.OptionMessageHolder.OptionPairKey;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix44.DerivativeSecurityList;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

public class OptionMessageHolderTest extends TestCase {

	public void testGetKey() throws ParseException, FieldNotFound, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		MSymbol symbol = new MSymbol("IBM");
		FieldMap strikeInfo = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StrikePrice(new BigDecimal("72.5")));
		strikeInfo.setField(new MaturityMonthYear("200711"));

		FieldMap callMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+AE"));

		FieldMap putMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+RE"));

		OptionMessageHolder holder = new OptionMessageHolder("IBM", strikeInfo, callMessage, putMessage);
		AccessViolator violator = new AccessViolator(holder.getClass());
		OptionPairKey key = (OptionPairKey) violator.invokeMethod("getKey", holder);
		assertEquals(0, key.getExpirationDay());
		assertEquals(11, key.getExpirationMonth());
		assertEquals(2007, key.getExpirationYear());
		assertEquals("IBM", key.getOptionRoot());
		assertEquals(new BigDecimal("72.5"), key.getStrikePrice());
	}

	public void testGetMarketDataForSymbol() throws ParseException, FieldNotFound {
		MSymbol symbol = new MSymbol("IBM");
		FieldMap strikeInfo = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StrikePrice(new BigDecimal("72.5")));
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
	
	public void testCompareTo() throws Exception {
		FieldMap strikeInfo = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StrikePrice(new BigDecimal("72.5")));
		strikeInfo.setField(new MaturityMonthYear("200711"));

		FieldMap callMessage = new MarketDataSnapshotFullRefresh();
		callMessage.setField(new Symbol("IBM+AE"));

		FieldMap putMessage = new MarketDataSnapshotFullRefresh();
		putMessage.setField(new Symbol("IBM+RE"));

		FieldMap callExtraInfo = new DerivativeSecurityList.NoRelatedSym();
		callExtraInfo.setField(new Symbol("IBM+AE"));
		
		FieldMap putExtraInfo = new DerivativeSecurityList.NoRelatedSym();
		putExtraInfo.setField(new Symbol("IBM+RE"));

		OptionMessageHolder holder1 = new OptionMessageHolder("IBM", strikeInfo, callExtraInfo, putExtraInfo);
		OptionMessageHolder holder2 = new OptionMessageHolder("IBM", strikeInfo, callExtraInfo, putExtraInfo);

		FieldMap strikeInfo3 = new DerivativeSecurityList.NoRelatedSym();
		strikeInfo.setField(new StrikePrice(new BigDecimal("80")));
		strikeInfo.setField(new MaturityMonthYear("200711"));
		
		OptionMessageHolder holder3 = new OptionMessageHolder("IBM", strikeInfo, callExtraInfo, putExtraInfo);

		assertEquals(1, holder1.compareTo(null));
		assertEquals(0, holder2.compareTo(holder1));
		assertEquals(-1, holder2.compareTo(holder3));
		
	}

}
