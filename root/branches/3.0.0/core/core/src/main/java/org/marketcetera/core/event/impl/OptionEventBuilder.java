package org.marketcetera.core.event.impl;

import java.math.BigDecimal;

import org.marketcetera.core.options.ExpirationType;
import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 * Indicates that the underlying event builder supports the attributes necessary to build option events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: OptionEventBuilder.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public interface OptionEventBuilder<B>
{
    /**
     * Sets the underlyingInstrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>B</code> value
     */
    public B withUnderlyingInstrument(Instrument inUnderlyingInstrument);
    /**
     * Sets the expirationType value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value or <code>null</code>
     * @return a <code>B</code> value
     */
    public B withExpirationType(ExpirationType inExpirationType);
    /**
     * Sets the multiplier value.
     *
     * @param inMultiplier a <code>BigDecimal</code> value
     * @return a <code>B</code> value
     */
    public B withMultiplier(BigDecimal inMultiplier);
    /**
     * Sets the hasDeliverable value.
     *
     * @param inHasDeliverable a <code>boolean</code> value
     * @return a <code>B</code> value
     */
    public B hasDeliverable(boolean inHasDeliverable);
}
