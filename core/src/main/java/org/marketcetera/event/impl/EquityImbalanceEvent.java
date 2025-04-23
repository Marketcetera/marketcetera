package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.ImbalanceEvent;
import org.marketcetera.event.beans.ImbalanceBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link ImbalanceEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="equityImbalance")
@ClassVersion("$Id$")
public class EquityImbalanceEvent
        extends AbstractImbalanceEvent
        implements EquityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractImbalanceEventImpl#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return (Equity)super.getInstrument();
    }
    /**
     * Create a new EquityImbalanceEvent instance.
     *
     * @param inImbalance
     */
    public EquityImbalanceEvent(ImbalanceBean inImbalance)
    {
        super(inImbalance);
    }
    /**
     * Create a new EquityImbalanceEvent instance.
     */
    @SuppressWarnings("unused")
    private EquityImbalanceEvent()
    {
        super();
    }
    private static final long serialVersionUID = -7112262441692602002L;
}
