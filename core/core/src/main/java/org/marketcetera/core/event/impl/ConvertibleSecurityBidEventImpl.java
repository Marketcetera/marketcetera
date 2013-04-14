package org.marketcetera.core.event.impl;

import org.marketcetera.core.event.BidEvent;
import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.beans.ConvertibleSecurityBean;
import org.marketcetera.core.event.beans.QuoteBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Represents a bid event for a convertible security.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class ConvertibleSecurityBidEventImpl
        extends AbstractQuoteEventImpl
        implements ConvertibleSecurityEvent, BidEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public ConvertibleSecurity getInstrument()
    {
        return (ConvertibleSecurity)super.getInstrument();
    }
    /**
     * Create a new ConvertibleSecurityBidEventImpl instance.
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
    ConvertibleSecurityBidEventImpl(QuoteBean inQuote,
                                ConvertibleSecurityBean inConvertibleSecurity)
    {
        super(inQuote);
        security = ConvertibleSecurityBean.copy(inConvertibleSecurity);
        security.validate();
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
    private static final String description = "Convertible Security Bid"; //$NON-NLS-1$
    /**
     * the convertible security attributes 
     */
    private final ConvertibleSecurityBean security;
    private static final long serialVersionUID = 1L;
}
