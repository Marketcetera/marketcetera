package org.marketcetera.event;

import static org.marketcetera.marketdata.Messages.CANNOT_RETRIEVE_STORED_EVENT_INFORMATION;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Base class for <code>IEventTranslator</code> implementations.
 * 
 * <p>This class provides utilities and a framework for <code>IEventTranslator</code>
 * instances.
 * 
 * <p>Implementors are <em>strongly</em> recommended to call {@link #updateEventFixMessageSnapshot(EventBase)}
 * before returning <code>EventBase</code> values from {@link IEventTranslator#translate(Object)}.
 * This guarantees that the most recent snapshot information is available on the <code>EventBase</code>
 * value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class AbstractEventTranslator
        implements IEventTranslator
{
    /**
     * this is an arbitrary value chosen to limit the memory used
     */
    private static final int MAX_BOOK_DEPTH = 10;
    /**
     * Updates the given <code>EventBase</code> value's FIX component.
     *
     * <p>The incoming event represents a single component of the Bid, Ask, Trade
     * 3-tuple.  The event will be updated with a FIX message that represents
     * the most recent version of each component for the given symbol (Best Bid-and-Offer and
     * Full Depth-of-Book) if available.
     *
     * @param inEvent an <code>EventBase</code> value
     * @throws NullPointerException if <code>inEvent</code> is null
     */
    protected final void updateEventFixMessageSnapshot(EventBase inEvent)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        // first, update the snapshot record for this symbol
        updateSnapshot(inEvent);
        // get the updated snapshot
        try {
            MessageTuple message = getSnapshot(inEvent);
            if(message != null) {
                if(inEvent instanceof BidAskEvent) {
                    ((BidAskEvent)inEvent).updateBBO(message.getBBO());
                    ((BidAskEvent)inEvent).updateFullBook(message.getFullBook());
                }
                if(inEvent instanceof SymbolExchangeEvent) {
                    ((SymbolExchangeEvent)inEvent).updateLatestTick(message.getLatestTick());
                }
            }
        } catch (FieldNotFound e) {
            CANNOT_RETRIEVE_STORED_EVENT_INFORMATION.warn(this,
                                                          inEvent);
        }
    }
    /**
     * tracks the most recent event aggregation by symbol 
     */
    private final Map<String,OrderBook> mSnapshots = new HashMap<String,OrderBook>();
    /**
     * Returns the most comprehensive snapshot available for the symbol represented by the
     * given event.
     *
     * @param inEvent an <code>EventBase</code> value
     * @return a <code>MessageTuple</code> value or null if <code>inEvent</code> does not contain any symbol information
     * @throws FieldNotFound 
     */
    private MessageTuple getSnapshot(EventBase inEvent) 
        throws FieldNotFound
    {
        if(!(inEvent instanceof SymbolExchangeEvent)) {
            return null;
        }
        SymbolExchangeEvent event = (SymbolExchangeEvent)inEvent;
        synchronized(mSnapshots) {
            OrderBook book = mSnapshots.get(event.getSymbol());
            if(book == null) {
                throw new NullPointerException();
            }
            return new MessageTuple(book.getBestBidAndOffer(),
                                    book.getDepthOfBook(),
                                    book.getLatestTick());
        }
    }
    /**
     * Updates the stored snapshot for the symbol represented by the given <code>EventBase</code> value.
     *
     * @param inEvent an <code>EventBase</code> value
     */
    private void updateSnapshot(EventBase inEvent)
    {
        // mSnapshots stores bid/ask/trade for each symbol
        // the passed Event represents one piece of the 3-tuple that is the information that
        //  is needed for the symbol.  update the stored FIX message with the piece of info
        //  the passed event represents
        synchronized(mSnapshots) {
            // pay attention only to this sub-family of events.  other events don't have a symbol and
            //  can't, therefore, be interesting
            if(inEvent instanceof SymbolExchangeEvent) {
                String symbol = ((SymbolExchangeEvent)inEvent).getSymbol();
                // look to see if we're already storing some info about this symbol
                OrderBook book = mSnapshots.get(symbol);
                if(book == null) {
                    // nothing yet, this is the first event we're getting on this symbol - create a new entry
                    book = new OrderBook(new MSymbol(symbol),
                                         MAX_BOOK_DEPTH);
                    mSnapshots.put(symbol, 
                                   book);
                }
                // record the new event in the order book
                book.processEvent((SymbolExchangeEvent)inEvent);
            } else {
                SLF4JLoggerProxy.debug(this, "Received an unknown event type: {}", inEvent); //$NON-NLS-1$
            }
        }
    }
    /**
     * Contains the best-book-and-offer, latest tick, and full-depth-of-book for a given symbol.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$") //$NON-NLS-1$
    private static class MessageTuple
    {
        /**
         * the best-bid-and-offer for a given symbol
         */
        private final Message mBBO;
        /**
         * the full depth-of-book for a given symbol
         */
        private final Message mFullBook;
        /**
         * the latest tick for a given symbol
         */
        private final Message mLatestTick;
        /**
         * Create a new MessageTuple instance.
         *
         * @param inBBO a <code>Message</code> value
         * @param inFullBook a <code>Message</code> value
         * @param inLatestTick a <code>Message</code> value
         */
        private MessageTuple(Message inBBO,
                             Message inFullBook,
                             Message inLatestTick)
        {
            mBBO = inBBO;
            mFullBook = inFullBook;
            mLatestTick = inLatestTick;
        }
        /**
         * Get the Best-Bid-and-Offer value.
         *
         * @return a <code>MessageTuple</code> value
         */
        private Message getBBO()
        {
            return mBBO;
        }
        /**
         * Get the full book value.
         *
         * @return a <code>MessageTuple</code> value
         */
        private Message getFullBook()
        {
            return mFullBook;
        }
        /**
         * Get the latest tick value.
         *
         * @return a <code>MessageTuple</code> value
         */
        private Message getLatestTick()
        {
            return mLatestTick;
        }
    }
}
