package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.EventType;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an implementation of {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public abstract class AbstractTradeEventImpl
        implements TradeEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return marketData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getExchange()
     */
    @Override
    public final String getExchange()
    {
        return marketData.getExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getPrice()
     */
    @Override
    public final BigDecimal getPrice()
    {
        return marketData.getPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getSize()
     */
    @Override
    public final BigDecimal getSize()
    {
        return marketData.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getExchangeTimestamp()
     */
    @Override
    public final String getExchangeTimestamp()
    {
        return marketData.getExchangeTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getTradeDate()
     */
    @Override
    public String getTradeDate()
    {
        return getExchangeTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public final long getMessageId()
    {
        return marketData.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#getSource()
     */
    @Override
    public final Object getSource()
    {
        return marketData.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMetaType()
     */
    @Override
    public EventType getEventType()
    {
        return marketData.getEventType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#setEventType(org.marketcetera.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        marketData.setEventType(inEventType);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#getTimestamp()
     */
    @Override
    public final Date getTimestamp()
    {
        return marketData.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#setSource(java.lang.Object)
     */
    @Override
    public final void setSource(Object inSource)
    {
        marketData.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getProvider()
     */
    @Override
    public String getProvider()
    {
        return marketData.getProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setProvider(java.lang.String)
     */
    @Override
    public void setProvider(String inProvider)
    {
        marketData.setProvider(inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.TimestampCarrier#getTimeMillis()
     */
    @Override
    public final long getTimeMillis()
    {
        return marketData.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return marketData.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return marketData.getInstrumentAsString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getTradeCondition()
     */
    @Override
    public String getTradeCondition()
    {
        return marketData.getTradeCondition();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return EventServices.eventHashCode(this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        return EventServices.eventEquals(this,
                                         obj);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append(getDescription()).append("(").append(getMessageId()).append(" ").append(getEventType()).append(") for ").append(getInstrument()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        output.append(" ").append(getInstrument()).append(" ").append(getExchange()).append(" at ").append(getExchangeTimestamp()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
   /**
     * Create a new TradeEventImpl instance.
     *
     * @param inMarketData a <code>MarketDataBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     */
    protected AbstractTradeEventImpl(MarketDataBean inMarketData)
    {
        marketData = MarketDataBean.copy(inMarketData);
        marketData.setDefaults();
        marketData.validate();
    }
    /**
     * Create a new AbstractTradeEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    protected AbstractTradeEventImpl()
    {
        marketData = new MarketDataBean();
    }
    /**
     * Gets a description of the type of event.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getDescription();
    /**
     * market data attributes
     */
    @XmlElement
    private final MarketDataBean marketData;
    private static final long serialVersionUID = 1L;
}
