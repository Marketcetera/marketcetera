package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.CANNOT_CONVERT_EVENT_TO_ENTRY_TYPE;
import static org.marketcetera.marketdata.Messages.ORDER_BOOK_DEPTH_MUST_BE_POSITIVE;
import static org.marketcetera.marketdata.Messages.SYMBOL_DOES_NOT_MATCH_ORDER_BOOK_SYMBOL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidAskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.SymbolExchangeEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.BidAskEvent.Action;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Group;
import quickfix.Message;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.field.SendingTime;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

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
 * <p>To populate the <code>OrderBook</code>, add {@link BidAskEvent} objects
 * via {@link #processEvent(BidAskEvent)}.  It is important that the events so added
 * all be unique according to the event's natural ordering.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OrderBook
{
    /**
     * indicates the order book has no maximum depth
     */
    public final static int UNLIMITED_DEPTH = -1;
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
     * the latest trade tick
     */
    private TradeEvent mLatestTrade;
    /**
     * the latest bid tick
     */
    private BidEvent mLatestBid;
    /**
     * the latest ask tick
     */
    private AskEvent mLatestAsk;
    /**
     * the maximum depth of the order book 
     */
    private final int mMaxDepth;
    /**
     * Create a new OrderBook instance with unlimited depth.
     *
     * @param inSymbol a <code>MSymbol</code> instance
     */
    public OrderBook(MSymbol inSymbol)
    {
        this(inSymbol,
             UNLIMITED_DEPTH);
    }
    /**
     * Create a new OrderBook instance.
     *
     * @param inSymbol a <code>MSymbol</code> instance
     * @param inMaxDepth an <code>int</code> instance
     */
    public OrderBook(MSymbol inSymbol,
                     int inMaxDepth)
    {
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        if(inMaxDepth != UNLIMITED_DEPTH &&
           inMaxDepth <= 0) {
            throw new IllegalArgumentException(ORDER_BOOK_DEPTH_MUST_BE_POSITIVE.getText());
        }
        mSymbol = inSymbol;
        mAskBook = new BookCollection<AskEvent>(inMaxDepth);
        mBidBook = new BookCollection<BidEvent>(inMaxDepth);
        mMaxDepth = inMaxDepth;
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
     * Returns a Full Refresh Market Data Snapshot containing the full depth of
     * book such as is known at this time.
     *
     * @return a <code>Message</code> value
     */
    public final Message getDepthOfBook()
    {
        return constructMessage(MessageType.FULL_BOOK);
    }
    /**
     * Returns a Full Refresh Market Data Snapshot containing the best bid and offer on the
     * order book.
     * 
     * <p>If the order book does not contain any bids or offers, the corresponding group will not be present
     * in the returned snapshot.  The snapshot will not contain a trade group.
     *
     * @return a <code>Message</code> value
     */
    public final Message getBestBidAndOffer()
    {
        return constructMessage(MessageType.BBO);
    }
    /**
     * Returns a Full Refresh Market Data Snapshot containing the latest tick on the order book.
     *
     * <p>If the order book does not contain any bids, trades, or offers, the corresponding group will not be present
     * in the returned snapshot.
     * 
     * @return a <code>Message</code> value
     */
    public final Message getLatestTick()
    {
        return constructMessage(MessageType.LATEST_TICK);
    }
    /**
     * Gets the current state of the <code>Bid</code> book. 
     *
     * @return a <code>List&lt;BidEvent&gt;</code> value
     */
    public final List<BidEvent> getBidBook()
    {
        return mBidBook.getSortedView(BidAskEvent.BookPriceComparator.BidComparator);
    }
    /**
     * Gets the current state of the <code>Ask</code> book.
     *
     * @return a <code>List&lt;AskEvent&gt;</code> value
     */
    public final List<AskEvent> getAskBook()
    {
        return mAskBook.getSortedView(BidAskEvent.BookPriceComparator.AskComparator);
    }
    /**
     * Processes the given event for the order book.
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @throws IllegalArgumentException if the event's symbol does not match the book's symbol
     */
    public final void processEvent(SymbolExchangeEvent inEvent)
    {
        // make sure the event is valid before proceeding
        checkEvent(inEvent);
        SLF4JLoggerProxy.debug(this,
                               "Received {}\nBook starts at\n{}", //$NON-NLS-1$
                               inEvent,
                               this);
        // check to see if it's a bid or an ask, special processing is required, if so
        if(inEvent instanceof BidAskEvent) {
            BidAskEvent event = (BidAskEvent)inEvent;
            if(Action.ADD.equals(event.getAction())) {
                addEvent(event);
            } else if(Action.DELETE.equals(event.getAction())) {
                removeEvent(event);
            } else if(Action.CHANGE.equals(event.getAction())) {
                changeEvent(event);
            }
        } else {
            // it's neither a bid nor an ask, no special work is required
            addEvent(inEvent);
        }
        SLF4JLoggerProxy.debug(this,
                               "Book is now\n{}", //$NON-NLS-1$
                               this);
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
        Iterator<BidEvent> bidIterator = getBidBook().iterator();
        Iterator<AskEvent> askIterator = getAskBook().iterator();
        List<String> bids = new ArrayList<String>();
        List<String> asks = new ArrayList<String>();
        int maxSize = 10;
        while(true) {
            if(bidIterator.hasNext()) {
                BidEvent bid = bidIterator.next();
                StringBuilder entry = new StringBuilder();
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
        finalBook.append(getSymbol()).append("\n"); //$NON-NLS-1$
        finalBook.append("Latest Tick\n"); //$NON-NLS-1$
        finalBook.append(getLatestTrade()).append("\n"); //$NON-NLS-1$
        finalBook.append(getLatestBid()).append("\n"); //$NON-NLS-1$
        finalBook.append(getLatestAsk()).append("\n"); //$NON-NLS-1$
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
     * Constructs a Market Data Snapshot Full Refresh FIX message representing the
     * data in the order book.
     *
     * @param inIsFullBook a <code>boolean</code> value which, if true, returns the full depth of
     *  book, otherwise returns the best bid and offer
     * @return a <code>Message</code> value
     */
    private Message constructMessage(MessageType inMessageType)
    {
        // construct an empty message of the correct type
        Message snapshot = new MarketDataSnapshotFullRefresh();
        // set the symbol
        snapshot.setField(new Symbol(getSymbol().getFullSymbol()));
        // set the creation time in GMT
        snapshot.setField(new SendingTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime())); //$NON-NLS-1$
        // count the number of groups - there is a group for each ask/buy
        int groupCounter = 0;
        if(MessageType.LATEST_TICK.equals(inMessageType)) {
            groupCounter += addToGroup(getLatestAsk(),
                                       snapshot);
            groupCounter += addToGroup(getLatestBid(),
                                       snapshot);
            groupCounter += addToGroup(getLatestTrade(),
                                       snapshot);
        } else {
            // grab the ask side of the book and sort it appropriately
            List<AskEvent> asks = getAskBook();
            Iterator<AskEvent> askIterator = asks.iterator();
            // iterate over the asks, adding the first (ask book is sorted) or all depending on
            //  whether we want the full book or not
            while(askIterator.hasNext()) {
                groupCounter += addToGroup(askIterator.next(),
                                           snapshot);
                // for BBO, quit after the first ask
                if(MessageType.BBO.equals(inMessageType)) {
                    break;
                }
            }
            // grab the bid side of the book and sort it appropriately
            List<BidEvent> bids = getBidBook();
            Iterator<BidEvent> bidIterator = bids.iterator();
            // iterate over the bids, adding the first (bid book is also sorted) or all depending on
            //  whether we want the full book or not
            while(bidIterator.hasNext()) {
                groupCounter += addToGroup(bidIterator.next(),
                                           snapshot);
                // for BBO, quit after the first bid
                if(MessageType.BBO.equals(inMessageType)) {
                    break;
                }
            }
        }
        // set the total number of groups
        snapshot.setField(new NoMDEntries(groupCounter));
        return snapshot;
    }
    /**
     * Add a <code>Group</code> to the given snapshot corresponding to the
     * given <code>SymbolExchangeEvent</code>.
     *
     * @param inEvent a <code>SymbolExchangeEvent</code> value containing the event to add to the snapshot or null to add nothing
     * @param inSnapshot a <code>Message</code> value containing the snapshot to which to add the group
     * @return an <code>int</code> value indicating how many groups were added to the snapshot
     */
    private int addToGroup(SymbolExchangeEvent inEvent,
                           Message inSnapshot)
    {
        if(inEvent == null) {
            return 0;
        }
        Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
        group.setField(getEntryType(inEvent));
        group.setField(new MDMkt(inEvent.getExchange()));
        if(inEvent instanceof BidAskEvent) {
            BidAskEvent bidAsk = (BidAskEvent)inEvent;
            group.setField(new MDEntryPx(bidAsk.getPrice()));
            group.setField(new MDEntrySize(bidAsk.getSize()));
        } else if(inEvent instanceof TradeEvent) {
            TradeEvent tradeEvent = (TradeEvent)inEvent;
            group.setField(new MDEntryPx(tradeEvent.getPrice()));
            group.setField(new MDEntrySize(tradeEvent.getSize()));
        }
        inSnapshot.addGroup(group);
        return 1;
    }
    /**
     * Determines the <code>MDEntryType</code> that corresponds to the given <code>SymbolExchangeEvent</code>.
     *
     * @param inEvent a <code>SymboLExchangeEvent</code> value
     * @return a <code>MDEntryType</code> value
     * @throws IllegalArgumentException if the given event does not correspond to a <code>MDEntryType</code> value
     */
    private MDEntryType getEntryType(SymbolExchangeEvent inEvent)
    {
        if(inEvent instanceof AskEvent) {
            return new MDEntryType(MDEntryType.OFFER);
        }
        if(inEvent instanceof BidEvent) {
            return new MDEntryType(MDEntryType.BID);
        }
        if(inEvent instanceof TradeEvent) {
            return new MDEntryType(MDEntryType.TRADE);
        }
        throw new IllegalArgumentException(CANNOT_CONVERT_EVENT_TO_ENTRY_TYPE.getText(inEvent));
    }
    /**
     * Get the latestTrade value.
     *
     * @return a <code>OrderBook</code> value
     */
    private TradeEvent getLatestTrade()
    {
        return mLatestTrade;
    }
    /**
     * Get the latestBid value.
     *
     * @return a <code>OrderBook</code> value
     */
    private BidEvent getLatestBid()
    {
        return mLatestBid;
    }
    /**
     * Get the latestAsk value.
     *
     * @return a <code>OrderBook</code> value
     */
    private AskEvent getLatestAsk()
    {
        return mLatestAsk;
    }
    /**
     * Sets the latestTrade value.
     *
     * @param a <code>OrderBook</code> value
     */
    private void setLatestTrade(TradeEvent inLatestTrade)
    {
        mLatestTrade = inLatestTrade;
    }
    /**
     * Sets the latestBid value.
     *
     * @param a <code>OrderBook</code> value
     */
    private void setLatestBid(BidEvent inLatestBid)
    {
        mLatestBid = inLatestBid;
    }
    /**
     * Sets the latestAsk value.
     *
     * @param a <code>OrderBook</code> value
     */
    private void setLatestAsk(AskEvent inLatestAsk)
    {
        mLatestAsk = inLatestAsk;
    }
    /**
     * Returns a string padded on the right with the given character to the given size.
     *
     * @param inBase a <code>String</code> value containing the string to pad
     * @param inSize an <code>int</code> value containing the total number of characters to return
     * @param inPadChar a <code>char</code> value containing the character with which to pad, if necessary
     * @return a <code>String</code> value containing the passed string padded, if necessary, with the passed char to reach the passed size
     */
    private String pad(String inBase,
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
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     * @throws IllegalArgumentException if the event's symbol does not match the book's symbol
     */
    private void checkEvent(SymbolExchangeEvent inEvent)
    {
        if(!inEvent.getSymbol().equals(getSymbol().getFullSymbol())) {
            throw new IllegalArgumentException(SYMBOL_DOES_NOT_MATCH_ORDER_BOOK_SYMBOL.getText(inEvent.getSymbol(),
                                                                                               getSymbol().getFullSymbol()));
        }
    }
    /**
     * Adds the given event to the order book.
     * 
     * @param inEvent a <code>SymbolExchangeEvent</code> value
     */
    private void addEvent(SymbolExchangeEvent inEvent)
    {
        if(inEvent instanceof BidEvent) {
            mBidBook.add((BidEvent)inEvent);
            setLatestBid((BidEvent)inEvent);
        } else if(inEvent instanceof AskEvent) {
            mAskBook.add((AskEvent)inEvent);
            setLatestAsk((AskEvent)inEvent);
        } else if(inEvent instanceof TradeEvent) {
            setLatestTrade((TradeEvent)inEvent);
        }
    }
    /**
     * Updates the given event on the order book if it is already present.
     *
     * <p>If the given event does not exist in the order book, this method does nothing.
     * 
     * @param inEvent a <code>BidAskEvent</code> value
     */
    private void changeEvent(BidAskEvent inEvent)
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
    private void removeEvent(BidAskEvent inEvent)
    {
        if(inEvent instanceof BidEvent) {
            mBidBook.remove((BidEvent)inEvent);
        } else if(inEvent instanceof AskEvent) {
            mAskBook.remove((AskEvent)inEvent);
        }
    }
    /**
     * Describes the type of FIX message to produce.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private enum MessageType
    {
        BBO,
        FULL_BOOK,
        LATEST_TICK
    }
    /**
     * Stores the orders of one side of a book.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private static class BookCollection<E extends BidAskEvent>
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
        private synchronized List<E> getSortedView(Comparator<BidAskEvent> inComparator)
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
}
