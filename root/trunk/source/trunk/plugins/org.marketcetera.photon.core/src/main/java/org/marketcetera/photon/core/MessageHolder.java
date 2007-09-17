package org.marketcetera.photon.core;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public class MessageHolder implements Comparable<MessageHolder> {
	private Message message;
	private long messageReference;
	private static AtomicLong counter = new AtomicLong();
	
	public MessageHolder(Message message) {
		this.message = message;
		this.messageReference = counter.incrementAndGet();
	}

	public Message getMessage() {
		return message;
	}
	
	public long getMessageReference()
	{
		return messageReference;
	}

	public int compareTo(MessageHolder mh) {  
		return (int)(messageReference - mh.messageReference);
	}
}
