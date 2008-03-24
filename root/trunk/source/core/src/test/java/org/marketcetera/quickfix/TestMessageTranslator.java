package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/**
 * Test implementation of {@link MessageTranslatorBase}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class TestMessageTranslator
        extends MessageTranslatorBase
{
    /**
     * Create a new TestMessageTranslator instance.
     *
     * @param inMessage
     * @throws MarketceteraException
     */
    public TestMessageTranslator(Message inMessage)
            throws MarketceteraException
    {
        super(inMessage);
    }
}
