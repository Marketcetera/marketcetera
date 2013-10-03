package org.marketcetera.ors.info;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class NameGeneratorTest
    extends TestCaseBase
{
    @Test
    public void all()
        throws Exception
    {
        NameGenerator g=new NameGenerator("x");
        assertEquals("x"+NameGenerator.INT_SEPARATOR+"0000000001",
                     g.getNextName());
        assertEquals("x"+NameGenerator.INT_SEPARATOR+"0000000002",
                     g.getNextName());
    }
}
