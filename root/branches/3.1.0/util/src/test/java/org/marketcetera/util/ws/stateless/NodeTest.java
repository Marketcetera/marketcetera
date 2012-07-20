package org.marketcetera.util.ws.stateless;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: NodeTest.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

public class NodeTest
    extends NodeTestBase
{
    @Test
    public void all()
    {
        singleNode(new Node(TEST_HOST,TEST_PORT),new Node());
    }
}
