package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/**
 * Translates between the specified external data type <code>T</code> and {@link Message} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public interface IMessageTranslator<T>
{
    /**
     * Translate from <code>FIX</code> to an external data format. 
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>T</code> value
     * @throws MarketceteraException
     */
    public T translate(Message inMessage)
        throws MarketceteraException;
    
    /**
     * Translate from an external data type to <code>FIX</code> format.
     *
     * @param inData an <code>T</code> value
     * @return a <code>Message</code> value
     * @throws MarketceteraException if the message cannot be translated
     */
    public Message translate(T inData)
        throws MarketceteraException;
}
