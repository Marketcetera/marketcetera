package org.marketcetera.photon.views;

import org.marketcetera.util.misc.ClassVersion;

/**
 * This is a relatively trivial subclass of {@link OrderTicketController}.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class StockOrderTicketController
	 extends OrderTicketController<StockOrderTicketModel> 
{
	/**
	 * Create a new controller
	 * 
	 * @param orderTicketModel the order ticket model
	 */
	public StockOrderTicketController(OrderTicketModel orderTicketModel) 
	{
		super((StockOrderTicketModel)orderTicketModel);
	}
	
	@Override
	public void clear() {
		getOrderTicketModel().clearOrderMessage();
	}
}
