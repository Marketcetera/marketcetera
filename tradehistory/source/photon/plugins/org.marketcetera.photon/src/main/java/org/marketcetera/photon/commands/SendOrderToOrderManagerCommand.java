package org.marketcetera.photon.commands;

import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;

public class SendOrderToOrderManagerCommand 
    extends MessageCommand
    implements Messages
{
	public SendOrderToOrderManagerCommand(Message message) {
		super(message);
	}

	public void execute() {
		try {
			Message theMessage = getMessage();
			sendOrder(theMessage);
		} catch (Exception ex){
			PhotonPlugin.getMainConsoleLogger().error(CANNOT_SEND_ORDER.getText(),
			                                          ex);
		}
	}

	public static void sendOrder(Message theMessage) {
			PhotonPlugin.getDefault().getPhotonController().handleInternalMessage(theMessage);
	}

}
