package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates an error occurred creating a {@link MarketDataRequest}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MarketDataRequestException
        extends CoreException
{
    private static final long serialVersionUID = 1L;
    /**
     * Create a new MarketDataRequestException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public MarketDataRequestException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new MarketDataRequestException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataRequestException instance.
     *
     * @param inCause a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestException(Throwable inCause,
                                      I18NBoundMessage inMessage)
    {
        super(inCause,
              inMessage);
    }
}
