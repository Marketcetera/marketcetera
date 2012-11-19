package org.marketcetera.marketdata.request;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 * Builds a {@link MarketDataRequest} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestBuilder
{
    /**
     * Specifies symbols for which to request market data.
     *
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withSymbols(String...inSymbols);
    /**
     * Specifies symbols for which to request market data.
     *
     * @param inSymbols a <code>Set&lt;String&gt;</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withSymbols(Set<String> inSymbols);
    /**
     * Specifies underlying symbols for which to request market data.
     *
     * @param inSymbols a <code>String[]</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withUnderlyingSymbols(String...inSymbols);
    /**
     * Specifies underlying symbols for which to request market data.
     *
     * @param inSymbols a <code>Set&lt;String&gt;</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withUnderlyingSymbols(Set<String> inSymbols);
    public MarketDataRequestBuilder withContent(Content...inContent);
    public MarketDataRequestBuilder withContent(Set<Content> inContent);
    public MarketDataRequestBuilder withContent(String...inContent);
    public MarketDataRequestBuilder withProvider(String inProvider);
    public MarketDataRequestBuilder withExchange(String inExchange);
    public MarketDataRequestBuilder withParameters(String inParameterList);
    public MarketDataRequestBuilder withParameters(Map<String,String> inParametersList);
    public MarketDataRequestBuilder withParameters(Properties inParametersList);
    public MarketDataRequest create();
}
