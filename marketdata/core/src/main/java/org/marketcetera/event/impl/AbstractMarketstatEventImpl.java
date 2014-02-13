package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.EventType;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.EventBean;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an implementation for {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
abstract class AbstractMarketstatEventImpl
        implements MarketstatEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public EventBean getEventBean()
    {
        return marketstat;
    }
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
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return marketstat.getInstrumentAsString();
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
     * @see org.marketcetera.event.MarketstatEvent#getValue()
     */
    @Override
    public BigDecimal getValue()
    {
        return marketstat.getValue();
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
     * @see org.marketcetera.event.Event#getMetaType()
     */
    @Override
    public EventType getEventType()
    {
        return marketstat.getEventType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.MarketstatEvent#setEventType(org.marketcetera.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        marketstat.setEventType(inEventType);
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
     * @see org.marketcetera.event.Event#getProvider()
     */
    @Override
    public String getProvider()
    {
        return marketstat.getProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setProvider(java.lang.String)
     */
    @Override
    public void setProvider(String inProvider)
    {
        marketstat.setProvider(inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return marketstat.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String closeDateString = getCloseDate() == null ? "---" : getCloseDate(); //$NON-NLS-1$
        String previousCloseDateString = getPreviousCloseDate() == null ? "---" : getPreviousCloseDate(); //$NON-NLS-1$
        return String.format("Statistics for %s [%s] -> Open: %s High: %s Low: %s Close: %s (%s) Previous Close: %s (%s) %s %s %s %s Volume: %s Value: %s", //$NON-NLS-1$
                             getInstrument().getSymbol(),
                             getEventType(),
                             getOpen(),
                             getHigh(),
                             getLow(),
                             getClose(),
                             closeDateString,
                             getPreviousClose(),
                             previousCloseDateString,
                             getOpenExchange(),
                             getHighExchange(),
                             getLowExchange(),
                             getCloseExchange(),
                             getVolume(),
                             getValue());
    }
   /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        return EventServices.eventHashCode(this);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(Object obj)
    {
        return EventServices.eventEquals(this,
                                         obj);
    }
    /**
     * Create a new EquityMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    protected AbstractMarketstatEventImpl(MarketstatBean inMarketstat)
    {
        marketstat = MarketstatBean.copy(inMarketstat);
        marketstat.setDefaults();
        marketstat.validate();
    }
    /**
     * the marketstat attributes
     */
    private final MarketstatBean marketstat;
    private static final long serialVersionUID = 1L;
}
