package org.marketcetera.marketdata.manager;

import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a market data request could not be executed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRequestFailed
        extends MarketDataException
{
    /**
     * Create a new MarketDataRequestFailed instance.
     */
    public MarketDataRequestFailed()
    {
    }
    /**
     * Create a new MarketDataRequestFailed instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataRequestFailed(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MarketDataRequestFailed instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestFailed(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataRequestFailed instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataRequestFailed(Throwable inNested,
                                   I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
