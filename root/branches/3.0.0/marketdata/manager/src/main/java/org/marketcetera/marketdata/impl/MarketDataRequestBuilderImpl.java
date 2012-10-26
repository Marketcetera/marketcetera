package org.marketcetera.marketdata.impl;

import java.util.*;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.Util;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;

/* $License$ */

/**
 * Builds {@link MarketDataRequest} objects.
 * 
 * <p>This class requires external synchronization.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MarketDataRequestBuilderImpl
        implements MarketDataRequestBuilder
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withInstruments(org.marketcetera.core.trade.Instrument[])
     */
    @Override
    public MarketDataRequestBuilder withInstruments(Instrument... inInstruments)
    {
        instruments.clear();
        if(inInstruments != null) {
            instruments.addAll(Arrays.asList(inInstruments));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withInstruments(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withInstruments(Set<Instrument> inInstruments)
    {
        instruments.clear();
        if(inInstruments != null) {
            instruments.addAll(inInstruments);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withSymbols(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withSymbols(String... inSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withSymbols(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withSymbols(Set<String> inSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingInstruments(org.marketcetera.core.trade.Instrument[])
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingInstruments(Instrument... inInstruments)
    {
        underlyingInstruments.clear();
        if(inInstruments != null) {
            underlyingInstruments.addAll(Arrays.asList(inInstruments));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingInstruments(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingInstruments(Set<Instrument> inInstruments)
    {
        underlyingInstruments.clear();
        if(inInstruments != null) {
            underlyingInstruments.addAll(inInstruments);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingSymbols(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingSymbols(String... inSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withUnderlyingSymbols(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withUnderlyingSymbols(Set<String> inSymbols)
    {
        throw new UnsupportedOperationException(); // TODO
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
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withRequiredCapabilities(java.util.Set)
     */
    @Override
    public MarketDataRequestBuilder withRequiredCapabilities(Set<Capability> inCapabilities)
    {
        requiredCapabilities.clear();
        if(inCapabilities != null) {
            requiredCapabilities.addAll(inCapabilities);
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withRequiredCapabilities(org.marketcetera.marketdata.Capability[])
     */
    @Override
    public MarketDataRequestBuilder withRequiredCapabilities(Capability... inCapabilities)
    {
        requiredCapabilities.clear();
        if(inCapabilities != null) {
            requiredCapabilities.addAll(Arrays.asList(inCapabilities));
        }
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequestBuilder#withRequiredCapabilities(java.lang.String[])
     */
    @Override
    public MarketDataRequestBuilder withRequiredCapabilities(String... inCapabilities)
    {
        requiredCapabilities.clear();
        if(inCapabilities != null) {
            for(String capabilityString : inCapabilities) {
                capabilityString = StringUtils.trimToNull(capabilityString);
                requiredCapabilities.add(Capability.valueOf(capabilityString));
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
        request.getInstruments().addAll(instruments);
        request.getUnderlyingInstruments().addAll(underlyingInstruments);
        request.getContent().addAll(content);
        request.getRequiredCapabilities().addAll(requiredCapabilities);
        request.getParameters().putAll(parameters);
        request.setProvider(provider);
        request.setExchange(exchange);
        return request;
    }
    /**
     * instruments value
     */
    private final Set<Instrument> instruments = new LinkedHashSet<Instrument>();
    /**
     * underlying instruments value
     */
    private final Set<Instrument> underlyingInstruments = new LinkedHashSet<Instrument>();
    /**
     * content value
     */
    private final Set<Content> content = new LinkedHashSet<Content>();
    /**
     * required capabilities value
     */
    private final Set<Capability> requiredCapabilities = new LinkedHashSet<Capability>();
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
