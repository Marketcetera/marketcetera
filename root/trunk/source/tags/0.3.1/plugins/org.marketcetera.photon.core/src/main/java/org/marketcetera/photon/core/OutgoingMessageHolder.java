package org.marketcetera.photon.core;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class OutgoingMessageHolder extends MessageHolder {
	public OutgoingMessageHolder(Message message) {
		super(message);
	}
	public OutgoingMessageHolder(Message message, int referenceNo) {
		super(message, referenceNo);
	}	
}
