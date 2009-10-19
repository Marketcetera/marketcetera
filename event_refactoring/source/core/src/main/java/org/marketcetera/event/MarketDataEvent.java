package org.marketcetera.event;

import java.math.BigDecimal;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataEvent
        extends Event, HasInstrument
{
    public String getExchange();
    public BigDecimal getPrice();
    public BigDecimal getSize();
}
