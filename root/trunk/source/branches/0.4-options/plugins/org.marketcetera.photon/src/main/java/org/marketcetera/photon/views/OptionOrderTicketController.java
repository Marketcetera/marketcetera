package org.marketcetera.photon.views;

import org.eclipse.jface.util.Assert;


public class OptionOrderTicketController extends AbstractOrderTicketController {
	
	private OrderTicketControllerHelper controllerHelper;
	
	@Override
	protected OrderTicketControllerHelper getOrderTicketControllerHelper() {
		Assert.isNotNull(controllerHelper, "Controller is not yet bound.");
		return controllerHelper;
	}

	public void bind( IOptionOrderTicket ticket ) {
		if( controllerHelper != null ) {
			controllerHelper.dispose();
		}
		controllerHelper = new OptionOrderTicketControllerHelper(ticket);
		controllerHelper.init();
	}
}
