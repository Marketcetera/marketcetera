package org.marketcetera.photon.core;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class IncomingMessageHolder extends MessageHolder {

	public IncomingMessageHolder(Message message) {
		super(message);
	}

	public IncomingMessageHolder(Message message, long referenceNo) {
		super(message, referenceNo);
	}	
}
