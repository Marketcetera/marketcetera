package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;
import quickfix.field.Text;
import quickfix.field.TransactTime;
import quickfix.field.converter.UtcTimestampConverter;

/* $License$ */
/**
 * Tests {@link OrderCancelReject}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
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
        final BrokerID cID = new BrokerID("blah");
        // null message
        new ExpectedFailure<NullPointerException>(){
             protected void run() throws Exception {
                 sFactory.createOrderCancelReject(null, cID, Originator.Server, null, null);
             }
         };
        //null originator
        new ExpectedFailure<NullPointerException>(){
             protected void run() throws Exception {
                 sFactory.createOrderCancelReject
                     (getSystemMessageFactory().newOrderCancelReject(),
                      cID, null, null, null);
             }
         };
        // wrong quickfix message type
        final Message message = getSystemMessageFactory().newBasicOrder();
        new ExpectedFailure<MessageCreationException>(
                Messages.NOT_CANCEL_REJECT, message.toString()){
            protected void run() throws Exception {
                sFactory.createOrderCancelReject
                    (message, null, Originator.Server, null, null);
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
        OrderCancelReject report = sFactory.createOrderCancelReject
            (msg, null, Originator.Server, null, null);
        assertReportBaseValues(report, null, null, null, null, null, null,
                               null, Originator.Server, null, null);
        assertNull(report.getReportID());
        //Verify toString() doesn't fail.
        report.toString();

        //report with fields filled in
        BrokerID cID = new BrokerID("bro1");
        String destOrderID = "brok3";
        OrderID orderID = new OrderID("ord3");
        OrderID origOrderID = new OrderID("ord2");
        OrderStatus orderStatus = OrderStatus.Rejected;
        String text = "Cancel it please.";
        LocalDateTime sendingTime = LocalDateTime.now();
        LocalDateTime transactTime = LocalDateTime.now();
        UserID actorID = new UserID(2);
        UserID viewerID = new UserID(3);
        msg = getSystemMessageFactory().newOrderCancelReject(
                new quickfix.field.OrderID(destOrderID),
                new ClOrdID(orderID.getValue()),
                new OrigClOrdID(origOrderID.getValue()),
                text, null);
        msg.getHeader().setField(new SendingTime(sendingTime));
        msg.setField(new TransactTime(transactTime));
        report = sFactory.createOrderCancelReject(msg, cID, Originator.Broker,
                                                  actorID, viewerID);
        assertReportBaseValues(report, cID, orderID, orderStatus, origOrderID,
                               sendingTime, text, destOrderID,
                               Originator.Broker, actorID, viewerID);
        assertNull(report.getReportID());
        report.toString();
        
        //Verify FIX fields returned in the map.
        Map<Integer,String> expected = new HashMap<Integer, String>();
        expected.put(OrdStatus.FIELD, String.valueOf(orderStatus.getFIXValue()));
        expected.put(quickfix.field.OrderID.FIELD, destOrderID);
        expected.put(Text.FIELD, text);
        expected.put(CxlRejResponseTo.FIELD,
                String.valueOf(CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        expected.put(OrigClOrdID.FIELD, origOrderID.getValue());
        expected.put(ClOrdID.FIELD, orderID.getValue());
        expected.put(TransactTime.FIELD,
                     UtcTimestampConverter.convert(transactTime,
                                                   quickfix.UtcTimestampPrecision.MILLIS));
        final Map<Integer, String> actual = ((FIXMessageSupport) report).getFields();
        assertEquals(expected, actual);
        //Verify that the map is not modifiable
        new ExpectedFailure<UnsupportedOperationException>(){
            protected void run() throws Exception {
                actual.clear();
            }
        };
        assertSame(msg, ((FIXMessageSupport)report).getMessage());
    }
}
