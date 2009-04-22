package org.marketcetera.marketdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBook;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBook;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Represents the order book for a given symbol.
 * 
 * <p>An <code>OrderBook</code> is a snapshot of open orders for a given
 * security.  The orders in an <code>OrderBook</code> may be from a single
 * or multiple exchanges.  <code>OrderBook</code> imposes no restrictions
 * on the source exchange allowing the object to represent the orders from
 * a specific exchange or an aggregation of orders from multiple exchanges.
 * 
 * <p>To populate the <code>OrderBook</code>, add {@link QuoteEvent} objects
 * via {@link #process(QuoteEvent)}.  It is important that the events so added
 * all be unique according to the event's natural ordering.
 * 
 * <p>{@link QuoteEvent} objects all have an <code>Action</code> attribute.
 * The <code>Action</code> attribute dictates whether the event is inserted
 * into the book, or changes or deletes an existing order.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public class OrderBook
    implements Messages
{
    /**
     * indicates the order book has no maximum depth
     */
    public final static int UNLIMITED_DEPTH = -1;
    /**
     * the order book depth if none is specified
     */
    public final static int DEFAULT_DEPTH = 10;
    /**
     * Create a new OrderBook instance a reasonable maximum depth.
     *
     * <p>The resulting <code>OrderBook</code> will have an
     * unlimited maximum depth.
     * 
     * @param inSymbol a <code>MSymbol</code> instance
     */
    public OrderBook(MSymbol inSymbol)
    {
        this(inSymbol,
             UNLIMITED_DEPTH);
    }
    /**
     * Checks the given depth to see if it is a valid maximum book depth.
     *
     * @param inMaximumDepth an <code>int</code> value
     */
    public static void validateMaximumBookDepth(int inMaximumDepth)
    {
        if(inMaximumDepth != UNLIMITED_DEPTH &&
           inMaximumDepth <= 0) {
            throw new IllegalArgumentException(ORDER_BOOK_DEPTH_MUST_BE_POSITIVE.getText());
        }
    }
    /**
     * Get the symbol value.
     *
     * @return a <code>OrderBook</code> value
     */
    public final MSymbol getSymbol()
    {
        return mSymbol;
    }
    /**
     * Get the maxDepth value.
     *
     * @return a <code>OrderBook</code> value
     */
    public int getMaxDepth()
    {
        return mMaxDepth;
    }
    /**
     * Gets the {@link TopOfBook} view of the order book.
     * 
     * @return a <code>TopOfBook</code> value
     */
    public final TopOfBook getTopOfBook()
    {
        List<BidEvent> bidBook = getBidBook();
        List<AskEvent> askBook = getAskBook();
        return new TopOfBook(bidBook.isEmpty() ? null : bidBook.get(0),
                             askBook.isEmpty() ? null : askBook.get(0),
                             new Date(),
                             getSymbol());
    }
    /**
     * Returns the {@link DepthOfBook} view of the order book. 
     *
     * @return a <code>DepthOfBook</code> value
     */
    public final DepthOfBook getDepthOfBook()
    {
        return new DepthOfBook(getBidBook(),
                               getAskBook(),
                               new Date(),
                               getSymbol());
    }
    /**
     * Gets the current state of the <code>Bid</code> book. 
     *
     * @return a <code>List&lt;BidEvent&gt;</code> value
     */
    public final List<BidEvent> getBidBook()
    {
        return mBidBook.getSortedView(QuoteEvent.BookPriceComparator.BidComparator);
    }
    /**
     * Gets the current state of the <code>Ask</code> book.
     *
     * @return a <code>List&lt;AskEvent&gt;</code> value
     */
    public final List<AskEvent> getAskBook()
    {
        return mAskBook.getSortedView(QuoteEvent.BookPriceComparator.AskComparator);
    }
    /**
     * Processes the given event for the order book.
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value containing the event displaced by the change or null
     * @throws IllegalArgumentException if the event's symbol does not match the book's symbol
     */
    public final QuoteEvent process(QuoteEvent inEvent)
    {
        // make sure the event is valid before proceeding
        checkEvent(inEvent);
        SLF4JLoggerProxy.debug(this,
                               "Received {}\nBook starts at\n{}", //$NON-NLS-1$
                               inEvent,
                               this);
        QuoteEvent eventToReturn = null;
        switch(inEvent.getAction()) {
            case ADD :
                eventToReturn = addEvent(inEvent);
                break;
            case DELETE :
                removeEvent(inEvent);
                break;
            case CHANGE :
                changeEvent(inEvent);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        SLF4JLoggerProxy.debug(this,
                               "Book is now\n{}", //$NON-NLS-1$
                               this);
        return eventToReturn;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mSymbol == null) ? 0 : mSymbol.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OrderBook other = (OrderBook) obj;
        if (mSymbol == null) {
            if (other.mSymbol != null)
                return false;
        } else if (!mSymbol.equals(other.mSymbol))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder book = new StringBuilder();
        book.append(getSymbol()).append(SystemUtils.LINE_SEPARATOR);
        book.append(printBook(getBidBook().iterator(),
                              getAskBook().iterator(),
                              false));
        return book.toString();
    }
    /**
     * Creates a human-readable representation of an order book as defined
     * by the given {@link Iterator} values.
     *
     * @param bidIterator an <code>Iterator&lt;BidEvent&gt;</code> value representing the bids to display
     * @param askIterator an <code>Iterator&lt;AskEvent&gt;</code> value representing the asks to display
     * @param inShowExchange a <code>boolean</code> value indicating whether to display the exchange associated with each bid and ask
     * @return a <code>String</code> containing the human-readable representation of the order book implied by the given <code>Iterator</code> objects
     */
    public static String printBook(Iterator<BidEvent> bidIterator,
                                   Iterator<AskEvent> askIterator,
                                   boolean inShowExchange)
    {
        List<String> bids = new ArrayList<String>();
        List<String> asks = new ArrayList<String>();
        int maxSize = 10;
        while(true) {
            if(bidIterator.hasNext()) {
                BidEvent bid = bidIterator.next();
                StringBuilder entry = new StringBuilder();
                if(inShowExchange) {
                    entry.append(" ").append(bid.getExchange()); //$NON-NLS-1$
                }
                entry.append(" ").append(bid.getSize()).append(" ").append(bid.getPrice()).append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                String line = entry.toString();
                maxSize = Math.max(maxSize,
                                   line.length());
                bids.add(line);
            }
            if(askIterator.hasNext()) {
                AskEvent ask = askIterator.next();
                StringBuilder entry = new StringBuilder();
                entry.append(ask.getPrice()).append(" ").append(ask.getSize()); //$NON-NLS-1$
                if(inShowExchange) {
                    entry.append(" ").append(ask.getExchange()); //$NON-NLS-1$
                }
                maxSize = Math.max(maxSize,
                                   entry.toString().length());
                String line = entry.toString();
                maxSize = Math.max(maxSize,
                                   line.length());
                asks.add(line);
            }
            if(bidIterator.hasNext() ||
               askIterator.hasNext()) {
                continue;
            } else {
                break;
            }
        }
        String bidHeader = "bid"; //$NON-NLS-1$
        String askHeader = "ask"; //$NON-NLS-1$
        StringBuilder finalBook = new StringBuilder();
        finalBook.append("Order Book\n"); //$NON-NLS-1$
        finalBook.append(bidHeader);
        for(int i=0;i<maxSize-bidHeader.length();i++) {
            finalBook.append(" "); //$NON-NLS-1$
        }
        finalBook.append("| ").append(askHeader).append("\n"); //$NON-NLS-1$ //$NON-NLS-2$
        finalBook.append(pad("", //$NON-NLS-1$
                             maxSize,
                             '-'));
        finalBook.append("+"); //$NON-NLS-1$
        finalBook.append(pad("", //$NON-NLS-1$
                             maxSize,
                             '-'));
        finalBook.append("\n"); //$NON-NLS-1$
        Iterator<String> bidStringIterator = bids.iterator();
        Iterator<String> askStringIterator = asks.iterator();
        while(true) {
            if(bidStringIterator.hasNext()) {
                String bidString = bidStringIterator.next();
                finalBook.append(pad(bidString,
                                     maxSize,
                                     ' '));
            } else {
                finalBook.append(pad("", //$NON-NLS-1$
                                     maxSize,
                                     ' '));
            }
            finalBook.append("|"); //$NON-NLS-1$
            if(askStringIterator.hasNext()) {
                String askString = askStringIterator.next();
                finalBook.append(" ").append(askString); //$NON-NLS-1$
            }
            finalBook.append("\n"); //$NON-NLS-1$
            if(bidStringIterator.hasNext() ||
                    askStringIterator.hasNext()) {
                continue;
            } else {
                break;
            }
        }

        return finalBook.toString();
    }
    /**
     * Create a new OrderBook instance.
     * 
     * <p>An <code>OrderBook</code> with a maximum depth will 
     * never grow larger than the specified depth. 
     *
     * @param inSymbol a <code>MSymbol</code> instance
     * @param inMaxDepth an <code>int</code> instance
     * @throws IllegalArgumentException if the given depth is invalid
     */
    OrderBook(MSymbol inSymbol,
              int inMaxDepth)
    {
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        validateMaximumBookDepth(inMaxDepth);
        mSymbol = inSymbol;
        mAskBook = new BookCollection<AskEvent>(inMaxDepth);
        mBidBook = new BookCollection<BidEvent>(inMaxDepth);
        mMaxDepth = inMaxDepth;
    }
    /**
     * Returns a string padded on the right with the given character to the given size.
     *
     * @param inBase a <code>String</code> value containing the string to pad
     * @param inSize an <code>int</code> value containing the total number of characters to return
     * @param inPadChar a <code>char</code> value containing the character with which to pad, if necessary
     * @return a <code>String</code> value containing the passed string padded, if necessary, with the passed char to reach the passed size
     */
    private static String pad(String inBase,
                              int inSize, 
                              char inPadChar)
    {
        StringBuilder output = new StringBuilder(inBase);
        for(int i=output.length();i<inSize;i++) {
            output.append(inPadChar);
        }
        return output.toString();
    }
    /**
     * Checks the given event to make sure it is appropriate to add to the <code>OrderBook</code>. 
     *
     * @param inEvent a <code>QuoteEvent</code> value
     * @throws IllegalArgumentException if the event's symbol does not match the book's symbol
     */
    private void checkEvent(QuoteEvent inEvent)
    {
        if(!inEvent.getSymbol().equals(getSymbol())) {
            throw new IllegalArgumentException(SYMBOL_DOES_NOT_MATCH_ORDER_BOOK_SYMBOL.getText(inEvent.getSymbol(),
                                                                                               getSymbol()));
        }
    }
    /**
     * Adds the given event to the order book.
     * 
     * @param inEvent a <code>QuoteEvent</code> value
     * @return a <code>QuoteEvent</code> value containing the event displaced by the new event or null
     */
    private QuoteEvent addEvent(QuoteEvent inEvent)
    {
        if(inEvent instanceof BidEvent) {
            return mBidBook.add((BidEvent)inEvent);
        } else if(inEvent instanceof AskEvent) {
            return mAskBook.add((AskEvent)inEvent);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Updates the given event on the order book if it is already present.
     *
     * <p>If the given event does not exist in the order book, this method does nothing.
     * 
     * @param inEvent a <code>BidAskEvent</code> value
     */
    private void changeEvent(QuoteEvent inEvent)
    {
        if(inEvent instanceof BidEvent) {
            mBidBook.change((BidEvent)inEvent);
        } else if(inEvent instanceof AskEvent) {
            mAskBook.change((AskEvent)inEvent);
        }
    }
    /**
     * Removes the given event from the order book.
     * 
     * <p>If the given event does not exist in the order book, this method does nothing.
     * 
     * @param inEvent a <code>BidAskEvent</code> value
     */
    private void removeEvent(QuoteEvent inEvent)
    {
        if(inEvent instanceof BidEvent) {
            mBidBook.remove((BidEvent)inEvent);
        } else if(inEvent instanceof AskEvent) {
            mAskBook.remove((AskEvent)inEvent);
        }
    }
    /**
     * Stores the orders of one side of a book.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private static class BookCollection<E extends QuoteEvent>
    {
        /**
         * the set of events that make of the book
         */
        private final Set<E> mBook;
        /**
         * if a max book depth is set, this collection tracks the order events were added to the book - note that this collection must not
         * be used to retrieve values for events - use {@link #mBook} instead
         */
        private final Deque<E> mBookOrder;
        /**
         * the maximum depth of the book.  if set to {@link OrderBook#UNLIMITED_DEPTH}, the book has no maximum depth. 
         */
        private final int mMaxDepth;
        /**
         * Create a new BookCollection instance.
         *
         * @param inMaxDepth an <code>int</code> value indicating the maximum depth of the book or {@link OrderBook#UNLIMITED_DEPTH} if the book is to have no depth limit 
         */
        private BookCollection(int inMaxDepth)
        {
            mMaxDepth = inMaxDepth;
            if(inMaxDepth == UNLIMITED_DEPTH) {
                mBook = new HashSet<E>();
                mBookOrder = null;
            } else {
                // an order book will generally fill to its max depth, so pre-allocate the memory, if a max depth is set
                mBook = new HashSet<E>(mMaxDepth);
                mBookOrder = new LinkedList<E>();
            }
        }
        /**
         * Adds the given event to the book.
         *
         * @param inEvent an <code>E</code> value to add to the book
         * @return an <code>E</code> value if the incoming event displaced an existing event because the book is already at its maximum depth or null if no event was displaced
         */
        private synchronized E add(E inEvent)
        {
            // holds the value to return, if any
            E oldestEvent = null;
            // if we are tracking depth, extra work needs to be done
            if(mMaxDepth != UNLIMITED_DEPTH) {
                // add the new event to the front of the deque tracking event age
                mBookOrder.addFirst(inEvent);
                // check to see if the max depth will be exceeded
                if(mBook.size() >= mMaxDepth) {
                    // remove the event on the back of the deque (oldest event)
                    oldestEvent = mBookOrder.removeLast();
                    // remove the same event from the actual book
                    mBook.remove(oldestEvent);
                }
            }
            // add the new event to the book
            mBook.add(inEvent);
            // return the displaced event, if any
            return oldestEvent;
        }
        /**
         * Updates the given event, if present.
         * 
         * <p>If the event is not present in the order book, this method does nothing.  Executing
         * this method does not change the age of the order on the book.
         *
         * @param inEvent an <code>E</code> value
         */
        private synchronized void change(E inEvent)
        {
            if(mBook.contains(inEvent)) {
                mBook.remove(inEvent);
                mBook.add(inEvent);
            }
        }
        /**
         * Removes the given event from the book, if present.
         *
         * <p>If the event is not present in the order book, this method does nothing.
         *
         * @param inEvent an <code>E</code> value
         */
        private synchronized void remove(E inEvent)
        {
            // remove the event from the book.  this operation is O(1).
            mBook.remove(inEvent);
            // if the book has a maximum depth, 
            if(mMaxDepth != UNLIMITED_DEPTH) {
                // this operation is O(n) which is clearly not ideal, but you'd get the same behavior in a LinkedHashMap which is what this class essentially
                //  emulates.  further, if an order book has a depth set, by definition this suggests a maximum value of n which is small enough that this
                //  operation is not likely to be overly punitive.  so there.
                mBookOrder.remove(inEvent);
            }
        }
        /**
         * Returns a view of the book sorted by the given comparator.
         *
         * @param inComparator a <code>Comparator&lt;BidAskEvent&gt;</code> value to sort the list
         * @return a <code>List&lt;E&gt;</code> value
         */
        private synchronized List<E> getSortedView(Comparator<QuoteEvent> inComparator)
        {
            List<E> events = new ArrayList<E>();
            for(E event : mBook) {
                events.add(event);
            }
            // before you ask, the reason why the book has to be re-sorted in a list with a special comparator as opposed to
            //  using a TreeSet with the comparator baked in to store the book in the first place, is that the book needs
            //  to use the messageId on the event as the key in order to support CRUD properly.  the results of the book, though
            //  are displayed sorted in a user-sensible order.
            Collections.sort(events,
                             inComparator);
            return Collections.unmodifiableList(events);
        }
    }
    /**
     * the symbol for this book
     */
    private final MSymbol mSymbol;
    /**
     * the ask side of the book
     */
    private final BookCollection<AskEvent> mAskBook;
    /**
     * the bid side of the book
     */
    private final BookCollection<BidEvent> mBidBook;
    /**
     * the maximum depth of the order book 
     */
    private final int mMaxDepth;
}
