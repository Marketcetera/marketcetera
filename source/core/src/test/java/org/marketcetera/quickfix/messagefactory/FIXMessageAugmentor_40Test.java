package org.marketcetera.quickfix.messagefactory;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_40Test extends TestCase {
    public FIXMessageAugmentor_40Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_40Test.class);
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(4, new FIXMessageAugmentor_40().getApplicableMsgTypes().size());
    }

    /* Verify the behaviour where we have a MarketOnClose order
       We get a Market order and At_The_Close, and we since
       AtTheClose only shows up in FIX.4.3 we need to translate it
       into MARKET_ON_CLOSE and DAY order
    */
    public void testMarketOnClose() throws Exception
    {
        FIXMessageFactory factory = FIXVersion.FIX40.getMessageFactory();
        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", 123, Side.BUY, factory);
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        buy = augmentor.newOrderSingleAugment(buy);

        assertEquals(OrdType.MARKET_ON_CLOSE, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", 213, Side.BUY, factory);
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(buy);
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

    public void testLimitOnClose() throws Exception
    {
        FIXMessageFactory factory = FIXVersion.FIX40.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", 123, 100, Side.BUY, factory);
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        buy = augmentor.newOrderSingleAugment(buy);

        assertEquals(OrdType.LIMIT_ON_CLOSE, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", 213, 100, Side.BUY, factory);
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(buy);
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

}
