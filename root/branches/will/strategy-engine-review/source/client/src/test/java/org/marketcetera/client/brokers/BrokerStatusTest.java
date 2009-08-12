package org.marketcetera.client.brokers;

import org.junit.Test;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class BrokerStatusTest
    extends TestCaseBase
{
    private static final String TEST_NAME=
        "metc";
    private static final BrokerID TEST_BROKER_ID=
        new BrokerID("bro");

    @Test
    public void all()
        throws Exception
    {
        BrokerStatus s=new BrokerStatus(TEST_NAME,TEST_BROKER_ID,true);
        assertEquals(TEST_NAME,s.getName());
        assertEquals(TEST_BROKER_ID,s.getId());
        assertTrue(s.getLoggedOn());
        assertEquals("Broker: metc(bro,true)",s.toString());
    }
}
