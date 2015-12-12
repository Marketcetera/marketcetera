package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.TradeBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@XmlRootElement(name="equityTrade")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class EquityTradeEventImpl
        extends AbstractTradeEventImpl
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
     * Create a new EquityTradeEventImpl instance.
     *
     * @param inTradeData a <code>TradeBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     */
    EquityTradeEventImpl(TradeBean inTradeData)
    {
        super(inTradeData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractQuoteEventImpl#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return description;
    }
    /**
     * Create a new EquityTradeEventImpl instance.
     *
     * <p>This constructor is intended to be used by JAXB only.
     */
    @SuppressWarnings("unused")
    private EquityTradeEventImpl() {}
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Equity Trade"; //$NON-NLS-1$
    private static final long serialVersionUID = 1L;
}
