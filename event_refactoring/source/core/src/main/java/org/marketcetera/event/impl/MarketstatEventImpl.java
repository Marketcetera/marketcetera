package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
abstract class MarketstatEventImpl
        implements MarketstatEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getCloseDate()
     */
    @Override
    public String getCloseDate()
    {
        return marketstat.getCloseDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getCloseExchange()
     */
    @Override
    public String getCloseExchange()
    {
        return marketstat.getCloseExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getClosePrice()
     */
    @Override
    public BigDecimal getClose()
    {
        return marketstat.getClose();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getHighExchange()
     */
    @Override
    public String getHighExchange()
    {
        return marketstat.getHighExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getHighPrice()
     */
    @Override
    public BigDecimal getHigh()
    {
        return marketstat.getHigh();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return marketstat.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getLowExchange()
     */
    @Override
    public String getLowExchange()
    {
        return marketstat.getLowExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getLowPrice()
     */
    @Override
    public BigDecimal getLow()
    {
        return marketstat.getLow();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getOpenExchange()
     */
    @Override
    public String getOpenExchange()
    {
        return marketstat.getOpenExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getOpenPrice()
     */
    @Override
    public BigDecimal getOpen()
    {
        return marketstat.getOpen();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getPreviousCloseDate()
     */
    @Override
    public String getPreviousCloseDate()
    {
        return marketstat.getPreviousCloseDate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getPreviousClosePrice()
     */
    @Override
    public BigDecimal getPreviousClose()
    {
        return marketstat.getPreviousClose();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getTradeHighTime()
     */
    @Override
    public String getTradeHighTime()
    {
        return marketstat.getTradeHighTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getTradeLowTime()
     */
    @Override
    public String getTradeLowTime()
    {
        return marketstat.getTradeLowTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getVolume()
     */
    @Override
    public BigDecimal getVolume()
    {
        return marketstat.getVolume();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return marketstat.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return marketstat.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return marketstat.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        marketstat.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return marketstat.getTimeMillis();
    }
    /**
     * Create a new EquityMarketstatEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inOpenPrice
     * @param inHighPrice
     * @param inLowPrice
     * @param inClosePrice
     * @param inPreviousClosePrice
     * @param inCloseDate
     * @param inPreviousCloseDate
     * @param inTradeHighTime
     * @param inTradeLowTime
     * @param inOpenExchange
     * @param inHighExchange
     * @param inLowExchange
     * @param inCloseExchange
     */
    protected MarketstatEventImpl(long inMessageId,
                                  Date inTimestamp,
                                  Instrument inInstrument,
                                  BigDecimal inOpenPrice,
                                  BigDecimal inHighPrice,
                                  BigDecimal inLowPrice,
                                  BigDecimal inClosePrice,
                                  BigDecimal inPreviousClosePrice,
                                  String inCloseDate,
                                  String inPreviousCloseDate,
                                  String inTradeHighTime,
                                  String inTradeLowTime,
                                  String inOpenExchange,
                                  String inHighExchange,
                                  String inLowExchange,
                                  String inCloseExchange)
    {
        marketstat.setInstrument(inInstrument);
        marketstat.setOpen(inOpenPrice);
        marketstat.setHigh(inHighPrice);
        marketstat.setLow(inLowPrice);
        marketstat.setClose(inClosePrice);
        marketstat.setPreviousClose(inPreviousClosePrice);
        marketstat.setCloseDate(inCloseDate);
        marketstat.setPreviousCloseDate(inPreviousCloseDate);
        marketstat.setTradeHighTime(inTradeHighTime);
        marketstat.setTradeLowTime(inTradeLowTime);
        marketstat.setOpenExchange(inOpenExchange);
        marketstat.setHighExchange(inHighExchange);
        marketstat.setLowExchange(inLowExchange);
        marketstat.setCloseExchange(inCloseExchange);
        marketstat.setMessageId(inMessageId);
        marketstat.setTimestamp(inTimestamp);
    }
    /**
     * 
     */
    private final MarketstatBean marketstat = new MarketstatBean();
    private static final long serialVersionUID = 1L;
}
