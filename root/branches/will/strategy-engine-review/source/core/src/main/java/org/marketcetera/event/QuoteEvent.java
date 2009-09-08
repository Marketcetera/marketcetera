package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Common class for {@link Bid} and {@link Ask} events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public abstract class QuoteEvent
        extends SymbolExchangeEvent
{
    /**
     * Create a new QuoteEvent instance.
     *
     * @param messageId a <code>long</code> value uniquely identifying this market event
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     * @param inSymbol an <code>MSymbol</code> value containing the symbol quoted in this event
     * @param inExchange a <code>String</code> value containing the exchange on which the quote occurred 
     * @param inPrice a <code>BigDecimal</code> value containing the price of this event
     * @param inSize a <code>BigDecimal</code> value containing the size of this event
     * @param inAction an <code>Action</code> value
     * @throws IllegalArgumentException if <code>inMessageID</code> or <code>inTimestamp</code> &lt; 0
     * @throws IllegalArgumentException if <code>inExchange</code> is non-null but empty
     * @throws NullPointerException if <code>inSymbol</code>, <code>inExchange</code>, <code>inPrice</code>,
     *  <code>inSize</code>, or <code>inAction</code> is null
     */
    protected QuoteEvent(long inMessageID,
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
              inSize);
        if(inAction == null) {
          throw new NullPointerException();  
        }
        mAction = inAction;
    }
    /**
     * Get the action value.
     *
     * @return an <code>Action</code> value
     */
    public Action getAction()
    {
        return mAction;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append(getDescription()).append("(").append(getAction()).append("-").append(getMessageId()).append(") for ").append(getSymbol()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        output.append(" ").append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(DateUtils.dateToString(getTimestampAsDate())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
    }
    /**
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteEvent.Action} which
     * will always be {@link QuoteEvent.Action#DELETE}. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value
     */
    public static QuoteEvent deleteEvent(QuoteEvent inEvent)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        if(inEvent instanceof BidEvent) {
            return BidEvent.deleteEvent((BidEvent)inEvent);
        }
        if(inEvent instanceof AskEvent) {
            return AskEvent.deleteEvent((AskEvent)inEvent);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteEvent.Action} which
     * will always be {@link QuoteEvent.Action#CHANGE}, size, and timestamp.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @param inNewTimestamp a <code>long</code> value containing the timestamp to apply to the returned event 
     * @param inNewSize a <code>BigDecimal</code> value containing the size to use for the returned event
     * @return a <code>QuoteEvent</code> value
     */
    public static QuoteEvent changeEvent(QuoteEvent inEvent,
                                         long inNewTimestamp,
                                         BigDecimal inNewSize)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        if(inEvent instanceof BidEvent) {
            return BidEvent.changeEvent((BidEvent)inEvent,
                                        inNewTimestamp,
                                        inNewSize);
        }
        if(inEvent instanceof AskEvent) {
            return AskEvent.changeEvent((AskEvent)inEvent,
                                        inNewTimestamp,
                                        inNewSize);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Creates a <code>QuoteEvent</code> of the same type as the given event
     * with the same attributes except for the {@link QuoteEvent.Action} which
     * will always be {@link QuoteEvent.Action#ADD}.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value
     */
    public static QuoteEvent addEvent(QuoteEvent inEvent)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        if(inEvent instanceof BidEvent) {
            return BidEvent.addEvent((BidEvent)inEvent);
        }
        if(inEvent instanceof AskEvent) {
            return AskEvent.addEvent((AskEvent)inEvent);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Gets a description of the type of event.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getDescription();
    /**
     * Indicates the action to be taken.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$")
    public enum Action 
    {
        ADD,
        CHANGE,
        DELETE
    }
    /**
     * Compares orders for the purpose of sorting bids and asks according to their price order.
     * 
     * <p>Note that this <code>Comparator</code> breaks the {@link Set} contract.  The result of
     * {@link #compare(QuoteEvent, QuoteEvent)} is not the same as the result of
     * {@link EventBase#equals(Object)}.  This is OK, but it should be noted.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$")
    public final static class BookPriceComparator
        implements Comparator<QuoteEvent>
    {
        /**
         * a <code>Comparator</code> suitable for sorting bids in an order book
         */
        public static final BookPriceComparator BidComparator = new BookPriceComparator(false);
        /**
         * a <code>Comparator</code> suitable for sorting asks in an order book
         */
        public static final BookPriceComparator AskComparator = new BookPriceComparator(true);
        /**
         * indicates whether to sort ascending or descending
         */
        private final boolean mIsAscending;
        /**
         * Create a new BookComparator instance.
         *
         * @param inIsAscending a <code>boolean</code> value
         */
        private BookPriceComparator(boolean inIsAscending)
        {
            mIsAscending = inIsAscending;
        }
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(QuoteEvent inO1,
                           QuoteEvent inO2)
        {
            if(inO1 == inO2) {
                return 0;
            }
            // the first key is price, either ascending or descending
            int result = inO1.getPrice().compareTo(inO2.getPrice());
            if(result == 0) {
                // prices are equal
                // secondary sort should be on the timestamp
                result = new Long(inO1.getTimeMillis()).compareTo(inO2.getTimeMillis());
            }
            // invert the result to be returned if necessary to get a descending sort 
            return result * (mIsAscending ? 1 : -1);
        }
    }
    /**
     * Compares two <code>QuoteEvent</code> values based on their price and size
     * only.
     * 
     * <p>Price is compared first, followed by size, if necessary.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    public final static class PriceAndSizeComparator
        implements Comparator<QuoteEvent>
    {
        /**
         * the instance to use for comparisons
         */
        public static final PriceAndSizeComparator instance = new PriceAndSizeComparator();
        /**
         * Create a new PriceAndSizeComparator instance.
         */
        private PriceAndSizeComparator() {}
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(QuoteEvent inO1,
                           QuoteEvent inO2)
        {
            if(inO1 == inO2) {
                return 0;
            }
            int result;
            if((result = inO1.getPrice().compareTo(inO2.getPrice())) != 0) {
                return result;
            }
            return inO1.getSize().compareTo(inO2.getSize());
        }
    }
    /**
     * indicates the action of the event
     */
    private final Action mAction;
    private static final long serialVersionUID = 1L;
}
