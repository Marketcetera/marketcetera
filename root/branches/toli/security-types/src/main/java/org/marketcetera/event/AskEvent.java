package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents an Ask for a symbol on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class AskEvent
        extends QuoteEvent
{
    /**
     * Create a new AskEvent {@link QuoteEvent.Action#ADD} event.
     *
     * @param inMessageID a <code>long</code> value containing the unique identifier for this event
     * @param inTimestamp a <code>long</code> value containing the timestamp for this event
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for this event
     * @param inExchange a <code>String</code> value containing the exchange code for this event
     * @param inPrice a <code>BigDecimal</code> value containing the price of the ask event
     * @param inSize a <code>BigDecimal</code> value containing the size of the ask event
     * @throws IllegalArgumentException if <code>inMessageID</code> or <code>inTimestamp</code> &lt; 0
     * @throws IllegalArgumentException if <code>inExchange</code> is non-null but empty
     * @throws NullPointerException if <code>inSymbol</code>, <code>inExchange</code>, <code>inPrice</code>,
     *  or <code>inSize</code> is null
     */
    public AskEvent(long inMessageID,
                    long inTimestamp,
                    MSymbol inSymbol,
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
     * Returns a {@link QuoteEvent.Action#DELETE} event that is otherwise equal to the
     * given <code>AskEvent</code>.
     * 
     * @param inAsk an <code>AskEvent</code> value
     * @return an <code>AskEvent</code> exactly the same as the given <code>AskEvent</code> of type 
     * {@link QuoteEvent.Action#DELETE}
     */
    public static AskEvent deleteEvent(AskEvent inAsk)
    {
        return new AskEvent(inAsk,
                            Action.DELETE);
    }
    /**
     * Returns a {@link QuoteEvent.Action#ADD} event that is otherwise equal to the
     * given <code>AskEvent</code>.
     * 
     * @param inAsk an <code>AskEvent</code> value
     * @return an <code>AskEvent</code> exactly the same as the given <code>AskEvent</code> of type 
     * {@link QuoteEvent.Action#ADD}
     */
    public static AskEvent addEvent(AskEvent inAsk)
    {
        return new AskEvent(inAsk,
                            Action.ADD);
    }
    /**
     * Returns a {@link QuoteEvent.Action#CHANGE} event that is, but also for the size, otherwise equal to the
     * given <code>AskEvent</code>.
     *
     * @param inAsk an <code>AskEvent</code> value
     * @param inNewTimestamp a <code>long</code> value containing the new timestamp
     * @param inNewSize a <code>BigDecimal</code> value containing the new size 
     * @return an <code>AskEvent</code> exactly the same as the given <code>AskEvent</code> of type 
     * {@link QuoteEvent.Action#CHANGE} and with the new given size and timestamp
     */
    public static AskEvent changeEvent(AskEvent inAsk,
                                       long inNewTimestamp,
                                       BigDecimal inNewSize)
    {
        return new AskEvent(inAsk.getMessageId(),
                            inNewTimestamp,
                            inAsk.getSymbol(),
                            inAsk.getExchange(),
                            inAsk.getPrice(),
                            inNewSize,
                            Action.CHANGE);
    }
    /**
     * Create a new AskEvent instance.
     *
     * @param inMessageID a <code>long</code> value containing the unique identifier for this event
     * @param inTimestamp a <code>long</code> value containing the timestamp for this event
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for this event
     * @param inExchange a <code>String</code> value containing the exchange code for this event
     * @param inPrice a <code>BigDecimal</code> value containing the price of the ask event
     * @param inSize a <code>BigDecimal</code> value containing the size of the ask event
     * @param inAction an <code>Action</code> value containing the type of the ask event
     */
    private AskEvent(long inMessageID,
                     long inTimestamp,
                     MSymbol inSymbol,
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
    /* (non-Javadoc)
     * @see org.marketcetera.automation.event.AskAskEvent#getDescription()
     */
    protected final String getDescription()
    {
        return "Ask"; //$NON-NLS-1$
    }
    private static final long serialVersionUID = 1L;
}
