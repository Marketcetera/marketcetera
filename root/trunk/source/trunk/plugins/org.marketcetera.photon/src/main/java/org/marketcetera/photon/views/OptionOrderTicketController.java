package org.marketcetera.photon.views;

import org.eclipse.jface.util.Assert;
import quickfix.Message;


/**
 * 
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class OptionOrderTicketController extends AbstractOrderTicketController {
	
	private OptionOrderTicketControllerHelper controllerHelper;
	
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
	
	public boolean hasBindErrors() {
		return controllerHelper.hasBindErrors();
	}

    public void onMessages(Message[] messages) {
        controllerHelper.handleMarketDataList(messages);
    }

    public void onMessage(Message aMessage) {
        controllerHelper.handleMarketDataList(new Message[] {aMessage});
    }
}
