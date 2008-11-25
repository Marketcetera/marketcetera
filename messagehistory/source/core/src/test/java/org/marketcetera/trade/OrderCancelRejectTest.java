package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/* $License$ */
/**
 * Tests {@link OrderCancelReject}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderCancelRejectTest extends TypesTestBase {
    /**
     * Test report creation failures.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void failures() throws Exception {
        final DestinationID cID = new DestinationID("blah");
        // null message
        new ExpectedFailure<NullPointerException>(null){
             protected void run() throws Exception {
                 sFactory.createOrderCancelReject(null, cID);
             }
         };
        final Message message = getSystemMessageFactory().newBasicOrder();
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_CANCEL_REJECT, message.toString()){
            protected void run() throws Exception {
                sFactory.createOrderCancelReject(
                        message, null);
            }
        };
    }

    /**
     * Test report getters.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void getters() throws Exception {
        // report with all empty fields
        Message msg = getSystemMessageFactory().newOrderCancelReject();
        OrderCancelReject report = sFactory.createOrderCancelReject(msg, null);
        assertReportBaseValues(report, null, null, null, null, null, null);

        //report with fields filled in
        DestinationID cID = new DestinationID("bro1");
        OrderID orderID = new OrderID("ord3");
        OrderID origOrderID = new OrderID("ord2");
        OrderStatus orderStatus = OrderStatus.Rejected;
        String text = "Cancel it please.";
        Date sendingTime = new Date();
        msg = getSystemMessageFactory().newOrderCancelReject(
                new quickfix.field.OrderID("brok3"),
                new ClOrdID(orderID.getValue()),
                new OrigClOrdID(origOrderID.getValue()),
                text, null);
        msg.getHeader().setField(new SendingTime(sendingTime));
        report = sFactory.createOrderCancelReject(msg, cID);
        assertReportBaseValues(report, cID, orderID, orderStatus, origOrderID,
                sendingTime, text);
        
        //Verify FIX fields returned in the map.
        Map<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(OrdStatus.FIELD, String.valueOf(orderStatus.getFIXValue()));
        expected.put(quickfix.field.OrderID.FIELD, "brok3");
        expected.put(Text.FIELD, text);
        expected.put(CxlRejResponseTo.FIELD,
                String.valueOf(CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        expected.put(OrigClOrdID.FIELD, origOrderID.getValue());
        expected.put(ClOrdID.FIELD, orderID.getValue());
        expected.put(TransactTime.FIELD, msg.getField(
                new StringField(TransactTime.FIELD)).getValue());

        final Map<Integer, String> actual = ((FIXMessageSupport) report).getFields();
        assertEquals(expected, actual);
        //Verify that the map is not modifiable
        new ExpectedFailure<UnsupportedOperationException>(null){
            protected void run() throws Exception {
                actual.clear();
            }
        };
        assertSame(msg, ((FIXMessageSupport)report).getMessage());
    }
}
