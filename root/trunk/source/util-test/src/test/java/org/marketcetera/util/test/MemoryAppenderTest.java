package org.marketcetera.util.test;

import java.util.LinkedList;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryAppenderTest
{
    private static final String TEST_CATEGORY=
        MemoryAppenderTest.class.getName();
    private static final String TEST_MESSAGE=
        "Test message (expected)";

    @Test
    public void appenderStoresMessages()
    {
        MemoryAppender appender=new MemoryAppender();
		BasicConfigurator.configure(appender);

        Logger logger=Logger.getLogger(TEST_CATEGORY);
        logger.setLevel(Level.ERROR);

        logger.error(TEST_MESSAGE);

        LinkedList<LoggingEvent> events=appender.getEvents();
        assertEquals(1,events.size());

        LoggingEvent event=events.getFirst();
        assertEquals(Level.ERROR,event.getLevel());
        assertEquals(TEST_CATEGORY,event.getLoggerName());
        assertEquals(TEST_MESSAGE,event.getMessage());

        appender.clear();
        assertEquals(0,events.size());

        logger.error(TEST_MESSAGE);
        assertEquals(1,appender.getEvents().size());
    }
}
