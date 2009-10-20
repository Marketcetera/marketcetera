package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.InstrumentBean;
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
        return marketstat.getClosePrice();
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
        return marketstat.getHighPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument.getInstrument();
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
        return marketstat.getLowPrice();
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
        return marketstat.getOpenPrice();
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
        return marketstat.getPreviousClosePrice();
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
        event = new EventImpl(inMessageId,
                              inTimestamp);
        instrument.setInstrument(inInstrument);
        marketstat.setOpenPrice(inOpenPrice);
        marketstat.setHighPrice(inHighPrice);
        marketstat.setLowPrice(inLowPrice);
        marketstat.setClosePrice(inClosePrice);
        marketstat.setPreviousClosePrice(inPreviousClosePrice);
        marketstat.setCloseDate(inCloseDate);
        marketstat.setPreviousCloseDate(inPreviousCloseDate);
        marketstat.setTradeHighTime(inTradeHighTime);
        marketstat.setTradeLowTime(inTradeLowTime);
        marketstat.setOpenExchange(inOpenExchange);
        marketstat.setHighExchange(inHighExchange);
        marketstat.setLowExchange(inLowExchange);
        marketstat.setCloseExchange(inCloseExchange);
    }
    private final InstrumentBean instrument = new InstrumentBean();
    /**
     * 
     */
    private final MarketstatBean marketstat = new MarketstatBean();
    /**
     * 
     */
    private final EventImpl event;
    private static final long serialVersionUID = 1L;
}
