package org.marketcetera.photon.messaging;

import org.marketcetera.photon.views.MarketDataView;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.spring.JMSFIXMessageConverter;

import quickfix.Message;

public class StockOrderTicketAdapter extends DirectMessageListenerAdapter {

	private StockOrderTicket ticket;

	public StockOrderTicketAdapter() {
		super();
		this.setMessageConverter(new JMSFIXMessageConverter());
	}

	@Override
	protected Object doOnMessage(Object convertedMessage) {
		if (ticket != null){
			ticket.onQuote(((Message)convertedMessage));
		}
		return null;
	}

	public void setStockOrderTicket(StockOrderTicket ticket) {
		this.ticket = ticket;
	}

	public StockOrderTicket getStockOrderTicket() {
		return ticket;
	}

}
