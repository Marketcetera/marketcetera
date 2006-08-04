package org.marketcetera.photon.model;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class MessageHolder {
	private Message message;
	private int messageReference;

	public MessageHolder(Message message) {
		this.message = message;
	}

	public MessageHolder(Message message, int referenceNo) {
		this.message = message;
		this.messageReference = referenceNo;
	}

	public Message getMessage() {
		return message;
	}
	
	public int getMessageReference()
	{
		return messageReference;
	}
}
