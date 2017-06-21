package org.marketcetera.event.beans;

import java.io.Serializable;

import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.marketcetera.event.SpreadEvent;

/* $License$ */

/**
 *
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
        // TODO
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
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>SpreadBean</code> value
     * @param inRecipient a <code>SpreadBean</code> value
     */
    protected static void copyAttributes(SpreadBean inDonor,
                                         SpreadBean inRecipient)
    {
        inRecipient.setLeg1Bean(inDonor.getLeg1Bean());
        inRecipient.setLeg2Bean(inDonor.getLeg2Bean());
        inRecipient.setProviderSymbol(inDonor.getProviderSymbol());
    }
    /**
     * the provider symbol of the future, if available
     */
    @XmlAttribute
    private String providerSymbol;
    /**
     * leg1 value
     */
    @XmlElement(name="leg1")
    private FutureBean leg1Bean;
    /**
     * leg2 value
     */
    @XmlElement(name="leg2")
    private FutureBean leg2Bean;
    private static final long serialVersionUID = -1065907987516658942L;
}
