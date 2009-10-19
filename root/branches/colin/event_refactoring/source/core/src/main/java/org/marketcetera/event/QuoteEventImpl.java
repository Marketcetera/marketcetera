package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Date;

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
        return exchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return price;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getQuoteTime()
     */
    @Override
    public String getQuoteTime()
    {
        return quoteTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getSize()
     */
    @Override
    public BigDecimal getSize()
    {
        return size;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
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
        return action;
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
        instrument = inInstrument;
        exchange = inExchange;
        price = inPrice;
        size = inSize;
        quoteTime = inQuoteTime;
        action = inAction;
     }
    /**
     * 
     */
    private final EventImpl event;
    /**
     * 
     */
    private final BigDecimal price;
    /**
     * 
     */
    private final BigDecimal size;
    /**
     * 
     */
    private final String quoteTime;
    /**
     * 
     */
    private final String exchange;
    /**
     * 
     */
    private final QuoteAction action;
    /**
     * 
     */
    private final Instrument instrument;
    private static final long serialVersionUID = 1L;
}
