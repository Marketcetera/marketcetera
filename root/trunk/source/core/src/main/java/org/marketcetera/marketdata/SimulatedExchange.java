package org.marketcetera.marketdata;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.event.util.PriceAndSizeComparator;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Simulates an exchange with managed order books for multiple instruments.
 * 
 * <p>This exchange manages a separate order book for each instrument for which requests are made.
 * If a request is made for a instrument which is not yet managed, an order book seeded with a random
 * starting price will be added.  The order book for a instrument will continue to be maintained until
 * the exchange is stopped via {@link SimulatedExchange#stop()}.
 * 
 * <p>To begin simulating an exchange, instantiate the exchange with a specified book depth.  The
 * exchange will simulate market activity to the specified book depth following the rules set
 * down in {@link OrderBook} for maximum depth.  It is generally a good idea to limit memory consumption
 * by setting the maximum depth to 10 or so.
 * 
 * <p>The exchange has two modes in which it can operate: <em>scripted</em> and <em>random</em>.
 * For scripted mode, the exchange executes a specified script of {@link QuoteEvent} objects.  The
 * events are executed synchronously when the exchange is started.  When all events have been executed,
 * the exchange stays running, but will not execute any further changes.
 * Scripted mode is invoked by calling {@link SimulatedExchange#start(List)} with a non-empty, non-null
 * list of {@link QuoteEvent} objects.
 * 
 * <p>Random mode is invoked by calling {@link SimulatedExchange#start()} or by calling
 * {@link SimulatedExchange#start(List)} with a null or empty list.  In random mode, the exchange will
 * continue to simulate behavior in a modified Monte Carlo method.  The exchange will continue simulating
 * market data until stopped. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class SimulatedExchange
    implements Exchange<org.marketcetera.marketdata.SimulatedExchange.Token>, Messages
{
    /**
     * Create a new <code>SimulatedExchange</code> instance.
     * 
     * @param inName a <code>String</code> value containing the name to associate with the exchange
     * @param inCode a <code>String</code> value containing the exchange code of this exchange
     * @param inMaxBookDepth an <code>int</code> value containing the maximum depth to maintain for the order books.  This value
     *  must conform to the requirements established for {@link OrderBook#OrderBook(org.marketcetera.trade.Instrument,int)}.
     * @throws IllegalArgumentException if the given <code>inMaxBookDepth</code> does not correspond to a valid {@link OrderBook} maximum depth
     */
    public SimulatedExchange(String inName,
                             String inCode,
                             int inMaxBookDepth)
    {
        if(inName == null ||
           inCode == null) {
            throw new NullPointerException();
        }
        OrderBook.validateMaximumBookDepth(inMaxBookDepth);
        name = inName;
        code = inCode;
        maxDepth = inMaxBookDepth;
    }
    /**
     * Create a new <code>SimulatedExchange</code> instance with a reasonable maximum book depth.
     * 
     * <p>The exchange will have a maximum book depth of {@link OrderBook#DEFAULT_DEPTH}.
     * 
     * @param inName a <code>String</code> value containing the name to associate with the exchange
     * @param inCode a <code>String</code> value containing the exchange code of this exchange
     */
    public SimulatedExchange(String inName,
                             String inCode)
    {
        this(inName,
             inCode,
             OrderBook.DEFAULT_DEPTH);
    }
    /**
     * Get the name of the exchange.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Get the exchange code.
     *
     * @return a <code>String</code> value
     */
    public String getCode()
    {
        return code;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDepthOfBook(org.marketcetera.trade.Instrument, int)
     */
    @Override
    public DepthOfBookEvent getDepthOfBook(Instrument inInstrument)
    {
        validateSymbolAndState(inInstrument);
        return getBookWrapper(inInstrument).getBook().getDepthOfBook();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getTopOfBook(org.marketcetera.trade.Instrument)
     */
    @Override
    public TopOfBookEvent getTopOfBook(Instrument inInstrument)
    {
        validateSymbolAndState(inInstrument);
        return getBookWrapper(inInstrument).getBook().getTopOfBook();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getLatestTick(org.marketcetera.trade.Instrument)
     */
    @Override
    public TradeEvent getLatestTick(Instrument inInstrument)
    {
        validateSymbolAndState(inInstrument);
        return getBookWrapper(inInstrument).getLatestTick();
    }
    /**
     * Returns statistical data for the given instrument.
     *
     * <p>The data returned by this method are random.  Though the data returned will be
     * consistent with respect to itself, subsequent calls to this method will return
     * unrelated random data.
     * 
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>MarketstatEvent</code> value
     */
    @Override
    public MarketstatEvent getStatistics(Instrument inInstrument)
    {
        if(inInstrument == null) {
            throw new NullPointerException();
        }
        if(!status.isRunning()) {
            throw new IllegalStateException();
        }
        // to properly implement this behavior, we would need an arbitrary amount of
        //  historical data.  there is currently no facility to persist quotes and trades
        //  in this simulated exchange because the cost (memory and performance) does not
        //  justify the benefit
        // for now, we'll just return some blatantly random data
        OrderBookWrapper wrapper = getBookWrapper(inInstrument);
        // this is the timestamp we will use for determining the current date
        long currentTime = System.currentTimeMillis();
        // create a value clustered around the current book value just to make the data a little
        //  more useful
        // synchronization and accuracy aren't relevant here because the data are random
        //  anyway
        BigDecimal currentValue = wrapper.getValue();
        // determine open and close prices (set to current value +/- 0.00-9.99 inclusive)
        BigDecimal openPrice = currentValue.add(randomDecimalDifference(10));
        if(openPrice.compareTo(BigDecimal.ZERO) == -1) {
            openPrice = PENNY;
        }
        BigDecimal closePrice = currentValue.add(randomDecimalDifference(10));
        if(closePrice.compareTo(BigDecimal.ZERO) == -1) {
            closePrice = PENNY;
        }
        BigDecimal previousClosePrice = currentValue.add(randomDecimalDifference(10));
        if(closePrice.compareTo(BigDecimal.ZERO) == -1) {
            closePrice = PENNY;
        }
        // calculate high price (the max of current, open, and close + 0.00-4.99 inclusive)
        BigDecimal highPrice = currentValue.max(openPrice).max(closePrice).add(randomDecimalDifference(5).abs());
        // calculate low price (the min of current, open, and close - 0.00-4.99 inclusive)
        BigDecimal lowPrice = currentValue.min(openPrice).min(closePrice).subtract(randomDecimalDifference(5).abs());
        // ready to return the data
        return MarketstatEventBuilder.marketstat(inInstrument)
                                     .withTimestamp(new Date(currentTime))
                                     .withOpenPrice(openPrice)
                                     .withHighPrice(highPrice)
                                     .withLowPrice(lowPrice)
                                     .withClosePrice(closePrice)
                                     .withPreviousClosePrice(previousClosePrice)
                                     .withVolume(randomInteger(100000))
                                     .withCloseDate(DateUtils.dateToString(new Date(currentTime-(1000*60*60*8))))
                                     .withPreviousCloseDate(DateUtils.dateToString(new Date(currentTime-(1000*60*60*24))))
                                     .withTradeHighTime(DateUtils.dateToString(new Date(currentTime-(1000*60*60*4))))
                                     .withTradeLowTime(DateUtils.dateToString(new Date(currentTime-(1000*60*60*4))))
                                     .withOpenExchange(getCode())
                                     .withHighExchange(getCode())
                                     .withLowExchange(getCode())
                                     .withCloseExchange(getCode()).create();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getStatistics(org.marketcetera.trade.Instrument, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getStatistics(Instrument inInstrument,
                               ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} received statistics subscription request for {}", //$NON-NLS-1$
                               getCode(),
                               inInstrument);
        if(status == Status.RANDOM) {
            getBookWrapper(inInstrument);
        }
        return FilteringSubscriber.subscribe(inSubscriber,
                                             Type.STATISTICS,
                                             inInstrument,
                                             this); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDepthOfBook(org.marketcetera.trade.Instrument, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getDepthOfBook(Instrument inInstrument,
                                ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} received depth-of-book subscription request for {}", //$NON-NLS-1$
                               getCode(),
                               inInstrument);
        if(status == Status.RANDOM) {
            getBookWrapper(inInstrument);
        }
        return FilteringSubscriber.subscribe(inSubscriber,
                                             Type.DEPTH_OF_BOOK,
                                             inInstrument,
                                             this); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getLatestTick(org.marketcetera.trade.Instrument, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getLatestTick(Instrument inInstrument,
                               ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} received latest-tick subscription request for {}", //$NON-NLS-1$
                               getCode(),
                               inInstrument);
        if(status == Status.RANDOM) {
            getBookWrapper(inInstrument);
          }
        return FilteringSubscriber.subscribe(inSubscriber,
                                             Type.LATEST_TICK,
                                             inInstrument,
                                             this); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getTopOfBook(org.marketcetera.trade.Instrument, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getTopOfBook(Instrument inInstrument,
                              ISubscriber inSubscriber)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} received top-of-book subscription request for {}", //$NON-NLS-1$
                               getCode(),
                               inInstrument);
        if(status == Status.RANDOM) {
            getBookWrapper(inInstrument);
          }
        return FilteringSubscriber.subscribe(inSubscriber,
                                             Type.TOP_OF_BOOK,
                                             inInstrument,
                                             this); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#cancel(org.marketcetera.marketdata.Exchange.Token)
     */
    @Override
    public void cancel(Token inToken)
    {
        publisher.unsubscribe(inToken.getSubscriber());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#start()
     */
    @Override
    public void start()
    {
        start(null);
    }
    /**
     * Starts the exchange.
     * 
     * <p>If the given list of events is non-null and non-empty, the exchange
     * will be started in <em>scripted</em> mode instead of <em>random</em>
     * mode.  In scripted mode, the exchange will take the given events and
     * process them in order, one per exchange tick.  When the list of events
     * is empty, the exchange is stopped.
     *
     * @param inEvents a <code>List&lt;QuoteEvent&gt;</code> value
     * @throws IllegalStateException if the exchange is already running
     */
    public synchronized void start(List<QuoteEvent> inEvents)
    {
        if(status.isRunning()) {
            throw new IllegalStateException();
        }
        // clear the scripted events collection and then add the passed
        //  events if there are any.  the contents of the events list passed
        //  in dictates the mode of the exchange
        scriptedEvents.clear();
        if(inEvents != null &&
           !inEvents.isEmpty()) {
            STARTING_SCRIPTED_EXCHANGE.info(SimulatedExchange.class,
                                            getName());
            scriptedEvents.addAll(inEvents);
            status = Status.SCRIPTED;
            doScriptedTicks();
            status = Status.COMPLETE;
        } else {
            STARTING_RANDOM_EXCHANGE.info(SimulatedExchange.class,
                                          getName());
            status = Status.RANDOM;
            synchronized(seedBooks) {
                for(Instrument instrument : seedBooks) {
                    getBookWrapper(instrument);
                }
                seedBooks.clear();
            }
            // set up a job to run a tick every second until stopped
            updateTask = executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        executeTick();
                    } catch (Exception e){
                        SIMULATED_EXCHANGE_TICK_ERROR.warn(SimulatedExchange.class,
                                                           e,
                                                           getName());
                    }
                }
            },
                                                      0,
                                                      1,
                                                      TimeUnit.SECONDS);
        }
        // prepare to execute ticks
        readyForTick.set(true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#stop()
     */
    @Override
    public synchronized void stop()
    {
        try {
            if(!status.isRunning()) {
                throw new IllegalStateException();
            }
            STOPPING_SIMULATED_EXCHANGE.info(SimulatedExchange.class,
                                             getName());
            // turn off the update engine
            if(updateTask != null) {
                updateTask.cancel(true);
                executor.purge();
            }
            // clear the order books, resetting the values
            synchronized(books) {
                books.clear();
            }
            synchronized(seedBooks) {
                seedBooks.clear();
            }
        } finally {
            status = Status.STOPPED;
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s(%s)", //$NON-NLS-1$
                             getName(),
                             getCode());
    }
    /**
     * Get the status value.
     *
     * @return a <code>Status</code> value
     */
    public Status getStatus()
    {
        return status;
    }
    /**
     * Generates a random decimal value in the interval (-(inUpperBound-1).99,+(inUpperBound-1).99).
     *
     * @param inUpperBound an <code>int</code> value used to define the interval in which the returned value may occur
     * @return a <code>BigDecimal</code> value in the interval (-(inUpperBound-1).99,+(inUpperBound-1).99)
     */
    private static BigDecimal randomDecimalDifference(int inUpperBound)
    {
        if(random.nextBoolean()) {
            // higher
            return BigDecimal.ZERO.add(randomDecimal(inUpperBound));
        } else {
            // lower
            return BigDecimal.ZERO.subtract(randomDecimal(inUpperBound));
        }
    }
    /**
     * Generates a random integer in the interval (0,inUpperBound].
     *
     * @param inUpperBound an <code>int</code> value used to define the interval in which the returned value may occur
     * @return a <code>BigDecimal</code> value in the interval (0,inUpperBound]
     */
    private static BigDecimal randomInteger(int inUpperBound)
    {
        return new BigDecimal(random.nextInt(inUpperBound));
    }
    /**
     * Generates a random decimal value in the interval (0.00,(inUpperBound-1).99).
     *
     * @param inUpperBound an <code>int</code> value used to define the interval in which the returned value may occur
     * @return a <code>BigDecimal</code> value in the interval (0.00,(inUpperBound-1).99)
     */
    private static BigDecimal randomDecimal(int inUpperBound)
    {
        return new BigDecimal(String.format("%s.%s", //$NON-NLS-1$
                                            random.nextInt(inUpperBound),
                                            random.nextInt(100)));
    }
    /**
     * Gets the latest instrument value across all exchanges, if any.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BigDecimal</code> value or null if no current value exists
     */
    private static BigDecimal getSymbolValue(Instrument inInstrument)
    {
        synchronized(latestSymbolValues) {
            return latestSymbolValues.get(inInstrument);
        }
    }
    /**
     * Updates the instrument value for the given instrument across all exchanges.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inValue a <code>BigDecimal</code> value
     */
    private static void updateSymbolValue(Instrument inInstrument,
                                          BigDecimal inValue)
    {
        synchronized(latestSymbolValues) {
            latestSymbolValues.put(inInstrument,
                             inValue);
        }
    }
    /**
     * Adds the given instrument to the exchange's order books if not already present.
     * 
     * <p>If the given instrument is already represented in the exchange's order books,
     * this method does nothing.
     *
     * @param inInstrument inInstrument an <code>Instrument</code> value.
     * @return an <code>OrderBookWrapper</code> value
     */
    private OrderBookWrapper addSymbol(Instrument inInstrument)
    {
        synchronized(books) {
            OrderBookWrapper book = books.get(inInstrument);
            if(book == null) {
                book = new OrderBookWrapper(inInstrument,
                                            maxDepth,
                                            this); 
                books.put(inInstrument,
                          book);
            }
            return book;
        }
    }
    /**
     * Guarantees that the given instrument is valid and the exchange is in a state to
     * process requests.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    private void validateSymbolAndState(Instrument inInstrument)
    {
        if(inInstrument == null) {
            throw new NullPointerException();
        }
        if(!status.isRunning()) {
            throw new IllegalStateException();
        }
    }
    /**
     * Returns the helper object representing the given instrument.
     *
     * <p>If a helper object does not exist for this instrument, one
     * will be created.
     *
     * @param inInstrument an <code>Instrument</code> value.
     * @return an <code>OrderBookWrapper</code> value
     */
    private OrderBookWrapper getBookWrapper(Instrument inInstrument)
    {
        synchronized(books) {
            OrderBookWrapper book = books.get(inInstrument);
            if(book == null) {
                book = addSymbol(inInstrument);
                if(!status.isScripted()) {
                    // set some initial data in the book
                    doRandomBookTick(book);
                }
                return books.get(inInstrument);
            } else {
                return book;
            }
        }
    }
    /**
     * Executes one round of processing for all instruments.
     */
    private void executeTick() 
    {
        // if the previous tick hasn't completed yet, skip this tick and wait for the next one
        if(readyForTick.get()) {
            // the previous tick has completed, so we can begin this one
            long startTime = System.currentTimeMillis();
            try {
                // we are committed to completing this tick, but don't let another one start until we're done
                readyForTick.set(false);
                List<OrderBookWrapper> allBooks;
                synchronized(books) {
                    allBooks = new ArrayList<OrderBookWrapper>(books.values());
                }
                for(OrderBookWrapper book : allBooks) {
                    doRandomBookTick(book);
                }
            } finally {
                // indicate that we're ready for the next tick
                readyForTick.set(true);
                SLF4JLoggerProxy.debug(this,
                                       "Tick completed after {} ms", //$NON-NLS-1$
                                       System.currentTimeMillis() - startTime);
            }
        } else {
            // the previous tick has not yet completed, skip this one
            SLF4JLoggerProxy.debug(this,
                                   "Tick skipped"); //$NON-NLS-1$
        }
    }
    /**
     * Executes all the ticks of the exchange script in <code>SCRIPTED</code> mode.
     */
    private void doScriptedTicks()
    {
        for(QuoteEvent event : scriptedEvents) {
            try {
                // verify the event is for this exchange
                if(!getCode().equals(event.getExchange())) {
                    throw new IllegalArgumentException(SIMULATED_EXCHANGE_CODE_MISMATCH.getText(this,
                                                                                                event,
                                                                                                event.getExchange(),
                                                                                                getCode()));
                }
                // find the book that goes with the event
                OrderBookWrapper book = getBookWrapper(event.getInstrument());
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} executing scripted event {}", //$NON-NLS-1$
                                       this,
                                       event);
                // process the event
                book.process(event);
                // settle the book as a result of this change
                book.publish(OrderBookSettler.settleBook(book,
                                                         maxDepth));
            } catch (Exception e) {
                SIMULATED_EXCHANGE_SKIPPED_EVENT.warn(SimulatedExchange.class,
                                                      e,
                                                      this,
                                                      event);
            }
        }
        SIMULATED_EXCHANGE_OUT_OF_EVENTS.info(SimulatedExchange.class,
                                              getName());
    }
    /**
     * Executes a single tick for the given order book.
     *
     * @param inBook an <code>OrderBookWrapper</code> value
     */
    private void doRandomBookTick(OrderBookWrapper inBook)
    {
        // adjust the order book base value
        inBook.adjustPrice();
        // settle the book (generates additional activity which needs to be published)
        inBook.publish(OrderBookSettler.settleBook(inBook,
                                                   maxDepth));
        // produce statistics
        inBook.publish(Arrays.asList(new Event[] { getStatistics(inBook.getBook().getInstrument()) } ));
    }
    /**
     * Unique identifier for a specific subscription request to the {@link SimulatedExchange}.
     * 
     * <p>This object is used to identify a market data request.  When
     * executing a subscription request, as in to {@link Exchange#getDepthOfBook(org.marketcetera.trade.Instrument,org.marketcetera.core.publisher.ISubscriber)},
     * for instance, a <code>Token</code> value will be returned.  Updates will be published to the
     * given {@link ISubscriber} until the exchange is stopped or the request is canceled via
     * {@link SimulatedExchange#cancel(Token)}.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    public static final class Token
    {
        /**
         * the original requester of the data
         */
        private final FilteringSubscriber subscriber;
        /**
         * Create a new Token instance.
         * @param inSubscriber an <code>ISubscriber</code> value
         */
        private Token(FilteringSubscriber inSubscriber)
        {
            subscriber = inSubscriber;
        }
        /**
         * Gets the subscriber who requested the data.
         *
         * @return a <code>FilteringSubscriber</code> value
         */
        private FilteringSubscriber getSubscriber()
        {
            return subscriber;
        }
        /**
         * Cancels the subscription represented by this request.
         */
        public void cancel()
        {
            subscriber.exchange.cancel(this);
        }
    }
    /**
     * Provides interface between the {@link OrderBook} and the {@link SimulatedExchange}.
     * 
     * <p>One <code>OrderBookWrapper</code> exists for each {@link OrderBook}.  The <code>OrderBookWrapper</code>
     * manages traffic for the corresponding <code>OrderBook</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$")
    private static class OrderBookWrapper
    {
        /**
         * the order book
         */
        private final OrderBook book;
        /**
         * the exchange that owns this book
         */
        private final SimulatedExchange exchange;
        /**
         * the current base value which is used to generate bids and asks
         */
        private BigDecimal value;
        /**
         * the most recent trade on this book (may be null)
         */
        private TradeEvent latestTick;
        /**
         * Create a new OrderBookWrapper instance.
         *
         * @param inInstrument inInstrument an <code>Instrument</code> value
         * @param inMaxDepth an <code>int</code> value containing the maximum depth to maintain
         * @param inExchange a <code>SimulatedExchange</code> value containing the owning exchange
         */
        private OrderBookWrapper(Instrument inInstrument,
                                 int inMaxDepth,
                                 SimulatedExchange inExchange)
        {
            exchange = inExchange;
            book = new OrderBook(inInstrument,
                                 inMaxDepth);
            // check to see if there is already a value we should use to seed the book
            value = getSymbolValue(inInstrument);
            if(value == null) {
                // add PENNY just in case ZERO comes up as the random number
                value = randomDecimal(100).add(PENNY);
                updateSymbolValue(inInstrument,
                                  value);
            }
        }
        /**
         * Processes the given event through the underlying book and publishes
         * it to interested subscribers.
         *
         * @param inEvent a <code>QuoteEvent</code> value
         */
        private void process(QuoteEvent inEvent)
        {
            QuoteEvent displacedEvent = book.process(inEvent);
            publish(inEvent);
            if(displacedEvent != null) {
                publish(QuoteEventBuilder.delete(displacedEvent));
            }
        }
        /**
         * Publishes the given events to relevant subscribers, if any.
         *
         * @param inEvents a <code>List&lt;? extends Event&gt;</code> value
         */
        private void publish(List<? extends Event> inEvents)
        {
            for(Event event : inEvents) {
                publish(event);
            }
        }
        /**
         * Publishes the given event to relevant subscribers, if any. 
         *
         * @param inEvent an <code>Event</code> value
         */
        private void publish(Event inEvent)
        {
            publisher.publish(inEvent);
        }
        /**
         * Adjust the price of the book and submit new events accordingly.
         *
         * <p>This method causes the price to be adjusted randomly.  New bids and
         * asks are submitted to the object's order book.
         */
        private void adjustPrice()
        {
            if(random.nextBoolean()) {
                value = value.add(PENNY);
            } else {
                if(!value.equals(PENNY)) {
                    value = value.subtract(PENNY);
                }
            }
            // take the modified value and add a bid and an ask based on it
            Date timestamp = new Date();
            process(QuoteEventBuilder.askEvent(getBook().getInstrument())
                                     .withMessageId(System.nanoTime())
                                     .withTimestamp(timestamp)
                                     .withExchange(exchange.getCode())
                                     .withPrice(getValue().add(PENNY))
                                     .withSize(randomInteger(10000))
                                     .withQuoteDate(DateUtils.dateToString(timestamp)).create());
            process(QuoteEventBuilder.bidEvent(getBook().getInstrument())
                                     .withMessageId(System.nanoTime())
                                     .withTimestamp(timestamp)
                                     .withExchange(exchange.getCode())
                                     .withPrice(getValue().subtract(PENNY))
                                     .withSize(randomInteger(10000))
                                     .withQuoteDate(DateUtils.dateToString(timestamp)).create());
        }
        /**
         * Get the base value.
         *
         * @return a <code>BogusFeed.OrderBookWrapper</code> value
         */
        private BigDecimal getValue()
        {
            return value;
        }
        /**
         * Get the book value.
         *
         * @return a <code>BogusFeed.OrderBookWrapper</code> value
         */
        private OrderBook getBook()
        {
            return book;
        }
        /**
         * Records a <code>TradeEvent</code> as the most recent trade.
         *
         * @param inTrade a <code>TradeEvent</code> value
         */
        private void setLatestTick(TradeEvent inTrade)
        {
            latestTick = inTrade;
        }
        /**
         * Gets the most recent trade.
         *
         * @return a <code>TradeEvent</code> or <code>null</code> if no trade has occurred yet
         */
        private TradeEvent getLatestTick()
        {
            return latestTick;
        }
    }
    /**
     * Matches bids and offers to produce trades.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.6.0
     */
    @ClassVersion("$Id$")
    private static class OrderBookSettler
    {
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
         * pruned to the depth set by <code>inMaxDepth</code>.
         * 
         * <p>This method requires exclusive access to the <code>OrderBook</code> but
         * does not perform any synchronization explicitly.  It is the caller's
         * responsibility to guarantee exclusive access to the <code>OrderBook</code>.
         * 
         * @param inBook an <code>OrderBook</code> value
         * @param inMaxDepth an <code>int</code> value containing the maximum depth to allow
         * @return a <code>List&lt;MarketDataEvent&gt;</code> value containing the events created, if any, to settle the book
         */
        private static List<MarketDataEvent> settleBook(OrderBookWrapper inBook,
                                                        int inMaxDepth)
        {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "OrderBook starts at\n{}", //$NON-NLS-1$
                                   inBook.getBook());
            List<MarketDataEvent> eventsToReturn = new ArrayList<MarketDataEvent>();
            try {
                // this is the list of bids over which to operate - note this is a static list, it does
                //  not reflect the ongoing changes to the order book
                List<BidEvent> bids = new ArrayList<BidEvent>(inBook.getBook().getBidBook());
                // this is the time that we're going to use for all the trades
                long tradeTime = System.currentTimeMillis();
                // iterate over the bid list
                for(BidEvent bid : bids) {
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "OrderBookSettler looking for matches for {}", //$NON-NLS-1$
                                           bid);
                    // search for the first ask that matches the bid
                    BigDecimal bidPrice = bid.getPrice();
                    BigDecimal bidSize = bid.getSize();
                    // grab the list of asks, this, too, is a static list and is refreshed each bid iteration
                    List<AskEvent> asks = new ArrayList<AskEvent>(inBook.getBook().getAskBook());
                    // iterate over the list of asks looking for a match
                    for(AskEvent ask : asks) {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "Bid has {} left to fill", //$NON-NLS-1$
                                               bidSize);
                        // check to see if the bid is fully filled before continuing
                        if(bidSize.compareTo(BigDecimal.ZERO) != 1) {
                            // bid is fully filled
                            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                                   "{} fully filled", //$NON-NLS-1$
                                                   bid);
                            break; // out of the ask iteration loop
                        }
                        // bid is not fully filled
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
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
                            BigDecimal tradeSize = askSize;
                            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                                   "Trade is {} at {}", //$NON-NLS-1$
                                                   tradeSize.toPlainString(),
                                                   tradePrice.toPlainString());
                            // create the new trade
                            TradeEvent trade = TradeEventBuilder.tradeEvent(bid.getInstrument())
                                                                .withMessageId(System.nanoTime())
                                                                .withTimestamp(new Date(tradeTime))
                                                                .withExchange(bid.getExchange())
                                                                .withPrice(tradePrice)
                                                                .withSize(tradeSize)
                                                                .withTradeDate(DateUtils.dateToString(new Date(tradeTime))).create();
                            // these events are used to modify the orders in the book
                            BidEvent bidCorrection;
                            AskEvent askCorrection;
                            if(tradeSize.compareTo(bidSize) == -1) {
                                // trade is smaller than the bid, this is a partial fill
                                bidCorrection = QuoteEventBuilder.change(bid,
                                                                         new Date(tradeTime),
                                                                         bidSize.subtract(tradeSize));
                                askCorrection = QuoteEventBuilder.delete(ask); 
                            } else {
                                // trade is equal to the bid, this is a full fill
                                bidCorrection = QuoteEventBuilder.delete(bid);
                                askCorrection = tradeSize.equals(askSize) ? QuoteEventBuilder.delete(ask) :
                                                                            QuoteEventBuilder.change(ask,
                                                                                                     new Date(tradeTime),
                                                                                                     askSize.subtract(tradeSize));
                            }
                            // adjust the remainder we need to fill
                            bidSize = bidSize.subtract(tradeSize);
                            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                                   "OrderBookSettler is creating the following events:\n{}\n{}\n{}", //$NON-NLS-1$
                                                   trade,
                                                   bidCorrection,
                                                   askCorrection);
                            // post events to the feed's internal book
                            inBook.setLatestTick(trade);
                            inBook.getBook().process(bidCorrection);
                            inBook.getBook().process(askCorrection);
                            // collect the events to return to the subscribers
                            eventsToReturn.add(trade);
                            eventsToReturn.add(bidCorrection);
                            eventsToReturn.add(askCorrection);
                        } else {
                            // all the rest of the asks are higher than the highest bid, so no point in looking any more
                            SLF4JLoggerProxy.debug(SimulatedExchange.class,
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
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "OrderBook is now\n{}", //$NON-NLS-1$
                                       inBook.getBook());
            }
        }
    }
    /**
     * <code>ISubscriber</code> that filters publications to an enclosed <code>ISubscriber</code>
     * based on the original type of market data request and instrument.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    private static class FilteringSubscriber
        implements ISubscriber
    {
        /**
         * the original (external to this class) subscriber
         */
        private final ISubscriber originalSubscriber;
        /**
         * the type of request
         */
        private final Type type;
        /**
         * the instrument for which the request was made
         */
        private final Instrument instrument;
        /**
         * the subscription token returned to the caller
         */
        private final Token token;
        /**
         * the exchange to which this subscription was targeted
         */
        private final SimulatedExchange exchange;
        /**
         * Subscribes the given <code>ISubscriber</code> to market data updates of the given
         * type for the given instrument. 
         *
         * @param inOriginalSubscriber an <code>ISubscriber</code> value
         * @param inType a <code>Type</code> value
         * @param inInstrument an <code>Instrument</code> value
         * @param inExchange a <code>SimulatedExchange</code> value
         * @return a <code>Token</code> value representing the subscription
         */
        private static Token subscribe(ISubscriber inOriginalSubscriber,
                                       Type inType,
                                       Instrument inInstrument,
                                       SimulatedExchange inExchange)
        {
            synchronized(inExchange.seedBooks) {
                inExchange.seedBooks.add(inInstrument);
            }
            FilteringSubscriber subscriber = new FilteringSubscriber(inOriginalSubscriber,
                                                                     inType,
                    inInstrument,
                                                                     inExchange);
            publisher.subscribe(subscriber);
            return subscriber.getToken();
        }
        /**
         * Create a new FilteringSubscriber instance.
         *
         * @param inSubscriber an <code>ISubscriber</code> value
         * @param inType a <code>Type</code> value
         * @param inInstrument an <code>Instrument</code> value
         */
        private FilteringSubscriber(ISubscriber inSubscriber,
                                    Type inType,
                                    Instrument inInstrument,
                                    SimulatedExchange inExchange)
        {
            originalSubscriber = inSubscriber;
            type = inType;
            instrument = inInstrument;
            token = new Token(this);
            exchange = inExchange;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            // escape hatch for non-events
            if(!(inData instanceof Event)) {
                return true;
            }
            // verify the exchange matches
            if(inData instanceof MarketDataEvent) {
                if(!((MarketDataEvent)inData).getExchange().equals(exchange.getCode())) {
                    return false;
                }
            }
            // verify the object has a relevant instrument (if it has has one)
            if(inData instanceof HasInstrument) {
                if(!((HasInstrument)inData).getInstrument().equals(instrument)) {
                    return false;
                }
            }
            // verify the object's type is relevant
            switch(type) {
                case TOP_OF_BOOK :
                    return inData instanceof QuoteEvent;
                case LATEST_TICK :
                    return inData instanceof TradeEvent;
                case DEPTH_OF_BOOK :
                    return inData instanceof QuoteEvent;
                case STATISTICS :
                    return inData instanceof MarketstatEvent;
                default :
                    throw new UnsupportedOperationException();
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public synchronized void publishTo(Object inData)
        {
            if(type != Type.TOP_OF_BOOK) {
                originalSubscriber.publishTo(inData);
                return;
            }
            // top-of-book is a special case.  first, if we get this far, then the
            //  class of inData *should* be QuoteEvent, but let's not bank on that
            if(inData instanceof QuoteEvent) {
                // a QuoteEvent for top-of-book is an opportunity for a new top-of-book state
                //  for the exchange; an opportunity but *not* a guarantee
                // first, check to see if there is, in fact, a new top-of-book state for the
                //  exchange
                TopOfBookEvent currentState = exchange.getTopOfBook(instrument);
                try {
                    // we are guaranteed that this object is non-null, but its components may be null
                    // check to see if the quote event caused a change in the top-of-book state
                    if(!currentState.equals(lastKnownState)) {
                        // *something* has changed in the top-of-book, but we don't know what yet
                        // by design, top-of-book updates are sent as ADDs except if the quote is to be removed
                        //  (indicating there is no top-of-book) in which case the action will be DELETE
                        // also, it's currently not possible for a single quote event to cause a change
                        //  in both sides of the book, but that doesn't mean it can't happen some time in
                        //  the future.  it's an easy check to make, so go ahead and check both sides.  note
                        //  that there's no guarantee that the object published to originalSubscriber is the
                        //  same object passed to this method, nor is there any kind of multiplicity guarantees
                        //  (can be one-for-one, many-for-one, none-for-one)
                        // has the bid changed?
                        publishCurrentSideIfNecessary(currentState.getBid(),
                                                      (lastKnownState == null ? null : lastKnownState.getBid()));
                        // has the ask changed?
                        publishCurrentSideIfNecessary(currentState.getAsk(),
                                                      (lastKnownState == null ? null : lastKnownState.getAsk()));
                    }
                } finally {
                    lastKnownState = currentState;
                }
            }
        }
        private void publishCurrentSideIfNecessary(QuoteEvent inCurrentTop,
                                                   QuoteEvent inLastTop)
        {
            if(inCurrentTop == null) {
                // there is no current top quote, was there one before?
                if(inLastTop != null) {
                    // yes, there used to be a top quote, but it should go away now
                    originalSubscriber.publishTo(QuoteEventBuilder.delete(inLastTop));
                } else {
                    // there didn't used to be a top quote, so don't do anything
                }
            } else {
                // there is a current top quote, compare it to the one that used to be here
                if(inLastTop == null) {
                    // there didn't used to be a top quote, just add the new one
                    originalSubscriber.publishTo(QuoteEventBuilder.add(inCurrentTop));
                } else {
                    // there used to be a top quote, check to see if it's different than the current one
                    // btw, we know that current quote and last known quote are both non-null
                    if(PriceAndSizeComparator.instance.compare(inLastTop,
                                                               inCurrentTop) != 0) {
                        originalSubscriber.publishTo(QuoteEventBuilder.add(inCurrentTop));
                    } else {
                        // the current and previous tops are identical, so don't do anything
                    }
                }
            }
        }
        private TopOfBookEvent lastKnownState = null;
        /**
         * Gets the <code>Token</code> corresponding to this subscription.
         *
         * @return a <code>Token</code> value
         */
        private Token getToken()
        {
            return token;
        }
    }
    /**
     * The status of the exchange.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    public static enum Status
    {
        /**
         * exchange is not running
         */
        STOPPED,
        /**
         * exchange is running generating random data
         */
        RANDOM,
        /**
         * exchange is running using scripted data
         */
        SCRIPTED,
        /**
         * exchange is running using scripted data, and has completed its script
         */
        COMPLETE;
        /**
         * Indicates if the exchange is running or not.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isRunning()
        {
            return this == RANDOM || this == SCRIPTED || this == COMPLETE;
        }
        /**
         * Indicates if the exchange is running in scripted mode or not.
         *
         * @return a <code>boolean</code> value
         */
        private boolean isScripted()
        {
            return this == SCRIPTED || this == COMPLETE;
        }
    }
    /**
     * publishes events generated by order books from exchanges and manages subscriptions 
     */
    private static final PublisherEngine publisher = new PublisherEngine(true);
    /**
     * the exchange status
     */
    private Status status = Status.STOPPED;
    /**
     * the order books for the instruments managed by this exchange
     */
    private final Map<Instrument,OrderBookWrapper> books = new HashMap<Instrument,OrderBookWrapper>();
    /**
     * the name of this exchange
     */
    private final String name;
    /**
     * the exchange code of this exchange
     */
    private final String code;
    /**
     * the maximum depth for orderbooks held by this exchange
     */
    private final int maxDepth;
    /**
     * stores the handle for the task submitted to the scheduler to run updates on this exchange
     */
    private ScheduledFuture<?> updateTask = null;
    /**
     * tracks the instruments for which requests have been made before the exchange starts
     */
    private final List<Instrument> seedBooks = new ArrayList<Instrument>();
    /**
     * the list of events to use in SCRIPTED mode - may be empty
     */
    private final List<QuoteEvent> scriptedEvents = new ArrayList<QuoteEvent>();
    /**
     * indicates if the previous tick processing has completed or not
     */
    private final AtomicBoolean readyForTick = new AtomicBoolean(true);
    /**
     * random generator used to manipulate prices
     */
    private static final Random random = new Random(System.nanoTime());
    /**
     * value used to add to or subtract from prices
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01"); //$NON-NLS-1$
    /**
     * mechanism which manages the threads that create the market data
     */
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    /**
     * tracks the latest reported instrument value across all simulated exchanges - can be used to sync values if desired
     */
    private static final Map<Instrument,BigDecimal> latestSymbolValues = new HashMap<Instrument,BigDecimal>();
}
