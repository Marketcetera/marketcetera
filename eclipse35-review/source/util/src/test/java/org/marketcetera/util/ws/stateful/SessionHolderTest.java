package org.marketcetera.util.ws.stateful;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SessionHolderTest
    extends TestCaseBase
{
    private static final String TEST_USER=
        "metc";
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();
    private static final Object TEST_SESSION=
        NumberUtils.INTEGER_ONE;

    
    @Test
    public void all()
        throws Exception
    {
        SessionHolder<Object> holder=new SessionHolder<Object>
            (TEST_USER,TEST_CONTEXT);
        assertEquals(TEST_USER,holder.getUser());
        assertEquals(TEST_CONTEXT,holder.getCreationContext());
        assertEquals(0,holder.getLastAccess());
        assertNull(holder.getSession());

        holder.setSession(TEST_SESSION);
        assertEquals(TEST_SESSION,holder.getSession());

        holder.setSession(null);
        assertNull(holder.getSession());

        holder.markAccess();
        long time=holder.getLastAccess();
        assertTrue(time>0);
        Thread.sleep(100);
        holder.markAccess();
        assertTrue(holder.getLastAccess()>time);
    }
}
