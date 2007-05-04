package org.marketcetera.photon.commands;

import org.marketcetera.photon.IPhotonCommand;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.StockOrderTicket;

import quickfix.Message;

public class ShowOrderInTicketCommand implements IPhotonCommand {

	Message order;
	
	public ShowOrderInTicketCommand(Message order) {
		super();
		this.order = order;
	}

	public void execute() {
		PhotonPlugin.getDefault().getStockOrderTicketController().handleCancel();
		PhotonPlugin.getDefault().getStockOrderTicketController().showMessage(order);
	}

}
