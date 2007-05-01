package org.marketcetera.photon.views;

import quickfix.Message;

public abstract class AbstractOrderTicketController implements
		IOrderTicketController {

	public AbstractOrderTicketController() {
	}

	/**
	 * @return an OrderTicketControllerHelper, never null.
	 */
	protected abstract OrderTicketControllerHelper getOrderTicketControllerHelper();

	public void dispose() {
		OrderTicketControllerHelper controllerHelper = getOrderTicketControllerHelper();
		controllerHelper.dispose();
		controllerHelper = null;
	}

	public Message getMessage() {
		OrderTicketControllerHelper controllerHelper = getOrderTicketControllerHelper();
		return controllerHelper.getMessage();
	}

	public void handleCancel() {
		OrderTicketControllerHelper controllerHelper = getOrderTicketControllerHelper();
		controllerHelper.handleCancel();
	}

	public void handleSend() {
		OrderTicketControllerHelper controllerHelper = getOrderTicketControllerHelper();
		controllerHelper.handleSend();
	}

	public void showMessage(Message aMessage) {
		OrderTicketControllerHelper controllerHelper = getOrderTicketControllerHelper();
		controllerHelper.showMessage(aMessage);
	}
}
