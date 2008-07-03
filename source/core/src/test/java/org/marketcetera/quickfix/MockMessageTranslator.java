package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/**
 * Test implementation of <code>AbstractMessageTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class MockMessageTranslator
        extends AbstractMessageTranslator<String>
{
    private static boolean sTranslateThrows = false;
    /**
     * Create a new TestMessageTranslator instance.
     *
     */
    public MockMessageTranslator()
    {
    }

    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(quickfix.Message)
     */
    public String translate(Message inMessage)
            throws MarketceteraException
    {
        if(getTranslateThrows()) {
            throw new NullPointerException("This exception is expected");
        }
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

    public static boolean getTranslateThrows()
    {
        return sTranslateThrows;
    }

    public static void setTranslateThrows(boolean inTranslateThrows)
    {
        sTranslateThrows = inTranslateThrows;
    }
}
