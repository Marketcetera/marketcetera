package org.marketcetera.marketdata.request;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 * Represents a request for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
public interface MarketDataRequest
{
    /**
     * Gets the symbols for which market data is requested.
     * 
     * <p>At least one symbol or underlying symbol is required. The symbol
     * will be interpreted by the market data provider.
     * 
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getSymbols();
    /**
     * Gets the underlying symbols for which market data is requested.
     * 
     * <p>At least one symbol or underlying symbol is required. A market data request
     * for an underlying symbol will seek to return market data for all option/future instruments
     * with the underlying instrument that corresponds to the given symbol. The underlying symbol
     * will be interpreted by the market data provider.
     * 
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getUnderlyingSymbols();
    /**
     * Get the content type or types for which market data is requested.
     *
     * <p>At least one content type must be requested.
     *
     * @return a <code>Set&lt;Content&gt;</code> value
     */
    public Set<Content> getContent();
    /**
     * Gets the market data provider to which to target this request.
     * 
     * <p>If specified, the market data request will be targeted to the given provider
     * only. This value is optional.
     *
     * @return a <code>String</code> value
     */
    public String getProvider();
    /**
     * Gets the exchange from which market data is requested.
     *
     * <p>If specified, the market data request will return market data from the given
     * exchange only. This value is optional. If not specified, no exchange filtering is done.
     * This value may or may not make sense depending on the provider. If, for example, the market
     * data provider is a direct exchange connection, and, therefore, all market data from the provider
     * is by definition from the same exchange, the market data provider may choose to ignore this
     * attribute altogether.
     * 
     * @return a <code>String</code> value
     */
    public String getExchange();
    /**
     * Gets the exchange-specific parameters for the request.
     *
     * <p>If specified, returns a map of parameters that may affect how the request is executed. The
     * parameters are specific to the provider, so some knowledge of the implementation of the provider
     * is required.
     *
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    public Map<String,String> getParameters();
}
