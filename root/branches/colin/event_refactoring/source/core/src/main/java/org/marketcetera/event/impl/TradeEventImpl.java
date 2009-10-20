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
    public String getExchange()
    {
        return marketData.getExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return marketData.getPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getSize()
     */
    @Override
    public BigDecimal getSize()
    {
        return marketData.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getTradeTime()
     */
    @Override
    public String getExchangeTimestamp()
    {
        return marketData.getExchangeTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return marketData.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return marketData.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return marketData.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        marketData.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketData.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
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
                             Instrument inEquity,
                             String inExchange,
                             BigDecimal inPrice,
                             BigDecimal inSize,
                             String inTradeTime)
    {
        marketData.setMessageId(inMessageId);
        marketData.setTimestamp(inTimestamp);
        marketData.setInstrument(inEquity);
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
