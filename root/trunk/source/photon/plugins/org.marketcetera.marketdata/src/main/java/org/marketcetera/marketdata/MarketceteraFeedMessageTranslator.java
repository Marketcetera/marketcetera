/**
 * 
 */
package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;
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
        throws CoreException
    {
        return inMessage;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(java.lang.Object)
     */
    public Message asMessage(Message inData) 
        throws CoreException
    {
        return inData;
    }
}
