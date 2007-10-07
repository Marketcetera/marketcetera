package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import junit.framework.TestCase;
import junit.framework.Test;
import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.OrdType;
import quickfix.field.TimeInForce;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_43Test extends TestCase {
    public FIXMessageAugmentor_43Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_43Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(30, new FIXMessageAugmentor_43().getApplicableMsgTypes().size());
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testMarketOnClose() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", 123, Side.BUY, factory);
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));

        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, buy.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", 213, Side.BUY, factory);
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testLimitOnClose() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", 123, 100, Side.BUY, factory);
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));

        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, buy.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", 213, 100, Side.BUY, factory);
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }
}
