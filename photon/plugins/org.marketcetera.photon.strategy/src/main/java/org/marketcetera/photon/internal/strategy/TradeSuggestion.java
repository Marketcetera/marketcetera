package org.marketcetera.photon.internal.strategy;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.Validate;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Instrument;
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
public class TradeSuggestion
{
    /**
     * Create a new TradeSuggestion instance.
     * 
     * @param inSuggestion an <code>OrderSingleSuggestion</code> value
     * @param inSource the source of the suggestion
     * @param inTimestamp the time the suggestion was received
     */
    TradeSuggestion(OrderSingleSuggestion inSuggestion,
                    String inSource,
                    Date inTimestamp)
    {
        Validate.notNull(inSuggestion);
        suggestion = inSuggestion;
        source = inSource;
        timestamp = inTimestamp;
        identifier = suggestion.getIdentifier();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TradeSuggestion [source=").append(source).append(", timestamp=").append(timestamp)
                .append(", suggestion=").append(suggestion).append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TradeSuggestion)) {
            return false;
        }
        TradeSuggestion other = (TradeSuggestion) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }
    /**
     * Returns the underlying order.
     * 
     * @return the underlying order
     */
    public OrderSingle getOrder() {
        return suggestion.getOrder();
    }
    /**
     * Get the suggestion value.
     *
     * @return an <code>OrderSingleSuggestion</code> value
     */
    public OrderSingleSuggestion getSuggestion()
    {
        return suggestion;
    }
    /**
     * Returns the identifier.
     * 
     * @return the identifier
     */
    public String getIdentifier()
    {
        return identifier;
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
     * Returns the instrument.
     * 
     * @return the instrument
     */
    public Instrument getInstrument() {
        return getOrder().getInstrument();
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
        return suggestion.getScore();
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
     * Returns the source.
     * 
     * @return the source
     */
    public String getSource() {
        return source;
    }
    /**
     * Returns the timestamp.
     * 
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
    /**
     * underlying suggestion value
     */
    private final OrderSingleSuggestion suggestion;
    /**
     * source value
     */
    private final String source;
    /**
     * timestamp value
     */
    private final Date timestamp;
    /**
     * identifier value
     */
    private final String identifier;
}
