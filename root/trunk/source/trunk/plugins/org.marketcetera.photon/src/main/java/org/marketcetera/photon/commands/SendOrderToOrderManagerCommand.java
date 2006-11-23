package org.marketcetera.photon.commands;

import org.marketcetera.photon.Application;

import quickfix.Message;

public class SendOrderToOrderManagerCommand extends MessageCommand {

	
	
	public SendOrderToOrderManagerCommand(Message message) {
		super(message);
	}

	public void execute() {
		try {
			Application.getOrderManager().handleMessage(getMessage());
		} catch (Exception ex){
			Application.getMainConsoleLogger().error("Exception sending order", ex);
		}
	}

}
