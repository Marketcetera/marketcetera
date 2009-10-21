package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an implementation for {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
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
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inOpenPrice a <code>BigDecimal</code> value
     * @param inHighPrice a <code>BigDecimal</code> value
     * @param inLowPrice a <code>BigDecimal</code> value
     * @param inClosePrice a <code>BigDecimal</code> value
     * @param inPreviousClosePrice a <code>BigDecimal</code> value
     * @param inCloseDate a <code>String</code> value
     * @param inPreviousCloseDate a <code>String</code> value
     * @param inTradeHighTime a <code>String</code> value
     * @param inTradeLowTime a <code>String</code> value
     * @param inOpenExchange a <code>String</code> value
     * @param inHighExchange a <code>String</code> value
     * @param inLowExchange a <code>String</code> value
     * @param inCloseExchange a <code>String</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inInstrument</code> is <code>null</code>
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
        marketstat.setDefaults();
        marketstat.validate();
    }
    /**
     * the marketstat attributes
     */
    private final MarketstatBean marketstat = new MarketstatBean();
    private static final long serialVersionUID = 1L;
}
