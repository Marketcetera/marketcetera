package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Provides {@link MarketDataRequest} related support.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ImplementedBy(MarketDataRequestSupport.class)
@ClassVersion("$Id$")
public interface IMarketDataRequestSupport
{
    /**
     * Initializes a {@link MarketDataRequestBuilder} for the provided instrument.
     * 
     * @param instrument the instrument
     * @return a {@link MarketDataRequestBuilder} with instrument information
     */
    MarketDataRequestBuilder initializeRequest(Instrument instrument);
    /**
     * Returns whether fine grained market data is supported for options.
     * 
     * @return whether fine grained market data is supported for options
     */
    boolean useFineGrainedMarketDataForOptions();
}
