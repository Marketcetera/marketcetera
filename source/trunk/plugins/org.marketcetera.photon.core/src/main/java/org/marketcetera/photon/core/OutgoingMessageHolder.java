package org.marketcetera.photon.core;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class OutgoingMessageHolder extends MessageHolder {
	public OutgoingMessageHolder(Message message) {
		super(message);
	}
}
