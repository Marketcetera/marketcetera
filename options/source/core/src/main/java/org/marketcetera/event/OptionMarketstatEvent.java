package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the set of statistics of a specific {@link Option}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OptionMarketstatEvent
        extends MarketstatEvent, OptionEvent
{
    /**
     * Gets the change in volume since the previous close.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getVolumeChange();
    /**
     * Gets the change in interest since the previous close.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getInterestChange();
}
