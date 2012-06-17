package com.marketcetera.marketdata.reuters;

import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedEventTranslator.java 82348 2012-05-03 23:45:18Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: ReutersFeedEventTranslator.java 82348 2012-05-03 23:45:18Z colin $")
public class ReutersFeedEventTranslator
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
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventTranslator#fromEvent(org.marketcetera.event.Event)
     */
    @Override
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
}
