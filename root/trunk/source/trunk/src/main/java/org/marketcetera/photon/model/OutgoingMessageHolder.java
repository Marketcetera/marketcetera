package org.marketcetera.photon.model;

import quickfix.Message;

public class OutgoingMessageHolder extends MessageHolder {
	public OutgoingMessageHolder(Message message) {
		super(message);
	}
}
