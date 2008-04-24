package org.marketcetera.event;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;

/**
 * An event was received from a data feed but could not be translated.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public class UnsupportedEventException
        extends MarketceteraException
{

    private static final long serialVersionUID = -5589876622054757393L;

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inMessage
     */
    public UnsupportedEventException(String inMessage)
    {
        super(inMessage);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inNested
     */
    public UnsupportedEventException(Throwable inNested)
    {
        super(inNested);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inKey
     */
    public UnsupportedEventException(MessageKey inKey)
    {
        super(inKey);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inMsg
     * @param inNested
     */
    public UnsupportedEventException(String inMsg,
                                     Throwable inNested)
    {
        super(inMsg,
              inNested);
    }

    /**
     * Create a new UnsupportedEventException instance.
     *
     * @param inKey
     * @param inNested
     */
    public UnsupportedEventException(MessageKey inKey,
                                     Throwable inNested)
    {
        super(inKey,
              inNested);
    }
}
