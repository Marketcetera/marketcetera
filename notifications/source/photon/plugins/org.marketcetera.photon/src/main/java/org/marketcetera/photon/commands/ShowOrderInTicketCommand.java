package org.marketcetera.photon.commands;

import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IOrderTicketController;

import quickfix.Message;

/**
 * Command responsible for showing a particular order in the
 * correct order ticket window.  Depends on {@link PhotonPlugin#getOrderTicketController(Message)}
 * @author gmiller
 *
 */
public class ShowOrderInTicketCommand implements IPhotonCommand {

	Message order;
	
	public ShowOrderInTicketCommand(Message order) {
		super();
		this.order = order;
	}

	/**
	 * Shows an order in the appropriate order ticket window.
	 */
	public void execute() {
		IOrderTicketController controller = PhotonPlugin.getDefault().getOrderTicketController(order);
		if(controller != null) {
			controller.setOrderMessage(order);
		}
	}

}
