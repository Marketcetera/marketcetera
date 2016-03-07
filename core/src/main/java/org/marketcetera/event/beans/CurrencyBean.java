package org.marketcetera.event.beans;

import static org.marketcetera.event.Messages.VALIDATION_NULL_INSTRUMENT;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.*;

import org.marketcetera.event.CurrencyEvent;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link CurrencyEvent}.
 *
 */
@NotThreadSafe
@XmlRootElement(name="currency")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id: CurrencyBean.java")
public class CurrencyBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>CurrencyBean</code>.
     *
     * @param inBean a <code>CurrencyBean</code> value
     * @return a <code>CurrencyBean</code> value
     */
    public static CurrencyBean copy(CurrencyBean inBean)
    {
        CurrencyBean newBean = new CurrencyBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>CurrencyBean</code> based on the values of
     * the given event.
     *
     * @param inCurrencyEvent a <code>CurrencyEvent</code> value
     * @return a <code>CurrencyBean</code> value
     */
    public static CurrencyBean getCurrencyBeanFromEvent(CurrencyEvent inCurrencyEvent)
    {
        CurrencyBean currency = new CurrencyBean();
        currency.setInstrument(inCurrencyEvent.getInstrument());		//ToDo Set appropriate properties
        return currency;
    }
    /**
     * Gets the instrument.
     *
     * @return a <code>Currency</code> value
     */
    public Currency getInstrument()
    {
        return (Currency)instrument;
    }
    /**
     * Sets the instrument.
     *
     * @param inCurrency a <code>Currency</code> value
     */
    public void setInstrument(Currency inCurrency)
    {
        instrument = inCurrency;
    }
    /**
     * Get the deliveryType value.
     *
     * @return a <code>DeliveryType</code> value
     */
    public DeliveryType getDeliveryType()
    {
        return deliveryType;
    }
    /**
     * Sets the deliveryType value.
     *
     * @param inDeliveryType a <code>DeliveryType</code> value
     */
    public void setDeliveryType(DeliveryType inDeliveryType)
    {
        deliveryType = inDeliveryType;
    }
    /**
     * Performs validation of the attributes.
     *
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    public void validate()
    {
        if(instrument == null) {
            EventServices.error(VALIDATION_NULL_INSTRUMENT);
        }
        // TODO other validation necessary?
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
        return result;
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
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CurrencyBean)) {
            return false;
        }
        CurrencyBean other = (CurrencyBean) obj;
        if (deliveryType == null) {
            if (other.deliveryType != null) {
                return false;
            }
        } else if (!deliveryType.equals(other.deliveryType)) {
            return false;
        }
        if (instrument == null) {
            if (other.instrument != null) {
                return false;
            }
        } else if (!instrument.equals(other.instrument)) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Currency [instrument=%s, deliveryType=%s]", //$NON-NLS-1$
                             instrument,
                             deliveryType);
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor an <code>CurrencyBean</code> value
     * @param inRecipient an <code>CurrencyBean</code> value
     */
    protected static void copyAttributes(CurrencyBean inDonor,
                                         CurrencyBean inRecipient)
    {
        inRecipient.setDeliveryType(inDonor.getDeliveryType());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setContractSize(inDonor.getContractSize());
    }

    /**
     * Get the contractSize value.
     *
     * @return a <code>int</code> value
     */
    public int getContractSize()
    {
        return contractSize;
    }
    /**
     * Sets the contractSize value.
     *
     * @param inContractSize an <code>int</code> value
     */
    public void setContractSize(int inContractSize)
    {
        contractSize = inContractSize;
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
     * the delivery type value
     */
    @XmlAttribute
    private DeliveryType deliveryType;
    /**
     * the instrument of the currency
     */
    @XmlElement
    private Instrument instrument;
    /**
     * the contract size
     */
    @XmlAttribute
    private int contractSize = 1;
    private final static long serialVersionUID = 1L;
}
