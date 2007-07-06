package org.marketcetera.photon.commands;

import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;

public class SendOrderToOrderManagerCommand extends MessageCommand {

	
	
	public SendOrderToOrderManagerCommand(Message message) {
		super(message);
	}

	public void execute() {
		try {
			PhotonPlugin.getDefault().getPhotonController().handleInternalMessage(getMessage());
		} catch (Exception ex){
			PhotonPlugin.getMainConsoleLogger().error("Exception sending order", ex);
		}
	}

}
