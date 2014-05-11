package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
@ClassVersion("$Id: EquityMarketstatEventImpl.java 16854 2014-03-12 01:54:42Z colin $")
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
