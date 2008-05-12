package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestMessageTranslator
        extends AbstractMessageTranslator<String>
{

    /**
     * Create a new TestMessageTranslator instance.
     *
     */
    public TestMessageTranslator()
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(quickfix.Message)
     */
    public String translate(Message inMessage)
            throws MarketceteraException
    {
        return inMessage.toString();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(java.lang.Object)
     */
    public Message asMessage(String inData)
            throws MarketceteraException
    {
        return null;
    }
}
