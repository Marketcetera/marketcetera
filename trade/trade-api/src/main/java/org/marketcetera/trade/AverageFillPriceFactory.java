package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Creates new {@link AverageFillPrice} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AverageFillPriceFactory
{
    /**
     * Create a new <code>AverageFillPrice</code> value.
     *
     * @return an <code>AverageFillPrice</code> value
     */
    AverageFillPrice create();
    /**
     * Create a new <code>AverageFillPrice</code> value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inSide a <code>Side</code> value
     * @param inCumulativeQuantity a <code>BigDecimal</code> value
     * @param inAverageQuantity a <code>BigDecimal</code> value
     * @return an <code>AverageFillPrice</code> value
     */
    AverageFillPrice create(Instrument inInstrument,
                            Side inSide,
                            BigDecimal inCumulativeQuantity,
                            BigDecimal inAverageQuantity);
}
