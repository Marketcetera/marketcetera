package org.marketcetera.util.ws.stateless;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
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
