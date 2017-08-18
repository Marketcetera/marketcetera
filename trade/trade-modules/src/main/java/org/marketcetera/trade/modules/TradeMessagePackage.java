package org.marketcetera.trade.modules;

import org.marketcetera.brokers.Broker;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.HasBrokerID;
import org.marketcetera.trade.HasTradeMessage;
import org.marketcetera.trade.TradeMessage;

/* $License$ */

/**
 * Contains a {@link TradeMessage}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeMessagePackage
        implements HasTradeMessage,HasBrokerID
{
    /**
     * Create a new TradeMessagePackage instance.
     */
    public TradeMessagePackage() {}
    /**
     * Create a new TradeMessagePackage instance.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    public TradeMessagePackage(Broker inBroker,
                               TradeMessage inTradeMessage)
    {
        broker = inBroker;
        tradeMessage = inTradeMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasBrokerID#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return broker.getBrokerId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasTradeMessage#getTradeMessage()
     */
    @Override
    public TradeMessage getTradeMessage()
    {
        return tradeMessage;
    }
    /**
     * Get the broker value.
     *
     * @return a <code>Broker</code> value
     */
    public Broker getBroker()
    {
        return broker;
    }
    /**
     * Sets the broker value.
     *
     * @param inBroker a <code>Broker</code> value
     */
    public void setBroker(Broker inBroker)
    {
        broker = inBroker;
    }
    /**
     * Sets the tradeMessage value.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    public void setTradeMessage(TradeMessage inTradeMessage)
    {
        tradeMessage = inTradeMessage;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("TradeMessagePackage [broker=").append(broker).append(", tradeMessage=").append(tradeMessage)
                .append("]");
        return builder.toString();
    }
    /**
     * broker value
     */
    private Broker broker;
    /**
     * trade message value
     */
    private TradeMessage tradeMessage;
}
