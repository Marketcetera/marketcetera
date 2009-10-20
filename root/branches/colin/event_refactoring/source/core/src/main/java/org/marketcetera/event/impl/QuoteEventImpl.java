package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.beans.QuoteBean;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class QuoteEventImpl
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
    public String getEventTime()
    {
        return quote.getQuoteTime();
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
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
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
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /**
     * Create a new QuoteEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inQuoteTime
     * @param inAction
     */
    QuoteEventImpl(long inMessageId,
                   Date inTimestamp,
                   Instrument inInstrument,
                   String inExchange,
                   BigDecimal inPrice,
                   BigDecimal inSize,
                   String inQuoteTime,
                   QuoteAction inAction)
     {
        event = new EventImpl(inMessageId,
                              inTimestamp);
        quote.setAction(inAction);
        quote.setInstrument(inInstrument);
        quote.setExchange(inExchange);
        quote.setPrice(inPrice);
        quote.setSize(inSize);
        quote.setQuoteTime(inQuoteTime);
     }
    /**
     * 
     */
    private final EventImpl event;
    /**
     * 
     */
    private final QuoteBean quote = new QuoteBean();
    private static final long serialVersionUID = 1L;
}
