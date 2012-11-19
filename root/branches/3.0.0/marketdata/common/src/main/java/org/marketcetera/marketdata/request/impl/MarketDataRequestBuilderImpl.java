package org.marketcetera.marketdata.request.impl;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.Util;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestBuilder;

/* $License$ */

/**
 * Builds {@link MarketDataRequest} objects.
 * 
 * <p>This class requires external synchronization.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequestBuilderImpl.java 16328 2012-10-26 23:01:48Z colin $
 * @since $Release$
 */
@NotThreadSafe
public class MarketDataRequestBuilderImpl
        implements MarketDataRequestBuilder
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withSymbols(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withSymbols(String... inSymbols)
    {
        symbols.clear();
        if(inSymbols != null) {
            symbols.addAll(Arrays.asList(inSymbols));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withSymbols(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withSymbols(Set<String> inSymbols)
    {
        symbols.clear();
        if(inSymbols != null) {
            symbols.addAll(inSymbols);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingSymbols(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingSymbols(String... inSymbols)
    {
        underlyingSymbols.clear();
        if(inSymbols != null) {
            underlyingSymbols.addAll(Arrays.asList(inSymbols));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingSymbols(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingSymbols(Set<String> inSymbols)
    {
        underlyingSymbols.clear();
        if(inSymbols != null) {
            underlyingSymbols.addAll(inSymbols);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withContent(org.marketcetera.marketdata.Content[])
     */
    @Override
    public MarketDataRequestBuilder withContent(Content... inContent)
    {
        content.clear();
        if(inContent != null) {
            content.addAll(Arrays.asList(inContent));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withContent(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withContent(Set<Content> inContent)
    {
        content.clear();
        if(inContent != null) {
            content.addAll(inContent);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withContent(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withContent(String... inContent)
    {
        content.clear();
        if(inContent != null) {
            for(String contentString : inContent) {
                contentString = StringUtils.trimToNull(contentString);
                content.add(Content.valueOf(contentString));
            }
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withProvider(java.lang.String)
     */
    @Override
    public MarketDataRequestBuilder withProvider(String inProvider)
    {
        provider = StringUtils.trimToNull(inProvider);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withExchange(java.lang.String)
     */
    @Override
    public MarketDataRequestBuilder withExchange(String inExchange)
    {
        exchange = StringUtils.trimToNull(inExchange);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withParameters(java.lang.String)
     */
    @Override
    public MarketDataRequestBuilder withParameters(String inParameterList)
    {
        parameters.clear();
        inParameterList = StringUtils.trimToNull(inParameterList);
        if(inParameterList != null) {
            Properties properties = Util.propertiesFromString(inParameterList);
            for(String key : properties.stringPropertyNames()) {
                key = StringUtils.trimToNull(key);
                if(key != null) {
                    String value = StringUtils.trimToNull(properties.getProperty(key));
                    if(value != null) {
                        parameters.put(key,
                                       value);
                    }
                }
            }
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withParameters(java.util.Map)
     */
    @Override
    public MarketDataRequestBuilder withParameters(Map<String,String> inParametersList)
    {
        parameters.clear();
        if(inParametersList != null) {
            parameters.putAll(inParametersList);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withParameters(java.util.Properties)
     */
    @Override
    public MarketDataRequestBuilder withParameters(Properties inParametersList)
    {
        parameters.clear();
        if(inParametersList != null) {
            for(String key : inParametersList.stringPropertyNames()) {
                key = StringUtils.trimToNull(key);
                if(key != null) {
                    String value = StringUtils.trimToNull(inParametersList.getProperty(key));
                    if(value != null) {
                        parameters.put(key,
                                       value);
                    }
                }
            }
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#create()
     */
    @Override
    public MarketDataRequest create()
    {
        MarketDataRequestImpl request = new MarketDataRequestImpl();
        request.getSymbols().addAll(symbols);
        request.getUnderlyingSymbols().addAll(underlyingSymbols);
        request.getContent().addAll(content);
        request.getParameters().putAll(parameters);
        request.setProvider(provider);
        request.setExchange(exchange);
        return request;
    }
    /**
     * instruments value
     */
    private final Set<String> symbols = new LinkedHashSet<String>();
    /**
     * underlying instruments value
     */
    private final Set<String> underlyingSymbols = new LinkedHashSet<String>();
    /**
     * content value
     */
    private final Set<Content> content = new LinkedHashSet<Content>();
    /**
     * parameters value
     */
    private final Map<String,String> parameters = new LinkedHashMap<String,String>();
    /**
     * provider value
     */
    private String provider;
    /**
     * exchange value
     */
    private String exchange;
}
