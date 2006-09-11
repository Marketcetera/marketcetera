package org.marketcetera.photon.model;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class MessageHolder implements Comparable {
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

	// implementing Comparable is necessary simply to appease glazed lists which otherwise 
	// currently blows up when a new message is added to the list with a natural sort order 
	// (i.e., no comparator)
	public int compareTo(Object o) {  
		return 0;
	}
}
