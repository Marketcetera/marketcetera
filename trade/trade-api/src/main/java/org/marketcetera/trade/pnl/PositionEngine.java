package org.marketcetera.trade.pnl;

import org.marketcetera.core.Cacheable;
import org.marketcetera.trade.ExecutionReport;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PositionEngine
        extends Cacheable
{
    Position createPosition(ExecutionReport inReport,
                            Trade inTrade);
}
