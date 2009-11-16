package org.marketcetera.photon.internal.marketdata;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.inject.ImplementedBy;

/* $License$ */

/**
 * Provides {@link MarketDataRequest} related support.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@ImplementedBy(MarketDataRequestSupport.class)
public interface IMarketDataRequestSupport {

    /**
     * Initializes a {@link MarketDataRequest} for the provided instrument.
     * 
     * @param instrument
     *            the instrument
     * @return a {@link MarketDataRequest} with instrument information
     */
    MarketDataRequest initializeRequest(Instrument instrument);

    /**
     * Returns whether fine grained market data is supported for options.
     * 
     * @return whether fine grained market data is supported for options
     */
    boolean useFineGrainedMarketDataForOptions();
}
