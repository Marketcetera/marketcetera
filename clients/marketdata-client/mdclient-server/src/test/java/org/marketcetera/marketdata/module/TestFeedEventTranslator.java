package org.marketcetera.marketdata.module;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTranslator;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Translates events for {@link TestFeed}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TestFeedEventTranslator
        implements EventTranslator
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventTranslator#toEvent(java.lang.Object, java.lang.String)
     */
    @Override
    public List<Event> toEvent(Object inData,
                               String inHandle)
            throws CoreException
    {
        if(inData instanceof Event) {
            return Lists.newArrayList((Event)inData);
        }
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventTranslator#fromEvent(org.marketcetera.event.Event)
     */
    @Override
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        return inEvent;
    }
    /**
     * singleton instance
     */
    public static final TestFeedEventTranslator instance = new TestFeedEventTranslator();
}
