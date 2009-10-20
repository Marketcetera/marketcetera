package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.HasEquity;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class EquityTradeEventImpl
        implements TradeEvent, HasEquity
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getExchange()
     */
    @Override
    public String getExchange()
    {
        return exchangeCommon.getExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getPrice()
     */
    @Override
    public BigDecimal getPrice()
    {
        return exchangeCommon.getPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getSize()
     */
    @Override
    public BigDecimal getSize()
    {
        return exchangeCommon.getSize();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TradeEvent#getTradeTime()
     */
    @Override
    public String getEventTime()
    {
        return exchangeCommon.getExchangeTimestamp();
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
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
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
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return event.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getEquity()
     */
    @Override
    public Equity getEquity()
    {
        return getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return (Equity)equity.getInstrument();
    }
    /**
     * Create a new EquityTradeEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inEquity
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inTradeTime
     */
    EquityTradeEventImpl(long inMessageId,
                         Date inTimestamp,
                         Equity inEquity,
                         String inExchange,
                         BigDecimal inPrice,
                         BigDecimal inSize,
                         String inTradeTime)
    {
        event = new EventImpl(inMessageId,
                              inTimestamp);
        equity.setInstrument(inEquity);
        exchangeCommon.setExchange(inExchange);
        exchangeCommon.setPrice(inPrice);
        exchangeCommon.setSize(inSize);
        exchangeCommon.setExchangeTimestamp(inTradeTime);
    }
    /**
     * 
     */
    private final InstrumentBean equity = new InstrumentBean();
    /**
     * 
     */
    private final MarketDataBean exchangeCommon = new MarketDataBean();
    /**
     * 
     */
    private final EventImpl event;
    private static final long serialVersionUID = 1L;
}
