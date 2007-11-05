package org.marketcetera.photon.parser;

import java.math.BigDecimal;

import junit.framework.Test;

import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.model.FIXMessageHistoryTest;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

public class OrderFormatterTest extends FIXVersionedTestCase {

    public OrderFormatterTest(String inName, FIXVersion version) {
		super(inName, version);
	}

    public static Test suite() {
    	return new FIXVersionTestSuite(OrderFormatterTest.class, FIXVersion.values());
    }

	public void testFormat() {
		OrderFormatter formatter = new OrderFormatter(fixDD.getDictionary());
		Message order1 = msgFactory.newMarketOrder("1", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), TimeInForce.DAY, null);
		assertEquals("B 100 IBM MKT", formatter.format(order1));

		Message order2 = msgFactory.newLimitOrder("1", Side.SELL_SHORT, new BigDecimal(100), new MSymbol("IBM"), BigDecimal.TEN, TimeInForce.DAY, null);
		assertEquals("SS 100 IBM 10", formatter.format(order2));
		
		Message optionOrder = msgFactory.newMarketOrder("1", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), TimeInForce.DAY, null);
		assertEquals("B 100 IBM MKT", formatter.format(order1));

	}

}
