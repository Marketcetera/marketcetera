package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.EventType;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implements {@link QuoteEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
public abstract class AbstractQuoteEventImpl
        implements QuoteEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return quote;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getExchange()
     */
    @Override
    public String getExchange()
    {
        return quote.getExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return quote.getPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getExchangeTimestamp()
     */
    @Override
    public String getExchangeTimestamp()
    {
        return quote.getExchangeTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getQuoteDate()
     */
    @Override
    public String getQuoteDate()
    {
        return getExchangeTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getSize()
     */
    @Override
    public BigDecimal getSize()
    {
        return quote.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return quote.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return quote.getInstrumentAsString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return quote.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return quote.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return quote.getTimeMillis();
    }
    /**
     * Get the action value.
     *
     * @return a <code>QuoteAction</code> value
     */
    public QuoteAction getAction()
    {
        return quote.getAction();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return quote.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        quote.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getProvider()
     */
    @Override
    public String getProvider()
    {
        return quote.getProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setProvider(java.lang.String)
     */
    @Override
    public void setProvider(String inProvider)
    {
        quote.setProvider(inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMetaType()
     */
    @Override
    public EventType getEventType()
    {
        return quote.getEventType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#setEventType(org.marketcetera.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        quote.setEventType(inEventType);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#getProcessedTimestamp()
     */
    @Override
    public long getProcessedTimestamp()
    {
        return quote.getProcessedTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#setProcessedTimestamp(long)
     */
    @Override
    public void setProcessedTimestamp(long inTimestamp)
    {
        quote.setProcessedTimestamp(inTimestamp);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#getReceivedTimestamp()
     */
    @Override
    public long getReceivedTimestamp()
    {
        return quote.getReceivedTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketDataEvent#setReceivedTimestamp(long)
     */
    @Override
    public void setReceivedTimestamp(long inTimestamp)
    {
        quote.setReceivedTimestamp(inTimestamp);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getLevel()
     */
    @Override
    public int getLevel()
    {
        return quote.getLevel();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getCount()
     */
    @Override
    public int getCount()
    {
        return quote.getCount();
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
        output.append(getDescription()).append("(").append(getAction()).append("-").append(getMessageId()).append(" ").append(getEventType()).append(") for ").append(getInstrument()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        output.append(" ").append(getInstrument()).append(" ").append(getExchange()).append(" at ").append(getExchangeTimestamp()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
    /**
     * Gets a description of the type of event.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getDescription();
    /**
     * Create a new QuoteEventImpl instance.
     *
     * @param inQuote a <code>QuoteBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     */
    protected AbstractQuoteEventImpl(QuoteBean inQuote)
    {
        quote = QuoteBean.copy(inQuote);
        quote.setDefaults();
        quote.validate();
    }
    /**
     * Create a new AbstractQuoteEventImpl instance.
     * 
     * <p>Intended to be used by JAXB only.
     */
    protected AbstractQuoteEventImpl()
    {
        quote = new QuoteBean();
    }
    /**
     * quote attributes
     */
    @XmlElement
    private final QuoteBean quote;
    private static final long serialVersionUID = 1L;
}
