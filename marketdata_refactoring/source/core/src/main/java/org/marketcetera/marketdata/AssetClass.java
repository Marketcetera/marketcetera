package org.marketcetera.marketdata;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The asset class for market data requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequest.java 10885 2009-11-17 19:22:56Z klim $
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public enum AssetClass
{
    /**
     * equities
     */
    EQUITY,
    /**
     * options
     */
    OPTION
}