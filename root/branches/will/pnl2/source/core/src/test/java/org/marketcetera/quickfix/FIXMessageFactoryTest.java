package org.marketcetera.quickfix;

import junit.framework.Test;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

public class FIXMessageFactoryTest extends FIXVersionedTestCase {


	
	public FIXMessageFactoryTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(FIXMessageFactoryTest.class, FIXVersion.values(), 
                new HashSet<String>(Arrays.asList("testNewBusinessMessageReject")), FIXVersionTestSuite.FIX42_PLUS_VERSIONS); //$NON-NLS-1$
	}
	
	public void testNewLimitOrder() throws FieldNotFound {
        String clOrderID = "1"; //$NON-NLS-1$
        char side = Side.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        MSymbol symbol = new MSymbol("MRKT", org.marketcetera.trade.SecurityType.CommonStock); //$NON-NLS-1$
        BigDecimal price = BigDecimal.ONE;
        char timeInForce = TimeInForce.GOOD_TILL_CROSSING;
        String account = "ASDF"; //$NON-NLS-1$

        Message limitOrder = this.msgFactory.newLimitOrder(clOrderID, side, quantity, symbol, price, timeInForce, account);

        assertEquals(clOrderID, limitOrder.getString(ClOrdID.FIELD));
        assertEquals(side, limitOrder.getChar(Side.FIELD));
        assertEquals(quantity, limitOrder.getDecimal(OrderQty.FIELD));
        assertEquals(symbol.toString(), limitOrder.getString(Symbol.FIELD));
        assertEquals(symbol.getSecurityType().getFIXValue(), limitOrder.getString(SecurityType.FIELD));
        assertEquals(OrdType.LIMIT, limitOrder.getChar(OrdType.FIELD));
        assertEquals(price, limitOrder.getDecimal(Price.FIELD));
        assertEquals(timeInForce, limitOrder.getChar(TimeInForce.FIELD));
        assertEquals(account, limitOrder.getString(Account.FIELD));
    }

	public void testNewMarketOrder() throws FieldNotFound {
        String clOrderID = "1"; //$NON-NLS-1$
        char side = Side.BUY;
        BigDecimal quantity = BigDecimal.TEN;
        MSymbol symbol = new MSymbol("MRKT", org.marketcetera.trade.SecurityType.Option); //$NON-NLS-1$
        char timeInForce = TimeInForce.GOOD_TILL_CROSSING;
        String account = "ASDF"; //$NON-NLS-1$

        Message marketOrder = this.msgFactory.newMarketOrder(clOrderID, side, quantity, symbol, timeInForce, account);

        assertEquals(clOrderID, marketOrder.getString(ClOrdID.FIELD));
        assertEquals(side, marketOrder.getChar(Side.FIELD));
        assertEquals(quantity, marketOrder.getDecimal(OrderQty.FIELD));
        assertEquals(symbol.toString(), marketOrder.getString(Symbol.FIELD));
        assertEquals(symbol.getSecurityType().getFIXValue(), marketOrder.getString(SecurityType.FIELD));
        assertEquals(OrdType.MARKET, marketOrder.getChar(OrdType.FIELD));
        assertEquals(timeInForce, marketOrder.getChar(TimeInForce.FIELD));
        assertEquals(account, marketOrder.getString(Account.FIELD));
    }

	public void testNewBasicOrder() throws FieldNotFound {
		Message basicOrder = msgFactory.newBasicOrder();
		assertEquals(MsgType.ORDER_SINGLE, basicOrder.getHeader().getString(MsgType.FIELD));
		if (this.fixDD.getDictionary().isRequiredField(MsgType.ORDER_SINGLE,
                TransactTime.FIELD) && FIXVersion.FIX_SYSTEM != fixVersion){
			// just make sure it's there:
			basicOrder.getString(TransactTime.FIELD);
		}			
	}


    /** Verify that if we include a "failing" order as a Text reason in a reject,
     * the SOH fields get appropriately escaped
     */
    public void testNewOrderCancelReject_escapesSOH() throws Exception {
        Message basicOrder = msgFactory.newBasicOrder();
		Message reject = msgFactory.newOrderCancelReject(new OrderID("35"), new ClOrdID("36"), new OrigClOrdID("37"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                basicOrder.toString(), new CxlRejReason(CxlRejReason.UNKNOWN_ORDER));
        assertTrue("reject doesn't contain |:" +reject.toString(), reject.toString().indexOf(FIXMessageFactory.SOH_REPLACE_CHAR) != -1); //$NON-NLS-1$
        assertNotNull(new Message(reject.toString()));
    }

    public void testNewOrderCancelReject() throws Exception {
        Message basicOrder = msgFactory.newBasicOrder();
		Message reject = msgFactory.newOrderCancelReject(new OrderID("bob"), new ClOrdID("36"), new OrigClOrdID("37"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                basicOrder.toString(), new CxlRejReason(CxlRejReason.UNKNOWN_ORDER));
        assertTrue(reject.isSetField(CxlRejReason.FIELD));
        assertNotNull(new Message(reject.toString()));
    }
    
    public void testNewResendRequest() throws Exception {
    	Message rr;
    	rr = msgFactory.newResendRequest(null, null);
    	assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(MsgType.FIELD));
    	assertEquals(0, rr.getInt(BeginSeqNo.FIELD));
    	assertEquals(0, rr.getInt(EndSeqNo.FIELD));
    	rr = msgFactory.newResendRequest(null, BigInteger.TEN);
    	assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(MsgType.FIELD));
    	assertEquals(0, rr.getInt(BeginSeqNo.FIELD));
    	assertEquals(10, rr.getInt(EndSeqNo.FIELD));
    	rr = msgFactory.newResendRequest(BigInteger.TEN, null);
    	assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(MsgType.FIELD));
    	assertEquals(10, rr.getInt(BeginSeqNo.FIELD));
    	assertEquals(0, rr.getInt(EndSeqNo.FIELD));
    	rr = msgFactory.newResendRequest(new BigInteger("24"), new BigInteger("38")); //$NON-NLS-1$ //$NON-NLS-2$
    	assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(MsgType.FIELD));
    	assertEquals(24, rr.getInt(BeginSeqNo.FIELD));
    	assertEquals(38, rr.getInt(EndSeqNo.FIELD));
    }

    public void testNewBusinessMessageReject() throws Exception {
        Message msg = msgFactory.newBusinessMessageReject(MsgType.BID_REQUEST, BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE, "bob"); //$NON-NLS-1$
        assertEquals(MsgType.BUSINESS_MESSAGE_REJECT, msg.getHeader().getString(MsgType.FIELD));
        assertEquals(BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE, msg.getInt(BusinessRejectReason.FIELD));
        assertEquals("bob", msg.getString(Text.FIELD)); //$NON-NLS-1$
    }

    // Verify that LOC is preserved to the Cancel/Replace from buy order
    public void testNewCancelReplaceFromMessage_withLOC() throws Exception {
        Message buy = FIXMessageUtilTest.createNOS("IBM", new BigDecimal("85.84"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        // make it "On close" order - and the augmentor will translate it appropriately
        buy.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
        buy = fixVersion.getMessageFactory().getMsgAugmentor().newOrderSingleAugment(buy);
        char oldOrdType = buy.getChar(OrdType.FIELD);
        char oldTIF = buy.getChar(TimeInForce.FIELD);

        Message replace = msgFactory.newCancelReplaceFromMessage(buy);

        assertEquals("ord types different", oldOrdType, replace.getChar(OrdType.FIELD)); //$NON-NLS-1$
        assertEquals("TIF different", oldTIF, replace.getChar(TimeInForce.FIELD)); //$NON-NLS-1$
    }
}
