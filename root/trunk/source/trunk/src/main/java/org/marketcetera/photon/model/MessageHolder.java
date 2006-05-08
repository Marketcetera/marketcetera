package org.marketcetera.photon.model;

import quickfix.Message;

public class MessageHolder {
	Message message;

	public MessageHolder(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
