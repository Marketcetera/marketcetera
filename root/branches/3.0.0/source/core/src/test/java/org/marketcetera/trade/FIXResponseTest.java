package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/**
 * Tests {@link FIXResponse} and {@link FIXResponseImpl}.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id: FIXResponseTest.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: FIXResponseTest.java 16063 2012-01-31 18:21:55Z colin $")
public class FIXResponseTest
    extends TypesTestBase
{
    @Test
    public void failures()
        throws Exception
    {
        final BrokerID brokerID=new BrokerID("blah");
        final UserID actorID=new UserID(2);
        final UserID viewerID=new UserID(3);
        final Message msg=createEmptyExecReport();
        // null message.
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run() throws Exception
            {
                sFactory.createFIXResponse
                    (null,brokerID,Originator.Server,actorID,viewerID);
            }
        };
        // null originator.
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run() throws Exception
            {
                sFactory.createFIXResponse(msg,brokerID,null,actorID,viewerID);
            }
        };
    }

    @Test
    public void getters()
        throws Exception
    {
        Message msg=createEmptyExecReport();

        // Null values (where allowed).

        FIXResponse response=sFactory.createFIXResponse
            (msg,null,Originator.Server,null,null);
        assertFIXResponseValues(response,null,Originator.Server,msg,null,null);
        response.toString();

        // All non-null values.

        BrokerID brokerID=new BrokerID("blah");
        UserID actorID=new UserID(2);
        UserID viewerID=new UserID(3);
        response=sFactory.createFIXResponse
            (msg,brokerID,Originator.Broker,actorID,viewerID);
        assertFIXResponseValues
            (response,brokerID,Originator.Broker,msg,actorID,viewerID);
        response.toString();
        assertEquals(new HashMap<Integer,String>(),response.getFields());
    }
}
