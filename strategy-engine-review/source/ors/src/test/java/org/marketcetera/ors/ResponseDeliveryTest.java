package org.marketcetera.ors;

import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.ors.filters.SimpleMessageModifierManager;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.ActiveLocale;
import quickfix.Message;
import quickfix.field.ClearingFirm;

import static org.junit.Assert.*;
import static org.marketcetera.trade.TypesTestBase.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class ResponseDeliveryTest
    extends ORSTestBase
{
    private static final String TEST_CATEGORY=
        "org.marketcetera.client.ClientImpl$TradeMessageReceiver";


    @Test(timeout=300000)
    public void responseDelivery()
        throws Exception
    {
        Date start=new Date();
        // Allow clock to advance.
        Thread.sleep(1500);
        ORSTestClient c=getAdminClient();

        Message msg=createEmptyExecReport();
        completeExecReport(msg);
        assertFalse(msg.isSetField(ClearingFirm.FIELD));
        emulateFirstBrokerResponse(msg);
        ExecutionReport er=Factory.getInstance().createExecutionReport
            (msg,getFirstBrokerID(),Originator.Broker,null,null);
        ExecutionReport err=
            (ExecutionReport)(c.getReportListener().getNext());
        assertExecReportEquals(er,err);
        // Test response message modifiers.
        assertEquals(SimpleMessageModifierManager.FIRM,
                     ((HasFIXMessage)err).getMessage().
                     getString(ClearingFirm.FIELD));
        
        msg=createEmptyOrderCancelReject();
        completeOrderCancelReject(msg);
        assertFalse(msg.isSetField(ClearingFirm.FIELD));
        emulateFirstBrokerResponse(msg);
        OrderCancelReject ocr=Factory.getInstance().createOrderCancelReject
            (msg,getFirstBrokerID(),Originator.Broker,null,null);
        OrderCancelReject ocrr=
            (OrderCancelReject)(c.getReportListener().getNext());
        assertCancelRejectEquals(ocr,ocrr);
        // Test response message modifiers.
        assertEquals(SimpleMessageModifierManager.FIRM,
                     ((HasFIXMessage)ocrr).getMessage().
                     getString(ClearingFirm.FIELD));

        msg=createEmptyBusinessMessageReject();
        assertFalse(msg.isSetField(ClearingFirm.FIELD));
        setupTestCaseBase();
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setDefaultLevel(Level.OFF);
        setLevel(TEST_CATEGORY,Level.WARN);
        getAppender().clear();        
        emulateFirstBrokerResponse(msg);
        // Wait for message to reach client.
        Thread.sleep(5000);
        FIXResponse response=Factory.getInstance().createFIXResponse
            (msg,getFirstBrokerID(),Originator.Broker,null,null);
        // Test response message modifiers.
        ((HasFIXMessage)response).getMessage().setField
            (new ClearingFirm(SimpleMessageModifierManager.FIRM));
        assertSomeEvent
            (Level.WARN,TEST_CATEGORY,
             "Received a fix report that was neither an execution report "+
             "nor an order cancel reject: '"+
             response.toString()+
             "'. Client applications do "+
             "not yet support this message type, so it was not forwarded "+
             "to the application or its embedded strategies.",
             TEST_CATEGORY);

        ReportBase[] rs=c.getClient().getReportsSince(start);
        assertEquals(2,rs.length);
        assertExecReportEquals
            (er,(ExecutionReport)rs[0]);
        assertCancelRejectEquals
            (ocr,(OrderCancelReject)rs[1]);
    }
}
