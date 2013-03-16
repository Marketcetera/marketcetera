package org.marketcetera.core.event.beans;

import java.io.Serializable;

import org.marketcetera.core.event.ConvertibleBondEvent;
import org.marketcetera.core.event.Messages;
import org.marketcetera.core.event.util.EventServices;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondBean
        implements Serializable
{
    /**
     * Creates a shallow copy of the given <code>ConvertibleBondBean</code>.
     *
     * @param inBean a <code>ConvertibleBondBean</code> value
     * @return a <code>ConvertibleBondBean</code> value
     */
    public static ConvertibleBondBean copy(ConvertibleBondBean inBean)
    {
        ConvertibleBondBean newBean = new ConvertibleBondBean();
        copyAttributes(inBean,
                       newBean);
        return newBean;
    }
    /**
     * Builds a <code>ConvertibleBondBean</code> based on the values of
     * the given event.
     *
     * @param inConvertibleBondEvent a <code>ConvertibleBondEvent</code> value
     * @return a <code>ConvertibleBondBean</code> value
     */
    public static ConvertibleBondBean getConvertibleBondBeanFromEvent(ConvertibleBondEvent inConvertibleBondEvent)
    {
        ConvertibleBondBean bean = new ConvertibleBondBean();
        bean.setInstrument(inConvertibleBondEvent.getInstrument());
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
     * @param inDonor a <code>ConvertibleBondBean</code> value
     * @param inRecipient a <code>ConvertibleBondBean</code> value
     */
    protected static void copyAttributes(ConvertibleBondBean inDonor,
                                         ConvertibleBondBean inRecipient)
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
