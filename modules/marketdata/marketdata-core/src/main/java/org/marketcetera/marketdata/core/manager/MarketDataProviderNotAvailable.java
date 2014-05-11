package org.marketcetera.marketdata.core.manager;

import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a requested market data provider was not available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataProviderNotAvailable.java 16375 2012-11-19 21:02:22Z colin $
 * @since 2.4.0
 */
@ClassVersion("$Id$")
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
