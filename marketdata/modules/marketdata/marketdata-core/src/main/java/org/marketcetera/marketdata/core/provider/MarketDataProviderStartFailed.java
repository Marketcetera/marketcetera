package org.marketcetera.marketdata.core.provider;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a market data provider could not be started.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProviderStartFailed.java 16422 2013-01-03 19:43:24Z colin $
 * @since $Release$
 */
public class MarketDataProviderStartFailed
        extends CoreException
{
    /**
     * Create a new MarketDataProviderStartFailed instance.
     */
    public MarketDataProviderStartFailed() {}
    /**
     * Create a new MarketDataProviderStartFailed instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public MarketDataProviderStartFailed(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new MarketDataProviderStartFailed instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataProviderStartFailed(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new MarketDataProviderStartFailed instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public MarketDataProviderStartFailed(Throwable inNested,
                                         I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
