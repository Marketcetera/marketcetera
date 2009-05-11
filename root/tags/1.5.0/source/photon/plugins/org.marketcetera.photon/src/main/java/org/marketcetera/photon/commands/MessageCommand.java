package org.marketcetera.photon.commands;

import org.marketcetera.photon.IPhotonCommand;

import quickfix.Message;

public abstract class MessageCommand implements IPhotonCommand {

	protected Message message;

	public MessageCommand(Message message) {
		super();
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}


}
