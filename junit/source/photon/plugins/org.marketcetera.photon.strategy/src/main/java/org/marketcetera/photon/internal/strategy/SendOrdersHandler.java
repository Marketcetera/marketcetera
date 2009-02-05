package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Send selected orders to the server.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class SendOrdersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		Shell shell = HandlerUtil.getActiveShellChecked(event);
		try {
			Client client = ClientManager.getInstance();
			MultiStatus status = new MultiStatus(
					Activator.PLUGIN_ID,
					IStatus.ERROR,
					Messages.SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE_SEE_DETAILS
							.getText(), null);
			for (Object obj : selection.toArray()) {
				TradeSuggestion suggestion = (TradeSuggestion) obj;
				OrderSingle order = suggestion.getOrder();
				try {
					client.sendOrder(order);
					TradeSuggestionManager.getCurrent().removeSuggestion(
							suggestion);
				} catch (Exception e) {
					Messages.SEND_ORDERS_HANDLER_SEND_ORDER_FAILURE.error(this, order.toString());
					status.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							order.toString(), e));
				}
			}
			if (status.getChildren().length > 0) {
				ErrorDialog.openError(shell, null,
						Messages.SEND_ORDERS_HANDLER_SEND_ORDERS_FAILURE
								.getText(), status);
			}
		} catch (I18NException e) {
			Messages.SEND_ORDERS_HANDLER_SERVER_FAILURE.error(this, e);
			ErrorDialog.openError(shell, null,
					Messages.SEND_ORDERS_HANDLER_SERVER_FAILURE.getText(),
					new Status(IStatus.ERROR, Activator.PLUGIN_ID, e
							.getLocalizedMessage()));
		}
		return null;
	}
}
