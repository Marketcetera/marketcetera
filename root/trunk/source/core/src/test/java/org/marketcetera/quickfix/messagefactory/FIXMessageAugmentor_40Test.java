package org.marketcetera.quickfix.messagefactory;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageAugmentor_40Test extends FIXVersionedTestCase {
    public FIXMessageAugmentor_40Test(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        // we dont' really want to setup multi-versioned tests, but it's the easiest way to create the factories
        return new FIXVersionTestSuite(FIXMessageAugmentor_40Test.class, new FIXVersion[] {FIXVersion.FIX40});
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
        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("123"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        buy = augmentor.newOrderSingleAugment(buy);

        assertEquals(OrdType.MARKET_ON_CLOSE, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("213"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(buy);
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

    // same but for cancel/replace orders
    public void testMarketOnClose_cxr() throws Exception
    {
        FIXMessageFactory factory = FIXVersion.FIX40.getMessageFactory();

        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("123"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        Message cancelReplace = factory.newCancelReplaceFromMessage(buy);

        assertEquals(OrdType.MARKET, cancelReplace.getChar(OrdType.FIELD));
        cancelReplace.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        cancelReplace = augmentor.cancelReplaceRequestAugment(cancelReplace);

        assertEquals(OrdType.MARKET_ON_CLOSE, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("213"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        cancelReplace = factory.newCancelReplaceFromMessage(buy);
        buy = augmentor.cancelReplaceRequestAugment(cancelReplace);
        assertEquals(OrdType.MARKET, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));
    }

    public void testLimitOnClose_cxr() throws Exception
    {
        FIXMessageFactory factory = FIXVersion.FIX40.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("123"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
        Message cancelReplace = factory.newCancelReplaceFromMessage(buy);

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        cancelReplace = augmentor.cancelReplaceRequestAugment(buy);

        assertEquals(OrdType.LIMIT_ON_CLOSE, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("213"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        cancelReplace = factory.newCancelReplaceFromMessage(buy);
        cancelReplace = augmentor.newOrderSingleAugment(cancelReplace);
        assertEquals(OrdType.LIMIT, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));
    }

    public void testLimitOnClose() throws Exception
    {
        FIXMessageFactory factory = FIXVersion.FIX40.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("123"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor_40 augmentor = new FIXMessageAugmentor_40();
        buy = augmentor.newOrderSingleAugment(buy);

        assertEquals(OrdType.LIMIT_ON_CLOSE, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("213"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(buy);
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

}
