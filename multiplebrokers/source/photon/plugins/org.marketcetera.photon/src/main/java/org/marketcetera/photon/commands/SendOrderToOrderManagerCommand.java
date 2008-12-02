package org.marketcetera.photon.commands;

import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;

public class SendOrderToOrderManagerCommand 
    extends MessageCommand
    implements Messages
{
	private final String mDestination;

	public SendOrderToOrderManagerCommand(Message message) {
		this(message, null);
	}
	
	public SendOrderToOrderManagerCommand(Message message, String destination) {
		super(message);
		mDestination = destination;
	}
	
	public String getDestination() {
		return mDestination;
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
