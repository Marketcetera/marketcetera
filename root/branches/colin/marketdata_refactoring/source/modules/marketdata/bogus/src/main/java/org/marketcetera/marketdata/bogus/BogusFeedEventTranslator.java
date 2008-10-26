package org.marketcetera.marketdata.bogus;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.IEventTranslator;
import org.marketcetera.event.UnsupportedEventException;
import org.marketcetera.util.log.I18NBoundMessage1P;

/* $License$ */

/**
 * Bogus feed {@link IEventTranslator} instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedEventTranslator.java 9458 2008-08-01 05:27:12Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusFeedEventTranslator.java 9458 2008-08-01 05:27:12Z colin $") //$NON-NLS-1$
public class BogusFeedEventTranslator
        implements IEventTranslator, Messages
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> translate(Object inData)
            throws CoreException
    {
        if(!(inData instanceof EventBase)) {
            throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_EVENT_TYPE,
                                                                       inData));
        }
        EventBase event = (EventBase)inData;
        return Arrays.asList(new EventBase[] { event } );
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public Object translate(EventBase inEvent)
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
