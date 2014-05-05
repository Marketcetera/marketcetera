package org.marketcetera.util.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TestCaseBaseTest
{
    private TestCaseBase mTestCaseBase;
    private static void testDirExists
        (String name)
    {
        File dir=new File(name);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }


    @Before
    public void setupTestCaseBaseTest()
    {
        mTestCaseBase=new TestCaseBase();
        mTestCaseBase.setupTestCaseBase();
    }


    // Custom additional tests.

    @Test
    public void dirsExist()
    {
        testDirExists(TestCaseBase.DIR_ROOT);
        testDirExists(TestCaseBase.DIR_TARGET);
        testDirExists(TestCaseBase.DIR_CLASSES);
        testDirExists(TestCaseBase.DIR_CLASSES+File.separator+"org");
        testDirExists(TestCaseBase.DIR_TEST_CLASSES);
        testDirExists(TestCaseBase.DIR_TEST_CLASSES+File.separator+"org");
    }
}
