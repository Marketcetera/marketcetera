package org.marketcetera.util.ws.stateful;

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

public class FixedAuthenticatorTest
    extends TestCaseBase
{
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();
    private static final String TEST_USER=
        "metc";
    private static final String TEST_USER_D=
        "metcD";
    private static final char[] TEST_PASSWORD=
        "metc".toCharArray();
    private static final char[] TEST_PASSWORD_D=
        "metcD".toCharArray();


    private static void single
        (FixedAuthenticator a,
         StatelessClientContext context)
        throws Exception
    {
        assertTrue(a.shouldAllow(context,TEST_USER,TEST_PASSWORD));

        assertFalse(a.shouldAllow(context,TEST_USER_D,TEST_PASSWORD));
        assertFalse(a.shouldAllow(context,TEST_USER,TEST_PASSWORD_D));

        assertFalse(a.shouldAllow(context,null,TEST_PASSWORD));
        assertFalse(a.shouldAllow(context,TEST_USER,null));
    }

    
    @Test
    public void all()
        throws Exception
    {
        FixedAuthenticator a=new FixedAuthenticator();
        single(a,TEST_CONTEXT);
        single(a,null);
    }
}
