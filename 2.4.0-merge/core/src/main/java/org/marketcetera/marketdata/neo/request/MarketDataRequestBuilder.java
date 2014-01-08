package org.marketcetera.marketdata.neo.request;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.marketdata.Content;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Builds a {@link MarketDataRequest} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestBuilder.java 16375 2012-11-19 21:02:22Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
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
    /**
     * Specifies content of which to request market data.
     *
     * @param inContent a <code>Content[]</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withContent(Content...inContent);
    /**
     * Specifies content of which to request market data.
     *
     * @param inContent a <code>Set&lt;Content&gt;</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withContent(Set<Content> inContent);
    /**
     * Specifies content of which to request market data.
     *
     * @param inContent a <code>Content[]</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withContent(String...inContent);
    /**
     * Specifies the provider from which to request market data.
     *
     * @param inProvider a <code>String</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withProvider(String inProvider);
    /**
     * Specifies the exchange from which to request market data.
     *
     * @param inExchange a <code>String</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withExchange(String inExchange);
    /**
     * Specifies parameters to pass to the provider.
     *
     * @param inParameterList a <code>String</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withParameters(String inParameterList);
    /**
     * Specifies parameters to pass to the provider.
     *
     * @param inParametersList a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withParameters(Map<String,String> inParametersList);
    /**
     * Specifies parameters to pass to the provider.
     *
     * @param inParametersList a <code>Properties</code> value
     * @return a <code>MarketDataRequestBuilder</code> value
     */
    public MarketDataRequestBuilder withParameters(Properties inParametersList);
    /**
     * Creates the market data request.
     *
     * @return a <code>MarketDataRequest</code> value
     */
    public MarketDataRequest create();
}
