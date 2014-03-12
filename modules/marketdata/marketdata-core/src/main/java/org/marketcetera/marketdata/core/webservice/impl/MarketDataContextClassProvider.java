package org.marketcetera.marketdata.core.webservice.impl;

import java.util.Arrays;
import java.util.List;

import org.marketcetera.event.impl.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

import com.google.common.collect.Lists;

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
        if(additionalClasses != null && !additionalClasses.isEmpty()) {
            List<Class<?>> allClasses = Lists.newArrayList(additionalClasses);
            allClasses.addAll(Arrays.asList(EVENT_CLASSES));
            return allClasses.toArray(new Class<?>[allClasses.size()]);
        }
        return EVENT_CLASSES;
    }
    /**
     * Get the additionalClasses value.
     *
     * @return a <code>List&lt;Class&lt;?&gt;&gt;</code> value
     */
    public List<Class<?>> getAdditionalClasses()
    {
        return additionalClasses;
    }
    /**
     * Sets the additionalClasses value.
     *
     * @param inAdditionalClasses a <code>List&lt;Class&lt;?&gt;&gt;</code> value
     */
    public void setAdditionalClasses(List<Class<?>> inAdditionalClasses)
    {
        additionalClasses = inAdditionalClasses;
    }
    /**
     * additional classes to provide, may be empty
     */
    private List<Class<?>> additionalClasses = Lists.newArrayList();
    /**
     * list of event classes
     */
    private static final Class<?>[] EVENT_CLASSES = new Class<?>[] {
        ConvertibleBondAskEventImpl.class,ConvertibleBondBidEventImpl.class,ConvertibleBondMarketstatEventImpl.class,ConvertibleBondTradeEventImpl.class,
        CurrencyAskEventImpl.class,CurrencyBidEventImpl.class,CurrencyMarketstatEventImpl.class,CurrencyTradeEventImpl.class,
        EquityAskEventImpl.class,EquityBidEventImpl.class,EquityMarketstatEventImpl.class,EquityTradeEventImpl.class,
        FutureAskEventImpl.class,FutureBidEventImpl.class,FutureMarketstatEventImpl.class,FutureTradeEventImpl.class,
        OptionAskEventImpl.class,OptionBidEventImpl.class,OptionMarketstatEventImpl.class,OptionTradeEventImpl.class,
        DividendEventImpl.class };
}
