package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="equityMarketstat")
@ClassVersion("$Id$")
public class EquityMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements EquityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return (Equity)super.getInstrument();
    }
    /**
     * Create a new EquityMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    EquityMarketstatEventImpl(MarketstatBean inMarketstat)
    {
        super(inMarketstat);
    }
    /**
     * Create a new EquityMarketstatEventImpl instance.
     *
     * <p>This constructor is intended to be used for JAXB only.
     */
    @SuppressWarnings("unused")
    private EquityMarketstatEventImpl() {}
    private static final long serialVersionUID = 1L;
}
