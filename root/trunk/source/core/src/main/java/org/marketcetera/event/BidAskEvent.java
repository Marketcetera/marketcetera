package org.marketcetera.event;

import java.math.BigDecimal;

import quickfix.Message;

/**
 * Common class for {@link Bid} and {@link Ask} events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
abstract class BidAskEvent
        extends SymbolExchangeEvent
{
    /**
     * the price of the ask on the quote 
     */
    private final BigDecimal mPrice;
    /**
     * the size of the ask on the quote 
     */
    private final BigDecimal mSize;

    /**
     * Describes the type of a <code>BidAsk</code> event.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: $
     * @since 0.43-SNAPSHOT
     */
    protected enum Type { Bid, Ask };
    
    /**
     * Create a new AskEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inFixMessage
     * @param inSymbol
     * @param inExchange
     */
    protected BidAskEvent(long inMessageID,
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
              inExchange);
        
        mPrice = inPrice;
        mSize = inSize;
    }

    /**
     * Get the price value.
     *
     * @return a <code>AskEvent</code> value
     */
    public BigDecimal getPrice()
    {
        return mPrice;
    }

    /**
     * Get the size value.
     *
     * @return a <code>AskEvent</code> value
     */
    public BigDecimal getSize()
    {
        return mSize;
    }

    /**
     * Gets the type of event.
     *
     * @return a <code>Type</code> value
     */
    protected abstract Type getType();
    
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append(getType()).append(" for ").append(getSymbol()).append(": ").append(getPrice()).append(" ").append(getSize());
        output.append(" ").append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(getTimestampAsDate());
        return output.toString();
    }
}
