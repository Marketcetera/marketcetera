package org.marketcetera.core.event.beans;

import java.io.Serializable;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.Messages;
import org.marketcetera.core.event.util.EventServices;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Contains the attributes of a convertible security.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleSecurityBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>ConvertibleSecurityBean</code>.
     *
     * @param inBean a <code>ConvertibleSecurityBean</code> value
     * @return a <code>ConvertibleSecurityBean</code> value
     */
    public static ConvertibleSecurityBean copy(ConvertibleSecurityBean inBean)
    {
        ConvertibleSecurityBean newBean = new ConvertibleSecurityBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>ConvertibleSecurityBean</code> based on the values of
     * the given event.
     *
     * @param inConvertibleSecurityEvent a <code>ConvertibleSecurityEvent</code> value
     * @return a <code>ConvertibleSecurityBean</code> value
     */
    public static ConvertibleSecurityBean getConvertibleSecurityBeanFromEvent(ConvertibleSecurityEvent inConvertibleSecurityEvent)
    {
        ConvertibleSecurityBean bean = new ConvertibleSecurityBean();
        bean.setInstrument(inConvertibleSecurityEvent.getInstrument());
        return bean;
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
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /**
     * Performs validation of the attributes.
     *
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    public void validate()
    {
        if(instrument == null) {
            EventServices.error(Messages.VALIDATION_NULL_INSTRUMENT);
        }
        // TODO other validation necessary?
    }
    /**
     * Copies all member attributes from the donor to the recipient.
     *
     * @param inDonor a <code>ConvertibleSecurityBean</code> value
     * @param inRecipient a <code>ConvertibleSecurityBean</code> value
     */
    protected static void copyAttributes(ConvertibleSecurityBean inDonor,
                                         ConvertibleSecurityBean inRecipient)
    {
        inRecipient.setInstrument(inDonor.getInstrument());
    }
    /**
     * the instrument value
     */
    private Instrument instrument;
    {
    }
    private static final long serialVersionUID = 1L;
}
