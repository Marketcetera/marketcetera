package org.marketcetera.ors;

import java.util.Date;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.ActiveLocale;
import quickfix.Message;

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
        startORS();

        Date start=new Date();
        // Allow clock to advance.
        Thread.sleep(1500);
        ORSTestClient c=getAdminClient();

        Message msg=createEmptyExecReport();
        emulateFirstBrokerResponse(msg);
        ExecutionReport er=Factory.getInstance().createExecutionReport
            (msg,getFirstBrokerID(),Originator.Broker,null,null);
        assertExecReportEquals
            (er,(ExecutionReport)(c.getReportListener().getNext()));

        msg=createEmptyOrderCancelReject();
        emulateFirstBrokerResponse(msg);
        OrderCancelReject ocr=Factory.getInstance().createOrderCancelReject
            (msg,getFirstBrokerID(),Originator.Broker,null,null);
        assertCancelRejectEquals
            (ocr,(OrderCancelReject)(c.getReportListener().getNext()));

        msg=createEmptyBusinessMessageReject();
        setupTestCaseBase();
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setDefaultLevel(Level.OFF);
        setLevel(TEST_CATEGORY,Level.WARN);
        getAppender().clear();        
        emulateFirstBrokerResponse(msg);
        // Wait for message to reach client.
        Thread.sleep(5000);
        assertSomeEvent
            (Level.WARN,TEST_CATEGORY,
             "Received a fix report that was neither an execution report "+
             "nor an order cancel reject: '"+
             Factory.getInstance().createFIXResponse
             (msg,getFirstBrokerID(),Originator.Broker,null,null).
             toString()+
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

        stopORS();
    }
}
