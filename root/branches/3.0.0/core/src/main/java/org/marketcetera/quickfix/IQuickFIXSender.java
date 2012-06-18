package org.marketcetera.quickfix;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;

public interface IQuickFIXSender {
	/**
	 * Send a message to the session specified in the message's target
	 * identifiers.
	 */
	boolean sendToTarget(Message message) throws SessionNotFound;

	/** Send a message to the session specified by the provided session ID. */
	boolean sendToTarget(Message message, SessionID sessionID)
			throws SessionNotFound;

	/**
	 * Send a message to the session specified in the message's target
	 * identifiers.
	 */
	boolean sendToTarget(Message message, java.lang.String qualifier)
			throws SessionNotFound;

	/**
	 * Send a message to the session specified by the provided target company
	 * ID.
	 */
	boolean sendToTarget(Message message, java.lang.String senderCompID,
			java.lang.String targetCompID) throws SessionNotFound;

	/**
	 * Send a message to the session specified by the provided target company
	 * ID.
	 */
	boolean sendToTarget(Message message, java.lang.String senderCompID,
			java.lang.String targetCompID, java.lang.String qualifier)
			throws SessionNotFound;
}
