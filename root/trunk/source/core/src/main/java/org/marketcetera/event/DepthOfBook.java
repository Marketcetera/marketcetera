package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * Represents the Depth-of-Book for a specific security.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class DepthOfBook
        extends AggregateEvent
{
    /**
     * Create a new DepthOfBook instance.
     *
     * @param inBids a <code>List&lt;BidEvent&gt;</code> value
     * @param inAsks a <code>List&lt;AskEvent&gt;</code> value
     * @param inTimestamp a <code>Date</code> value indicating when the event occurred
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for which the event occurred
     * @throws IllegalArgumentException if any of the events passed are for a symbol other than the given symbol
     * @throws NullPointerException if the lists or any entries in the lists are null
     */
    public DepthOfBook(List<BidEvent> inBids,
                       List<AskEvent> inAsks,
                       Date inTimestamp,
                       MSymbol inSymbol)
    {
        super(inTimestamp,
              inSymbol);
        validateList(inBids,
                     inSymbol);
        bids.addAll(inBids);
        validateList(inAsks,
                     inSymbol);
        asks.addAll(inAsks);
    }
    /**
     * Get the bids value.
     *
     * @return a <code>List<BidEvent></code> value
     */
    public List<BidEvent> getBids()
    {
        return Collections.unmodifiableList(bids);
    }
    /**
     * Get the asks value.
     *
     * @return a <code>List<AskEvent></code> value
     */
    public List<AskEvent> getAsks()
    {
        return Collections.unmodifiableList(asks);
    }
    /**
     * Determines if the given <code>DepthOfBook</code> is equivalent
     * to this object.
     *
     * <p>Equivalent is defined as containing the same number of bids
     * and asks in the same order with each having the same price and quantity
     * for the same symbol.
     *
     * @param other a <code>DepthOfBook</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equivalent(DepthOfBook other)
    {
        if(other == null) {
            return false;
        }
        if(this == other) {
            return true;
        }
        if(!getSymbol().equals(other.getSymbol())) {
            return false;
        }
        return compareList(asks,
                           other.asks) &&
               compareList(bids,
                           other.bids);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<EventBase> decompose()
    {
        List<EventBase> events = new ArrayList<EventBase>();
        events.addAll(bids);
        events.addAll(asks);
        return Collections.unmodifiableList(events);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("Depth of book for ").append(getSymbol()).append(" at ").append(DateUtils.dateToString(getTimestampAsDate())).append(SystemUtils.LINE_SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
        output.append(OrderBook.printBook(bids.iterator(),
                                          asks.iterator(),
                                          true));
        return output.toString();
    }
    /**
     * Validates the contents of the given list.
     *
     *
     * @param inQuotes a <code>List&lt;? extends QuoteEvent&gt;</code> value
     * @param inSymbol an <code>MSymbol</code> value
     * @throws NullPointerException if the list or any entry in the list is null
     * @throws IllegalArgumentException if an entry in the list is for a different symbol than the one given
     */
    private static void validateList(List<? extends QuoteEvent> inQuotes,
                                     MSymbol inSymbol)
    {
        for(QuoteEvent quote : inQuotes) {
            if(quote == null) {
                throw new NullPointerException();
            }
            if(!inSymbol.equals(quote.getSymbol())) {
                throw new IllegalArgumentException();
            }
        }
    }
    /**
     * Compares two lists of objects of a given subclass of {@link QuoteEvent} on the basis of their
     * price and size only. 
     *
     * @param inQuotes1 a <code>List&lt;T&gt;</code> value
     * @param inQuotes2 a <code>List&lt;T&gt;</code> value
     * @return a <code>boolean</code> value
     */
    private static <T extends QuoteEvent> boolean compareList(List<T> inQuotes1,
                                                              List<T> inQuotes2)
    {
        if(inQuotes1.size() != inQuotes2.size()) {
            return false;
        }
        int index = 0;
        for(QuoteEvent quote : inQuotes1) {
            QuoteEvent otherQuote = inQuotes2.get(index++);
            if(QuoteEvent.PriceAndSizeComparator.instance.compare(quote,
                                                                  otherQuote) != 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * the bid side of the book
     */
    private final List<BidEvent> bids = new ArrayList<BidEvent>();
    /**
     * the ask side of the book
     */
    private final List<AskEvent> asks = new ArrayList<AskEvent>();
    private static final long serialVersionUID = 1L;
}
