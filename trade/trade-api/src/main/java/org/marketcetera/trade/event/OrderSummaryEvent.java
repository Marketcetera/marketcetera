package org.marketcetera.trade.event;

import org.marketcetera.trade.HasOrderSummary;
import org.marketcetera.trade.OrderSummary;

/* $License$ */

/**
 * Indicates that an {@link OrderSummary} record has been created or updated.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummaryEvent
        extends HasOrderSummary
{
}
