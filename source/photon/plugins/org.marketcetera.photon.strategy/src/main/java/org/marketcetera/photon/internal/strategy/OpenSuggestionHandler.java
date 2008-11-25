package org.marketcetera.photon.internal.strategy;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Opens the trade suggestion in the ticket view to be modified.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OpenSuggestionHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getCurrentSelectionChecked(event);
		TradeSuggestion suggestion = (TradeSuggestion) selection
				.getFirstElement();
		Message message = getFIXMessage(suggestion.getOrder());
		if (message != null) {
			IOrderTicketController controller = PhotonPlugin.getDefault()
					.getOrderTicketController(message);
			if (controller != null) {
				controller.setOrderMessage(message);
			}
		}
		return null;
	}

	private Message getFIXMessage(OrderSingle agnosticMessage) {
		// TODO: convert Pojo to FIX using T2's API
		return null;
	}

}
