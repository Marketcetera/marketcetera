package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Represents a Bid for a symbol on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class BidEvent
        extends BidAskEvent
{
    private static final long serialVersionUID = 1L;
    /**
     * Create a new BidEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inSymbol
     * @param inExchange
     * @param inPrice
     * @param inSize
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
     * Create a new BidEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inSymbol
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inAction an <code>Action</code> value
     */
    public BidEvent(long inMessageID,
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
     */
    public BidEvent(BidEvent inBid)
    {
        this(inBid,
             inBid.getAction());
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
    public static BidEvent deleteEvent(BidEvent inBid)
    {
        return new BidEvent(inBid,
                            Action.DELETE);
    }
    public static BidEvent changeEvent(BidEvent inBid,
                                       BigDecimal inNewSize)
    {
        return new BidEvent(inBid.getMessageId(),
                            inBid.getTimeMillis(),
                            inBid.getSymbol(),
                            inBid.getExchange(),
                            inBid.getPrice(),
                            inNewSize,
                            Action.CHANGE);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.automation.event.BidAskEvent#getDescription()
     */
    protected String getDescription()
    {
        return "Bid"; //$NON-NLS-1$
    }
}
