package org.marketcetera.photon.views;

import org.eclipse.jface.util.Assert;
import org.marketcetera.photon.PhotonPlugin;

import quickfix.Message;


/**
 * 
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class OptionOrderTicketController extends AbstractOrderTicketController {
	
	private OptionOrderTicketControllerHelper controllerHelper;
	
	@Override
	public OrderTicketControllerHelper getOrderTicketControllerHelper() {
		Assert.isNotNull(controllerHelper, "Controller is not yet bound.");

		return controllerHelper;
	}

	public void bind( IOptionOrderTicket ticket ) {
		if( controllerHelper != null ) {
			controllerHelper.dispose();
		}
		
		controllerHelper = new OptionOrderTicketControllerHelper(ticket, new OptionSeriesManager(ticket));
		controllerHelper.init();
	}
	
	public boolean hasBindErrors() {
		return controllerHelper.hasBindErrors();
	}

    public void onMessages(Message[] messages) {
    	if(messages == null || messages.length < 1) {
    		return;
    	}
        controllerHelper.getOptionSeriesManager().onMessages(messages);
    }

    public void onMessage(Message aMessage) {
        controllerHelper.getOptionSeriesManager().onMessage(aMessage);
    }
}
