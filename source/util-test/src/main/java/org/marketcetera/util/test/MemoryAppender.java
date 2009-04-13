package org.marketcetera.util.test;

import java.util.LinkedList;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Retains logging events to a memory list, for use by tests that need
 * to confirm generation of such events.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class MemoryAppender
    extends AppenderSkeleton
{

    // INSTANCE DATA.

    private PatternLayout mLayout=new PatternLayout("%C"); //$NON-NLS-1$
    private LinkedList<LoggingEvent> mEvents=new LinkedList<LoggingEvent>();


    // AppenderSkeleton.

    @Override
    protected void append
        (LoggingEvent event)
    {
        // A side-effect of formatting is that the location
        // information of the event is set.
        mLayout.format(event);

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