package org.marketcetera.photon.views;

import quickfix.Message;

public interface IOrderTicketController {
	void setOrderMessage(Message order);

	Message getOrderMessage();
	
	void setBrokerId(String id);
	
	void clear();
	
	void dispose();
}
