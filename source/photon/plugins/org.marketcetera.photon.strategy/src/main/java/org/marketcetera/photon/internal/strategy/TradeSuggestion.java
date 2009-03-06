package org.marketcetera.photon.internal.strategy;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderCapacity;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.PositionEffect;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Photon UI abstraction for a trade suggestion.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradeSuggestion {

	private final OrderSingleSuggestion mSuggestion;

	private final Date mTimestamp;

	/**
	 * Constructor.
	 * 
	 * @param suggestion
	 *            the trade suggestion
	 * @param timestamp
	 *            the time the suggestion was received
	 */
	TradeSuggestion(OrderSingleSuggestion suggestion, Date timestamp) {
		Validate.notNull(suggestion);
		mSuggestion = suggestion;
		mTimestamp = timestamp;
	}

	/**
	 * Returns the underlying order.
	 * 
	 * @return the underlying order
	 */
	OrderSingle getOrder() {
		return mSuggestion.getOrder();
	}

	/**
	 * Returns the identifier.
	 * 
	 * @return the identifier
	 */
	public String getIdentifier() {
		return mSuggestion.getIdentifier();
	}

	/**
	 * Returns the side.
	 * 
	 * @return the side
	 */
	public Side getSide() {
		return getOrder().getSide();
	}

	/**
	 * Returns the security type.
	 * 
	 * @return the security type
	 */
	public SecurityType getSecurityType() {
		return getOrder().getSecurityType();
	}

	/**
	 * Returns the quantity.
	 * 
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return getOrder().getQuantity();
	}

	/**
	 * Returns the symbol.
	 * 
	 * @return the symbol
	 */
	public MSymbol getSymbol() {
		return getOrder().getSymbol();
	}

	/**
	 * Returns the price.
	 * 
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return getOrder().getPrice();
	}

	/**
	 * Returns the order type.
	 * 
	 * @return the order type
	 */
	public OrderType getOrderType() {
		return getOrder().getOrderType();
	}

	/**
	 * Returns the time in force.
	 * 
	 * @return the time in force
	 */
	public TimeInForce getTimeInForce() {
		return getOrder().getTimeInForce();
	}

	/**
	 * Returns the order capacity.
	 * 
	 * @return the order capacity
	 */
	public OrderCapacity getOrderCapacity() {
		return getOrder().getOrderCapacity();
	}

	/**
	 * Returns the position effect.
	 * 
	 * @return the position effect
	 */
	public PositionEffect getPositionEffect() {
		return getOrder().getPositionEffect();
	}

	/**
	 * Returns the score.
	 * 
	 * @return the score
	 */
	public BigDecimal getScore() {
		return mSuggestion.getScore();
	}

	/**
	 * Returns the account.
	 * 
	 * @return the account
	 */
	public String getAccount() {
		return getOrder().getAccount();
	}

	/**
	 * Returns the broker ID.
	 * 
	 * @return the broker ID
	 */
	public BrokerID getBrokerID() {
		return getOrder().getBrokerID();
	}

	/**
	 * Returns the timestamp.
	 * 
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return mTimestamp;
	}
}
