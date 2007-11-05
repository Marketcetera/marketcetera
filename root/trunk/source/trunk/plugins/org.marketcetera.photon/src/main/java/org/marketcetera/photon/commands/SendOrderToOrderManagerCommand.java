package org.marketcetera.photon.commands;

import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;

public class SendOrderToOrderManagerCommand extends MessageCommand {

	
	
	public SendOrderToOrderManagerCommand(Message message) {
		super(message);
	}

	public void execute() {
		try {
			Message theMessage = getMessage();
			sendOrder(theMessage);
		} catch (Exception ex){
			PhotonPlugin.getMainConsoleLogger().error("Exception sending order", ex);
		}
	}

	public static void sendOrder(Message theMessage) {
			PhotonPlugin.getDefault().getPhotonController().handleInternalMessage(theMessage);
	}

}
