package org.marketcetera.util.test;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @version $Id$
 * @since $Release$
 */

/* $License$ */

public class LogTestAssistTest
    extends LogTestBase
{
    private static final String TEST_LOCATION=
        LogTestAssistTest.class.getName();

    private LogTestAssist mAssist;


    // LogTestAssistBase.

    @Override
    protected void setDefaultLevel
        (Level level)
    {
        LogTestAssist.setDefaultLevel(level);
    }

    @Override
    protected void setLevel
        (String name,
         Level level)
    {
        LogTestAssist.setLevel(name,level);
    }

    @Override
    protected void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message,
         String location)
    {
        LogTestAssist.assertEvent(event,level,logger,message,location);
    }

    @Override
    protected MemoryAppender getAppender()
    {
        return getAssist().getAppender();
    }

    @Override
    protected void assertEventCount
        (int count)
    {
        getAssist().assertEventCount(count);
    }

    @Override
    protected void assertNoEvents()
    {
        getAssist().assertNoEvents();
    }

    @Override
    protected void assertLastEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getAssist().assertLastEvent(level,category,message,location);
    }

    @Override
    protected void assertSomeEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getAssist().assertSomeEvent(level,category,message,location);
    }

    @Override
    protected void assertSingleEvent
        (Level level,
         String category,
         String message,
         String location)
    {
        getAssist().assertSingleEvent(level,category,message,location);
    }


    // Custom additional utilities.

    private LogTestAssist getAssist()
    {
        return mAssist;
    }

    private void trackLogger
        (String name,
         Level level)
    {
        getAssist().trackLogger(name,level);
    }

    private void trackLogger
        (String name)
    {
        getAssist().trackLogger(name);
    }

    private void resetAppender()
    {
        getAssist().resetAppender();
    }

    private String getEventsAsString()
    {
        return getAssist().getEventsAsString();
    }



    @Before
    public void setupLogTestAssistTest()
    {
        mAssist=new LogTestAssist();
        BasicConfigurator.configure(getAppender());
    }


    // Custom additional tests.

    @Test
    public void normalConstructor()
    {
        String c1=getNextTestCategory();
        String c2=getNextTestCategory();
        mAssist=new LogTestAssist(c1,Level.ERROR);

        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        Logger.getLogger(c1).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());

        // Not tracking any other category.
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
    }

    @Test
    public void noLevelConstructor()
    {
        String c1=getNextTestCategory();
        String c2=getNextTestCategory();
        mAssist=new LogTestAssist(c1);

        // Default logging is turned off during parent class setup.
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());

        setDefaultLevel(Level.WARN);
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(c1).info(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());

        setLevel(c1,Level.ERROR);
        Logger.getLogger(c1).error(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());

        // Not tracking any other category.
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());
    }

    @Test
    public void defaultConstructor()
    {
        String c1=getNextTestCategory();
        String c2=getNextTestCategory();
        // mAssist was already constructed in setup method.

        // Default logging is turned off during parent class setup.
        Logger.getLogger(c1).error(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());

        setLevel(c1,Level.ERROR);
        Logger.getLogger(c1).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());

        // Not tracking any other category.
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
    }


    @Test
    public void untrackedCategory()
    {
        String c=getNextTestCategory();
        // Undo addition of appender to the root logger for this test,
        // so that we can test explicit tracking (which, if we did not
        // do this removal, would result in duplicate messages emitted
        // when the appender is added to a specific logger).
        Logger.getRootLogger().removeAppender(getAppender());

        // Default logging is turned off during parent class setup.
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
    }

    @Test
    public void categoryTrack()
    {
        String c1=getNextTestCategory();
        String c2=getNextTestCategory();
        String c3=getNextTestCategory();
        String c4=getNextTestCategory();
        // Undo addition of appender to the root logger for this test,
        // so that we can test explicit tracking (which, if we did not
        // do this removal, would result in duplicate messages emitted
        // when the appender is added to a specific logger).
        Logger.getRootLogger().removeAppender(getAppender());

        // Default logging is turned off during parent class setup.
        Logger.getLogger(c1).error(TEST_MESSAGE);
        Logger.getLogger(c2).info(TEST_MESSAGE);
        assertEquals(0,getAppender().getEvents().size());
        
        trackLogger(c2,Level.ERROR);
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(c1).info(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());
        Logger.getLogger(c3).warn(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());

        trackLogger(c3,Level.WARN);
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(2,getAppender().getEvents().size());
        Logger.getLogger(c3).error(TEST_MESSAGE);
        assertEquals(3,getAppender().getEvents().size());
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(3,getAppender().getEvents().size());
        Logger.getLogger(c3).info(TEST_MESSAGE);
        assertEquals(3,getAppender().getEvents().size());

        setDefaultLevel(Level.INFO);
        trackLogger(c4);
        Logger.getLogger(c2).error(TEST_MESSAGE);
        assertEquals(4,getAppender().getEvents().size());
        Logger.getLogger(c3).warn(TEST_MESSAGE);
        assertEquals(5,getAppender().getEvents().size());
        Logger.getLogger(c3).info(TEST_MESSAGE);
        assertEquals(5,getAppender().getEvents().size());
        Logger.getLogger(c4).info(TEST_MESSAGE);
        assertEquals(6,getAppender().getEvents().size());
        Logger.getLogger(c4).debug(TEST_MESSAGE);
        assertEquals(6,getAppender().getEvents().size());
        Logger.getLogger(c1).warn(TEST_MESSAGE);
        assertEquals(6,getAppender().getEvents().size());
    }


    @Test
    public void appenderReset()
    {
        String c=getNextTestCategory();

        setLevel(c,Level.ERROR);
        Logger.getLogger(c).error(TEST_MESSAGE);
        assertEquals(1,getAppender().getEvents().size());

        resetAppender();
        assertEquals(0,getAppender().getEvents().size());
    }


    @Test
    public void noEventsAsString()
    {
        assertEquals("Event count: 0",getEventsAsString());
    }

    @Test
    public void twoEventsAsString()
    {
        String c=getNextTestCategory();
        setLevel(c,Level.INFO);
        Logger.getLogger(c).info(TEST_MESSAGE);
        Logger.getLogger(c).warn(TEST_MESSAGE);
        assertEquals("Event count: 2"+
                     SystemUtils.LINE_SEPARATOR+
                     "Event 0: level: INFO"+
                     "; logger: "+c+
                     "; message: "+TEST_MESSAGE+
                     "; location: "+TEST_LOCATION+
                     SystemUtils.LINE_SEPARATOR+
                     "Event 1: level: WARN"+
                     "; logger: "+c+
                     "; message: "+TEST_MESSAGE+
                     "; location: "+TEST_LOCATION,getEventsAsString());
    }
}
