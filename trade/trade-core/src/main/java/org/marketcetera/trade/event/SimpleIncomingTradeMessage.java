package org.marketcetera.trade.event;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.marketcetera.trade.TradeMessage;

/* $License$ */

/**
 * Provides a simple {@link IncomingTradeMessage} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SuppressFBWarnings(value="IS2_INCONSISTENT_SYNC", justification="Immutable")
public class SimpleIncomingTradeMessage
        implements IncomingTradeMessage
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasTradeMessage#getTradeMessage()
     */
    @Override
    public TradeMessage getTradeMessage()
    {
        return tradeMessage;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("IncomingTradeMessage [tradeMessage=").append(tradeMessage).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleIncomingTradeMessage instance.
     *
     * @param inTradeMessage a <code>TradeMessage</code> value
     */
    public SimpleIncomingTradeMessage(TradeMessage inTradeMessage)
    {
        tradeMessage = inTradeMessage;
    }
    /**
     * trade message value
     */
    private final TradeMessage tradeMessage;
}
