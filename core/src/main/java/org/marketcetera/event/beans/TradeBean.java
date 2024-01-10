package org.marketcetera.event.beans;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.TradeEvent;

/* $License$ */

/**
 * Stores the attributes necessary for {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@XmlRootElement(name="trade")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class TradeBean
        extends MarketDataBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>TradeBean</code>.
     *
     * @param inBean a <code>TradeBean</code> value
     * @return a <code>TradeBean</code> value
     */
    public static TradeBean copy(TradeBean inBean)
    {
        TradeBean newBean = new TradeBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Get the tradeCondition value.
     *
     * @return a <code>String</code> value
     */
    public final String getTradeCondition()
    {
        return tradeCondition;
    }
    /**
     * Sets the tradeCondition value.
     *
     * @param inTradeCondition a <code>String</code> value
     */
    public final void setTradeCondition(String inTradeCondition)
    {
        tradeCondition = inTradeCondition;
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     */
    @Override
    public void validate()
    {
        super.validate();
    }
    /* (non-Javadoc)s
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(super.hashCode()).append(tradeCondition).toHashCode();
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
        if (!(obj instanceof TradeBean)) {
            return false;
        }
        TradeBean other = (TradeBean) obj;
        return new EqualsBuilder().append(tradeCondition,other.tradeCondition).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Trade: %s at %s of %s on %s at %s %s %s [%s with source %s at %s]", //$NON-NLS-1$
                             getSize(),
                             getPrice(),
                             getInstrument(),
                             getExchange(),
                             getExchangeTimestamp(),
                             getEventType(),
                             tradeCondition,
                             getMessageId(),
                             getSource(),
                             getTimestamp());
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>TradeBean</code> value
     * @param inRecipient a <code>TradeBean</code> value
     */
    protected static void copyAttributes(TradeBean inDonor,
                                         TradeBean inRecipient)
    {
        MarketDataBean.copyAttributes(inDonor,
                                      inRecipient);
        inRecipient.setTradeCondition(inDonor.getTradeCondition());
    }
    /**
     * market data trade condition
     */
    @XmlAttribute
    private String tradeCondition;
    private static final long serialVersionUID = -5204822157837756706L;
}
