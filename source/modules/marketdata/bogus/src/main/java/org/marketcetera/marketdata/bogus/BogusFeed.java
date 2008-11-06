package org.marketcetera.marketdata.bogus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.FeedException;
import org.marketcetera.marketdata.MarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTokenSpec;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Group;
import quickfix.field.CFICode;
import quickfix.field.MaturityDate;
import quickfix.field.MaturityMonthYear;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityRequestResult;
import quickfix.field.SecurityResponseID;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;
import quickfix.fix44.DerivativeSecurityList;

/* $License$ */

/**
 * Sample implementation of {@link MarketDataFeed}.
 *
 * <p>This implementation generates random market data for each
 * symbol for which a market data request is received.  Data is returned
 * from the feed via {@link EventBase} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $") //$NON-NLS-1$
public class BogusFeed 
    extends AbstractMarketDataFeed<BogusFeedToken,
                                   BogusFeedCredentials,
                                   BogusFeedMessageTranslator,
                                   BogusFeedEventTranslator,
                                   BogusMessage,
                                   BogusFeed> 
{
    /**
     * indicates if the feed has been logged in to
     */
    private boolean mLoggedIn;
    /**
     * value used to add to or subtract from prices
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01"); //$NON-NLS-1$
    /**
     * bogus market exchange code
     */
    private static final String BGUS_MARKET = "BGUS"; //$NON-NLS-1$
    /**
     * mechanism which manages the threads that create the market data
     */
    private ScheduledThreadPoolExecutor mExecutor;
    /**
     * collection of all symbols currently in use by active requests
     */
    private final Map<MSymbol,OrderBookWrapper> mSymbolValues = new HashMap<MSymbol,OrderBookWrapper>();
    /**
     * indicates if the previous tick processing has completed or not
     */
    private final AtomicBoolean mReadyForTick = new AtomicBoolean(true);
    /**
     * random generator used to manipulate prices
     */
    private static final Random sRandom = new Random(System.nanoTime());
    /**
     * static instance of <code>BogusFeed</code>
     */
    private static BogusFeed sInstance;
    /**
     * Returns an instance of <code>BogusFeed</code>.
     *
     * @param inProviderName a <code>String</code> value
     * @return a <code>BogusFeed</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
    public synchronized static BogusFeed getInstance(String inProviderName) 
        throws NoMoreIDsException
    {
        if(sInstance != null) {
            return sInstance;
        }
        sInstance = new BogusFeed(inProviderName);
        return sInstance;
    }
    /**
     * Create a new BogusFeed instance.
     *
     * @param inProviderName a <code>String</code> value
     * @throws NoMoreIDsException if a unique identifier could not be generated to
     *   be assigned
     */
	private BogusFeed(String inProviderName) 
		throws NoMoreIDsException 
	{
		super(FeedType.SIMULATED,
              inProviderName);
        setLoggedIn(false);
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#start()
     */
    @Override
	public void start() {
        if(getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        mReadyForTick.set(true);
        // the executor causes the tick controller, sendQuotes, to be executed once a second or so
        mExecutor = new ScheduledThreadPoolExecutor(1);
        mExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					executeTick();
				} catch (Throwable t){
					t.printStackTrace();
				}
			}
        }, 0, 1, TimeUnit.SECONDS);
        super.start();
	}
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#stop()
     */
    @Override
    public void stop() {
        if(!getFeedStatus().isRunning()) {
            throw new IllegalStateException();
        }
        mExecutor.shutdownNow();
        super.stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doCancel(java.lang.String)
     */
    @Override
    protected final synchronized void doCancel(String inHandle)
    {
        // cancel the request, which removes it from the list of active requests
        Request r = Request.cancelRequest(inHandle);
        SLF4JLoggerProxy.debug(BogusFeed.class,
                               "Canceling {}", //$NON-NLS-1$
                               r);
        // now, if there are no more requests for this symbol, remove it from the symbol table to free up the memory
        if(r != null) {
            MSymbol symbol = r.getSymbol();
            OrderBookWrapper book = mSymbolValues.get(symbol);
            if(book != null) {
                int referenceCount = book.decrementReferenceCount();
                SLF4JLoggerProxy.debug(BogusFeed.class,
                                       "Reference count for symbol {} is now {}", //$NON-NLS-1$
                                       symbol,
                                       referenceCount);
                if(referenceCount <= 0) {
                    SLF4JLoggerProxy.debug(BogusFeed.class,
                                           "Removing order book for symbol {}", //$NON-NLS-1$
                                           symbol);
                    mSymbolValues.remove(symbol);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLevelOneMarketDataRequest(java.lang.Object)
     */
    @Override
    protected final synchronized List<String> doMarketDataRequest(BogusMessage inData)
        throws FeedException
    {
        try {
            return Request.submit(inData,
                                  this);
        } catch (Throwable t) {
            throw new FeedException(t);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doDerivativeSecurityListRequest(java.lang.Object)
     */
    @Override
    protected final synchronized List<String> doDerivativeSecurityListRequest(BogusMessage inData)
            throws FeedException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doSecurityListRequest(java.lang.Object)
     */
    @Override
    protected final synchronized List<String> doSecurityListRequest(BogusMessage inData)
            throws FeedException
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogin(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean doLogin(BogusFeedCredentials inCredentials)
    {
        setLoggedIn(true);
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#doLogout()
     */
    @Override
    protected final void doLogout()
    {
        setLoggedIn(false);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#generateToken(quickfix.Message)
     */
    @Override
    protected final BogusFeedToken generateToken(MarketDataFeedTokenSpec inTokenSpec)
            throws FeedException
    {
        return BogusFeedToken.getToken(inTokenSpec,
                                       this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getEventTranslator()
     */
    @Override
    protected final BogusFeedEventTranslator getEventTranslator()
    {
        return BogusFeedEventTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getMessageTranslator()
     */
    @Override
    protected final BogusFeedMessageTranslator getMessageTranslator()
    {
        return BogusFeedMessageTranslator.getInstance();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#isLoggedIn(org.marketcetera.marketdata.AbstractMarketDataFeedCredentials)
     */
    @Override
    protected final boolean isLoggedIn()
    {
        return mLoggedIn;
    }
    /**
     * Adds the given symbol to the feed's active symbol register.
     *
     * @param inSymbol a <code>MSymbol</code> value
     */
    private void addRequestForSymbol(MSymbol inSymbol)
    {
        OrderBookWrapper book = mSymbolValues.get(inSymbol);
        if(book == null) {
            mSymbolValues.put(inSymbol,
                              new OrderBookWrapper(inSymbol));
        } else {
            book.incrementReferenceCount();
        }
    }
    /**
     * Executes one round of processing for all symbols and requests.
     */
    private synchronized void executeTick() 
    {
        // if the previous tick hasn't completed yet, skip this tick and wait for the next one
        if(mReadyForTick.get()) {
            // the previous tick has completed, so we can begin this one
            long startTime = System.currentTimeMillis();
            try {
                // we are committed to completing this tick, but don't let another one start until we're done
                mReadyForTick.set(false);
                // update order books for each symbol
                Map<MSymbol,List<EventBase>> requestsBySymbol = new HashMap<MSymbol,List<EventBase>>();
                for(OrderBookWrapper book : mSymbolValues.values()) {
                    book.adjustPrice();
                    requestsBySymbol.put(book.getBook().getSymbol(),
                                         book.getEvents());
                }
                // send updates for each request
                for(Request request : Request.getActiveRequests()) {
                    sendQuote(request,
                              requestsBySymbol.get(request.getSymbol()));
                }
                requestsBySymbol.clear();
            } finally {
                // indicate that we're ready for the next tick
                mReadyForTick.set(true);
                SLF4JLoggerProxy.debug(BogusFeed.class,
                                       "Tick completed after {} ms", //$NON-NLS-1$
                                       System.currentTimeMillis() - startTime);
            }
        } else {
            // the previous tick has not yet completed, skip this one
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "Tick skipped at {}", //$NON-NLS-1$
                                   Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime()); //$NON-NLS-1$
        }
	}
    /**
     * Sends updates to subscribers for the given query.
     *
     * @param inRequest a <code>Request</code> value containing a query for which to issue updates
     * @param inEvents a <code>List&lt;EventBase&gt;</code> value containing the events to submit for the given handle
     */
	private void sendQuote(Request inRequest,
	                       List<EventBase> inEvents) 
    {
	    // each request refers to a single symbol - submit the queued events for that symbol
	    for(EventBase event : inEvents) {
	        dataReceived(inRequest.getHandle(),
	                     event);
	    }
	}
    /**
     * Sets the loggedIn value.
     *
     * @param a <code>BogusFeed</code> value
     */
    private void setLoggedIn(boolean inLoggedIn)
    {
        mLoggedIn = inLoggedIn;
    }
    /**
     * Creates a sample security list.
     *
     *<p>This method is a relic of the original implementation of <code>BogusFeed</code> and is not used
     *for now.
     *
     * @param symbol
     * @param callSuffixes
     * @param putSuffixes
     * @param strikePrices
     * @return
     */
    @SuppressWarnings("unused") //$NON-NLS-1$
    private static DerivativeSecurityList createDummySecurityList(String symbol, 
                                                                  String[] callSuffixes, 
                                                                  String [] putSuffixes, 
                                                                  BigDecimal[] strikePrices) 
    {
        SecurityRequestResult resultCode = new SecurityRequestResult(SecurityRequestResult.VALID_REQUEST);
        DerivativeSecurityList responseMessage = new DerivativeSecurityList();
        responseMessage.setField(new SecurityReqID("bob")); //$NON-NLS-1$
        responseMessage.setField(new SecurityResponseID("123")); //$NON-NLS-1$

        responseMessage.setField(new UnderlyingSymbol(symbol));
        for (int i = 0; i < callSuffixes.length; i++) {
            MSymbol putSymbol = new MSymbol(symbol+"+"+putSuffixes[i]); //$NON-NLS-1$
            // put first
            Group optionGroup = new DerivativeSecurityList.NoRelatedSym();
            optionGroup.setField(new Symbol(putSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OPASPS")); //$NON-NLS-1$
            optionGroup.setField(new MaturityMonthYear("200801")); //$NON-NLS-1$
            optionGroup.setField(new MaturityDate("20080122")); //$NON-NLS-1$
            responseMessage.addGroup(optionGroup);

            MSymbol callSymbol = new MSymbol(symbol + "+" + callSuffixes[i]); //$NON-NLS-1$
            // now call
            optionGroup.setField(new Symbol(callSymbol.toString()));
            optionGroup.setField(new StrikePrice(strikePrices[i]));
            optionGroup.setField(new CFICode("OCASPS")); //$NON-NLS-1$
            optionGroup.setField(new MaturityMonthYear("200801")); //$NON-NLS-1$
            optionGroup.setField(new MaturityDate("20080122")); //$NON-NLS-1$
            responseMessage.addGroup(optionGroup);

        }
        responseMessage.setField(resultCode);
        return responseMessage;
    }
    /**
     * Matches bids and offers to produce trades.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $
     * @since 0.6.0
     */
    @ClassVersion("$Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $") //$NON-NLS-1$
    private static class OrderBookSettler
    {
        /**
         * to maintain performance and limit memory use, limit the depth of the order book
         */
        private static final int MAX_ORDERS = 10;
        /**
         * Removes oldest orders as necessary to maintain the proper book depth.
         * 
         * <p>If the depth of the book on either the <code>ask</code> or <code>bid</code> side
         * exceeds the given depth, the oldest orders on the appropriate side will be pruned
         * until the depth equals the given max depth.  Note that the age of the order is the only
         * consideration; its price and size are not considered.
         *
         * @param inBook an <code>OrderBook</code> value on which to operate
         * @param inMaxDepth an <code>int</code> value indicating the book depth to which to prune
         * @return a <code>List&lt;EventBase&gt;</code> value containing the events created, if any, to prune the order book
         */
        private static List<EventBase> expireOrders(OrderBook inBook, 
                                                    int inMaxDepth)
        {
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "Pruning oldest orders from the order book if necessary"); //$NON-NLS-1$
            List<EventBase> eventsToReturn = new ArrayList<EventBase>();
            // prune each side of the book of all except the youngest inMaxDepth entries
            // retrieve the bids in the bid book
            List<BidEvent> bids = new ArrayList<BidEvent>(inBook.getBidBook());
            // check to see if there are more bids in the bid book than the max allowed
            if(bids.size() > inMaxDepth) {
                // whoops, too many bids, have to prune
                SLF4JLoggerProxy.debug(BogusFeed.class,
                                       "Pruning {} bid(s)...", //$NON-NLS-1$
                                       bids.size() - inMaxDepth);
                // sort the bids from oldest to newest
                Collections.sort(bids,
                                 EventBase.BookAgeComparator.OldestToNewestComparator);
                // trim bids until the book is the appropriate depth
                while(bids.size() > inMaxDepth) {
                    // create a delete event for the bid to be deleted
                    BidEvent bidDelete = BidEvent.deleteEvent(bids.get(0));
                    SLF4JLoggerProxy.debug(BogusFeed.class,
                                           "Executing {}", //$NON-NLS-1$
                                           bidDelete);
                    // execute the event, causing the bid to be removed from the book
                    inBook.processEvent(bidDelete);
                    eventsToReturn.add(bidDelete);
                    // remove the bid from our snapshot list of bids
                    bids.remove(0);
                }
            }
            // the bid side now contains at most inMaxDepth orders
            List<AskEvent> asks = new ArrayList<AskEvent>(inBook.getAskBook());
            // retrieve the asks in the ask book
            if(asks.size() > inMaxDepth) {
                // there are too many asks
                SLF4JLoggerProxy.debug(BogusFeed.class,
                                       "Pruning {} ask(s)...", //$NON-NLS-1$
                                       asks.size() - inMaxDepth);
                // sort the asks from oldest to newest
                Collections.sort(asks,
                                 EventBase.BookAgeComparator.OldestToNewestComparator);
                // trim the asks
                while(asks.size() > inMaxDepth) {
                    // create a delete for the oldest ask
                    AskEvent askDelete = AskEvent.deleteEvent(asks.get(0));
                    SLF4JLoggerProxy.debug(BogusFeed.class,
                                           "Executing {}", //$NON-NLS-1$
                                           askDelete);
                    // execute the delete on the book
                    inBook.processEvent(askDelete);
                    eventsToReturn.add(askDelete);
                    // remove the ask from our snapshot list
                    asks.remove(0);
                }
            }
            // both sides of the book are now in compliance with the max depth
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "Done pruning oldest orders"); //$NON-NLS-1$
            return eventsToReturn;
        }
        /**
         * Examines the given <code>OrderBook</code>, matching bids and asks
         * until all the bids are either filled or there are no matching asks.
         *
         * <p>Each bid is considered in turn, from the highest price to the lowest.
         * For each bid, the asks are examined in order from lowest to highest.
         * If the bid price is greater than or equal to the ask price, a trade
         * is created for the minimum value of the set of the bid size and ask
         * size.  Both the bid and the ask are adjusted as appropriate.  If the
         * bid is fully filled, it is removed from the book, otherwise, the ask
         * is removed and the next ask is considered.
         * 
         * <p>Before examining the book for trades, the <code>OrderBook</code> is
         * pruned to the depth set by {@link #MAX_ORDERS}.
         * 
         * <p>This method requires exclusive access to the <code>OrderBook</code> but
         * does not perform any synchronization explicitly.  It is the caller's
         * responsibility to guarantee exclusive access to the <code>OrderBook</code>.
         * 
         * @param inBook an <code>OrderBook</code> value
         * @return a <code>List&lt;EventBase&gt;</code> value containing the events created, if any, to settle the book
         */
        private static List<EventBase> settleBook(OrderBook inBook)
        {
            SLF4JLoggerProxy.debug(BogusFeed.class,
                                   "OrderBook starts at\n{}", //$NON-NLS-1$
                                   inBook);
            List<EventBase> eventsToReturn = new ArrayList<EventBase>();
            try {
                // prune the book to the maximum depth
                eventsToReturn.addAll(expireOrders(inBook, 
                                                   MAX_ORDERS));
                // this is the list of bids over which to operate - note this is a static list, it does
                //  not reflect the ongoing changes to the order book
                List<BidEvent> bids = new ArrayList<BidEvent>(inBook.getBidBook());
                // this is the time that we're going to use for all the trades
                long tradeTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime(); //$NON-NLS-1$
                // iterate over the bid list
                for(BidEvent bid : bids) {
                    SLF4JLoggerProxy.debug(BogusFeed.class,
                                           "OrderBookSettler looking for matches for {}", //$NON-NLS-1$
                                           bid);
                    // search for the first ask that matches the bid
                    BigDecimal bidPrice = bid.getPrice();
                    BigDecimal bidSize = bid.getSize();
                    // grab the list of asks, this, too, is a static list and is refreshed each bid iteration
                    List<AskEvent> asks = new ArrayList<AskEvent>(inBook.getAskBook());
                    // iterate over the list of asks looking for a match
                    for(AskEvent ask : asks) {
                        SLF4JLoggerProxy.debug(BogusFeed.class,
                                               "Bid has {} left to fill", //$NON-NLS-1$
                                               bidSize);
                        // check to see if the bid is fully filled before continuing
                        if(bidSize.compareTo(BigDecimal.ZERO) != 1) {
                            // bid is fully filled
                            SLF4JLoggerProxy.debug(BogusFeed.class,
                                                   "{} fully filled", //$NON-NLS-1$
                                                   bid);
                            break; // out of the ask iteration loop
                        }
                        // bid is not fully filled
                        SLF4JLoggerProxy.debug(BogusFeed.class,
                                               "Examining {}", //$NON-NLS-1$
                                               ask);
                        BigDecimal askPrice = ask.getPrice();
                        // if the buyer is willing to pay at least as much as the seller will take (bid >= ask)
                        if(bidPrice.compareTo(askPrice) != -1) {
                            // hooray, we have a transaction
                            BigDecimal askSize = ask.getSize();
                            // these values are important - they are used to create the trade and to adjust the bid and the ask
                            // the price is the lower of what the buyer is willing to pay and what the seller will take
                            BigDecimal tradePrice = bidPrice.min(askPrice);
                            // the size is the lower of what the buyer wants and what the seller is willing to sell
                            BigDecimal tradeSize = bidSize.min(askSize);
                            SLF4JLoggerProxy.debug(BogusFeed.class,
                                                   "Trade is {} at {}", //$NON-NLS-1$
                                                   tradeSize.toPlainString(),
                                                   tradePrice.toPlainString());
                            // create the new trade
                            TradeEvent trade = new TradeEvent(System.nanoTime(),
                                                              tradeTime,
                                                              bid.getSymbol(),
                                                              bid.getExchange(),
                                                              tradePrice,
                                                              tradeSize);
                            // these events are used to modify the orders in the book
                            BidEvent bidCorrection;
                            AskEvent askCorrection;
                            if(tradeSize.compareTo(bidSize) == -1) {
                                // trade is smaller than the bid, this is a partial fill
                                bidCorrection = BidEvent.changeEvent(bid,
                                                                     bidSize.subtract(tradeSize));
                                askCorrection = AskEvent.deleteEvent(ask); 
                            } else {
                                // trade is equal to the bid, this is a full fill
                                bidCorrection = BidEvent.deleteEvent(bid);
                                askCorrection = tradeSize.equals(askSize) ? AskEvent.deleteEvent(ask) :
                                                                            AskEvent.changeEvent(ask,
                                                                                                 askSize.subtract(tradeSize));
                            }
                            // adjust the remainder we need to fill
                            bidSize = bidSize.subtract(tradeSize);
                            SLF4JLoggerProxy.debug(BogusFeed.class,
                                                   "OrderBookSettler is creating the following events:\n{}\n{}\n{}", //$NON-NLS-1$
                                                   trade,
                                                   bidCorrection,
                                                   askCorrection);
                            // post events to the feed's internal book
                            inBook.processEvent(trade);
                            inBook.processEvent(bidCorrection);
                            inBook.processEvent(askCorrection);
                            // collect the events to return to the subscribers
                            eventsToReturn.add(trade);
                            eventsToReturn.add(bidCorrection);
                            eventsToReturn.add(askCorrection);
                        } else {
                            // all the rest of the asks are higher than the highest bid, so no point in looking any more
                            SLF4JLoggerProxy.debug(BogusFeed.class,
                                                   "Best Bid is less than Best Ask, quitting"); //$NON-NLS-1$
                            bids.clear();
                            asks.clear();
                            return eventsToReturn;
                        }
                    }
                    asks.clear();
                }
                bids.clear();
                return eventsToReturn;
            } finally {
                SLF4JLoggerProxy.debug(BogusFeed.class,
                                       "OrderBook is now\n{}", //$NON-NLS-1$
                                       inBook);
            }
        }
    }
    /**
     * Represents a request for data submitted to the feed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $
     * @since 0.6.0
     */
    @ClassVersion("$Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $") //$NON-NLS-1$
    private static class Request
    {
        /**
         * all requests submitted mapped by handle
         */
        private final static Map<String,Request> sRequests = new LinkedHashMap<String,Request>();
        /**
         * Cancels an active request.
         *
         * @param inHandle a <code>String</code> value containing the handle of the request to cancel
         * @return a <code>Request</code> value that was canceled
         */
        private static Request cancelRequest(String inHandle)
        {
            return sRequests.remove(inHandle);
        }
        /**
         * Submits a request to the feed.
         *
         * @param inData a <code>BogusMessage</code> value
         * @param inFeed a <code>BogusFeed</code> value
         * @return a <code>List&lt;String&gt;</code> value containing the handles created for the request
         */
        private static List<String> submit(BogusMessage inData,
                                           BogusFeed inFeed) 
        {
            List<String> handles = new ArrayList<String>();
            List<String> symbols = inData.getSymbols();
            // create a request for each symbol in the passed message
            for(String symbol : symbols) {
                MSymbol theSymbol = new MSymbol(symbol);
                Request request = new Request(theSymbol);
                sRequests.put(request.getHandle(),
                              request);
                handles.add(request.getHandle());
                inFeed.addRequestForSymbol(theSymbol);
            }
            return handles;
        }
        /**
         * Returns all the active requests.
         *
         * @return a <code>Set&lt;Request&gt;</code> value
         */
        private static Set<Request> getActiveRequests()
        {
            Set<Request> activeRequests = new LinkedHashSet<Request>();
            activeRequests.addAll(sRequests.values());
            return activeRequests;
        }
        /**
         * the handle of the request
         */
        private final String mHandle;
        /**
         * the symbol of the request
         */
        private final MSymbol mSymbol;
        /**
         * used to generate handle IDs to track specific requests
         */
        private static final IDFactory sIDFactory = new InMemoryIDFactory(5000);
        /**
         * Create a new Request instance.
         *
         * @param inData
         * @param inSymbol
         */
        private Request(MSymbol inSymbol) 
        {
            mSymbol = inSymbol;
            mHandle = generateHandle();
        }
        /**
         * Generates a unique handle for the request.
         *
         * @return a <code>String</code> value
         */
        private static String generateHandle() 
        {
            try {
                return sIDFactory.getNext();
            } catch (NoMoreIDsException e) {
                return Long.toString(System.nanoTime());
            }
        }
        /**
         * Get the handle value.
         *
         * @return a <code>BogusFeed.Request</code> value
         */
        private String getHandle()
        {
            return mHandle;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((mHandle == null) ? 0 : mHandle.hashCode());
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
            final Request other = (Request) obj;
            if (mHandle == null) {
                if (other.mHandle != null)
                    return false;
            } else if (!mHandle.equals(other.mHandle))
                return false;
            return true;
        }
        /**
         * Get the symbol value.
         *
         * @return a <code>BogusFeed.Request</code> value
         */
        private MSymbol getSymbol()
        {
            return mSymbol;
        }
    }
    /**
     * Helper class for {@link OrderBook}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $
     * @since 0.6.0
     */
    @ClassVersion("$Id: BogusFeed.java 9477 2008-08-08 23:38:47Z klim $") //$NON-NLS-1$
    private static class OrderBookWrapper
    {
        /**
         * the order book
         */
        private final OrderBook mBook;
        /**
         * the current base value which is used to generate bids and asks
         */
        private BigDecimal mValue;
        /**
         * the reference count of this order book
         */
        private int mReferenceCount = 0;
        /**
         * the events that need to be sent to subscribers
         */
        private final List<EventBase> mPendingEvents;
        /**
         * Create a new OrderBookWrapper instance.
         *
         * @param inSymbol
         */
        private OrderBookWrapper(MSymbol inSymbol)
        {
            mBook = new OrderBook(inSymbol);
            // add PENNY just in case ZERO comes up as the random number
            mValue = getRandPrice().add(PENNY);
            // set reference count to 1
            incrementReferenceCount();
            mPendingEvents = new ArrayList<EventBase>();
        }
        /**
         * Increment the reference count of this book.
         *
         * @return an <code>int</code> value containing the new count
         */
        private int incrementReferenceCount()
        {
            return ++mReferenceCount;
        }
        /**
         * Decrement the reference count of this book.
         *
         * @return an <code>int</code> value containing the new count
         */
        private int decrementReferenceCount()
        {
            return --mReferenceCount;
        }
        /**
         * Adjust the price of the book and submit new events accordingly.
         *
         * <p>This method causes the price to be adjusted randomly.  New bids and
         * asks are submitted to the object's order book and the book is then
         * settled.
         */
        private void adjustPrice()
        {
            synchronized(mPendingEvents) {
                if(sRandom.nextBoolean()) {
                    mValue = mValue.add(PENNY);
                } else {
                    if(!mValue.equals(PENNY)) {
                        mValue = mValue.subtract(PENNY);
                    }
                }
                // take the modified value and add a bid and an ask based on it
                AskEvent ask = new AskEvent(System.nanoTime(),
                                            System.currentTimeMillis(),
                                            getBook().getSymbol().getBaseSymbol(),
                                            BGUS_MARKET,
                                            getValue().add(PENNY),
                                            new BigDecimal(sRandom.nextInt(50000) + 1));
                getBook().processEvent(ask);
                mPendingEvents.add(ask);
                BidEvent bid = new BidEvent(System.nanoTime(),
                                            System.currentTimeMillis(),
                                            getBook().getSymbol().getBaseSymbol(),
                                            BGUS_MARKET,
                                            getValue().subtract(PENNY),
                                            new BigDecimal(sRandom.nextInt(50000) + 1));
                getBook().processEvent(bid);
                mPendingEvents.add(bid);
                mPendingEvents.addAll(OrderBookSettler.settleBook(getBook()));
            }
        }
        /**
         * Generates a random price.
         *
         * @return a <code>BigDecimal</code> value
         */
        private static BigDecimal getRandPrice() 
        {
            return BigDecimalUtils.multiply(new BigDecimal(100),
                                            sRandom.nextDouble()).setScale(2, 
                                                                          RoundingMode.HALF_UP);
        }
        /**
         * Get the value value.
         *
         * @return a <code>BogusFeed.OrderBookWrapper</code> value
         */
        private BigDecimal getValue()
        {
            return mValue;
        }
        /**
         * Get the book value.
         *
         * @return a <code>BogusFeed.OrderBookWrapper</code> value
         */
        private OrderBook getBook()
        {
            return mBook;
        }
        /**
         * Returns a view of the events that represent the changes to the order book since the last time this method was executed.
         *
         * @return a <code>List&lt;EventBase&gt;</code> value
         */
        private List<EventBase> getEvents()
        {
            synchronized(mPendingEvents) {
                List<EventBase> events = new ArrayList<EventBase>(mPendingEvents);
                mPendingEvents.clear();
                return events;
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataFeed#getTimeout()
     */
    @Override
    protected long getTimeout()
    {
        return 60*10;
    }
}
