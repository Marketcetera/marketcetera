package org.marketcetera.photon.views;

import org.marketcetera.marketdata.IMarketDataListener;
import quickfix.Message;

public abstract class AbstractOrderTicketController implements
		IOrderTicketController, IMarketDataListener {

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
		controllerHelper.handleCancel();
		controllerHelper.showMessage(aMessage);
	}

    public void onMessage(Message aMessage) {
        // no-op
    }

    public void onMessages(Message[] messages) {
        for (Message message : messages) {
            onMessage(message);
        }
    }
}
