package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Represents an Ask for a symbol on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AskEvent
        extends BidAskEvent
{
    /**
     * Create a new AskEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inSymbol
     * @param inExchange
     * @param inPrice
     * @param inSize
     */
    public AskEvent(long inMessageID,
                    long inTimestamp,
                    String inSymbol,
                    String inExchange,
                    BigDecimal inPrice,
                    BigDecimal inSize)
    {
        this(inMessageID,
             inTimestamp,
             inSymbol,
             inExchange,
             inPrice,
             inSize, 
             Action.ADD);
    }
    /**
     * Create a new AskEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inSymbol
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inAction an <code>Action</code> value
     */
    public AskEvent(long inMessageID,
                    long inTimestamp,
                    String inSymbol,
                    String inExchange,
                    BigDecimal inPrice,
                    BigDecimal inSize,
                    Action inAction)
    {
        super(inMessageID,
              inTimestamp,
              inSymbol,
              inExchange,
              inPrice,
              inSize,
              inAction);
    }
    /**
     * Create a new AskEvent instance.
     *
     * @param inAsk an <code>AskEvent</code> value which serves as the source for the new ask
     */
    public AskEvent(AskEvent inAsk)
    {
        this(inAsk,
             inAsk.getAction());
    }
    /**
     * Create a new AskEvent instance.
     *
     * @param inAsk an <code>AskEvent</code> value which serves as the source for the new ask
     * @param inAction an <code>Action</code> value
     */
    private AskEvent(AskEvent inAsk,
                     Action inAction)
    {
        this(inAsk.getMessageId(),
             inAsk.getTimeMillis(),
             inAsk.getSymbol(),
             inAsk.getExchange(),
             inAsk.getPrice(),
             inAsk.getSize(),
             inAction);
    }
    public static AskEvent deleteEvent(AskEvent inAsk)
    {
        return new AskEvent(inAsk,
                            Action.DELETE);
    }
    public static AskEvent changeEvent(AskEvent inAsk,
                                       BigDecimal inNewSize)
    {
        return new AskEvent(inAsk.getMessageId(),
                            inAsk.getTimeMillis(),
                            inAsk.getSymbol(),
                            inAsk.getExchange(),
                            inAsk.getPrice(),
                            inNewSize,
                            Action.CHANGE);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.automation.event.AskAskEvent#getDescription()
     */
    protected String getDescription()
    {
        return "Ask"; //$NON-NLS-1$
    }
}
