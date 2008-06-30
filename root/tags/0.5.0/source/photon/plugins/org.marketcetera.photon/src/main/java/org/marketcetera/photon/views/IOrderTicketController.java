package org.marketcetera.photon.views;

import quickfix.Message;

public interface IOrderTicketController {
	void setOrderMessage(Message order);

	Message getOrderMessage();
	
	void clear();
	
	void dispose();
}
