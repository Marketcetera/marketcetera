package org.marketcetera.marketdata.bogus;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.EventTranslator;
import org.marketcetera.core.event.UnsupportedEventException;
import org.marketcetera.core.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 * Bogus feed {@link EventTranslator} instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedEventTranslator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
public class BogusFeedEventTranslator
        implements EventTranslator, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<Event> toEvent(Object inData,
                                   String inHandle)
            throws CoreException
    {
        if(!(inData instanceof Event)) {
            throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_EVENT_TYPE,
                                                                       ObjectUtils.toString(inData,
                                                                                            null)));
        }
        Event event = (Event)inData;
        return Arrays.asList(new Event[] { event } );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.event.IEventTranslator#translate(org.marketcetera.core.event.EventBase)
     */
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /**
     * static instance of <code>BogusFeedEventTranslator</code>
     */
    private static final BogusFeedEventTranslator sInstance = new BogusFeedEventTranslator();
    /**
     * Gets a <code>BogusFeedEventTranslator</code> instance.
     * 
     * @return a <code>BogusFeedEventTranslator</code> instance
     */
    static BogusFeedEventTranslator getInstance()
    {
        return sInstance;
    }
}
