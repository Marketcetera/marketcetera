package org.marketcetera.util.test;

import java.io.File;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TestCaseBaseTest
    extends LogTestBase
{
    private TestCaseBase mTestCaseBase;


    // LogTestAssistBase.

    @Override
    protected void setDefaultLevel
        (Level level)
    {
        TestCaseBase.setDefaultLevel(level);
    }

    @Override
    protected void setLevel
        (String name,
         Level level)
    {
        TestCaseBase.setLevel(name,level);
    }

    @Override
    protected void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message,
         String location)
    {
        TestCaseBase.assertEvent(event,level,logger,message,location);
    }

    @Override
    protected MemoryAppender getAppender()
    {
        return getTestCaseBase().getAppender();
    }

    @Override
    protected void assertEventCount
        (int count)
    {
        getTestCaseBase().assertEventCount(count);
    }

    @Override
    protected void assertNoEvents()
    {
        getTestCaseBase().assertNoEvents();
    }

    @Override
    protected void assertLastEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getTestCaseBase().assertLastEvent(level,category,message,location);
    }

    @Override
    protected void assertSomeEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getTestCaseBase().assertSomeEvent(level,category,message,location);
    }

    @Override
    protected void assertSingleEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getTestCaseBase().assertSingleEvent(level,category,message,location);
    }


    // Custom additional utilities.

    private TestCaseBase getTestCaseBase()
    {
        return mTestCaseBase;
    }

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

    @Test
    public void logAssistIsValid()
    {
        assertNotNull(getTestCaseBase().getLogAssist());
    }
}
