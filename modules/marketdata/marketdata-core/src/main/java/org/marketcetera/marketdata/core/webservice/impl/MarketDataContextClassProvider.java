package org.marketcetera.marketdata.core.webservice.impl;

import org.marketcetera.event.impl.ConvertibleBondAskEventImpl;
import org.marketcetera.event.impl.ConvertibleBondBidEventImpl;
import org.marketcetera.event.impl.ConvertibleBondImbalanceEvent;
import org.marketcetera.event.impl.ConvertibleBondMarketstatEventImpl;
import org.marketcetera.event.impl.ConvertibleBondTradeEventImpl;
import org.marketcetera.event.impl.CurrencyAskEventImpl;
import org.marketcetera.event.impl.CurrencyBidEventImpl;
import org.marketcetera.event.impl.CurrencyImbalanceEvent;
import org.marketcetera.event.impl.CurrencyMarketstatEventImpl;
import org.marketcetera.event.impl.CurrencyTradeEventImpl;
import org.marketcetera.event.impl.DividendEventImpl;
import org.marketcetera.event.impl.EquityAskEventImpl;
import org.marketcetera.event.impl.EquityBidEventImpl;
import org.marketcetera.event.impl.EquityImbalanceEvent;
import org.marketcetera.event.impl.EquityMarketstatEventImpl;
import org.marketcetera.event.impl.EquityTradeEventImpl;
import org.marketcetera.event.impl.FutureAskEventImpl;
import org.marketcetera.event.impl.FutureBidEventImpl;
import org.marketcetera.event.impl.FutureImbalanceEvent;
import org.marketcetera.event.impl.FutureMarketstatEventImpl;
import org.marketcetera.event.impl.FutureTradeEventImpl;
import org.marketcetera.event.impl.OptionAskEventImpl;
import org.marketcetera.event.impl.OptionBidEventImpl;
import org.marketcetera.event.impl.OptionImbalanceEvent;
import org.marketcetera.event.impl.OptionMarketstatEventImpl;
import org.marketcetera.event.impl.OptionTradeEventImpl;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Provides the context classes necessary for the Market Data Nexus service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketDataContextClassProvider
        implements ContextClassProvider
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.ContextClassProvider#getContextClasses()
     */
    @Override
    public Class<?>[] getContextClasses()
    {
        return EVENT_CLASSES;
    }
    /**
     * static instance
     */
    public static final MarketDataContextClassProvider INSTANCE = new MarketDataContextClassProvider();
    /**
     * list of event classes
     */
    private static final Class<?>[] EVENT_CLASSES = new Class<?>[] {
        ConvertibleBondAskEventImpl.class,ConvertibleBondBidEventImpl.class,ConvertibleBondMarketstatEventImpl.class,ConvertibleBondTradeEventImpl.class,ConvertibleBondImbalanceEvent.class,
        CurrencyAskEventImpl.class,CurrencyBidEventImpl.class,CurrencyMarketstatEventImpl.class,CurrencyTradeEventImpl.class,CurrencyImbalanceEvent.class,
        EquityAskEventImpl.class,EquityBidEventImpl.class,EquityMarketstatEventImpl.class,EquityTradeEventImpl.class,EquityImbalanceEvent.class,
        FutureAskEventImpl.class,FutureBidEventImpl.class,FutureMarketstatEventImpl.class,FutureTradeEventImpl.class,FutureImbalanceEvent.class,
        OptionAskEventImpl.class,OptionBidEventImpl.class,OptionMarketstatEventImpl.class,OptionTradeEventImpl.class,OptionImbalanceEvent.class,
        DividendEventImpl.class };
}
