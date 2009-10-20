package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.util.QuoteAction;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class EquityAskEventImpl
        implements AskEvent, EquityEvent
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
    public Equity getInstrument()
    {
        return (Equity)quote.getInstrument();
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
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return (Equity)quote.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.QuoteEvent#getAction()
     */
    @Override
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
    }
    /**
     * Create a new EquityAskEventImpl instance.
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
    EquityAskEventImpl(long inMessageId,
                       Date inTimestamp,
                       Equity inInstrument,
                       String inExchange,
                       BigDecimal inPrice,
                       BigDecimal inSize,
                       String inQuoteTime,
                       QuoteAction inAction)
    {
        quote = new QuoteEventImpl(inMessageId,
                                   inTimestamp,
                                   inInstrument,
                                   inExchange,
                                   inPrice,
                                   inSize,
                                   inQuoteTime,
                                   inAction);
    }
    /**
     * 
     */
    private final QuoteEventImpl quote;
    private static final long serialVersionUID = 1L;
}
