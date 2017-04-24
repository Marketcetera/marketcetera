package org.marketcetera.client.jms;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.marketcetera.client.ClientTest;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.TypesTestBase;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Test {@link DataEnvelope}.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

public class DataEnvelopeTest
    extends TestCaseBase
{
    private static final SessionId TEST_SESSION_ID=
        SessionId.generate();
    private static final OrderSingle TEST_ORDER=
        ClientTest.createOrderSingle();

    @Test
    public void all()
        throws Exception
    {
        DataEnvelope o=new DataEnvelope(TEST_ORDER,TEST_SESSION_ID);
        assertEquals(TEST_SESSION_ID,o.getSessionId());
        TypesTestBase.assertOrderSingleEquals
            (TEST_ORDER,(OrderSingle)o.getOrder());
    }
}
