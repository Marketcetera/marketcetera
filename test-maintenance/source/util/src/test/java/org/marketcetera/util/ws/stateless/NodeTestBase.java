package org.marketcetera.util.ws.stateless;

import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class NodeTestBase
    extends TestCaseBase
{
    protected static final String TEST_HOST=
        "testHost";
    protected static final int TEST_PORT=
        1;
    private static final String TEST_CLASS_NAME=
        "org_marketcetera_util_ws_stateless_NodeTestBase_TestClass";
    private static final String TEST_URL=
        "http://testHost:1/"+TEST_CLASS_NAME;


    private static final class TestClass {}


    protected static void singleNode
        (Node node,
         Node empty)
    {
        assertNotNull(Node.DEFAULT_HOST);
        assertFalse(Node.DEFAULT_PORT==0);

        assertEquals(TEST_HOST,node.getHost());
        assertEquals(TEST_PORT,node.getPort());
        assertEquals(TEST_URL,node.getConnectionUrl(TestClass.class));

        assertEquals(Node.DEFAULT_HOST,empty.getHost());
        assertEquals(Node.DEFAULT_PORT,empty.getPort());

        assertNotNull(node.getId());
        assertFalse(node.getId().equals(empty.getId()));
    }
}
