package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.*;
import org.marketcetera.event.beans.HasEventBean;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an implementation for {@link ImbalanceEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
abstract class AbstractImbalanceEvent
        implements ImbalanceEvent, HasEventBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return imbalance.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return imbalance.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return imbalance.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        imbalance.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getProvider()
     */
    @Override
    public String getProvider()
    {
        return imbalance.getProvider();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setProvider(java.lang.String)
     */
    @Override
    public void setProvider(String inProvider)
    {
        imbalance.setProvider(inProvider);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return imbalance.getTimeMillis();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEventType#getEventType()
     */
    @Override
    public EventType getEventType()
    {
        return imbalance.getEventType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEventType#setEventType(org.marketcetera.event.EventType)
     */
    @Override
    public void setEventType(EventType inEventType)
    {
        imbalance.setEventType(inEventType);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return imbalance.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return imbalance.getInstrumentAsString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.beans.HasEventBean#getEventBean()
     */
    @Override
    public ImbalanceBean getEventBean()
    {
        return imbalance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getAuctionType()
     */
    @Override
    public AuctionType getAuctionType()
    {
        return imbalance.getAuctionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getExchange()
     */
    @Override
    public String getExchange()
    {
        return imbalance.getExchange();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getFarPrice()
     */
    @Override
    public BigDecimal getFarPrice()
    {
        return imbalance.getFarPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getImbalanceVolume()
     */
    @Override
    public BigDecimal getImbalanceVolume()
    {
        return imbalance.getImbalanceVolume();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getInstrumentStatus()
     */
    @Override
    public InstrumentStatus getInstrumentStatus()
    {
        return imbalance.getInstrumentStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getImbalanceus()
     */
    @Override
    public MarketStatus getMarketStatus()
    {
        return imbalance.getMarketStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getNearPrice()
     */
    @Override
    public BigDecimal getNearPrice()
    {
        return imbalance.getNearPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getPairedVolume()
     */
    @Override
    public BigDecimal getPairedVolume()
    {
        return imbalance.getPairedVolume();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getReferencePrice()
     */
    @Override
    public BigDecimal getReferencePrice()
    {
        return imbalance.getReferencePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#getImbalanceType()
     */
    @Override
    public ImbalanceType getImbalanceType()
    {
        return imbalance.getImbalanceType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.ImbalanceEvent#isShortSaleRestricted()
     */
    @Override
    public boolean isShortSaleRestricted()
    {
        return imbalance.getShortSaleRestricted();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Imbalance for ").append(imbalance.getInstrument().getFullSymbol()).append(" [").append(imbalance.getEventType()).append("] ")
            .append(" auction: ").append(imbalance.getAuctionType()).append(" on exchange ").append(imbalance.getExchange()).append(" instrument status: ").append(imbalance.getInstrumentStatus())
            .append(" market status: ").append(imbalance.getMarketStatus()).append(" at ").append(imbalance.getTimestamp());
        return builder.toString();
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
     * Create a new AbstractImbalanceEventImpl instance.
     *
     * @param inImbalanceBean a <code>ImbalanceBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    protected AbstractImbalanceEvent(ImbalanceBean inImbalance)
    {
        imbalance = ImbalanceBean.copy(inImbalance);
        imbalance.setDefaults();
        imbalance.validate();
    }
    /**
     * Create a new AbstractImbalanceEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    protected AbstractImbalanceEvent()
    {
        imbalance = new ImbalanceBean();
    }
    /**
     * imbalance value
     */
    @XmlElement
    private final ImbalanceBean imbalance;
    private static final long serialVersionUID = 981566862437586058L;
}
