package org.marketcetera.event.beans;

import static org.marketcetera.event.Messages.VALIDATION_NULL_INSTRUMENT;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.FutureEvent;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link FutureEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@NotThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class FutureBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>FutureBean</code>.
     *
     * @param inBean a <code>FutureBean</code> value
     * @return a <code>FutureBean</code> value
     */
    public static FutureBean copy(FutureBean inBean)
    {
        FutureBean newBean = new FutureBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>FutureBean</code> based on the values of
     * the given event.
     *
     * @param inFutureEvent a <code>FutureEvent</code> value
     * @return a <code>FutureBean</code> value
     */
    public static FutureBean getFutureBeanFromEvent(FutureEvent inFutureEvent)
    {
        FutureBean future = new FutureBean();
        future.setDeliveryType(inFutureEvent.getDeliveryType());
        future.setType(inFutureEvent.getType());
        future.setStandardType(inFutureEvent.getStandardType());
        future.setUnderlyingAssetType(inFutureEvent.getUnderylingAssetType());
        future.setInstrument(inFutureEvent.getInstrument());
        future.setProviderSymbol(inFutureEvent.getProviderSymbol());
        future.setContractSize(inFutureEvent.getContractSize());
        return future;
    }
    /**
     * Gets the instrument.
     *
     * @return a <code>Future</code> value
     */
    public Future getInstrument()
    {
        return (Future)instrument;
    }
    /**
     * Sets the instrument.
     *
     * @param inFuture a <code>Future</code> value
     */
    public void setInstrument(Future inFuture)
    {
        instrument = inFuture;
    }
    /**
     * Get the futureType value.
     *
     * @return a <code>FutureType</code> value
     */
    public FutureType getType()
    {
        return futureType;
    }
    /**
     * Sets the futureType value.
     *
     * @param inFutureType a <code>FutureType</code> value
     */
    public void setType(FutureType inFutureType)
    {
        futureType = inFutureType;
    }
    /**
     * Get the underlyingAssetType value.
     *
     * @return a <code>FutureUnderlyingAssetType</code> value
     */
    public FutureUnderlyingAssetType getUnderlyingAssetType()
    {
        return underlyingAssetType;
    }
    /**
     * Sets the underlyingAssetType value.
     *
     * @param inUnderlyingAssetType a <code>FutureUnderlyingAssetType</code> value
     */
    public void setUnderlyingAssetType(FutureUnderlyingAssetType inUnderlyingAssetType)
    {
        underlyingAssetType = inUnderlyingAssetType;
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
     * Get the standardType value.
     *
     * @return a <code>StandardType</code> value
     */
    public StandardType getStandardType()
    {
        return standardType;
    }
    /**
     * Sets the standardType value.
     *
     * @param inStandardType a <code>StandardType</code> value
     */
    public void setStandardType(StandardType inStandardType)
    {
        standardType = inStandardType;
    }
    /**
     * Get the native Symbol value.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getProviderSymbol()
    {
        return providerSymbol;
    }
    /**
     * Sets the native Symbol value.
     *
     * @param inOriginalSymbol a <code>String</code> value
     */
    public void setProviderSymbol(String inOriginalSymbol)
    {
        providerSymbol = inOriginalSymbol;
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
        result = prime * result + ((deliveryType == null) ? 0 : deliveryType.hashCode());
        result = prime * result + ((futureType == null) ? 0 : futureType.hashCode());
        result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
        result = prime * result + ((providerSymbol == null) ? 0 : providerSymbol.hashCode());
        result = prime * result + ((standardType == null) ? 0 : standardType.hashCode());
        result = prime * result + ((underlyingAssetType == null) ? 0 : underlyingAssetType.hashCode());
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
        if (!(obj instanceof FutureBean)) {
            return false;
        }
        FutureBean other = (FutureBean) obj;
        if (deliveryType == null) {
            if (other.deliveryType != null) {
                return false;
            }
        } else if (!deliveryType.equals(other.deliveryType)) {
            return false;
        }
        if (futureType == null) {
            if (other.futureType != null) {
                return false;
            }
        } else if (!futureType.equals(other.futureType)) {
            return false;
        }
        if (instrument == null) {
            if (other.instrument != null) {
                return false;
            }
        } else if (!instrument.equals(other.instrument)) {
            return false;
        }
        if (providerSymbol == null) {
            if (other.providerSymbol != null) {
                return false;
            }
        } else if (!providerSymbol.equals(other.providerSymbol)) {
            return false;
        }
        if (standardType == null) {
            if (other.standardType != null) {
                return false;
            }
        } else if (!standardType.equals(other.standardType)) {
            return false;
        }
        if (underlyingAssetType == null) {
            if (other.underlyingAssetType != null) {
                return false;
            }
        } else if (!underlyingAssetType.equals(other.underlyingAssetType)) {
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
        return String.format("Future [instrument=%s, futureType=%s, underlyingAssetType=%s, deliveryType=%s, standardType=%s, providerSymbol=%s]", //$NON-NLS-1$
                             instrument,
                             futureType,
                             underlyingAssetType,
                             deliveryType,
                             standardType,
                             providerSymbol);
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor an <code>FutureBean</code> value
     * @param inRecipient an <code>FutureBean</code> value
     */
    protected static void copyAttributes(FutureBean inDonor,
                                         FutureBean inRecipient)
    {
        inRecipient.setDeliveryType(inDonor.getDeliveryType());
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setProviderSymbol(inDonor.getProviderSymbol());
        inRecipient.setStandardType(inDonor.getStandardType());
        inRecipient.setType(inDonor.getType());
        inRecipient.setUnderlyingAssetType(inDonor.getUnderlyingAssetType());
        inRecipient.setContractSize(inDonor.getContractSize());
    }
    /**
     * Get the futureType value.
     *
     * @return a <code>FutureType</code> value
     */
    public FutureType getFutureType()
    {
        return futureType;
    }
    /**
     * Sets the futureType value.
     *
     * @param inFutureType a <code>FutureType</code> value
     */
    public void setFutureType(FutureType inFutureType)
    {
        futureType = inFutureType;
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
     * the future type value 
     */
    @XmlAttribute
    private FutureType futureType;
    /**
     * the underlying asset type value
     */
    private transient FutureUnderlyingAssetType underlyingAssetType;
    /**
     * the delivery type value
     */
    @XmlAttribute
    private DeliveryType deliveryType;
    /**
     * the standard type value
     */
    @XmlAttribute
    private StandardType standardType;
    /**
     * the provider symbol of the future, if available
     */
    @XmlAttribute
    private String providerSymbol;
    /**
     * the instrument of the future
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
