package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.MSymbol;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import quickfix.Message;
import quickfix.field.*;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

/* $License$ */
/**
 * Tests {@link Factory#createOrder(quickfix.Message, DestinationID)}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
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
        final DestinationID id = new DestinationID("blah");
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
     * Tests destinationID initialization, setters and getters.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void checkDestinationID() throws Exception {
        final Message msg = FIXVersion.FIX44.getMessageFactory().newBasicOrder();
        final DestinationID id = new DestinationID("blah");
        // null destination not allowed.
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                sFactory.createOrder(msg, null);
            }
        };
        final FIXOrder order = sFactory.createOrder(msg, id);
        assertOrderValues(order,id, null);
        DestinationID cID = new DestinationID("meh");
        order.setDestinationID(cID);
        assertOrderValues(order,cID, null);
        new ExpectedFailure<NullPointerException>(null) {
            protected void run() throws Exception {
                order.setDestinationID(null);
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
        DestinationID id = new DestinationID("blah");
        FIXOrder order = sFactory.createOrder(msg, id);
        assertSame(msg, order.getMessage());
        //Verify toString() doesn't fail
        order.toString();

        //Test map of fields
        String clOrdID = "blah";
        String account = "myacc";
        BigDecimal qty = new BigDecimal("234.34");
        MSymbol symbol = new MSymbol("IBM", SecurityType.CommonStock);
        BigDecimal price = new BigDecimal("3498.343");
        msg = FIXVersion.FIX44.getMessageFactory().newLimitOrder(clOrdID,
                quickfix.field.Side.BUY, qty, symbol, price,
                quickfix.field.TimeInForce.DAY, account);
        order = sFactory.createOrder(msg, id);
        assertOrderValues(order, id, SecurityType.CommonStock);
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
}
