package org.marketcetera.util.ws.stateful;

import org.junit.Test;
import org.marketcetera.util.ws.stateless.ClientContextTestBase;
import org.marketcetera.util.ws.tags.SessionId;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ClientContextTest
    extends ClientContextTestBase
{
    private static final SessionId TEST_SESSION=
        SessionId.generate();
    private static final SessionId TEST_SESSION_D=
        SessionId.generate();


    protected static void fillContext
        (ClientContext context)
    {
        ClientContextTestBase.fillContext(context);
        context.setSessionId(TEST_SESSION);
    }


    @Test
    public void all()
    {
        ClientContext context=new ClientContext();
        fillContext(context);
        ClientContext copy=new ClientContext();
        fillContext(copy);
        ClientContext empty=new ClientContext();
        single(context,copy,empty,"; session "+TEST_SESSION);

        assertEquals(TEST_SESSION,context.getSessionId());

        assertNull(empty.getSessionId());

        context.setSessionId(TEST_SESSION_D);
        assertEquals(TEST_SESSION_D,context.getSessionId());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale ''; session "+TEST_SESSION_D,
                      context.toString());

        context.setSessionId(null);
        assertNull(context.getSessionId());
        assertEquals("Protocol version null; application null; "+
                     "client null; locale ''; session null",
                      context.toString());

        assertEquals(empty,context);
    }
}
