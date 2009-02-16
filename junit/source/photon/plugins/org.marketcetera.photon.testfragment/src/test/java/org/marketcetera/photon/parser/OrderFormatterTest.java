package org.marketcetera.photon.parser;

import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.Message;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.TimeInForce;

public class OrderFormatterTest extends FIXVersionedTestCase {

    public OrderFormatterTest(String inName, FIXVersion version) {
		super(inName, version);
	}

    public static Test suite() {
    	return new FIXVersionTestSuite(OrderFormatterTest.class, FIXVersion.values());
    }

	public void testFormat() throws ParseException {
		OrderFormatter formatter = new OrderFormatter(fixDD.getDictionary());
		Message order1 = msgFactory.newMarketOrder("1", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), TimeInForce.DAY, null);
		assertEquals("B 100 IBM MKT", formatter.format(order1));

		Message order2 = msgFactory.newLimitOrder("1", Side.SELL_SHORT, new BigDecimal(100), new MSymbol("IBM"), BigDecimal.TEN, TimeInForce.DAY, null);
		assertEquals("SS 100 IBM 10", formatter.format(order2));
		
	}
	
	public void testOptionFormat() throws ParseException {
		OrderFormatter formatter = new OrderFormatter(fixDD.getDictionary());
		Message order1 = msgFactory.newMarketOrder("1", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), TimeInForce.DAY, null);
		order1.setField(new SecurityType(SecurityType.OPTION));
		order1.setField(new MaturityMonthYear("200810"));
		order1.setField(new StrikePrice(new BigDecimal(75)));
		order1.setField(new PutOrCall(PutOrCall.PUT));
		assertEquals("B 100 IBM 08Oct75P MKT", formatter.format(order1));

		Message order2 = msgFactory.newLimitOrder("1", Side.SELL, new BigDecimal(100), new MSymbol("IBM"), BigDecimal.TEN, TimeInForce.DAY, null);
		order2.setField(new SecurityType(SecurityType.OPTION));
		order2.setField(new MaturityDate("20081015"));
		order2.setField(new StrikePrice(new BigDecimal(75)));
		order2.setField(new PutOrCall(PutOrCall.CALL));
		assertEquals("S 100 IBM 08Oct75C 10", formatter.format(order2));
		
		
	}

}
