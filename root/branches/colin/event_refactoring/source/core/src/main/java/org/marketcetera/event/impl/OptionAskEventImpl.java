package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.OptionQuoteEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class OptionAskEventImpl
        implements AskEvent, OptionQuoteEvent
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
        return quote.getEventTime();
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
    public Option getInstrument()
    {
        return option.getInstrument();
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
     * @see org.marketcetera.event.OptionEvent#getExpirationType()
     */
    @Override
    public ExpirationType getExpirationType()
    {
        return option.getExpirationType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return option.getExpiry();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getMultiplier()
     */
    @Override
    public int getMultiplier()
    {
        return option.getMultiplier();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return option.getOptionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getStrike()
     */
    @Override
    public BigDecimal getStrike()
    {
        return option.getStrike();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#hasDeliverable()
     */
    @Override
    public boolean hasDeliverable()
    {
        return option.hasDeliverable();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasOption#getOption()
     */
    @Override
    public Option getOption()
    {
        return option.getOption();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasUnderlyingEquity#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return option.getUnderlyingEquity();
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
     * @see org.marketcetera.event.QuoteEvent#getAction()
     */
    @Override
    public QuoteAction getAction()
    {
        return quote.getAction();
    }
    /**
     * Create a new OptionAskEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inQuoteTime
     * @param inUnderlyingEquity
     * @param inStrike
     * @param inOptionType
     * @param inExpiry
     * @param inHasDeliverable
     * @param inMultiplier
     * @param inExpirationType
     * @param inAction TODO
     */
    OptionAskEventImpl(long inMessageId,
                       Date inTimestamp,
                       Option inInstrument,
                       String inExchange,
                       BigDecimal inPrice,
                       BigDecimal inSize,
                       String inQuoteTime,
                       Equity inUnderlyingEquity,
                       BigDecimal inStrike,
                       OptionType inOptionType,
                       String inExpiry,
                       boolean inHasDeliverable,
                       int inMultiplier,
                       ExpirationType inExpirationType,
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
        option = new OptionEventImpl(inInstrument,
                                     inUnderlyingEquity,
                                     inStrike,
                                     inOptionType,
                                     inExpiry,
                                     inHasDeliverable,
                                     inMultiplier,
                                     inExpirationType);
    }
    /**
     * 
     */
    private final QuoteEventImpl quote;
    /**
     * 
     */
    private final OptionEventImpl option;
    private static final long serialVersionUID = 1L;
}
