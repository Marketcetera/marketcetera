package org.marketcetera.util.ws.stateless;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: NodeTest.java 17112 2016-02-11 17:58:19Z colin $
 */

/* $License$ */

public class NodeTest
    extends NodeTestBase
{
    @Test
    public void all()
    {
        singleNodeServer(new Node(TEST_HOST,TEST_PORT),new Node());
    }
}
