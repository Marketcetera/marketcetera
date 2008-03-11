package org.marketcetera.quickfix;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;

/**
 * An unsupported QuickFix message was received. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public class UnsupportedMessageException
        extends MarketceteraException
{
    private static final long serialVersionUID = -5489749428430923539L;

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inMessage
     */
    public UnsupportedMessageException(String inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inNested
     */
    public UnsupportedMessageException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inKey
     */
    public UnsupportedMessageException(MessageKey inKey)
    {
        super(inKey);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inMsg
     * @param inNested
     */
    public UnsupportedMessageException(String inMsg,
                                       Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    /**
     * Create a new UnsupportedMessageException instance.
     *
     * @param inKey
     * @param inNested
     */
    public UnsupportedMessageException(MessageKey inKey,
                                       Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
