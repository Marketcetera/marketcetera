package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementor has an {@link AverageFillPrice}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasAverageFillPrice
{
    /**
     * Get the average fill price value.
     *
     * @return an <code>AverageFillPrice</code> value
     */
    AverageFillPrice getAverageFillPrice();
}
