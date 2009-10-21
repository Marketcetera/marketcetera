package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Implements {@link QuoteEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
abstract class AbstractQuoteEventImpl
        implements QuoteEvent
{
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
     * @see org.marketcetera.event.QuoteEvent#getQuoteTime()
     */
    @Override
    public String getExchangeTimestamp()
    {
        return quote.getExchangeTimestamp();
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (quote.getMessageId() ^ (quote.getMessageId() >>> 32));
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractQuoteEventImpl other = (AbstractQuoteEventImpl) obj;
        if (quote.getMessageId() != other.quote.getMessageId())
            return false;
        return true;
    }
    /**
     * Create a new QuoteEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inQuoteTime a <code>String</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>inPrice</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inSize</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inQuoteTime</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>QuoteAction</code> is <code>null</code>
     */
    AbstractQuoteEventImpl(long inMessageId,
                           Date inTimestamp,
                           Instrument inInstrument,
                           String inExchange,
                           BigDecimal inPrice,
                           BigDecimal inSize,
                           String inQuoteTime,
                           QuoteAction inAction)
     {
        quote.setMessageId(inMessageId);
        quote.setTimestamp(inTimestamp);
        quote.setInstrument(inInstrument);
        quote.setExchange(inExchange);
        quote.setPrice(inPrice);
        quote.setSize(inSize);
        quote.setExchangeTimestamp(inQuoteTime);
        quote.setAction(inAction);
        quote.setDefaults();
        quote.validate();
     }
    /**
     * quote attributes
     */
    private final QuoteBean quote = new QuoteBean();
    private static final long serialVersionUID = 1L;
}
