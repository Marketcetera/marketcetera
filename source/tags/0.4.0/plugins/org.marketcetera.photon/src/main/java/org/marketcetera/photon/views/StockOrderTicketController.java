package org.marketcetera.photon.views;

import org.eclipse.jface.util.Assert;

public class StockOrderTicketController extends AbstractOrderTicketController {
	private OrderTicketControllerHelper controllerHelper;

	@Override
	public OrderTicketControllerHelper getOrderTicketControllerHelper() {
		Assert.isNotNull(controllerHelper, "Controller is not yet bound.");
		return controllerHelper;
	}

	public void bind(IStockOrderTicket ticket) {
		if (controllerHelper != null) {
			controllerHelper.dispose();
		}
		controllerHelper = new OrderTicketControllerHelper(ticket);
		controllerHelper.init();
	}
}
