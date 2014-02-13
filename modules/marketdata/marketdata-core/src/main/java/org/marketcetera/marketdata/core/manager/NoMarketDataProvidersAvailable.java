package org.marketcetera.marketdata.core.manager;

import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that there are no available market data providers to execute a market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: NoMarketDataProvidersAvailable.java 16375 2012-11-19 21:02:22Z colin $
 * @since $Release$
 */
public class NoMarketDataProvidersAvailable
        extends MarketDataRequestFailed
{
    /**
     * Create a new NoMarketDataProvidersAvailable instance.
     */
    public NoMarketDataProvidersAvailable()
    {
    }
    /**
     * Create a new NoMarketDataProvidersAvailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public NoMarketDataProvidersAvailable(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new NoMarketDataProvidersAvailable instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public NoMarketDataProvidersAvailable(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new NoMarketDataProvidersAvailable instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public NoMarketDataProvidersAvailable(Throwable inNested,
                                          I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
