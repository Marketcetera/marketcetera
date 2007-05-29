package org.marketcetera.quickfix;

import junit.framework.Test;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.MSymbol;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;

public class FIXMessageFactoryTest extends FIXVersionedTestCase {


	
	public FIXMessageFactoryTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(FIXMessageFactoryTest.class, FIXVersion.values());
	}
	
	public void testNewLimitOrder() throws FieldNotFound {
		String clOrderID = "1";
		char side = Side.BUY;
		BigDecimal quantity = BigDecimal.TEN;
		MSymbol symbol = new MSymbol("MRKT");
		BigDecimal price = BigDecimal.ONE;
		char timeInForce = TimeInForce.GOOD_TILL_CROSSING;
		String account = "ASDF";

		Message limitOrder = this.msgFactory.newLimitOrder(clOrderID, side, quantity, symbol, price, timeInForce, account);

		assertEquals(clOrderID, limitOrder.getString(ClOrdID.FIELD));
		assertEquals(side, limitOrder.getChar(Side.FIELD));
		assertEquals(quantity.toPlainString(), limitOrder.getString(OrderQty.FIELD));
		assertEquals(symbol.toString(), limitOrder.getString(Symbol.FIELD));
		assertEquals(OrdType.LIMIT, limitOrder.getChar(OrdType.FIELD));
		assertEquals(price.toPlainString(), limitOrder.getString(Price.FIELD));
		assertEquals(timeInForce, limitOrder.getChar(TimeInForce.FIELD));
		assertEquals(account, limitOrder.getString(Account.FIELD));
	}

	public void testNewMarketOrder() throws FieldNotFound {
		String clOrderID = "1";
		char side = Side.BUY;
		BigDecimal quantity = BigDecimal.TEN;
		MSymbol symbol = new MSymbol("MRKT");
		char timeInForce = TimeInForce.GOOD_TILL_CROSSING;
		String account = "ASDF";

		Message marketOrder = this.msgFactory.newMarketOrder(clOrderID, side, quantity, symbol, timeInForce, account);

		assertEquals(clOrderID, marketOrder.getString(ClOrdID.FIELD));
		assertEquals(side, marketOrder.getChar(Side.FIELD));
		assertEquals(quantity.toPlainString(), marketOrder.getString(OrderQty.FIELD));
		assertEquals(symbol.toString(), marketOrder.getString(Symbol.FIELD));
		assertEquals(OrdType.MARKET, marketOrder.getChar(OrdType.FIELD));
		assertEquals(timeInForce, marketOrder.getChar(TimeInForce.FIELD));
		assertEquals(account, marketOrder.getString(Account.FIELD));
	}

	public void testNewBasicOrder() throws FieldNotFound {
		Message basicOrder = msgFactory.newBasicOrder();
		assertEquals(MsgType.ORDER_SINGLE, basicOrder.getHeader().getString(MsgType.FIELD));
		if (this.fixDD.getDictionary().isRequiredField(MsgType.ORDER_SINGLE, TransactTime.FIELD)){
			// just make sure it's there:
			basicOrder.getString(TransactTime.FIELD);
		}			
	}


    /** Verify that if we include a "failing" order as a Text reason in a reject,
     * the SOH fields get appropriately escaped
     */
    public void testNewOrderCancelReject_escapesSOH() throws Exception {
        Message basicOrder = msgFactory.newBasicOrder();
		Message reject = msgFactory.newOrderCancelReject(new OrderID("35"), new ClOrdID("36"), new OrigClOrdID("37"),
                basicOrder.toString(), new CxlRejReason(CxlRejReason.UNKNOWN_ORDER));
        assertTrue("reject doesn't contain |:" +reject.toString(), reject.toString().indexOf(FIXMessageFactory.SOH_REPLACE_CHAR) != -1);
        assertNotNull(new Message(reject.toString()));
    }

    public void testNewOrderCancelReject() throws Exception {
        Message basicOrder = msgFactory.newBasicOrder();
		Message reject = msgFactory.newOrderCancelReject(new OrderID("bob"), new ClOrdID("36"), new OrigClOrdID("37"),
                basicOrder.toString(), new CxlRejReason(CxlRejReason.UNKNOWN_ORDER));
        assertTrue(reject.isSetField(CxlRejReason.FIELD));
        assertNotNull(new Message(reject.toString()));
    }

}
