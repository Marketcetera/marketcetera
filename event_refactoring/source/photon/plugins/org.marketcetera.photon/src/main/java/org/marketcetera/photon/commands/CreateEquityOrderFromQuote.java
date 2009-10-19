package org.marketcetera.photon.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.model.marketdata.MDDepthOfBook;
import org.marketcetera.photon.model.marketdata.MDItem;
import org.marketcetera.photon.model.marketdata.MDPackage;
import org.marketcetera.photon.model.marketdata.MDQuote;
import org.marketcetera.photon.ui.EquityPerspectiveFactory;
import org.marketcetera.photon.views.StockOrderTicketController;
import org.marketcetera.photon.views.StockOrderTicketView;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/* $License$ */

/**
 * Initializes an equity order ticket from the selection quote.  Currently this assumes the quote is an {@link MDQuote} child of
 * a {@link MDDepthOfBook} object.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class CreateEquityOrderFromQuote extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object selection = ((StructuredSelection) HandlerUtil.getActiveMenuSelection(event))
				.getFirstElement();
		if (selection instanceof MDQuote) {
			MDQuote quote = (MDQuote) selection;
			char side = Side.SELL;
			if (quote.eContainingFeature() == MDPackage.Literals.MD_DEPTH_OF_BOOK__ASKS) {
				side = Side.BUY;
			}
			String symbol = ((MDItem) quote.eContainer()).getSymbol();
			Message order = PhotonPlugin.getDefault().getMessageFactory().newLimitOrder("", side, //$NON-NLS-1$
					quote.getSize(), new Equity(symbol), quote.getPrice(), TimeInForce.DAY, ""); //$NON-NLS-1$
			StockOrderTicketController controller = PhotonPlugin.getDefault()
					.getStockOrderTicketController();
			controller.setOrderMessage(order);
			try {
				PlatformUI.getWorkbench().showPerspective(EquityPerspectiveFactory.ID,
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(StockOrderTicketView.ID);
			} catch (WorkbenchException e) {
				SLF4JLoggerProxy.error(this, e);
				Shell shell = HandlerUtil.getActiveShellChecked(event);
				ErrorDialog.openError(shell, null, null, new Status(IStatus.ERROR,
						PhotonPlugin.ID, e.getLocalizedMessage()));
			}
		}
		return null;
	}
}
