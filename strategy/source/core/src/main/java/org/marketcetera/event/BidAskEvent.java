package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Common class for {@link Bid} and {@link Ask} events.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class BidAskEvent
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
     * indicates the action of the event
     */
    private final Action mAction;
    /**
     * Create a new AskEvent instance.
     *
     * @param inMessageID
     * @param inTimestamp
     * @param inSymbol
     * @param inExchange
     * @param inAction an <code>Action</code> value
     */
    protected BidAskEvent(long inMessageID,
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
              inExchange);
        mPrice = inPrice;
        mSize = inSize;
        mAction = inAction;
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
    public String toString()
    {
        StringBuffer output = new StringBuffer();
        output.append(getDescription()).append("(").append(getAction()).append(") for ").append(getSymbol()).append(": ").append(getPrice()).append(" ").append(getSize()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        output.append(" ").append(getSymbol()).append(" ").append(getExchange()).append(" at ").append(getTimestampAsDate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return output.toString();
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
    @ClassVersion("$Id$") //$NON-NLS-1$
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
     * {@link #compare(BidAskEvent, BidAskEvent)} is not the same as the result of
     * {@link EventBase#equals(Object)}.  This is OK, but it should be noted.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    public final static class BookPriceComparator
        implements Comparator<BidAskEvent>
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
        public int compare(BidAskEvent inO1,
                           BidAskEvent inO2)
        {
            // the first key is price, either ascending or descending
            int result = inO1.getPrice().compareTo(inO2.getPrice());
            if(result == 0) {
                // prices are equal
                // secondary sort should be on the timestamp
                result = new Long(inO1.getTimestamp()).compareTo(inO2.getTimestamp());
            }
            // invert the result to be returned if necessary to get a descending sort 
            return result * (mIsAscending ? 1 : -1);
        }
    }
}
