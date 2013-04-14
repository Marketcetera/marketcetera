package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.beans.ConvertibleSecurityBean;
import org.marketcetera.core.event.beans.MarketstatBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Provides a ConvertibleSecurity implementation of {@link MarketstatEvent}.
 *
 * @version $Id$
 * @since 2.1.0
 */
@ThreadSafe
class ConvertibleSecurityMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements ConvertibleSecurityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasConvertibleSecurity#getInstrument()
     */
    @Override
    public ConvertibleSecurity getInstrument()
    {
        return (ConvertibleSecurity)super.getInstrument();
    }
    /**
     * Create a new ConvertibleSecurityMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    ConvertibleSecurityMarketstatEventImpl(MarketstatBean inMarketstat,
                                       ConvertibleSecurityBean inConvertibleSecurity)
    {
        super(inMarketstat);
        security = inConvertibleSecurity;
        security.validate();
    }
    /**
     * the security attributes 
     */
    private final ConvertibleSecurityBean security;
    private static final long serialVersionUID = 1L;
}
