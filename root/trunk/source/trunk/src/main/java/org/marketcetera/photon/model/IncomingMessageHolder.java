package org.marketcetera.photon.model;

import quickfix.Message;

public class IncomingMessageHolder extends MessageHolder {

	public IncomingMessageHolder(Message message) {
		super(message);
	}

	public IncomingMessageHolder(Message message, int referenceNo) {
		super(message, referenceNo);
	}	
}
