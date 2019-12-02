package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Creates {@link SimpleAverageFillPrice} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleAverageFillPriceFactory
        implements AverageFillPriceFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AverageFillPriceFactory#create()
     */
    @Override
    public SimpleAverageFillPrice create()
    {
        return new SimpleAverageFillPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AverageFillPriceFactory#create(org.marketcetera.trade.Instrument, org.marketcetera.trade.Side, java.math.BigDecimal, java.math.BigDecimal)
     */
    @Override
    public SimpleAverageFillPrice create(Instrument inInstrument,
                                         Side inSide,
                                         BigDecimal inCumulativeQuantity,
                                         BigDecimal inAverageQuantity)
    {
        return new SimpleAverageFillPrice(inInstrument,
                                          inSide,
                                          inCumulativeQuantity,
                                          inAverageQuantity);
    }
}
