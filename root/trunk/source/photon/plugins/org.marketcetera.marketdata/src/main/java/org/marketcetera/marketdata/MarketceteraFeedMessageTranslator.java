/**
 * 
 */
package org.marketcetera.marketdata;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.AbstractMessageTranslator;

import quickfix.Message;

/**
 * @author colin
 *
 */
public class MarketceteraFeedMessageTranslator
    extends AbstractMessageTranslator<Object>
{

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(quickfix.Message)
     */
    public Object translate(Message inMessage) throws MarketceteraException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(java.lang.Object)
     */
    public Message translate(Object inData) throws MarketceteraException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
