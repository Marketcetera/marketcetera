package org.marketcetera.photon.views;

import org.marketcetera.quickfix.FIXMessageFactory;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.SecurityType;

/**
 * Implements the model of a stock order ticket.  It is
 * a fairly trivial subclass of {@link OrderTicketModel}
 * that adds two lists one each for bids and asks (stock
 * market data).
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 *
 */
public class StockOrderTicketModel 
    extends OrderTicketModel 
{
    /**
	 * Create a {@link StockOrderTicketModel} with the given
	 * {@link FIXMessageFactory} for message creation and
	 * augmentation.
	 * 
	 * @param messageFactory the message factory
	 */
	public StockOrderTicketModel(FIXMessageFactory messageFactory) 
	{
		super(messageFactory);
	}
	
    @Override
    protected Message createNewOrder()
    {
        Message aMessage = getMessageFactory().newBasicOrder();
        aMessage.setString(SecurityType.FIELD, SecurityType.COMMON_STOCK);
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
        return aMessage;
    }
}
