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
    extends AbstractMessageTranslator<Message>
{
    private static final MarketceteraFeedMessageTranslator sInstance = new MarketceteraFeedMessageTranslator();
    static MarketceteraFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    private MarketceteraFeedMessageTranslator()
    {        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(quickfix.Message)
     */
    public Message translate(Message inMessage) 
        throws MarketceteraException
    {
        return inMessage;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(java.lang.Object)
     */
    public Message asMessage(Message inData) 
        throws MarketceteraException
    {
        return inData;
    }
}
