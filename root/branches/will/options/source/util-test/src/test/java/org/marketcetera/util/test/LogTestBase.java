package org.marketcetera.util.test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Base class for logger tests. Subclasses provide certain method
 * overrides that this class uses to perform basic testing of said
 * subclasses.
 *
 * @author tlerios@marketcetera.com
 * @version $Id$
 * @since $Release$
 */

/* $License$ */

public abstract class LogTestBase
{
    protected static final String TEST_MESSAGE=
        "Test message (expected)";

    private static final String TEST_CATEGORY_PREFIX=
        "TestCategory";
    private static final String TEST_LOCATION=
        LogTestBase.class.getName();

    private static int sNexttTextCategory=
        0;


    // Operations provided by subclasses.

    protected abstract void setDefaultLevel
        (Level level);

    protected abstract void setLevel
        (String name,
         Level level);

    protected abstract void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message,
         String location);

    protected abstract MemoryAppender getAppender();

    protected abstract void assertEventCount
        (int count);

    protected abstract void assertNoEvents();

    protected abstract void assertLastEvent
        (Level level,
         String category,
         String message,
         String location);

    protected abstract void assertSomeEvent
        (Level level,
         String category,
         String message,
         String location);

    protected abstract void assertSingleEvent
        (Level level,
         String category,
         String message,
         String location);


    // Test category generator.

    protected static String getNextTestCategory()
    {
        return TEST_CATEGORY_PREFIX+(sNexttTextCategory++);
    }


    // Setup: ensure default logging is off. Subclass must ensure that
    // the result of getAppender() is tied to the root logger.

    @Before
    public void setupLogTestBase()
    {
        Logger.getRootLogger().setLevel(Level.OFF);
    }


    // setDefaultLevel() tests.

    @Test
    public void defaultLevelSetting()
    {
        String c=getNextTestCategory();

        // Default logging is turned off during setup.
        assertEquals(Level.OFF,Logger.getRootLogger().getLevel());

        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());

        setDefaultLevel(Level.WARN);
        assertEquals(Level.WARN,Logger.getRootLogger().getLevel());

        Logger.getLogger(c).info(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());

        setDefaultLevel(Level.OFF);
        assertEquals(Level.OFF,Logger.getRootLogger().getLevel());

        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());
    }


    // setLevel() tests.

    @Test
    public void levelSetting()
    {
        String c=getNextTestCategory();

        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());

        setLevel(c,Level.WARN);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
    }


    // assertEvent() tests.

    @Test
    public void eventCorrect()
    {
        String c=getNextTestCategory();

        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        LoggingEvent event=getAppender().getEvents().getLast();
        assertEvent(event,Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(1,getAppender().getEvents().size());

        // "don't care" matches.
        assertEvent(event,null,c,TEST_MESSAGE,TEST_LOCATION);
        assertEvent(event,Level.ERROR,null,TEST_MESSAGE,TEST_LOCATION);
        assertEvent(event,Level.ERROR,c,null,TEST_LOCATION);
        assertEvent(event,Level.ERROR,c,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectLevel()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.WARN,c,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectName()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectMessage()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,c,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void eventIncorrectLocation()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEvent(getAppender().getEvents().getLast(),
                    Level.ERROR,c,TEST_MESSAGE,"");
    }


    // getAppender() tests.

    @Test
    public void appenderIsValid()
    {
        assertNotNull(getAppender());
    }


    // assertEventCount() tests.

    @Test
    public void twoEvents()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.INFO);
        Logger.getLogger(c).debug(TEST_MESSAGE);
        Logger.getLogger(c).info(TEST_MESSAGE);
        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEventCount(2);
    }

    @Test(expected=AssertionError.class)
    public void miscountedEvents()
    {
        assertEventCount(1);
    }


    // assertNoEvents() tests.

    @Test
    public void noEvents()
    {
        String c=getNextTestCategory();
        assertNoEvents();
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertNoEvents();
    }

    @Test(expected=AssertionError.class)
    public void someEvents()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertNoEvents();
    }


    // assertLastEvent() tests.

    @Test
    public void lastEventCorrect()
    {
        String c=getNextTestCategory();

        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(1,getAppender().getEvents().size());

        setLevel(c,Level.INFO);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertLastEvent(Level.INFO,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(2,getAppender().getEvents().size());

        // "don't care" matches.
        assertLastEvent(null,c,TEST_MESSAGE,TEST_LOCATION);
        assertLastEvent(Level.INFO,null,TEST_MESSAGE,TEST_LOCATION);
        assertLastEvent(Level.INFO,c,null,TEST_LOCATION);
        assertLastEvent(Level.INFO,c,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void noLastMatchingIncorrectLevel()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertLastEvent(Level.WARN,c,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noLastMatchingIncorrectName()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noLastMatchingIncorrectMessage()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,c,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noLastMatchingIncorrectLocation()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertLastEvent(Level.ERROR,c,TEST_MESSAGE,"");
    }

    @Test(expected=AssertionError.class)
    public void noLastEvent()
    {
        String c=getNextTestCategory();
        assertLastEvent(Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
    }


    // assertSomeEvent() tests.

    @Test
    public void someEventCorrect()
    {
        String c=getNextTestCategory();

        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(1,getAppender().getEvents().size());

        setLevel(c,Level.INFO);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(2,getAppender().getEvents().size());

        // "don't care" matches.
        assertSomeEvent(null,c,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,null,TEST_MESSAGE,TEST_LOCATION);
        assertSomeEvent(Level.INFO,c,null,TEST_LOCATION);
        assertSomeEvent(Level.INFO,c,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void noSomeMatchingIncorrectLevel()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSomeEvent(Level.WARN,c,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSomeMatchingIncorrectName()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSomeMatchingIncorrectMessage()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,c,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSomeMatchingIncorrectLocation()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSomeEvent(Level.ERROR,c,TEST_MESSAGE,"");
    }

    @Test(expected=AssertionError.class)
    public void noSomeEvent()
    {
        assertSomeEvent(null,null,null,null);
    }


    // assertSingleEvent() tests.

    @Test
    public void singleEventCorrect()
    {
        String c=getNextTestCategory();

        setLevel(c,Level.WARN);
        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        assertSingleEvent(Level.WARN,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(0,getAppender().getEvents().size());

        setLevel(c,Level.INFO);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        assertSingleEvent(Level.INFO,c,TEST_MESSAGE,TEST_LOCATION);
        assertEquals(0,getAppender().getEvents().size());

        // "don't care" matches.
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSingleEvent(null,c,TEST_MESSAGE,TEST_LOCATION);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,null,TEST_MESSAGE,TEST_LOCATION);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,c,null,TEST_LOCATION);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,c,TEST_MESSAGE,null);
    }

    @Test(expected=AssertionError.class)
    public void noSingleMatchingIncorrectLevel()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSingleEvent(Level.WARN,c,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSingleMatchingIncorrectName()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,"",TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSingleMatchingIncorrectMessage()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,c,"",TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void noSingleMatchingIncorrectLocation()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertSingleEvent(Level.ERROR,c,TEST_MESSAGE,"");
    }

    @Test(expected=AssertionError.class)
    public void noSingleEvent()
    {
        String c=getNextTestCategory();
        assertSingleEvent(Level.ERROR,c,TEST_MESSAGE,TEST_LOCATION);
    }

    @Test(expected=AssertionError.class)
    public void tooManyEvents()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.INFO);
        Logger.getLogger(c).warn(TEST_MESSAGE);
        Logger.getLogger(c).info(TEST_MESSAGE);
        assertSingleEvent(Level.INFO,c,TEST_MESSAGE,TEST_LOCATION);
    }
}
