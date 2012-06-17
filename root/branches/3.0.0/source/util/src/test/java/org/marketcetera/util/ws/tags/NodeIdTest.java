package org.marketcetera.util.ws.tags;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: NodeIdTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class NodeIdTest
    extends TagTestBase
{
    @Test
    public void all()
    {
        NodeId tag=new NodeId();
        tag.setValue(TEST_VALUE);
        NodeId copy=new NodeId();
        copy.setValue(TEST_VALUE);
        single(tag,copy,new NodeId());

        NodeId generated=NodeId.generate();
        assertNotNull(generated);
        assertNotNull(generated.getValue());
        assertFalse(generated.equals(NodeId.generate()));
    }
}
