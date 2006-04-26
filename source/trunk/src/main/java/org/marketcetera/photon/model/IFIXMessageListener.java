package org.marketcetera.photon.model;

import quickfix.Message;

public interface IFIXMessageListener {
	public void incomingMessage(Message message);
	public void outgoingMessage(Message message);
}
