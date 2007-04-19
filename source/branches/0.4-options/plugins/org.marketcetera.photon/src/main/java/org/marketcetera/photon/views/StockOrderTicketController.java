package org.marketcetera.photon.views;

public class StockOrderTicketController extends AbstractOrderTicketController {

	private OrderTicketControllerHelper controllerHelper;

	public StockOrderTicketController(IStockOrderTicket ticket) {
		controllerHelper = new OrderTicketControllerHelper(ticket);
	}

	@Override
	protected OrderTicketControllerHelper getOrderTicketControllerHelper() {
		return controllerHelper;
	}
}
