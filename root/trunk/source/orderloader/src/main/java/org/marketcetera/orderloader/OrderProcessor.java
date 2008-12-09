package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Order;

/* $License$ */
/**
 * Processes orders parsed by the {@link OrderParser}.
 * The processor keeps track of orders that could not be sent to the
 * server. 
 * Implementations may send the orders to the server or do
 * something else with them. For example, a processor might inject
 * the orders into a data flow or simply log them.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface OrderProcessor {
    /**
     * Processes the supplied order.
     *
     * @param inOrder the order to be sent.
     * @param inOrderIndex the index number of this order. When orders are
     * fed from a file, this will be the line number at which the order
     * appears in the file.
     *
     * @throws Exception if there was an error processing the order
     */
    public void processOrder(Order inOrder, int inOrderIndex) throws Exception;

    /**
     * Invoked to release resources when the system is done processing
     * orders.
     */
    void done();
}
