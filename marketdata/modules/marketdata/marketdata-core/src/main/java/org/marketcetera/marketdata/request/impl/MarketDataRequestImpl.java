package org.marketcetera.marketdata.request.impl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.Validate;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MapAdapter;
import org.marketcetera.marketdata.Messages;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 * Represents a market data request.
 * 
 * <p>This class requires external synchronization.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestImpl.java 16328 2012-10-26 23:01:48Z colin $
 * @since $Release$
 */
@NotThreadSafe
@XmlRootElement(name="marketDataRequest")
@XmlAccessorType(XmlAccessType.NONE)
class MarketDataRequestImpl
        implements MarketDataRequest
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getSymbols()
     */
    @Override
    public Set<String> getSymbols()
    {
        return symbols;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getUnderlyingSymbols()
     */
    @Override
    public Set<String> getUnderlyingSymbols()
    {
        return underlyingSymbols;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequest#getContent()
     */
    @Override
    public Set<Content> getContent()
    {
        return content;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequest#getProvider()
     */
    @Override
    public String getProvider()
    {
        return provider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequest#getExchange()
     */
    @Override
    public String getExchange()
    {
        return exchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequest#getParameters()
     */
    @Override
    public Map<String,String> getParameters()
    {
        return parameters;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketDataRequest");
        if(!symbols.isEmpty()) {
            builder.append(" symbols: ").append(symbols);
        }
        if(!underlyingSymbols.isEmpty()) {
            builder.append(" underlying symbols: ").append(underlyingSymbols);
        }
        if(!content.isEmpty()) {
            builder.append(" content: ").append(content);
        }
        if(provider != null) {
            builder.append(" provider: ").append(provider);
        }
        if(exchange != null) {
            builder.append(" exchange: ").append(exchange);
        }
        if(!parameters.isEmpty()) {
            builder.append(" parameters: ").append(parameters);
        }
        return builder.toString();
    }
    /**
     * Sets the provider value.
     *
     * @param inProvider a <code>String</code> value
     */
    void setProvider(String inProvider)
    {
        provider = inProvider;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value
     */
    void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * Validates the request object.
     * 
     * @throws IllegalArgumentException if validation fails
     */
    void validate()
    {
        Validate.isTrue(!(symbols.isEmpty() && underlyingSymbols.isEmpty()),
                        Messages.NO_SYMBOLS_OR_UNDERLYING_SYMBOLS.getText());
        Validate.notEmpty(content,
                          Messages.NO_CONTENT.getText());
    }
    /**
     * Create a new MarketDataRequestImpl instance.
     */
    MarketDataRequestImpl()
    {
    }
    /**
     * instruments value
     */
    @XmlElementWrapper(name="symbols",required=false)
    @XmlElement(name="symbol")
    private final Set<String> symbols = new LinkedHashSet<String>();
    /**
     * underlying instruments value
     */
    @XmlElementWrapper(name="underlyingSymbols",required=false)
    @XmlElement(name="underlyingSymbol")
    private final Set<String> underlyingSymbols = new LinkedHashSet<String>();
    /**
     * content value
     */
    @XmlElementWrapper(name="contentTypes",required=true)
    @XmlElement(name="content")
    private final Set<Content> content = new LinkedHashSet<Content>();
    /**
     * parameters value
     */
    @XmlElement(name="parameters",required=false)
    @XmlJavaTypeAdapter(MapAdapter.class)
    private final Map<String,String> parameters = new LinkedHashMap<String,String>();
    /**
     * provider value
     */
    @XmlElement(required=false)
    private String provider;
    /**
     * exchange value
     */
    @XmlElement(required=false)
    private String exchange;
}
