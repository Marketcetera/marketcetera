package org.marketcetera.photon.actions;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;


@ClassVersion("$Id$")
public class CommandEvent {

	public enum Destination {
		BROKER, EDITOR
	}

	private Message messageValue;
	private final Destination dest;

	public CommandEvent(Message message, Destination dest) {
		messageValue = message;
		this.dest = dest;
	}

	/**
	 * @return Returns the stringValue.
	 */
	public Message getMessage() {
		return messageValue;
	}

	/**
	 * @return Returns the dest.
	 */
	public Destination getDestination() {
		return dest;
	}

	
}
