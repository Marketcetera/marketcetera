package org.marketcetera.marketdata.request.impl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.impl.ConvertibleBondImpl;
import org.marketcetera.core.trade.impl.EquityImpl;
import org.marketcetera.core.trade.impl.FutureImpl;
import org.marketcetera.core.trade.impl.OptionImpl;
import org.marketcetera.marketdata.Content;
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
     * @see org.marketcetera.marketdata.MarketDataRequest#getInstruments()
     */
    @Override
    public Set<Instrument> getInstruments()
    {
        return instruments;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.MarketDataRequest#getUnderlyingInstruments()
     */
    @Override
    public Set<Instrument> getUnderlyingInstruments()
    {
        return underlyingInstruments;
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
        return ToStringBuilder.reflectionToString(this,
                                                  ToStringStyle.SHORT_PREFIX_STYLE);
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
     * Create a new MarketDataRequestImpl instance.
     */
    MarketDataRequestImpl()
    {
    }
    /**
     * instruments value
     */
    @XmlElementWrapper(name="instruments",required=false)
    @XmlElementRefs({ @XmlElementRef(type=ConvertibleBondImpl.class), @XmlElementRef(type=EquityImpl.class), @XmlElementRef(type=OptionImpl.class), @XmlElementRef(type=FutureImpl.class)} )
    private final Set<Instrument> instruments = new LinkedHashSet<Instrument>();
    /**
     * underlying instruments value
     */
    @XmlElementWrapper(name="underlyingInstruments",required=false)
    @XmlElementRefs({ @XmlElementRef(type=ConvertibleBondImpl.class), @XmlElementRef(type=EquityImpl.class), @XmlElementRef(type=OptionImpl.class), @XmlElementRef(type=FutureImpl.class)} )
    private final Set<Instrument> underlyingInstruments = new LinkedHashSet<Instrument>();
    /**
     * content value
     */
    @XmlElementWrapper(name="content",required=true)
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
