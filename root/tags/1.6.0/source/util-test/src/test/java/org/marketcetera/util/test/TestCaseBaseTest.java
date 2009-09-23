package org.marketcetera.util.test;

import java.io.File;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        "TestCategory";
    private static final String TEST_UNDEFINED_CATEGORY=
        "TestUndefinedCategory";
    private static final String TEST_MESSAGE=
        "Test message (expected)";
    private static final String TEST_LOCATION=
        TestCaseBaseTest.class.getName();


    @Before
    public void setupTestCaseBaseTest()
    {
        setLevel(TEST_CATEGORY,Level.ERROR);
    }


    private static void testDirExists
        (String name)
    {
        File dir=new File(name);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }

    @Test
    public void dirsExist()
    {
        testDirExists(DIR_ROOT);
        testDirExists(DIR_TARGET);
        testDirExists(DIR_CLASSES);
        testDirExists(DIR_CLASSES+File.separator+"org");
        testDirExists(DIR_TEST_CLASSES);
        testDirExists(DIR_TEST_CLASSES+File.separator+"org");
    }


    @Test
    public void appenderIsValid()
    {
        assertNotNull(getAppender());
    }


    @Test
    public void defaultLevelSetting()
    {
        setDefaultLevel(Level.WARN);
        assertEquals(Level.WARN,Logger.getRootLogger().getLevel());
        Logger.getLogger(TEST_UNDEFINED_CATEGORY).debug(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        Logger.getLogger(TEST_UNDEFINED_CATEGORY).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(TEST_UNDEFINED_CATEGORY).error(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());
        getAppender().getEvents().clear();

        setDefaultLevel(Level.OFF);
        assertEquals(Level.OFF,Logger.getRootLogger().getLevel());
        Logger.getLogger(TEST_UNDEFINED_CATEGORY).warn(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
    }

    @Test
    public void levelSetting()
    {
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        setLevel(TEST_CATEGORY,Level.WARN);
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
    }


    @Test
    public void eventCorrect()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        LoggingEvent event=getAppender().getEvents().getLast();
        assertEvent(event,Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEvent(event,null,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEvent(event,Level.ERROR,null,TEST_MESSAGE,TEST_LOCATION);
        assertEvent(event,Level.ERROR,TEST_CATEGORY,null,TEST_LOCATION);
        assertEvent(event,Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectLevel()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectName()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectMessage()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,TEST_CATEGORY,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectLocation()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,"");
    }


    @Test
    public void noEvents()
    {
        assertNoEvents();
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertNoEvents();
    }

    @Test(expected=AssertionError.class)
    public void someEvents()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertNoEvents();
    }


    @Test
    public void lastEventCorrect()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(1,getAppender().getEvents().size());
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertLastEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(2,getAppender().getEvents().size());
    }

    @Test(expected=AssertionError.class)
    public void noLastEvent()
    {
        assertLastEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }


    @Test
    public void someEventCorrect()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(null,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,null,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,null,TEST_LOCATION);
        assertSomeEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectLevel()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectName()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectMessage()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noMatchingIncorrectLocation()
    {
        Logger.getLogger(TEST_CATEGORY).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,"");
    }

    @Test(expected=AssertionError.class)
    public void noSomeEvent()
    {
        assertSomeEvent(null,null,null,null);
    }


    @Test
    public void singleEventCorrect()
    {
        setLevel(TEST_CATEGORY,Level.WARN);
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        assertSingleEvent(Level.WARN,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertNoEvents();
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
        assertNoEvents();
    }

    @Test(expected=AssertionError.class)
    public void noSingleEvent()
    {
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void tooManyEvents()
    {
        Logger.getLogger(TEST_CATEGORY).warn(TEST_MESSAGE);
        setLevel(TEST_CATEGORY,Level.INFO);
        Logger.getLogger(TEST_CATEGORY).info(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,TEST_CATEGORY,TEST_MESSAGE,TEST_LOCATION);
    }
}
