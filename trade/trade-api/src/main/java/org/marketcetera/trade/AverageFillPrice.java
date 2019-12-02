package org.marketcetera.trade;

import java.math.BigDecimal;

import org.marketcetera.event.HasInstrument;

/* $License$ */

/**
 * Indicates the average fill price for a particular {@link Side}/{@link Instrument} tuple.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AverageFillPrice
        extends HasInstrument,HasSide
{
    /**
     * Get the cumulative quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getCumulativeQuantity();
    /**
     * Get the average price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getAveragePrice();
}
