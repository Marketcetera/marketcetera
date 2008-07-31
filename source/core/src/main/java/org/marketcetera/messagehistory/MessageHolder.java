package org.marketcetera.messagehistory;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$") //$NON-NLS-1$
public class MessageHolder implements Comparable<MessageHolder> {
	private Message message;
	private long messageReference;
	private static AtomicLong counter = new AtomicLong();
	private String groupID = null;
	
	public MessageHolder(Message message) {
		this.message = message;
		this.messageReference = counter.incrementAndGet();
	}

	public MessageHolder(Message message, String groupID){
		this(message);
		this.groupID = groupID;
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

	public String getGroupID() {
		return groupID;
	}
	
	
}
