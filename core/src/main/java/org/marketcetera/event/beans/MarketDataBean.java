package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.event.EventType;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.Messages;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link MarketDataEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class MarketDataBean
        extends EventBean
{
    /**
     * Creates a shallow copy of the given <code>MarketDataBean</code>.
     *
     * @param inBean a <code>MarketDataBean</code> value
     * @return a <code>MarketDataBean</code> value
     */
    public static MarketDataBean copy(MarketDataBean inBean)
    {
        MarketDataBean newBean = new MarketDataBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public final Instrument getInstrument()
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
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public final void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Returns the meta-type of the event.
     *
     * @return an <code>EventMetaType</code> value
     */
    public final EventType getEventType()
    {
        return eventType;
    }
    /**
     * Sets the meta-type of the event.
     *
     * @param inEventType
     */
    public final void setEventType(EventType inEventType)
    {
        eventType = inEventType;
    }
    /**
     * Get the exchangeTimestamp value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchangeTimestamp()
    {
        return exchangeTimestamp;
    }
    /**
     * Sets the exchangeTimestamp value.
     *
     * @param inExchangeTimestamp a <code>String</code> value
     */
    public final void setExchangeTimestamp(String inExchangeTimestamp)
    {
        exchangeTimestamp = inExchangeTimestamp;
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getPrice()
    {
        return price;
    }
    /**
     * Sets the price value.
     *
     * @param inPrice a <code>BigDecimal</code> value
     */
    public final void setPrice(BigDecimal inPrice)
    {
        price = inPrice;
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getSize()
    {
        return size;
    }
    /**
     * Sets the size value.
     *
     * @param inSize a <code>BigDecimal</code> value
     */
    public final void setSize(BigDecimal inSize)
    {
        size = inSize;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchange()
    {
        return exchange;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value
     */
    public final void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * Get the tradeCondition value.
     *
     * @return a <code>String</code> value
     */
    public String getTradeCondition()
    {
        return tradeCondition;
    }
    /**
     * Sets the tradeCondition value.
     *
     * @param inTradeCondition a <code>String</code> value
     */
    public void setTradeCondition(String inTradeCondition)
    {
        tradeCondition = inTradeCondition;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>MetaType</code> is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        if(instrument == null) {
            EventServices.error(Messages.VALIDATION_NULL_INSTRUMENT);
        }
        if(price == null) {
            EventServices.error(Messages.VALIDATION_NULL_PRICE);
        }
        if(size == null) {
            EventServices.error(Messages.VALIDATION_NULL_SIZE);
        }
        if(exchange == null ||
           exchange.isEmpty()) {
            EventServices.error(Messages.VALIDATION_NULL_EXCHANGE);
        }
        if(exchangeTimestamp == null ||
           exchangeTimestamp.isEmpty()) {
            EventServices.error(Messages.VALIDATION_NULL_EXCHANGE_TIMESTAMP);
        }
        if(eventType == null) {
            EventServices.error(Messages.VALIDATION_NULL_META_TYPE);
        }
    }
    /* (non-Javadoc)s
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(exchange).append(exchangeTimestamp).append(instrument).append(price).append(size).append(eventType).append(tradeCondition).toHashCode();
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
        if (!(obj instanceof MarketDataBean)) {
            return false;
        }
        MarketDataBean other = (MarketDataBean) obj;
        return new EqualsBuilder().append(exchange,other.exchange).append(exchangeTimestamp,other.exchangeTimestamp).append(instrument,other.instrument)
                .append(price,other.price).append(size,other.size).append(eventType,other.eventType).append(tradeCondition,other.tradeCondition).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("MarketData: %s at %s of %s on %s at %s %s %s [%s with source %s at %s]", //$NON-NLS-1$
                             size,
                             price,
                             instrument,
                             exchange,
                             exchangeTimestamp,
                             eventType,
                             tradeCondition,
                             getMessageId(),
                             getSource(),
                             getTimestamp());
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>MarketDataBean</code> value
     * @param inRecipient a <code>MarketDataBean</code> value
     */
    protected static void copyAttributes(MarketDataBean inDonor,
                                         MarketDataBean inRecipient)
    {
        EventBean.copyAttributes(inDonor,
                                 inRecipient);
        inRecipient.setEventType(inDonor.getEventType());
        inRecipient.setExchange(inDonor.getExchange());
        inRecipient.setExchangeTimestamp(inDonor.getExchangeTimestamp());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setPrice(inDonor.getPrice());
        inRecipient.setSize(inDonor.getSize());
        inRecipient.setTradeCondition(inDonor.getTradeCondition());
    }
    /**
     * the market data price
     */
    @XmlAttribute
    private BigDecimal price;
    /**
     * the market data size
     */
    @XmlAttribute
    private BigDecimal size;
    /**
     * the market data exchange
     */
    @XmlAttribute
    private String exchange;
    /**
     * the market data exchange timestamp (format is dependent on the market data provider)
     */
    @XmlAttribute
    private String exchangeTimestamp;
    /**
     * the market data instrument
     */
    @XmlElement
    private Instrument instrument;
    /**
     * market data trade condition
     */
    @XmlAttribute
    private String tradeCondition;
    /**
     * the event meta-type
     */
    @XmlAttribute
    private EventType eventType = EventType.UNKNOWN;
    private static final long serialVersionUID = 1486216063784594404L;
}
