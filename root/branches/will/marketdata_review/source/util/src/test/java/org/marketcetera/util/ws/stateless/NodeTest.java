package org.marketcetera.util.ws.stateless;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
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
