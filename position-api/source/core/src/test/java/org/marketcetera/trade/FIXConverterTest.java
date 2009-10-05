package org.marketcetera.trade;

import org.junit.Test;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;
import quickfix.field.BusinessRejectReason;

/**
 * Tests {@link FIXConverter}.
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class FIXConverterTest
    extends TypesTestBase
{
    @Test
    public void fromQMessage()
        throws Exception
    {
        BrokerID brokerID=new BrokerID("blah");
        UserID actorID=new UserID(2);
        UserID viewerID=new UserID(3);

        Message msg=createEmptyExecReport();
        assertExecReportEquals
            (Factory.getInstance().createExecutionReport
             (msg,brokerID,Originator.Broker,actorID,viewerID),
             (ExecutionReport)FIXConverter.fromQMessage
             (msg,Originator.Broker,brokerID,actorID,viewerID));

        msg=getSystemMessageFactory().newOrderCancelReject();
        assertCancelRejectEquals
            (Factory.getInstance().createOrderCancelReject
             (msg,brokerID,Originator.Broker,actorID,viewerID),
             (OrderCancelReject)FIXConverter.fromQMessage
             (msg,Originator.Broker,brokerID,actorID,viewerID));

        msg=getSystemMessageFactory().newBusinessMessageReject
            ("QQ",BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
             "Bad message type");
        assertFIXResponseEquals
            (Factory.getInstance().createFIXResponse
             (msg,brokerID,Originator.Server,actorID,viewerID),
             (FIXResponse)FIXConverter.fromQMessage
             (msg,Originator.Server,brokerID,actorID,viewerID));
    }
}
