package org.marketcetera.event;

import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasEquity
        extends HasInstrument
{
    public Equity getEquity();
}
