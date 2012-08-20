package org.marketcetera.core.trade;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.ClOrdID;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Symbol;

import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link org.marketcetera.core.trade.Factory#createOrder(quickfix.Message, org.marketcetera.core.trade.BrokerID)}
 *
 * @author anshul@marketcetera.com
 * @version $Id: FIXOrderTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: FIXOrderTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public class FIXOrderTest extends TypesTestBase {
    /**
     * Tests FIX Message field accessors.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void checkFIXWrap() throws Exception {
        final BrokerID id = new BrokerID("blah");
        //Null value check for message.
        new ExpectedFailure<NullPointerException>(){
            protected void run() throws Exception {
                sFactory.createOrder(null,id);
            }
        };
        //create a message for each FIX version and verify that things work
        //Verify that the value is null when the field is not set.
        for(FIXVersion version: FIXVersionTestSuite.ALL_FIX_VERSIONS) {
            Message msg = version.getMessageFactory().newBasicOrder();
            msg.removeField(quickfix.field.SecurityType.FIELD);
            assertOrderValues(sFactory.createOrder(msg,id), id,
                    null);
        }
        //SecurityType is not available in 4.0
        //FIX version > 4.2 recommends using CFI Code instead of Security Type
        //for Futures and Options, but we'll ignore that for now and use
        //SecurityType field.
        for(FIXVersion version: FIXVersionTestSuite.ALL_FIX_VERSIONS) {
            Message msg = version.getMessageFactory().newBasicOrder();
            org.marketcetera.core.trade.SecurityType expectedValue;
            if(FIXVersion.FIX40 != version) {
                msg.setField(new quickfix.field.SecurityType(
                        quickfix.field.SecurityType.OPTION));
                expectedValue = org.marketcetera.core.trade.SecurityType.Option;
            } else {
                expectedValue = null;
            }
            assertOrderValues(sFactory.createOrder(msg,id), id, expectedValue);
        }
    }

    /**
     * Tests brokerID initialization, setters and getters.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void checkBrokerID() throws Exception {
        final Message msg = FIXVersion.FIX44.getMessageFactory().newBasicOrder();
        final BrokerID id = new BrokerID("blah");
        // null broker not allowed.
        new ExpectedFailure<NullPointerException>(){
            protected void run() throws Exception {
                sFactory.createOrder(msg, null);
            }
        };
        final FIXOrder order = sFactory.createOrder(msg, id);
        assertOrderValues(order,id, null);
        BrokerID cID = new BrokerID("meh");
        order.setBrokerID(cID);
        assertOrderValues(order,cID, null);
        new ExpectedFailure<NullPointerException>() {
            protected void run() throws Exception {
                order.setBrokerID(null);
            }
        };
    }

    /**
     * Asserts the FIX aspects of the created order.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void checkFIX() throws Exception {
        Message msg = FIXVersion.FIX44.getMessageFactory().newBasicOrder();
        BrokerID id = new BrokerID("blah");
        //Remove ClOrdID.
        msg.removeField(ClOrdID.FIELD);
        assertFalse(msg.isSetField(ClOrdID.FIELD));
        FIXOrder order = sFactory.createOrder(msg, id);
        assertSame(msg, order.getMessage());
        //Verify an orderID is assigned.
        assertNotNull(getClOrdID(order));
        //Verify toString() doesn't fail
        order.toString();

        //Test map of fields
        String account = "myacc";
        BigDecimal qty = new BigDecimal("234.34");
        Equity symbol = new Equity("IBM");
        BigDecimal price = new BigDecimal("3498.343");
        msg = FIXVersion.FIX44.getMessageFactory().newLimitOrder("blah",
                quickfix.field.Side.BUY, qty, symbol, price,
                quickfix.field.TimeInForce.DAY, account);
        //Remove ClOrdID.
        msg.removeField(ClOrdID.FIELD);
        assertFalse(msg.isSetField(ClOrdID.FIELD));
        order = sFactory.createOrder(msg, id);
        assertOrderValues(order, id, org.marketcetera.core.trade.SecurityType.CommonStock);
        //Verify an orderID is assigned.
        String clOrdID = getClOrdID(order);
        assertNotNull(clOrdID);
        HashMap<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(Account.FIELD, account);
        expected.put(Symbol.FIELD, symbol.getSymbol());
        expected.put(OrderQty.FIELD, qty.toString());
        expected.put(quickfix.field.Side.FIELD, String.valueOf(
                quickfix.field.Side.BUY));
        expected.put(quickfix.field.TimeInForce.FIELD, String.valueOf(
                quickfix.field.TimeInForce.DAY));
        expected.put(OrdType.FIELD, String.valueOf(OrdType.LIMIT));
        expected.put(ClOrdID.FIELD, clOrdID);
        expected.put(quickfix.field.SecurityType.FIELD, String.valueOf(
                quickfix.field.SecurityType.COMMON_STOCK));
        expected.put(Price.FIELD, price.toString());
        final Map<Integer,String> actual = order.getFields();
        //Verify that it's an unmodifiable map.
        new ExpectedFailure<UnsupportedOperationException>() {
            protected void run() throws Exception {
                actual.clear();
            }
        };
        //Create a copy for verification.
        Map<Integer,String> actualCopy = new HashMap<Integer, String>(actual);
        //remove fields that we do not want to check as they need not
        // be set by the client.
        for(int i: MAP_COMPARE_IGNORE_FIELDS) {
            actualCopy.remove(i);
        }
        assertEquals(expected, actualCopy);
        //Verify toString() doesn't fail
        order.toString();
    }

    private static String getClOrdID(FIXOrder inOrder) throws Exception {
        return inOrder.getMessage().getString(ClOrdID.FIELD);
    }
}
