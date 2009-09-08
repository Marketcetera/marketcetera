package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Represents a Bid for a symbol on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BidEvent
        extends QuoteEvent
{
    /**
     * Create a new BidEvent {@link QuoteEvent.Action#ADD} event.
     *
     * @param inMessageID a <code>long</code> value containing the unique identifier for this event
     * @param inTimestamp a <code>long</code> value containing the timestamp for this event
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for this event
     * @param inExchange a <code>String</code> value containing the exchange code for this event
     * @param inPrice a <code>BigDecimal</code> value containing the price of the bid event
     * @param inSize a <code>BigDecimal</code> value containing the size of the bid event
     * @throws IllegalArgumentException if <code>inMessageID</code> or <code>inTimestamp</code> &lt; 0
     * @throws IllegalArgumentException if <code>inExchange</code> is non-null but empty
     * @throws NullPointerException if <code>inSymbol</code>, <code>inExchange</code>, <code>inPrice</code>,
     *  or <code>inSize</code> is null
     */
    public BidEvent(long inMessageID,
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
     * given <code>BidEvent</code>.
     * 
     * @param inBid a <code>BidEvent</code> value
     * @return a <code>BidEvent</code> exactly the same as the given <code>BidEvent</code> of type 
     * {@link QuoteEvent.Action#DELETE}
     */
    public static BidEvent deleteEvent(BidEvent inBid)
    {
        return new BidEvent(inBid,
                            Action.DELETE);
    }
    /**
     * Returns a {@link QuoteEvent.Action#ADD} event that is otherwise equal to the
     * given <code>BidEvent</code>.
     * 
     * @param inBid a <code>BidEvent</code> value
     * @return a <code>BidEvent</code> exactly the same as the given <code>BidEvent</code> of type 
     * {@link QuoteEvent.Action#ADD}
     */
    public static BidEvent addEvent(BidEvent inBid)
    {
        return new BidEvent(inBid,
                            Action.ADD);
    }
    /**
     * Returns a {@link QuoteEvent.Action#CHANGE} event that is, but also for the size, otherwise equal to the
     * given <code>BidEvent</code>.
     *
     * @param inBid a <code>BidEvent</code> value
     * @param inNewTimestamp a <code>long</code> value containing the new timestamp
     * @param inNewSize a <code>BigDecimal</code> value containing the new size 
     * @return a <code>BidEvent</code> exactly the same as the given <code>BidEvent</code> of type 
     * {@link QuoteEvent.Action#CHANGE} and with the new given size and timestamp
     */
    public static BidEvent changeEvent(BidEvent inBid,
                                       long inNewTimestamp,
                                       BigDecimal inNewSize)
    {
        return new BidEvent(inBid.getMessageId(),
                            inNewTimestamp,
                            inBid.getSymbol(),
                            inBid.getExchange(),
                            inBid.getPrice(),
                            inNewSize,
                            Action.CHANGE);
    }
    /**
     * Create a new BidEvent instance.
     *
     * @param inMessageID a <code>long</code> value containing the unique identifier for this event
     * @param inTimestamp a <code>long</code> value containing the timestamp for this event
     * @param inSource an <code>Object</code> value containing information pointing to the source of the event, may be null
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for this event
     * @param inExchange a <code>String</code> value containing the exchange code for this event
     * @param inPrice a <code>BigDecimal</code> value containing the price of the bid event
     * @param inSize a <code>BigDecimal</code> value containing the size of the bid event
     * @param inAction an <code>Action</code> value containing the type of the bid event
     */
    private BidEvent(long inMessageID,
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
     * Create a new BidEvent instance.
     *
     * @param inBid a <code>BidEvent</code> value which serves as the source for the new bid
     * @param inAction an <code>Action</code> value
     */
    private BidEvent(BidEvent inBid,
                     Action inAction)
    {
        this(inBid.getMessageId(),
             inBid.getTimeMillis(),
             inBid.getSymbol(),
             inBid.getExchange(),
             inBid.getPrice(),
             inBid.getSize(),
             inAction);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.automation.event.BidAskEvent#getDescription()
     */
    protected final String getDescription()
    {
        return "Bid"; //$NON-NLS-1$
    }
    private static final long serialVersionUID = 1L;
}
