package org.marketcetera.photon.views;

import quickfix.Message;

public interface IOrderTicketController {
	void showMessage(Message aMessage);

	Message getMessage();
	
	void handleSend();

	void handleCancel();
	
	void dispose();
}
