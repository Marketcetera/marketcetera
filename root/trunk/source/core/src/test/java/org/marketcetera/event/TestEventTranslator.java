package org.marketcetera.event;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.MarketceteraException;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestEventTranslator
        implements IEventTranslator
{

    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> translate(Object inData)
            throws MarketceteraException
    {
        return Arrays.asList(new EventBase[] { new UnknownEvent(System.nanoTime(),
                                                                System.currentTimeMillis()) });
    }

    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public String translate(EventBase inEvent)
            throws MarketceteraException
    {
        return inEvent.toString();
    }
}
