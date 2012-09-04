package org.marketcetera.core.util.exec;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @since 0.5.0
 * @version $Id: ExecResultTest.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public class ExecResultTest
    extends TestCaseBase
{
    private static final int TEST_CODE=1;
    private static final byte[] TEST_OUTPUT=new byte[] {1,2};


    @Test
    public void result()
    {
        ExecResult r=new ExecResult(TEST_CODE,TEST_OUTPUT);
        assertEquals(TEST_CODE,r.getExitCode());
        assertEquals(TEST_OUTPUT,r.getOutput());

        r=new ExecResult(TEST_CODE,null);
        assertEquals(TEST_CODE,r.getExitCode());
        assertNull(r.getOutput());
    }
}
