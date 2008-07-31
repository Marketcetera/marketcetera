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
public class FIXMessageAugmentor_43Test extends FIXVersionedTestCase {
    public FIXMessageAugmentor_43Test(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(FIXMessageAugmentor_43Test.class, new FIXVersion[] {FIXVersion.FIX40});
    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(30, new FIXMessageAugmentor_43().getApplicableMsgTypes().size());
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testMarketOnClose() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("123"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));

        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, buy.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("213"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testLimitOnClose() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("123"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));

        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, buy.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("213"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        buy = augmentor.newOrderSingleAugment(new FIXMessageAugmentor_40().newOrderSingleAugment(buy));
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, buy.getChar(TimeInForce.FIELD));
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testMarketOnClose_cxr() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("123"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OrdType.MARKET, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
        Message cancelReplace = factory.newCancelReplaceFromMessage(buy);

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        cancelReplace = augmentor.cancelReplaceRequestAugment(new FIXMessageAugmentor_40().cancelReplaceRequestAugment(cancelReplace));

        assertEquals(OrdType.MARKET, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, cancelReplace.getChar(TimeInForce.FIELD));

        // now send a non-MoC order make sure no changes are made
        buy = FIXMessageUtilTest.createMarketNOS("TOLI", new BigDecimal("213"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        cancelReplace = factory.newCancelReplaceFromMessage(buy);
        cancelReplace = augmentor.cancelReplaceRequestAugment(new FIXMessageAugmentor_40().cancelReplaceRequestAugment(cancelReplace));
        assertEquals(OrdType.MARKET, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));
    }

    /** Verify that we undo whatever changes the {@link FIXMessageAugmentor_40} does. */
    public void testLimitOnClose_cxr() throws Exception {
        FIXMessageFactory factory = FIXVersion.FIX43.getMessageFactory();
        Message buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("123"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(OrdType.LIMIT, buy.getChar(OrdType.FIELD));
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
        Message cancelReplace = factory.newCancelReplaceFromMessage(buy);

        FIXMessageAugmentor augmentor = new FIXMessageAugmentor_43();
        cancelReplace = augmentor.cancelReplaceRequestAugment(new FIXMessageAugmentor_40().cancelReplaceRequestAugment(cancelReplace));

        assertEquals(OrdType.LIMIT, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.AT_THE_CLOSE, cancelReplace.getChar(TimeInForce.FIELD));

        // now send a non-LoC order make sure no changes are made
        buy = FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("213"), new BigDecimal("100"), Side.BUY, factory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.setField(new TimeInForce(TimeInForce.DAY));
        cancelReplace = factory.newCancelReplaceFromMessage(buy);
        cancelReplace = augmentor.cancelReplaceRequestAugment(new FIXMessageAugmentor_40().cancelReplaceRequestAugment(cancelReplace));
        assertEquals(OrdType.LIMIT, cancelReplace.getChar(OrdType.FIELD));
        assertEquals(TimeInForce.DAY, cancelReplace.getChar(TimeInForce.FIELD));
    }
}
