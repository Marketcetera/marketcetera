package org.marketcetera.marketdata.manager;

import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a requested market data provider was not available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataProviderNotAvailable
        extends MarketDataRequestFailed
{
    /**
     * Create a new MarketDataProviderNotAvailable instance.
     */
    public MarketDataProviderNotAvailable()
    {
    }
    /**
     * Create a new MarketDataProviderNotAvailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataProviderNotAvailable(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MarketDataProviderNotAvailable instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataProviderNotAvailable(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataProviderNotAvailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataProviderNotAvailable(Throwable inNested,
                                          I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
