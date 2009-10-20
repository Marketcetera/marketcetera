package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
abstract class TradeEventImpl
        implements TradeEvent
{
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
     * @see org.marketcetera.event.TradeEvent#getTradeTime()
     */
    @Override
    public final String getExchangeTimestamp()
    {
        return marketData.getExchangeTimestamp();
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
    public final Instrument getInstrument()
    {
        return marketData.getInstrument();
    }
    /**
     * Create a new TradeEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inTradeTime
     */
    protected TradeEventImpl(long inMessageId,
                             Date inTimestamp,
                             Instrument inInstrument,
                             String inExchange,
                             BigDecimal inPrice,
                             BigDecimal inSize,
                             String inTradeTime)
    {
        marketData.setMessageId(inMessageId);
        marketData.setTimestamp(inTimestamp);
        marketData.setInstrument(inInstrument);
        marketData.setExchange(inExchange);
        marketData.setPrice(inPrice);
        marketData.setSize(inSize);
        marketData.setExchangeTimestamp(inTradeTime);
    }
    /**
     * 
     */
    private final MarketDataBean marketData = new MarketDataBean();
    private static final long serialVersionUID = 1L;
}
