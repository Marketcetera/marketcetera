package org.marketcetera.photon.views;

import java.io.InputStream;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.PhotonPlugin;

/* $License$ */

/**
 * This class implements the view that provides the end user
 * the ability to type in--and graphically interact with--stock orders.
 * 
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class StockOrderTicketView extends OrderTicketView {

	private static final String NEW_EQUITY_ORDER = "New Equity Order"; //$NON-NLS-1$

	private static final String REPLACE_EQUITY_ORDER = "Replace Equity Order"; //$NON-NLS-1$

	public static String ID = "org.marketcetera.photon.views.StockOrderTicketView"; //$NON-NLS-1$
	

	public StockOrderTicketView() 
	{
	}

	@Override
	protected InputStream getXSWTResourceStream() {
		return getClass().getResourceAsStream("/stock_order_ticket.xswt"); //$NON-NLS-1$
	}


	@Override
	protected void setDefaultInput() {
		setInput(PhotonPlugin.getDefault().getStockOrderTicketModel());
	}

	@Override
	protected String getReplaceOrderString() {
		return REPLACE_EQUITY_ORDER;
	}

	protected String getNewOrderString() {
		return NEW_EQUITY_ORDER;
	}

	/**
	 * Gets the "default" StockOrderTicketView, that is the first one returned
	 * by {@link IWorkbenchPage#findView(String)}
	 * @return the default StockOrderTicketView
	 */
	public static StockOrderTicketView getDefault() {
		return (StockOrderTicketView) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().findView(
						StockOrderTicketView.ID);
	}

	@Override
	protected Class<? extends IOrderTicket> getXSWTInterfaceClass() {
		return IStockOrderTicket.class;
	}
}
