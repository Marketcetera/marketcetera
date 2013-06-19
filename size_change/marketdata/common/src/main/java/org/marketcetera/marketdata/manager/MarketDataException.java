package org.marketcetera.marketdata.manager;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates an error related to market data operation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataException
        extends CoreException
{
    /**
     * Create a new MarketDataException instance.
     */
    public MarketDataException()
    {
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataException(Throwable inNested,
                               I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
