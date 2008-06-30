package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import junit.framework.Test;
import junit.framework.TestCase;

import java.math.BigDecimal;

import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.TimeInForce;
import quickfix.field.Price;
import quickfix.field.OrderQty;

/**
 * Verify that the round-tripping of BigDecimals works with QFJ
 * this is testing the fix for bug QFJ-300 (http://www.quickfixj.org/jira/browse/QFJ-300)
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DecimalFieldTest extends TestCase {
    public DecimalFieldTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DecimalFieldTest.class);
    }

    /** Verify that the round-tripping of BigDecimals works with QFJ
     * this is testing the fix for bug QFJ-300 (http://www.quickfixj.org/jira/browse/QFJ-300)
     *  */
    public void testQFJ_BigDecimal() throws Exception {
        BigDecimal originalPrice = new BigDecimal("10.3000");
        assertEquals(4, originalPrice.scale());
        FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
        Message newOrder = msgFactory.newLimitOrder("ASDF", Side.BUY, new BigDecimal("100"), new MSymbol("FOO"),
                originalPrice, TimeInForce.DAY, "123");
        BigDecimal extractedPrice = newOrder.getDecimal(Price.FIELD);
        assertEquals(4, extractedPrice.scale());
        assertEquals(new BigDecimal("10.3000"), extractedPrice);
        String newOrderString = newOrder.toString();
        Message rehydratedMessage = new Message(newOrderString);
        BigDecimal rehydratedPrice = rehydratedMessage.getDecimal(Price.FIELD);
        assertEquals(new BigDecimal("10.3000"), rehydratedPrice);
        assertEquals(4, rehydratedPrice.scale());
    }

    public void testZero() throws Exception {
        FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
        Message newOrder = msgFactory.newLimitOrder("ASDF", Side.BUY, new BigDecimal(0), new MSymbol("FOO"),
                BigDecimal.ZERO, TimeInForce.DAY, "123");
        assertEquals(BigDecimal.ZERO, newOrder.getDecimal(Price.FIELD));
        assertEquals(BigDecimal.ZERO, newOrder.getDecimal(OrderQty.FIELD));
    }

}
