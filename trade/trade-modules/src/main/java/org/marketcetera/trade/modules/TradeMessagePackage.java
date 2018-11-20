package org.marketcetera.trade.modules;

import org.marketcetera.fix.ServerFixSession;
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
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    public TradeMessagePackage(ServerFixSession inServerFixSession,
                               TradeMessage inTradeMessage)
    {
        serverFixSession = inServerFixSession;
        tradeMessage = inTradeMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasBrokerID#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return new BrokerID(serverFixSession.getActiveFixSession().getFixSession().getBrokerId());
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
     * Get the serverFixSession value.
     *
     * @return a <code>ServerFixSession</code> value
     */
    public ServerFixSession getServerFixSession()
    {
        return serverFixSession;
    }
    /**
     * Sets the serverFixSession value.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     */
    public void setServerFixSession(ServerFixSession inServerFixSession)
    {
        serverFixSession = inServerFixSession;
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
        builder.append("TradeMessagePackage [serverFixSession=").append(serverFixSession).append(", tradeMessage=").append(tradeMessage)
                .append("]");
        return builder.toString();
    }
    /**
     * broker value
     */
    private ServerFixSession serverFixSession;
    /**
     * trade message value
     */
    private TradeMessage tradeMessage;
}
