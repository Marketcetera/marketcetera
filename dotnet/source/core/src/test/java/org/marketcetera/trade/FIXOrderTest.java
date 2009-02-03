package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import quickfix.Message;
import quickfix.field.*;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link Factory#createOrder(quickfix.Message, BrokerID)}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
        new ExpectedFailure<NullPointerException>(null){
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
            SecurityType expectedValue;
            if(FIXVersion.FIX40 != version) {
                msg.setField(new quickfix.field.SecurityType(
                        quickfix.field.SecurityType.OPTION));
                expectedValue = SecurityType.Option;
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
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                sFactory.createOrder(msg, null);
            }
        };
        final FIXOrder order = sFactory.createOrder(msg, id);
        assertOrderValues(order,id, null);
        BrokerID cID = new BrokerID("meh");
        order.setBrokerID(cID);
        assertOrderValues(order,cID, null);
        new ExpectedFailure<NullPointerException>(null) {
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
        MSymbol symbol = new MSymbol("IBM", SecurityType.CommonStock);
        BigDecimal price = new BigDecimal("3498.343");
        msg = FIXVersion.FIX44.getMessageFactory().newLimitOrder("blah",
                quickfix.field.Side.BUY, qty, symbol, price,
                quickfix.field.TimeInForce.DAY, account);
        //Remove ClOrdID.
        msg.removeField(ClOrdID.FIELD);
        assertFalse(msg.isSetField(ClOrdID.FIELD));
        order = sFactory.createOrder(msg, id);
        assertOrderValues(order, id, SecurityType.CommonStock);
        //Verify an orderID is assigned.
        String clOrdID = getClOrdID(order);
        assertNotNull(clOrdID);
        HashMap<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(Account.FIELD, account);
        expected.put(Symbol.FIELD, symbol.getFullSymbol());
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
        new ExpectedFailure<UnsupportedOperationException>(null) {
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
