package org.marketcetera.core.ws.stateless;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @since 1.0.0
 * @version $Id: StatelessClientContextTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class StatelessClientContextTest
    extends ClientContextTestBase
{
    @Test
    public void all()
    {
        StatelessClientContext context=new StatelessClientContext();
        fillContext(context);
        StatelessClientContext copy=new StatelessClientContext();
        fillContext(copy);
        StatelessClientContext empty=new StatelessClientContext();
        single(context,copy,empty,StringUtils.EMPTY);
        assertEquals(empty,context);
    }
}
