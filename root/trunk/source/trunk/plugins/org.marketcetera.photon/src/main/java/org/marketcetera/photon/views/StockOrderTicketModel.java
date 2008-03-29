package org.marketcetera.photon.views;

import org.eclipse.core.databinding.observable.list.WritableList;
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
 *
 */
public class StockOrderTicketModel extends OrderTicketModel {

	private final WritableList bidList = new WritableList();
	private final WritableList offerList = new WritableList();
	
	/**
	 * Create a {@link StockOrderTicketModel} with the given
	 * {@link FIXMessageFactory} for message creation and
	 * augmentation.
	 * 
	 * @param messageFactory the message factory
	 */
	public StockOrderTicketModel(FIXMessageFactory messageFactory) {
		super(messageFactory);
	}

	/**
	 * Get the list of offers (stock market data)
	 * @return the list of offers
	 */
	public WritableList getOfferList() {
		return offerList;
	}
	
	/**
	 * Get the list of bids (stock market data)
	 * @return the list of bids
	 */
	public WritableList getBidList() {
		return bidList;
	}

	/**
	 * Creates a new order with {@link SecurityType#COMMON_STOCK}
	 */
	@Override
	protected Message createNewOrder() {
		Message aMessage = getMessageFactory().newBasicOrder();
		aMessage.setString(SecurityType.FIELD, SecurityType.COMMON_STOCK);
		aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));
		return aMessage;
	}
	
}
