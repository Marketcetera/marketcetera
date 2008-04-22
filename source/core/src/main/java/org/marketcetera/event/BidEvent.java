package org.marketcetera.event;

import java.math.BigDecimal;

import quickfix.Message;

/**
 * Represents a Bid for a symbol on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class BidEvent
        extends BidAskEvent
{
    /**
     * Create a new BidEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inFixMessage
     * @param inSymbol
     * @param inExchange
     * @param inPrice
     * @param inSize
     */
    public BidEvent(long inMessageID,
                    long inTimestamp,
                    Message inFixMessage,
                    String inSymbol,
                    String inExchange,
                    BigDecimal inPrice,
                    BigDecimal inSize)
    {
        super(inMessageID,
              inTimestamp,
              inFixMessage,
              inSymbol,
              inExchange,
              inPrice,
              inSize);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.automation.event.BidAskEvent#getType()
     */
    protected Type getType()
    {
        return Type.Bid;
    }
}
