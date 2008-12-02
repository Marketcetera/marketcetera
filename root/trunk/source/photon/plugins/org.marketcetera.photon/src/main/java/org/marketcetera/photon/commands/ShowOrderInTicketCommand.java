package org.marketcetera.photon.commands;

import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IOrderTicketController;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/**
 * Command responsible for showing a particular order in the correct order
 * ticket window. Depends on
 * {@link PhotonPlugin#getOrderTicketController(Message)}.
 * 
 * @author gmiller
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 */
@ClassVersion("$Id$")
public class ShowOrderInTicketCommand implements IPhotonCommand {

	private Message mOrder;
	private String mBroker;

	public ShowOrderInTicketCommand(Message order, String broker) {
		mOrder = order;
		mBroker = broker;
	}

	/**
	 * Shows an order in the appropriate order ticket window.
	 */
	public void execute() {
		IOrderTicketController controller = PhotonPlugin.getDefault()
				.getOrderTicketController(mOrder);
		if (controller != null) {
			controller.setOrderMessage(mOrder);
			if (mBroker != null) {
				controller.setBrokerId(mBroker);
			}

		}
	}

}
