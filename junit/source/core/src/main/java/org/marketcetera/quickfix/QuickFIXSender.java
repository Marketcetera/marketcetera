package org.marketcetera.quickfix;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;

public class QuickFIXSender implements IQuickFIXSender {

	public boolean sendToTarget(Message message) throws SessionNotFound {
		return Session.sendToTarget(message);
	}

	public boolean sendToTarget(Message message, SessionID sessionID)
			throws SessionNotFound {
		return Session.sendToTarget(message, sessionID);
	}

	public boolean sendToTarget(Message message, String qualifier)
			throws SessionNotFound {
		return Session.sendToTarget(message, qualifier);
	}

	public boolean sendToTarget(Message message, String senderCompID,
			String targetCompID) throws SessionNotFound {
		return Session.sendToTarget(message, senderCompID, targetCompID);
	}

	public boolean sendToTarget(Message message, String senderCompID,
			String targetCompID, String qualifier) throws SessionNotFound {
		return Session.sendToTarget(message, senderCompID, targetCompID,
				qualifier);
	}
}
