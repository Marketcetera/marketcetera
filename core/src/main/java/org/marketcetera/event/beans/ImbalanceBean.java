package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.event.*;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Contains information about an imbalance event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class ImbalanceBean
        extends EventBean
{
    /**
     * Creates a shallow copy of the given <code>ImbalanceBean</code>.
     *
     * @param inBean an <code>ImbalanceBean</code> value
     * @return an <code>ImbalanceBean</code> value
     */
    public static ImbalanceBean copy(ImbalanceBean inBean)
    {
        ImbalanceBean newBean = new ImbalanceBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Create a new ImbalanceBean instance.
     */
    public ImbalanceBean()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Get the auctionType value.
     *
     * @return an <code>AuctionType</code> value
     */
    public AuctionType getAuctionType()
    {
        return auctionType;
    }
    /**
     * Sets the auctionType value.
     *
     * @param inAuctionType an <code>AuctionType</code> value
     */
    public void setAuctionType(AuctionType inAuctionType)
    {
        auctionType = inAuctionType;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value
     */
    public void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * Get the farPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getFarPrice()
    {
        return farPrice;
    }
    /**
     * Sets the farPrice value.
     *
     * @param inFarPrice a <code>BigDecimal</code> value
     */
    public void setFarPrice(BigDecimal inFarPrice)
    {
        farPrice = inFarPrice;
    }
    /**
     * Get the imbalanceVolume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getImbalanceVolume()
    {
        return imbalanceVolume;
    }
    /**
     * Sets the imbalanceVolume value.
     *
     * @param inImbalanceVolume a <code>BigDecimal</code> value
     */
    public void setImbalanceVolume(BigDecimal inImbalanceVolume)
    {
        imbalanceVolume = inImbalanceVolume;
    }
    /**
     * Get the instrumentStatus value.
     *
     * @return an <code>InstrumentStatus</code> value
     */
    public InstrumentStatus getInstrumentStatus()
    {
        return instrumentStatus;
    }
    /**
     * Sets the instrumentStatus value.
     *
     * @param inInstrumentStatus an <code>InstrumentStatus</code> value
     */
    public void setInstrumentStatus(InstrumentStatus inInstrumentStatus)
    {
        instrumentStatus = inInstrumentStatus;
    }
    /**
     * Get the marketStatus value.
     *
     * @return a <code>MarketStatus</code> value
     */
    public MarketStatus getMarketStatus()
    {
        return marketStatus;
    }
    /**
     * Sets the marketStatus value.
     *
     * @param inMarketStatus a <code>MarketStatus</code> value
     */
    public void setMarketStatus(MarketStatus inMarketStatus)
    {
        marketStatus = inMarketStatus;
    }
    /**
     * Get the nearPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getNearPrice()
    {
        return nearPrice;
    }
    /**
     * Sets the nearPrice value.
     *
     * @param inNearPrice a <code>BigDecimal</code> value
     */
    public void setNearPrice(BigDecimal inNearPrice)
    {
        nearPrice = inNearPrice;
    }
    /**
     * Get the pairedVolume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPairedVolume()
    {
        return pairedVolume;
    }
    /**
     * Sets the pairedVolume value.
     *
     * @param inPairedVolume a <code>BigDecimal</code> value
     */
    public void setPairedVolume(BigDecimal inPairedVolume)
    {
        pairedVolume = inPairedVolume;
    }
    /**
     * Get the referencePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getReferencePrice()
    {
        return referencePrice;
    }
    /**
     * Sets the referencePrice value.
     *
     * @param inReferencePrice a <code>BigDecimal</code> value
     */
    public void setReferencePrice(BigDecimal inReferencePrice)
    {
        referencePrice = inReferencePrice;
    }
    /**
     * Get the imbalanceType value.
     *
     * @return an <code>ImbalanceType</code> value
     */
    public ImbalanceType getImbalanceType()
    {
        return imbalanceType;
    }
    /**
     * Sets the imbalanceType value.
     *
     * @param inImbalanceType an <code>ImbalanceType</code> value
     */
    public void setImbalanceType(ImbalanceType inImbalanceType)
    {
        imbalanceType = inImbalanceType;
    }
    /**
     * Get the shortSaleRestricted value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getShortSaleRestricted()
    {
        return shortSaleRestricted;
    }
    /**
     * Sets the shortSaleRestricted value.
     *
     * @param inShortSaleRestricted a <code>boolean</code> value
     */
    public void setShortSaleRestricted(boolean inShortSaleRestricted)
    {
        shortSaleRestricted = inShortSaleRestricted;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Gets the instrument value as a <code>String</code>.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getInstrumentAsString()
    {
        if(instrument == null) {
            return null;
        }
        return instrument.getSymbol();
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Get the eventType value.
     *
     * @return an <code>EventType</code> value
     */
    public EventType getEventType()
    {
        return eventType;
    }
    /**
     * Sets the eventType value.
     *
     * @param inEventType an <code>EventType</code> value
     */
    public void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(shortSaleRestricted).append(auctionType).append(eventType).append(exchange).append(farPrice)
                .append(imbalanceType).append(imbalanceVolume).append(instrument).append(instrumentStatus).append(marketStatus)
                .append(nearPrice).append(pairedVolume).append(referencePrice).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof ImbalanceBean)) {
            return false;
        }
        ImbalanceBean other = (ImbalanceBean)obj;
        return new EqualsBuilder().append(shortSaleRestricted,other.shortSaleRestricted).append(auctionType,other.auctionType)
                .append(eventType,other.eventType).append(exchange,other.exchange).append(farPrice,other.farPrice)
                .append(imbalanceType,other.imbalanceType).append(imbalanceVolume,other.imbalanceVolume).append(instrument,other.instrument)
                .append(marketStatus,other.marketStatus).append(nearPrice,other.nearPrice).append(pairedVolume,other.pairedVolume)
                .append(referencePrice,other.referencePrice).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ImbalanceBean [instrument=").append(instrument).append(", auctionType=").append(auctionType) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", exchange=").append(exchange).append(", farPrice=").append(farPrice) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", imbalanceVolume=").append(imbalanceVolume).append(", instrumentStatus=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(instrumentStatus).append(", marketStatus=").append(marketStatus).append(", nearPrice=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(nearPrice).append(", pairedVolume=").append(pairedVolume).append(", referencePrice=") //$NON-NLS-1$ //$NON-NLS-2$
                .append(referencePrice).append(", imbalanceType=").append(imbalanceType) //$NON-NLS-1$
                .append(", shortSaleRestricted=").append(shortSaleRestricted).append(", eventType=").append(eventType) //$NON-NLS-1$ //$NON-NLS-2$
                .append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor an <code>ImbalanceBean</code> value
     * @param inRecipient an <code>ImbalanceBean</code> value
     */
    protected static void copyAttributes(ImbalanceBean inDonor,
                                         ImbalanceBean inRecipient)
    {
        EventBean.copyAttributes(inDonor,
                                 inRecipient);
        inRecipient.setAuctionType(inDonor.getAuctionType());
        inRecipient.setEventType(inDonor.getEventType());
        inRecipient.setExchange(inDonor.getExchange());
        inRecipient.setFarPrice(inDonor.getFarPrice());
        inRecipient.setImbalanceType(inDonor.getImbalanceType());
        inRecipient.setImbalanceVolume(inDonor.getImbalanceVolume());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setInstrumentStatus(inDonor.getInstrumentStatus());
        inRecipient.setMarketStatus(inDonor.getMarketStatus());
        inRecipient.setNearPrice(inDonor.getNearPrice());
        inRecipient.setPairedVolume(inDonor.getPairedVolume());
        inRecipient.setReferencePrice(inDonor.getReferencePrice());
        inRecipient.setShortSaleRestricted(inDonor.getShortSaleRestricted());
    }
    /**
     * auction type value
     */
    @XmlAttribute
    private AuctionType auctionType;
    /**
     * exchange value
     */
    @XmlAttribute
    private String exchange;
    /**
     * far price value
     */
    @XmlAttribute
    private BigDecimal farPrice;
    /**
     * imbalance volume value
     */
    @XmlAttribute
    private BigDecimal imbalanceVolume;
    /**
     * instrument status value
     */
    @XmlAttribute
    private InstrumentStatus instrumentStatus;
    /**
     * market status value
     */
    @XmlAttribute
    private MarketStatus marketStatus;
    /**
     * near price value
     */
    @XmlAttribute
    private BigDecimal nearPrice;
    /**
     * paired volume value
     */
    @XmlAttribute
    private BigDecimal pairedVolume;
    /**
     * reference price value
     */
    @XmlAttribute
    private BigDecimal referencePrice;
    /**
     * imbalance type value
     */
    @XmlAttribute
    private ImbalanceType imbalanceType;
    /**
     * short sale restricted value 
     */
    @XmlAttribute
    private boolean shortSaleRestricted;
    /**
     * the market data instrument
     */
    @XmlElement
    private Instrument instrument;
    /**
     * the event meta-type
     */
    @XmlAttribute
    private EventType eventType = EventType.UNKNOWN;
    private static final long serialVersionUID = -4827123484130209361L;
}
