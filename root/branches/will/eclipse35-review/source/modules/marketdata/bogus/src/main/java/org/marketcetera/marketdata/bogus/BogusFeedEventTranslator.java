package org.marketcetera.marketdata.bogus;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.event.UnsupportedEventException;
import org.marketcetera.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 * Bogus feed {@link EventTranslator} instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class BogusFeedEventTranslator
        implements EventTranslator, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> toEvent(Object inData,
                                   String inHandle)
            throws CoreException
    {
        if(!(inData instanceof EventBase)) {
            throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_EVENT_TYPE,
                                                                       ObjectUtils.toString(inData,
                                                                                            null)));
        }
        EventBase event = (EventBase)inData;
        return Arrays.asList(new EventBase[] { event } );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public Object fromEvent(EventBase inEvent)
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
