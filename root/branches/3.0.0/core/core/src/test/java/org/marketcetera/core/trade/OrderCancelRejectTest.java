package org.marketcetera.core.trade;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.attributes.ClassVersion;
import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;
import quickfix.field.Text;
import quickfix.field.TransactTime;
import quickfix.field.converter.UtcTimestampConverter;

import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link OrderCancelReject}
 *
 * @author anshul@marketcetera.com
 * @version $Id: OrderCancelRejectTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: OrderCancelRejectTest.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
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
        org.marketcetera.core.trade.OrderID orderID = new org.marketcetera.core.trade.OrderID("ord3");
        org.marketcetera.core.trade.OrderID origOrderID = new org.marketcetera.core.trade.OrderID("ord2");
        OrderStatus orderStatus = OrderStatus.Rejected;
        String text = "Cancel it please.";
        Date sendingTime = new Date();
        Date transactTime = new Date();
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
        expected.put(TransactTime.FIELD, UtcTimestampConverter.convert(
                transactTime, true));

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
