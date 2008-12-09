package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.OrderParsingException;

/**
 * A processor that extracts the field value from the supplied order row
 * and sets it in the supplied order.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
interface FieldProcessor {
    /**
     * Extracts the field value from the supplied order row and sets
     * it on the supplied order instance.
     *
     * @param inRow the order row being processed.
     * @param inOrder the order instance being created.
     *
     * @throws OrderParsingException if there were errors parsing field
     * value from the order row.
     */
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException;
}
