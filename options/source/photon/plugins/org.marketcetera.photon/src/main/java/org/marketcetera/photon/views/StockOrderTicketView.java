package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;

/* $License$ */

/**
 * The order ticket view for an equity order.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public class StockOrderTicketView extends
        OrderTicketView<StockOrderTicketModel, IStockOrderTicket> {

    public static final String ID = "org.marketcetera.photon.views.StockOrderTicketView"; //$NON-NLS-1$

    /**
     * Constructor.
     */
    public StockOrderTicketView() {
        super(IStockOrderTicket.class, PhotonPlugin.getDefault()
                .getStockOrderTicketModel());
    }

    @Override
    protected InputStream getXSWTResourceStream() {
        return getClass().getResourceAsStream("/stock_order_ticket.xswt"); //$NON-NLS-1$
    }

    @Override
    protected String getReplaceOrderString() {
        return Messages.STOCK_ORDER_TICKET_VIEW_REPLACE__HEADING
                .getText();
    }

    @Override
    protected String getNewOrderString() {
        return Messages.STOCK_ORDER_TICKET_VIEW_NEW__HEADING.getText();
    }

    /**
     * Gets the "default" StockOrderTicketView, that is the first one returned
     * by {@link IWorkbenchPage#findView(String)}.
     * 
     * @return the default StockOrderTicketView
     */
    public static StockOrderTicketView getDefault() {
        return (StockOrderTicketView) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().findView(
                        StockOrderTicketView.ID);
    }
}
