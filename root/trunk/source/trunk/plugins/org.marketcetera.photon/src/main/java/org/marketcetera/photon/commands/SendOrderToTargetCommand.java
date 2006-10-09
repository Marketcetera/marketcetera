package org.marketcetera.photon.commands;

import javax.jms.JMSException;

import org.marketcetera.photon.Application;
import org.marketcetera.photon.IPhotonCommand;

import quickfix.Message;

public class SendOrderToTargetCommand extends MessageCommand {

	public SendOrderToTargetCommand(Message message) {
		super(message);
	}

	public void execute() {
		try {
			Application.sendToQueue(message);
		} catch (JMSException e) {
			Application.getMainConsoleLogger().error(this, e);
		}
	}
}
