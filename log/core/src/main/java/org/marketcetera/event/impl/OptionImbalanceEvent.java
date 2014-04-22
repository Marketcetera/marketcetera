package org.marketcetera.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.HasOption;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an <code>ImbalanceEvent</code> implementation for an <code>Option</code> instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="optionImbalance")
@ClassVersion("$Id$")
public class OptionImbalanceEvent
        extends AbstractImbalanceEvent
        implements HasOption, OptionEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getExpirationType()
     */
    @Override
    public ExpirationType getExpirationType()
    {
        return option.getExpirationType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getMultiplier()
     */
    @Override
    public BigDecimal getMultiplier()
    {
        return option.getMultiplier();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#hasDeliverable()
     */
    @Override
    public boolean hasDeliverable()
    {
        return option.hasDeliverable();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasOption#getInstrument()
     */
    @Override
    public Option getInstrument()
    {
        return option.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasUnderlyingInstrument#getUnderlyingInstrument()
     */
    @Override
    public Instrument getUnderlyingInstrument()
    {
        return option.getUnderlyingInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getOpraSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return option.getProviderSymbol();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(option.toString());
        return builder.toString();
    }
    /**
     * Create a new OptionImbalanceEvent instance.
     *
     * @param inImbalanceBean a <code>ImbalanceBean</code> value
     * @param inOptionBean an <code>OptionBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>UnderlyingInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>ExpirationType</code> is <code>null</code>
     */
    public OptionImbalanceEvent(ImbalanceBean inImbalance,
                                OptionBean inOption)
    {
        super(inImbalance);
        option = OptionBean.copy(inOption);
        option.validate();
    }
    /**
     * Create a new OptionImbalanceEvent instance.
     * 
     * <p>This constructor is intended to be used by JAXB.
     */
    @SuppressWarnings("unused")
    private OptionImbalanceEvent()
    {
        option = new OptionBean();
    }
    /**
     * the option attributes
     */
    @XmlElement
    private final OptionBean option;
    private static final long serialVersionUID = 1L;
}
