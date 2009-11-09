package org.marketcetera.photon.views;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stock order ticket controller.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public class StockOrderTicketController extends
        OrderTicketController<StockOrderTicketModel> {
    /**
     * Constructor.
     * 
     * @param orderTicketModel
     *            the order ticket model
     */
    public StockOrderTicketController(StockOrderTicketModel orderTicketModel) {
        super(orderTicketModel);
    }
}
