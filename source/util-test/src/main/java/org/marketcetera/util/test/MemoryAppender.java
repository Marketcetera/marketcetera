package org.marketcetera.util.test;

import java.util.LinkedList;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.marketcetera.core.ClassVersion;

/**
 * Retains logging events to a memory list, for use by tests that need
 * to confirm generation of such events.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id")
public class MemoryAppender
    extends AppenderSkeleton
{

    // INSTANCE DATA.

    private LinkedList<LoggingEvent> mEvents=new LinkedList<LoggingEvent>();


    // AppenderSkeleton.

    @Override
    protected void append
        (LoggingEvent event)
    {
        mEvents.add(event);
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout()
    {
        return false;
    }


    // INSTANCE METHODS.

    /**
     * Returns all events retained by the receiver.
     *
     * @return The events.
     */

    public LinkedList<LoggingEvent> getEvents()
    {
        return mEvents;
    }

    /**
     * Clears the receiver's list of retained events.
     */

    public void clear()
    {
        getEvents().clear();
    }
}