package org.marketcetera.core.event.impl;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.ConvertibleBondEvent;
import org.marketcetera.core.event.beans.ConvertibleBondBean;
import org.marketcetera.core.event.beans.QuoteBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class ConvertibleBondAskEventImpl
        extends AbstractQuoteEventImpl
        implements ConvertibleBondEvent, AskEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public ConvertibleBond getInstrument()
    {
        return (ConvertibleBond)super.getInstrument();
    }
    /**
     * Create a new ConvertibleBondAskEventImpl instance.
     *
     * @param inQuote a <code>QuoteBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     */
    ConvertibleBondAskEventImpl(QuoteBean inQuote,
                                ConvertibleBondBean inConvertibleBond)
    {
        super(inQuote);
        bond = ConvertibleBondBean.copy(inConvertibleBond);
        bond.validate();
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
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Convertible Bond Ask"; //$NON-NLS-1$
    /**
     * the convertible bond attributes 
     */
    private final ConvertibleBondBean bond;
    private static final long serialVersionUID = 1L;
}
