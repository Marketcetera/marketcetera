package org.marketcetera.photon.views;

/* $License$ */

/**
 * Helps test {@link StockOrderTicketView}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class StockOrderTicketViewFixture extends
        OrderTicketViewFixture<IStockOrderTicket> {

    public StockOrderTicketViewFixture() throws Exception {
        super(StockOrderTicketView.ID);
    }
}
