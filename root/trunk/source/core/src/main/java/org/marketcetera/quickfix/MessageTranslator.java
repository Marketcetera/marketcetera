package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/**
 * Translates to and from {@link Message} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public interface MessageTranslator
{
    public Object translate(Message inMessage)
        throws MarketceteraException;
    
    public Message translate(Object inData)
        throws MarketceteraException;
}
