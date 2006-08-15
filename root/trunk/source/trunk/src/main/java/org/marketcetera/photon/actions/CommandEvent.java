package org.marketcetera.photon.actions;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;


/**
 * A simple wrapper for a text command coming from the user
 * through the command entry area.  Holds the message
 * representing the command and a Destination specifying 
 * which component of photon the message is bound for.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class CommandEvent {

	/**
	 * Destination specifies where this particular message should 
	 * be sent.  First is "BROKER", meaning out the JMS connection
	 * to the OMS and ultimately the counterparty.  Second is "EDITOR"
	 * meaning that the message is bound for more interal editing and
	 * user approval before being sent out to the JMS connection. 
	 * 
	 * @author gmiller
	 *
	 */
	public enum Destination {
		BROKER, EDITOR
	}

	private Message messageValue;
	private final Destination dest;

	/**
	 * Create a new CommandEvent
	 * 
	 * @param message the {@link quickfix.Message} representing the command
	 * @param dest the application-internal destination for the message
	 */
	public CommandEvent(Message message, Destination dest) {
		messageValue = message;
		this.dest = dest;
	}

	/**
	 * @return Returns the message representing the command.
	 */
	public Message getMessage() {
		return messageValue;
	}

	/**
	 * @return Returns the application-internal destination of this command.
	 */
	public Destination getDestination() {
		return dest;
	}

	
}
