package org.marketcetera.marketdata;

import java.util.List;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.IEventTranslator;

/**
 * 
 * @author colin
 */
public class MarketceteraFeedEventTranslator
    implements IEventTranslator
{

    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> translate(Object inData) throws MarketceteraException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public Object translate(EventBase inEvent) throws MarketceteraException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
