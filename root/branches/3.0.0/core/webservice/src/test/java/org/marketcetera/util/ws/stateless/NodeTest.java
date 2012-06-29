package org.marketcetera.util.ws.stateless;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: NodeTest.java 82324 2012-04-09 20:56:08Z colin $
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
