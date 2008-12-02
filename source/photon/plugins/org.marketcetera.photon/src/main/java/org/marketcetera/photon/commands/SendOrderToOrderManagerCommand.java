package org.marketcetera.photon.commands;

import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonController;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;

public class SendOrderToOrderManagerCommand 
    extends MessageCommand
    implements Messages
{
	private final String mBroker;

	public SendOrderToOrderManagerCommand(Message message) {
		this(message, null);
	}
	
	public SendOrderToOrderManagerCommand(Message message, String broker) {
		super(message);
		mBroker = broker;
	}
	
	public String getBroker() {
		return mBroker;
	}

	public void execute() {
		try {
			Message theMessage = getMessage();
			PhotonController photonController = PhotonPlugin.getDefault().getPhotonController();
			if (getBroker() == null) {
				photonController.handleInternalMessage(theMessage);
			} else {
				photonController.handleInternalMessage(theMessage, getBroker());
			}
		} catch (Exception ex){
			PhotonPlugin.getMainConsoleLogger().error(CANNOT_SEND_ORDER.getText(),
			                                          ex);
		}
	}

}
