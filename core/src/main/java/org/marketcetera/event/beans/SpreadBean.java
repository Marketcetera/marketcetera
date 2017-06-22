package org.marketcetera.event.beans;

import static org.marketcetera.event.Messages.VALIDATION_NULL_INSTRUMENT;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.SpreadEvent;
import org.marketcetera.event.util.EventServices;
import org.marketcetera.trade.Spread;

/* $License$ */

/**
 * Provides the attributes of a spread instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
public class SpreadBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>SpreadBean</code>.
     *
     * @param inBean a <code>SpreadBean</code> value
     * @return a <code>SpreadBean</code> value
     */
    public static SpreadBean copy(SpreadBean inBean)
    {
        SpreadBean newBean = new SpreadBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>SpreadBean</code> based on the values of
     * the given event.
     *
     * @param inSpreadEvent a <code>SpreadEvent</code> value
     * @return a <code>SpreadBean</code> value
     */
    public static SpreadBean getSpreadBeanFromEvent(SpreadEvent inSpreadEvent)
    {
        SpreadBean spread = new SpreadBean();
        spread.setInstrument(inSpreadEvent.getInstrument());
        spread.getLeg1Bean().setDeliveryType(inSpreadEvent.getLeg1DeliveryType());
        spread.getLeg1Bean().setType(inSpreadEvent.getLeg1Type());
        spread.getLeg1Bean().setStandardType(inSpreadEvent.getLeg1StandardType());
        spread.getLeg1Bean().setUnderlyingAssetType(inSpreadEvent.getLeg1UnderylingAssetType());
        spread.getLeg1Bean().setContractSize(inSpreadEvent.getLeg1ContractSize());
        spread.getLeg1Bean().setInstrument(spread.getInstrument().getLeg1());
        spread.setProviderSymbol(inSpreadEvent.getProviderSymbol());
        spread.getLeg2Bean().setDeliveryType(inSpreadEvent.getLeg2DeliveryType());
        spread.getLeg2Bean().setType(inSpreadEvent.getLeg2Type());
        spread.getLeg2Bean().setStandardType(inSpreadEvent.getLeg2StandardType());
        spread.getLeg2Bean().setUnderlyingAssetType(inSpreadEvent.getLeg2UnderylingAssetType());
        spread.getLeg2Bean().setContractSize(inSpreadEvent.getLeg2ContractSize());
        spread.getLeg2Bean().setInstrument(spread.getInstrument().getLeg2());
        return spread;
    }
    /**
     * Get the leg1Bean value.
     *
     * @return a <code>FutureBean</code> value
     */
    public FutureBean getLeg1Bean()
    {
        return leg1Bean;
    }
    /**
     * Sets the leg1Bean value.
     *
     * @param inLeg1Bean a <code>FutureBean</code> value
     */
    public void setLeg1Bean(FutureBean inLeg1Bean)
    {
        leg1Bean = inLeg1Bean;
    }
    /**
     * Get the leg2Bean value.
     *
     * @return a <code>FutureBean</code> value
     */
    public FutureBean getLeg2Bean()
    {
        return leg2Bean;
    }
    /**
     * Sets the leg2Bean value.
     *
     * @param inLeg2Bean a <code>FutureBean</code> value
     */
    public void setLeg2Bean(FutureBean inLeg2Bean)
    {
        leg2Bean = inLeg2Bean;
    }
    /**
     * Get the providerSymbol value.
     *
     * @return a <code>String</code> value
     */
    public String getProviderSymbol()
    {
        return providerSymbol;
    }
    /**
     * Sets the providerSymbol value.
     *
     * @param inProviderSymbol a <code>String</code> value
     */
    public void setProviderSymbol(String inProviderSymbol)
    {
        providerSymbol = inProviderSymbol;
    }
    /**
     * Get the instrument value.
     *
     * @return a <code>Spread</code> value
     */
    public Spread getInstrument()
    {
        return instrument;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument a <code>Spread</code> value
     */
    public void setInstrument(Spread inInstrument)
    {
        instrument = inInstrument;
        if(inInstrument == null) {
            leg1Bean.setInstrument(null);
            leg2Bean.setInstrument(null);
        } else {
            leg1Bean.setInstrument(inInstrument.getLeg1());
            leg2Bean.setInstrument(inInstrument.getLeg2());
        }
    }
    /**
     * Validate the object.
     */
    public void validate()
    {
        if(instrument == null) {
            EventServices.error(VALIDATION_NULL_INSTRUMENT);
        }
        leg1Bean.validate();
        leg2Bean.validate();
    }
    /**
     * Create a new SpreadBean instance.
     */
    public SpreadBean()
    {
        leg1Bean = new FutureBean();
        leg2Bean = new FutureBean();
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
        result = prime * result + ((leg1Bean == null) ? 0 : leg1Bean.hashCode());
        result = prime * result + ((leg2Bean == null) ? 0 : leg2Bean.hashCode());
        result = prime * result + ((providerSymbol == null) ? 0 : providerSymbol.hashCode());
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
        if (!(obj instanceof SpreadBean)) {
            return false;
        }
        SpreadBean other = (SpreadBean) obj;
        if (instrument == null) {
            if (other.instrument != null) {
                return false;
            }
        } else if (!instrument.equals(other.instrument)) {
            return false;
        }
        if (leg1Bean == null) {
            if (other.leg1Bean != null) {
                return false;
            }
        } else if (!leg1Bean.equals(other.leg1Bean)) {
            return false;
        }
        if (leg2Bean == null) {
            if (other.leg2Bean != null) {
                return false;
            }
        } else if (!leg2Bean.equals(other.leg2Bean)) {
            return false;
        }
        if (providerSymbol == null) {
            if (other.providerSymbol != null) {
                return false;
            }
        } else if (!providerSymbol.equals(other.providerSymbol)) {
            return false;
        }
        return true;
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>SpreadBean</code> value
     * @param inRecipient a <code>SpreadBean</code> value
     */
    protected static void copyAttributes(SpreadBean inDonor,
                                         SpreadBean inRecipient)
    {
        inRecipient.setInstrument(inDonor.getInstrument());
        inRecipient.setLeg1Bean(inDonor.getLeg1Bean());
        inRecipient.setLeg2Bean(inDonor.getLeg2Bean());
        inRecipient.setProviderSymbol(inDonor.getProviderSymbol());
    }
    /**
     * instrument value
     */
    @XmlElement
    private Spread instrument;
    /**
     * the provider symbol of the future, if available
     */
    @XmlAttribute
    private String providerSymbol;
    /**
     * leg1 value
     */
    @XmlElement(name="leg1")
    private FutureBean leg1Bean = new FutureBean();
    /**
     * leg2 value
     */
    @XmlElement(name="leg2")
    private FutureBean leg2Bean = new FutureBean();
    private static final long serialVersionUID = -1065907987516658942L;
}
