package org.marketcetera.marketdata;

import static org.marketcetera.marketdata.Messages.DIVIDEND_REQUEST_MISSING_INSTRUMENT;
import static org.marketcetera.marketdata.Messages.SIMULATED_EXCHANGE_CODE_MISMATCH;
import static org.marketcetera.marketdata.Messages.SIMULATED_EXCHANGE_OUT_OF_EVENTS;
import static org.marketcetera.marketdata.Messages.SIMULATED_EXCHANGE_SKIPPED_EVENT;
import static org.marketcetera.marketdata.Messages.SIMULATED_EXCHANGE_TICK_ERROR;
import static org.marketcetera.marketdata.Messages.STARTING_RANDOM_EXCHANGE;
import static org.marketcetera.marketdata.Messages.STARTING_SCRIPTED_EXCHANGE;
import static org.marketcetera.marketdata.Messages.STOPPING_SIMULATED_EXCHANGE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.Pair;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.event.HasInstrument;
import org.marketcetera.event.HasUnderlyingInstrument;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.DividendEventBuilder;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.event.util.PriceAndSizeComparator;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.DeliveryType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.StandardType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

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
@ThreadSafe
@ClassVersion("$Id$")
public class SimulatedExchange
        implements Exchange<SimulatedExchange.Token>
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
        Multimap<Instrument,FilteringSubscriber> unsynchronizedOptionChainSubscribers = HashMultimap.create();
        optionChainSubscribers = Multimaps.synchronizedMultimap(unsynchronizedOptionChainSubscribers);
        setStatus(Status.STOPPED);
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
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getCode()
     */
    @Override
    public String getCode()
    {
        return code;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDepthOfBook(org.marketcetera.marketdata.ExchangeRequest)
     */
    @Override
    public List<QuoteEvent> getDepthOfBook(ExchangeRequest inExchangeRequest)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received synchronous depth-of-book request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inExchangeRequest,
                                   requestId);
            validateSynchronousRequest(inExchangeRequest);
            updateInfo(inExchangeRequest);
            List<QuoteEvent> result = new ArrayList<QuoteEvent>();
            for(PrivateInstrumentInfo book : getAllAffectedBooks(inExchangeRequest)) {
                result.addAll(book.getBook().getDepthOfBook().decompose());
            }
            return result;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completed request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getTopOfBook(org.marketcetera.marketdata.ExchangeRequest)
     */
    @Override
    public List<QuoteEvent> getTopOfBook(ExchangeRequest inExchangeRequest)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received synchronous top-of-book request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inExchangeRequest,
                                   requestId);
            validateSynchronousRequest(inExchangeRequest);
            updateInfo(inExchangeRequest);
            List<QuoteEvent> result = new ArrayList<QuoteEvent>();
            for(PrivateInstrumentInfo book : getAllAffectedBooks(inExchangeRequest)) {
                result.addAll(book.getBook().getTopOfBook().decompose());
            }
            return result;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completing request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getLatestTick(org.marketcetera.marketdata.ExchangeRequest)
     */
    @Override
    public List<TradeEvent> getLatestTick(ExchangeRequest inExchangeRequest)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received synchronous latest tick request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inExchangeRequest,
                                   requestId);
            validateSynchronousRequest(inExchangeRequest);
            updateInfo(inExchangeRequest);
            List<TradeEvent> result = new ArrayList<TradeEvent>();
            for(PrivateInstrumentInfo book : getAllAffectedBooks(inExchangeRequest)) {
                result.add(book.getLatestTrade());
            }
            return result;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completing request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getStatistics(org.marketcetera.marketdata.ExchangeRequest)
     */
    @Override
    public List<MarketstatEvent> getStatistics(ExchangeRequest inExchangeRequest)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received synchronous statistics request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inExchangeRequest,
                                   requestId);
            validateSynchronousRequest(inExchangeRequest);
            updateInfo(inExchangeRequest);
            // to properly implement this behavior, we would need an arbitrary amount of
            //  historical data.  there is currently no facility to persist quotes and trades
            //  in this simulated exchange because the cost (memory and performance) does not
            //  justify the benefit
            List<MarketstatEvent> results = new ArrayList<MarketstatEvent>();
            // for now, we'll just return some blatantly random data
            for(PrivateInstrumentInfo book : getAllAffectedBooks(inExchangeRequest)) {
                // create a value clustered around the current book value just to make the data a little
                //  more useful
                // synchronization and accuracy aren't relevant here because the data are random
                //  anyway
                BigDecimal currentValue = book.getValue();
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
                if(previousClosePrice.compareTo(BigDecimal.ZERO) == -1) {
                    previousClosePrice = PENNY;
                }
                // calculate high price (the max of current, open, and close + 0.00-4.99 inclusive)
                BigDecimal highPrice = currentValue.max(openPrice).max(closePrice).add(randomDecimalDifference(5).abs());
                // calculate low price (the min of current, open, and close - 0.00-4.99 inclusive)
                BigDecimal lowPrice = currentValue.min(openPrice).min(closePrice).subtract(randomDecimalDifference(5).abs());
                // ready to return the data
                Instrument requestInstrument = book.getInstrument();
                MarketstatEventBuilder builder = MarketstatEventBuilder.marketstat(requestInstrument);
                builder.withEventType(EventType.UPDATE_PART)
                       .withOpenPrice(openPrice)
                       .withHighPrice(highPrice)
                       .withLowPrice(lowPrice)
                       .withClosePrice(closePrice)
                       .withPreviousClosePrice(previousClosePrice)
                       .withVolume(randomInteger(100000))
                       .withValue(randomInteger(100000))
                       .withCloseDate(DateUtils.dateToString(new Date(startingTime-(HOURms*8))))
                       .withPreviousCloseDate(DateUtils.dateToString(new Date(startingTime-(DAYms))))
                       .withTradeHighTime(DateUtils.dateToString(new Date(startingTime-(HOURms*4))))
                       .withTradeLowTime(DateUtils.dateToString(new Date(startingTime-(HOURms*4))))
                       .withOpenExchange(getCode())
                       .withHighExchange(getCode())
                       .withLowExchange(getCode())
                       .withCloseExchange(getCode());
                if(requestInstrument instanceof Option) {
                    SharedInstrumentInfo sharedInfo = getSharedInstrumentInfo(requestInstrument);
                    // this information must already be present (updateInfo creates it)
                    assert(sharedInfo != null);
                    assert(sharedInfo.getUnderlyingInstrument() != null);
                    builder.withExpirationType(getExpirationType((Option)requestInstrument))
                           .withUnderlyingInstrument(sharedInfo.getUnderlyingInstrument())
                           .withInterestChange(randomInteger(1000))
                           .withVolumeChange(randomInteger(1000));
                }
                if(requestInstrument instanceof Future) {
                    builder.withContractSize(100)
                           .withDeliveryType(DeliveryType.PHYSICAL)
                           .withStandardType(StandardType.STANDARD);
                }
                results.add(builder.create());
            }
            return results;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completing request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDividends(org.marketcetera.marketdata.ExchangeRequest)
     */
    @Override
    public List<DividendEvent> getDividends(ExchangeRequest inExchangeRequest)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received synchronous dividends request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inExchangeRequest,
                                   requestId);
            validateSynchronousRequest(inExchangeRequest);
            if(inExchangeRequest.getInstrument() == null) {
                throw new IllegalArgumentException(DIVIDEND_REQUEST_MISSING_INSTRUMENT.getText(inExchangeRequest.toString()));
            }
            if(inExchangeRequest.getInstrument() instanceof Option) {
                throw new IllegalArgumentException(DIVIDEND_REQUEST_MISSING_INSTRUMENT.getText(inExchangeRequest.toString()));
            }
            if(inExchangeRequest.getUnderlyingInstrument() != null) {
                throw new IllegalArgumentException(DIVIDEND_REQUEST_MISSING_INSTRUMENT.getText(inExchangeRequest.toString()));
            }
            updateInfo(inExchangeRequest);
            List<DividendEvent> results = new ArrayList<DividendEvent>();
            // note that, in the current implementation, there should be only one affected book
            // however, the cost of not assuming this is minimal, so pretend there can be more than one
            //  to match the implementation of other synchronous requests
            for(PrivateInstrumentInfo book : getAllAffectedBooks(inExchangeRequest)) {
                SharedInstrumentInfo sharedInfo = getSharedInstrumentInfo(book.getInstrument());
                assert(sharedInfo != null);
                results.addAll(sharedInfo.getDividends());
            }
            return results;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completing request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDividends(org.marketcetera.marketdata.ExchangeRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getDividends(ExchangeRequest inExchangeRequest,
                              ISubscriber inSubscriber)
    {
        return doAsynchronousRequest(inExchangeRequest,
                                     inSubscriber,
                                     Type.DIVIDENDS);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getStatistics(org.marketcetera.marketdata.ExchangeRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getStatistics(ExchangeRequest inExchangeRequest,
                               ISubscriber inSubscriber)
    {
        return doAsynchronousRequest(inExchangeRequest,
                                     inSubscriber,
                                     Type.STATISTICS);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getDepthOfBook(org.marketcetera.marketdata.ExchangeRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getDepthOfBook(ExchangeRequest inExchangeRequest,
                                ISubscriber inSubscriber)
    {
        return doAsynchronousRequest(inExchangeRequest,
                                     inSubscriber,
                                     Type.DEPTH_OF_BOOK);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getLatestTick(org.marketcetera.marketdata.ExchangeRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getLatestTick(ExchangeRequest inExchangeRequest,
                               ISubscriber inSubscriber)
    {
        return doAsynchronousRequest(inExchangeRequest,
                                     inSubscriber,
                                     Type.LATEST_TICK);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#getTopOfBook(org.marketcetera.marketdata.ExchangeRequest, org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public Token getTopOfBook(ExchangeRequest inExchangeRequest,
                              ISubscriber inSubscriber)
    {
        return doAsynchronousRequest(inExchangeRequest,
                                     inSubscriber,
                                     Type.TOP_OF_BOOK);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#cancel(java.lang.Object)
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
     * will be started in {@link SimulatedExchange.Status#SCRIPTED} mode 
     * instead of {@link SimulatedExchange.Status#RANDOM} mode.
     * 
     * <p>In scripted mode, the exchange will take the given events and
     * process them in order, one per exchange tick.  When the list of events
     * is empty, the exchange is stopped.
     *
     * @param inEvents a <code>List&lt;QuoteEvent&gt;</code> value
     * @throws IllegalStateException if the exchange is already running
     */
    public synchronized void start(List<QuoteEvent> inEvents)
    {
        if(getStatus().isRunning()) {
            throw new IllegalStateException();
        }
        // clear the scripted events collection and then add the passed
        //  events if there are any.  the contents of the events list passed
        //  in dictates the mode of the exchange
        if(inEvents != null &&
           !inEvents.isEmpty()) {
            STARTING_SCRIPTED_EXCHANGE.info(SimulatedExchange.class,
                                            getName());
            setStatus(Status.SCRIPTED);
            doScriptedTicks(inEvents);
            setStatus(Status.COMPLETE);
        } else {
            STARTING_RANDOM_EXCHANGE.info(SimulatedExchange.class,
                                          getName());
            setStatus(Status.RANDOM);
            // set up a job to run a tick every second until stopped
            ticker = executor.scheduleAtFixedRate(new Runnable()
            {
                public void run()
                {
                    try {
                        executeTick();
                    } catch (Exception e) {
                        SIMULATED_EXCHANGE_TICK_ERROR.warn(SimulatedExchange.class,
                                                           e,
                                                           getName());
                    }
                }
            },
                                                  0,
                                                  1,
                                                  TimeUnit.SECONDS);
            // prepare to execute ticks
            readyForTick.set(true);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.Exchange#stop()
     */
    @Override
    public synchronized void stop()
    {
        try {
            if(!getStatus().isRunning()) {
                throw new IllegalStateException();
            }
            STOPPING_SIMULATED_EXCHANGE.info(SimulatedExchange.class,
                                             getName());
            // turn off the update engine
            if(ticker != null) {
                ticker.cancel(true);
                executor.purge();
            }
            books.clear();
        } finally {
            setStatus(Status.STOPPED);
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
     * Get the max Depth value.
     *
     * @return an <code>int</code> value
     */
    public int getMaxDepth()
    {
        return maxDepth;
    }
    /**
     * Executes an asynchronous request with the given parameters. 
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     * @param inRequestType a <code>Type</code> value
     * @return a <code>Token</code> value
     */
    private Token doAsynchronousRequest(ExchangeRequest inExchangeRequest,
                                        ISubscriber inSubscriber,
                                        Type inRequestType)
    {
        long startingTime = System.currentTimeMillis();
        long requestId = requestCounter.incrementAndGet();
        try {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received asynchronous {} request: {}, assigned id: {}", //$NON-NLS-1$
                                   this,
                                   inRequestType,
                                   inExchangeRequest,
                                   requestId);
            // validate the request and subscriber
            validateAsynchronousRequest(inExchangeRequest,
                                        inSubscriber);
            // the request and subscriber are both valid
            // do any prep work for the instruments in the request
            updateInfo(inExchangeRequest);
            // shared and private instrument books are prepared and ready
            // this is the list that will hold all the books that this request touches
            List<PrivateInstrumentInfo> affectedBooks = new ArrayList<PrivateInstrumentInfo>();
            affectedBooks = getAllAffectedBooks(inExchangeRequest);
            // list of affected books is complete, now collect the instruments from each book
            List<Instrument> allAffectedInstruments = new ArrayList<Instrument>();
            // the list of instruments affected by this request is complete
            for(PrivateInstrumentInfo book : affectedBooks) {
                allAffectedInstruments.add(book.getInstrument());
            }
            Token token = FilteringSubscriber.subscribe(inSubscriber,
                                                        inRequestType,
                                                        allAffectedInstruments,
                                                        this,
                                                        inExchangeRequest); 
            return token;
        } finally {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} completing request {} in {}ms", //$NON-NLS-1$
                                   this,
                                   requestId,
                                   String.valueOf(System.currentTimeMillis() - startingTime));
        }
    }
    /**
     * Returns all books involved with the given request.
     * 
     * <p>Callers are guaranteed that the list of books returned is the complete list
     * at this time of books affected by the given request.
     * 
     * <p>All data (common and private) must have already been set up for this method
     * to complete successfully.  The caller must make sure that {@link #updateInfo(HasInstrument)}
     * has been invoked before calling this method.
     *
     * @param inExchangeRequest an <code>ExchangeRequest</code> value
     * @return a <code>List&lt;PrivateInstrumentInfo&gt;</code> value
     */
    private List<PrivateInstrumentInfo> getAllAffectedBooks(ExchangeRequest inExchangeRequest)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} searching for books relevant to {}", //$NON-NLS-1$
                               this,
                               inExchangeRequest);
        List<PrivateInstrumentInfo> affectedBooks = new ArrayList<PrivateInstrumentInfo>();
        // if the request specifies an instrument, that instrument is the only affected book
        if(inExchangeRequest.getInstrument() != null) {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "The request has a primary instrument {} - this is the only affected book", //$NON-NLS-1$
                                   inExchangeRequest.getInstrument());
            affectedBooks.add(getPrivateInstrumentInfo(inExchangeRequest.getInstrument()));
        } else {
            // instrument is null, this must be a request for the underlying instrument
            Instrument underlyingInstrument = inExchangeRequest.getUnderlyingInstrument();
            // this request does not specify an instrument, it *must* specify an underlying instrument
            //  or our understanding of ExchangeRequest is faulty
            assert(underlyingInstrument != null);
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "The request has no primary instrument, but does have an underlying instrument {}", //$NON-NLS-1$
                                   underlyingInstrument);
            SharedInstrumentInfo underlyingInfo = getSharedInstrumentInfo(underlyingInstrument);
            // underlyingInfo must already exist or somebody forgot to call updateInfo
            assert(underlyingInfo != null);
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "The underlying instrument has shared info: {}", //$NON-NLS-1$
                                   underlyingInfo);
            // get the instruments that make up the option chain
            for(Instrument optionChainInstrument : underlyingInfo.getOptionChain()) {
                PrivateInstrumentInfo book = getPrivateInstrumentInfo(optionChainInstrument);
                // the book for the instrument is supposed to already exist, also created during updateInfo
                assert(book != null);
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "Adding option chain book: {}", //$NON-NLS-1$
                                       book);
                affectedBooks.add(book);
            }
        }
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "Returning the following affected books: {}", //$NON-NLS-1$
                               affectedBooks);
        return affectedBooks;
    }
    /**
     * Validates that the given <code>ExchangeRequest</code> and <code>ISubscriber</code> are
     * appropriate to be used for an asynchronous market data request.
     *
     * @param inRequest an <code>ExchangeRequest</code> value
     * @param inSubscriber an <code>ISubscriber</code> value
     */
    private void validateAsynchronousRequest(ExchangeRequest inRequest,
                                             ISubscriber inSubscriber)
    {
        // no status check for the exchange because subscription requests may be submitted any time
        if(inSubscriber == null) {
            throw new NullPointerException();
        }
        doCommonValidation(inRequest);
    }
    /**
     * Validates that the given <code>ExchangeRequest</code> is
     * appropriate to be used for a synchronous market data request.
     *
     * @param inRequest an <code>ExchangeRequest</code> value
     * @throws IllegalStateException if the exchange is not running
     */
    private void validateSynchronousRequest(ExchangeRequest inRequest)
    {
        doCommonValidation(inRequest);
        // for a synchronous request, the exchange must be running
        if(!getStatus().isRunning()) {
            throw new IllegalStateException();
        }
    }
    /**
     * Validates that the given <code>ExchangeRequest</code> is
     * appropriate to be used for any market data request.
     *
     * @param inRequest an <code>ExchangeRequest</code> value
     */
    private void doCommonValidation(ExchangeRequest inRequest)
    {
        if(inRequest == null) {
            throw new NullPointerException();
        }
    }
    /**
     * Sets the status value.
     *
     * @param a <code>Status</code> value
     */
    private void setStatus(Status inStatus)
    {
        status = inStatus;
    }
    /**
     * Gets the <code>PrivateInstrumentInfo</code> associated with the given
     * <code>Instrument</code>.
     * 
     * <p>The <code>PrivateInstrumentInfo</code> must already exist.  It is the
     * caller's responsibility to make sure this is the case by making sure that
     * {@link #updateInfo(HasInstrument)} is called before invoking this method.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>PrivateInstrumentInfo</code> value
     */
    private PrivateInstrumentInfo getPrivateInstrumentInfo(Instrument inInstrument)
    {
        PrivateInstrumentInfo info = books.get(inInstrument);
        assert(info != null);
        return info;
    }
    /**
     * Updates shared and private exchange information using the given <code>HasInstrument</code>
     * value.
     * 
     * <p>This method will set up the exchange to handle the given instrument.  This method
     * may be called more than once with the same <code>HasInstrument</code> with no ill effect.
     *
     * @param inInstrumentProvider a <code>HasInstrument</code> value
     */
    private void updateInfo(HasInstrument inInstrumentProvider)
    {
        updateSharedInfo(inInstrumentProvider);
        updatePrivateInfo(inInstrumentProvider);
    }
    /**
     * Updates the private exchange information using the given <code>HasInstrument</code> value.
     * 
     * <p>This method will set up the exchange to handle the given instrument.  This method
     * may be called more than once with the same <code>HasInstrument</code> with no ill effect.
     *
     * @param inInstrumentProvider a <code>HasInstrument</code> value
     */
    private synchronized void updatePrivateInfo(HasInstrument inInstrumentProvider)
    {
        // this method is synchronized because of the put-if-absent performed on books
        Instrument primaryInstrument = inInstrumentProvider.getInstrument();
        // the instrument might be null if we're dealing with underlying-only
        if(primaryInstrument != null) {
            PrivateInstrumentInfo book = books.get(inInstrumentProvider.getInstrument());
            if(book == null) {
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} creating book for {}", //$NON-NLS-1$
                                       this,
                                       inInstrumentProvider.getInstrument());
                book = new PrivateInstrumentInfo(inInstrumentProvider.getInstrument());
                books.put(inInstrumentProvider.getInstrument(),
                          book);
                if(getStatus().isRunning() &&
                   getStatus() == Status.RANDOM) {
                    // set some initial data in the book
                    doRandomBookTick(book);
                }
            }
        }
        // create a book for the underlying instrument, if applicable
        if(inInstrumentProvider instanceof HasUnderlyingInstrument) {
            Instrument underlyingInstrument = ((HasUnderlyingInstrument)inInstrumentProvider).getUnderlyingInstrument();
            if(underlyingInstrument != null) {
                // there is an underlying instrument present - make sure it has a book, too
                PrivateInstrumentInfo underlyingBook = books.get(underlyingInstrument);
                if(underlyingBook == null) {
                    underlyingBook = new PrivateInstrumentInfo(underlyingInstrument);
                    books.put(underlyingInstrument,
                              underlyingBook);
                    if(getStatus().isRunning() &&
                       getStatus() == Status.RANDOM) {
                        // set some initial data in the book
                        doRandomBookTick(underlyingBook);
                    }
                }
                // there may be entries in the option chain for the underlying that do
                //  not yet have books in this exchange (added by another exchange, e.g.)
                // they need to be added here
                SharedInstrumentInfo sharedInfo = getSharedInstrumentInfo(underlyingInstrument);
                assert(sharedInfo != null);
                Collection<FilteringSubscriber> interestedSubscribers = optionChainSubscribers.get(underlyingInstrument);
                for(final Instrument optionChainInstrument : sharedInfo.getOptionChain()) {
                    updateInfo(new HasInstrument() {
                        @Override
                        public Instrument getInstrument()
                        {
                            return optionChainInstrument;
                        }
                        @Override
                        public String getInstrumentAsString()
                        {
                            return optionChainInstrument.getSymbol();
                        }
                    });
                    // make sure that any subscribers interested in the underlying instrument get the opportunity
                    //  to find out about what may be a new (to it) entry in the option chain
                    if(interestedSubscribers != null) {
                        for(FilteringSubscriber subscriber : interestedSubscribers) {
                            subscriber.noticeOfOptionChainEntry(optionChainInstrument);
                        }
                    }
                }
            }
        }
    }
    /**
     * Executes all the ticks of the exchange script in <code>SCRIPTED</code> mode.
     * 
     * @param inScriptedEvents a <code>List&lt;QuoteEvent&gt;</code> value
     */
    private void doScriptedTicks(List<QuoteEvent> inScriptedEvents)
    {
        for(QuoteEvent event : inScriptedEvents) {
            try {
                // verify the event is for this exchange
                if(!getCode().equals(event.getExchange())) {
                    throw new IllegalArgumentException(SIMULATED_EXCHANGE_CODE_MISMATCH.getText(this,
                                                                                                event,
                                                                                                event.getExchange(),
                                                                                                getCode()));
                }
                // prepare the exchange to handle the event's instrument
                updateInfo(event);
                // find the book that goes with the event
                PrivateInstrumentInfo book = getPrivateInstrumentInfo(event.getInstrument());
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} executing scripted event {}", //$NON-NLS-1$
                                       this,
                                       event);
                // process the event
                book.process(event);
                // settle the book as a result of this change
                Deque<MarketDataEvent> eventsToPublish = Lists.newLinkedList(settleBook(book));
                // note that the events from processing the event and settling the book are all published in one
                //  batch.  this has functional implications because it means that several interim top-of-book states
                //  may be compressed into one.  this is the most correct behavior because an un-settled book
                //  is a sub-atomic state
                publishEvents(eventsToPublish);
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
     * Publishes the given events to interested subscribers.
     *
     * @param inEventsToPublish a <code>Deque&lt;? extends Event&gt;</code> value
     */
    private void publishEvents(Deque<? extends Event> inEventsToPublish)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} publishing events: {}", //$NON-NLS-1$
                               this,
                               inEventsToPublish);
        if(inEventsToPublish.isEmpty()) {
            return;
        }
        Event lastEvent = inEventsToPublish.getLast();
        if(lastEvent instanceof HasEventType) {
            ((HasEventType)lastEvent).setEventType(EventType.UPDATE_FINAL);
        }
        for(Event event : inEventsToPublish) {
            publisher.publish(event);
        }
    }
    /**
     * Executes one round of processing for all instruments.
     */
    private void executeTick()
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "{} beginning tick {} at {}", //$NON-NLS-1$
                               this,
                               iterationCounter.incrementAndGet(),
                               DateUtils.dateToString(new Date()));
        // if the previous tick hasn't completed yet, skip this tick and wait for the next one
        if(readyForTick.getAndSet(false)) {
            // the previous tick has completed, so we can begin this one
            long startTime = System.currentTimeMillis();
            try {
                for(PrivateInstrumentInfo book : books.values()) {
                    doRandomBookTick(book);
                }
            } finally {
                // indicate that we're ready for the next tick
                readyForTick.set(true);
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} completed tick {} after {} ms", //$NON-NLS-1$
                                       this,
                                       iterationCounter.get(),
                                       System.currentTimeMillis() - startTime);
            }
        } else {
            // the previous tick has not yet completed, skip this one
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} skipped tick {}", //$NON-NLS-1$
                                   this,
                                   iterationCounter.get()); //$NON-NLS-1$
        }
    }
    /**
     * Executes a single tick for the given order book.
     *
     * @param inBook an <code>OrderBookWrapper</code> value
     */
    private void doRandomBookTick(PrivateInstrumentInfo inBook)
    {
        // adjust the order book base value
        inBook.adjustPrice();
        // settle the book (generates additional activity which needs to be published)
        Deque<Event> eventsToPublish = Lists.newLinkedList();
        eventsToPublish.addAll(settleBook(inBook));
        // produce statistics
        eventsToPublish.addAll(getStatistics(ExchangeRequestBuilder.newRequest().withInstrument(inBook.getBook().getInstrument())
                                                                                .withUnderlyingInstrument(inBook.getUnderlyingInstrument()).create()));
        if(inBook.getInstrument() instanceof Equity) {
            eventsToPublish.addAll(getDividends(ExchangeRequestBuilder.newRequest().withInstrument(inBook.getBook().getInstrument()).create()));
        }
        publishEvents(eventsToPublish);
    }
    /**
     * Determines the correct <code>ExpirationType</code> to use for the given <code>Option</code>.
     *
     * @param inOption an <code>Option</code> value
     * @return an <code>ExpirationType</code>
     */
    private static ExpirationType getExpirationType(Option inOption)
    {
        // this is entirely an arbitrary choice: our exchanges deal with options with American-style expiration, apparently
        return ExpirationType.AMERICAN;
    }
    /**
     * Gets the <code>SharedInstrumentInfo</code> associated with the given
     * <code>Instrument</code>.
     * 
     * <p>The <code>SharedInstrumentInfo</code> must already exist.  It is the
     * caller's responsibility to make sure this is the case by making sure that
     * {@link #updateInfo(HasInstrument)} is called before invoking this method.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>SharedInstrumentInfo</code> value
     */
    private static SharedInstrumentInfo getSharedInstrumentInfo(Instrument inInstrument)
    {
        SharedInstrumentInfo info = sharedInstruments.get(inInstrument); 
        assert(info != null);
        return info;
    }
    /**
     * Updates the shared exchange information using the given <code>HasInstrument</code> value.
     * 
     * <p>This method will set up the exchange to handle the given instrument.  This method
     * may be called more than once with the same <code>HasInstrument</code> with no ill effect.
     *
     * @param inInstrumentProvider a <code>HasInstrument</code> value
     */
    private static synchronized void updateSharedInfo(HasInstrument inInstrumentProvider)
    {
        // this method is synchronized because of the put-if-absent performed on sharedInstruments
        // figure out what information we have
        Instrument instrument = inInstrumentProvider.getInstrument();
        Instrument underlyingInstrument = null;
        if(inInstrumentProvider instanceof HasUnderlyingInstrument) {
            HasUnderlyingInstrument underlyingInstrumentProvider = (HasUnderlyingInstrument)inInstrumentProvider;
            underlyingInstrument = underlyingInstrumentProvider.getUnderlyingInstrument();
        }
        // check to see if we already know about this instrument
        if(instrument != null) {
            SharedInstrumentInfo info = sharedInstruments.get(instrument);
            if(info == null) {
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} is a new instrument, creating common info for it", //$NON-NLS-1$
                                       instrument);
                // this instrument is new, update the shared info (underlyingInstrument may be null, that's OK)
                info = new SharedInstrumentInfo(instrument,
                                                underlyingInstrument);
                sharedInstruments.put(instrument,
                                      info);
            }
            // sharedInstruments is updated and info is non-null
        }
        // if an underlying instrument is present, get that info, too
        if(underlyingInstrument != null) {
            SharedInstrumentInfo underlyingInfo = sharedInstruments.get(underlyingInstrument);
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} has an underlying instrument {} and shared info {}", //$NON-NLS-1$
                                   inInstrumentProvider,
                                   underlyingInstrument,
                                   underlyingInfo);
            // the presence of a non-null underlying instrument means that the primary instrument *must* be
            //  an option (if specified)
            assert(instrument == null || instrument instanceof Option);
            if(underlyingInfo == null) {
                // the info for the underlying instrument doesn't exist yet - create it
                underlyingInfo = new SharedInstrumentInfo(underlyingInstrument,
                                                          null);
                sharedInstruments.put(underlyingInstrument,
                                      underlyingInfo);
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "Created new underlying info {}", //$NON-NLS-1$
                                       underlyingInfo);
            }
            // if there's an instrument (which we now know is an option), add it to the underlying's
            //  option chain (might already be there, but won't hurt to add it again)
            if(instrument != null) {
                underlyingInfo.addToOptionChain(instrument);
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "Adding {} to option chain: {}", //$NON-NLS-1$
                                       instrument,
                                       underlyingInfo);
            }
        }
    }
    /**
     * Examines the given <code>PrivateInstrumentInfo</code>, matching bids and asks
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
     * <p>This method requires exclusive access to the <code>PrivateInstrumentInfo</code> but
     * does not perform any synchronization explicitly.  It is the caller's
     * responsibility to guarantee exclusive access to the <code>PrivateInstrumentInfo</code>.
     * 
     * @param inBook a <code>PrivateInstrumentInfo</code> value
     * @return a <code>List&lt;MarketDataEvent&gt;</code> value containing the events created, if any, to settle the book
     */
    private static List<MarketDataEvent> settleBook(PrivateInstrumentInfo inBook)
    {
        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                               "Settling book for {}: OrderBook starts at\n{}", //$NON-NLS-1$
                               inBook,
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
                                       "Settler looking for matches for {}", //$NON-NLS-1$
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
                        TradeEventBuilder<TradeEvent> tradeBuilder = TradeEventBuilder.tradeEvent(bid.getInstrument()).withEventType(EventType.UPDATE_PART)
                                                                                                                      .withExchange(bid.getExchange())
                                                                                                                      .withPrice(tradePrice)
                                                                                                                      .withSize(tradeSize)
                                                                                                                      .withTradeDate(DateUtils.dateToString(new Date(tradeTime)));
                        if(bid.getInstrument() instanceof Option) {
                            tradeBuilder.withExpirationType(getExpirationType((Option)bid.getInstrument()));
                            tradeBuilder.withUnderlyingInstrument(inBook.getUnderlyingInstrument());
                        }
                        if(bid.getInstrument() instanceof Future) {
                            tradeBuilder.withContractSize(100)
                                        .withDeliveryType(DeliveryType.PHYSICAL)
                                        .withStandardType(StandardType.STANDARD);
                        }
                        TradeEvent trade = tradeBuilder.create();
                        // these events are used to modify the orders in the book
                        BidEvent bidCorrection;
                        AskEvent askCorrection;
                        if(tradeSize.compareTo(bidSize) == -1) {
                            // trade is smaller than the bid, this is a partial fill
                            bidCorrection = QuoteEventBuilder.change(bid,
                                                                     new Date(tradeTime),
                                                                     bidSize.subtract(tradeSize));
                            bidCorrection.setEventType(EventType.UPDATE_PART);
                            askCorrection = QuoteEventBuilder.delete(ask); 
                            askCorrection.setEventType(EventType.UPDATE_PART); 
                        } else {
                            // trade is equal to the bid, this is a full fill
                            bidCorrection = QuoteEventBuilder.delete(bid);
                            bidCorrection.setEventType(EventType.UPDATE_PART);
                            askCorrection = tradeSize.equals(askSize) ? QuoteEventBuilder.delete(ask) :
                                                                        QuoteEventBuilder.change(ask,
                                                                                                 new Date(tradeTime),
                                                                                                 askSize.subtract(tradeSize));
                            askCorrection.setEventType(EventType.UPDATE_PART); 
                        }
                        // adjust the remainder we need to fill
                        bidSize = bidSize.subtract(tradeSize);
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "OrderBookSettler is creating the following events:\n{}\n{}\n{}", //$NON-NLS-1$
                                               trade,
                                               bidCorrection,
                                               askCorrection);
                        // post events to the feed's internal book
                        inBook.setLatestTrade(trade);
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
                                   "Book settling complete for {}, OrderBook is now\n{}", //$NON-NLS-1$
                                   inBook,
                                   inBook.getBook());
        }
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
    // immutable state of this exchange
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
     * indicates if the previous tick processing has completed or not
     */
    private final AtomicBoolean readyForTick = new AtomicBoolean(true);
    /**
     * the order books for the instruments managed by this exchange
     */
    private final Map<Instrument,PrivateInstrumentInfo> books = new ConcurrentHashMap<Instrument,PrivateInstrumentInfo>();
    /**
     * counter used to identify ticks in {@link Status#RANDOM} mode 
     */
    private final AtomicLong iterationCounter = new AtomicLong(0);
    /**
     * counter used to identify market data requests, both synchronous and asynchronous
     */
    private final AtomicLong requestCounter = new AtomicLong(0);
    /**
     * set of subscribers who are interested in the option chain of this object
     */
    private final Multimap<Instrument,FilteringSubscriber> optionChainSubscribers;
    // mutable state of this exchange
    /**
     * the exchange status
     */
    private volatile Status status;
    /**
     * stores the handle for the task submitted to the scheduler to run updates on this exchange
     */
    private volatile ScheduledFuture<?> ticker = null;
    // common to all exchanges
    // immutable state of all exchanges
    /**
     * value used to add to or subtract from prices
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01"); //$NON-NLS-1$
    /**
     * the number of milliseconds in one hour
     */
    private static final long HOURms = 1000l * 60l * 60l;
    /**
     * the number of milliseconds in one day
     */
    private static final long DAYms = HOURms * 24l;
    /**
     * random generator used to manipulate prices
     */
    private static final Random random = new Random(System.nanoTime());
    /**
     * data for instruments shared across exchanges
     */
    private static final Map<Instrument,SharedInstrumentInfo> sharedInstruments = new ConcurrentHashMap<Instrument,SharedInstrumentInfo>();
    /**
     * mechanism which manages the threads that create the market data
     */
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    /**
     * publishes events generated by order books from exchanges and manages subscriptions 
     */
    private static final PublisherEngine publisher = new PublisherEngine(true);
    // inner classes
    /**
     * The status of the exchange.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ClassVersion("$Id$")
    @ThreadSafe
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
    }
    /**
     * Compares <code>Instrument</code> values in the context of their use
     * as underlying instruments and option chain instruments.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    @ThreadSafe
    private static enum InstrumentComparator
            implements Comparator<Instrument>
    {
        INSTANCE;
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Instrument inO1,
                           Instrument inO2)
        {
            // this comparator will be used for two different cases:
            //  1) comparing underlying instrument to underlying instrument (straight symbol compare with no tie-breaker)
            //  2) comparing option-chain entry to option-chain entry (symbol, expiry, strike, type in order)
            int result = inO1.getSymbol().compareTo(inO2.getSymbol());
            if(result != 0) {
                return result;
            }
            if(inO1 instanceof Option &&
               inO2 instanceof Option) {
                Option option1 = (Option)inO1;
                Option option2 = (Option)inO2;
                result = option1.getExpiry().compareTo(option2.getExpiry());
                if(result != 0) {
                    return result;
                }
                result = option1.getStrikePrice().compareTo(option2.getStrikePrice());
                if(result != 0) {
                    return result;
                }
                return option1.getType().compareTo(option2.getType());
            }
            return 0;
        }
    }
    /**
     * Holds information specific to a particular exchange for a given <code>Instrument</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    @ThreadSafe
    @ClassVersion("$Id$")
    private class PrivateInstrumentInfo
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("%s [latest=%s value=%s]", //$NON-NLS-1$
                                 instrument,
                                 latestTrade,
                                 value);
        }
        /**
         * Gets the underlying instrument for this instrument, if any.
         *
         * @return an <code>Instrument</code> value or <code>null</code>
         */
        private Instrument getUnderlyingInstrument()
        {
            return getSharedInstrumentInfo(getInstrument()).getUnderlyingInstrument();
        }
        /**
         * Create a new PrivateInstrumentInfo instance.
         * 
         * <p>The given instrument must exist in the shared exchange information.
         *
         * @param inInstrument an <code>Instrument</code> value
         */
        private PrivateInstrumentInfo(Instrument inInstrument)
        {
            assert(inInstrument != null);
            instrument = inInstrument;
            latestTrade = null;
            SharedInstrumentInfo sharedInfo = getSharedInstrumentInfo(instrument);
            assert(sharedInfo != null);
            setValue(sharedInfo.getMostRecentValue());
            book = new OrderBook(instrument,
                                 getMaxDepth());
        }
        /**
         * Applies the changes implied by the given <code>Event</code> to
         * this book, if any.
         * 
         * <p>If the event is not relevant to this book, this method does nothing.
         * 
         * @param inEvent an <code>Event</code> value
         * @return a <code>Deque&lt;Event&gt;</code> value containing the events
         *  produced by the changes
         */
        private Deque<Event> process(Event inEvent)
        {
            Deque<Event> newEvents = Lists.newLinkedList();
            if(inEvent instanceof QuoteEvent) {
                QuoteEvent displacedEvent = book.process((QuoteEvent)inEvent);
                newEvents.add(inEvent);
                if(displacedEvent != null) {
                    QuoteEvent deleteQuote = QuoteEventBuilder.delete(displacedEvent);
                    deleteQuote.setEventType(EventType.UPDATE_PART);
                    newEvents.add(deleteQuote);
                }
            }
            publishEvents(newEvents);
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} processed {} and produced for publication: {}", //$NON-NLS-1$
                                   this,
                                   inEvent,
                                   newEvents);
            return newEvents;
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
            Instrument marketInstrument = getBook().getInstrument();
            // create an ask event builder
            QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.askEvent(marketInstrument);
            askBuilder.withEventType(EventType.UPDATE_PART)
                      .withExchange(getCode())
                      .withPrice(getValue().add(PENNY))
                      .withSize(randomInteger(10000))
                      .withCount(randomInteger(10).intValueExact()+1)
                      .withQuoteDate(DateUtils.dateToString(timestamp));
            // and a bid event builder
            QuoteEventBuilder<BidEvent> bidBuilder = QuoteEventBuilder.bidEvent(marketInstrument);
            bidBuilder.withEventType(EventType.UPDATE_PART)
                      .withExchange(getCode())
                      .withPrice(getValue().subtract(PENNY))
                      .withSize(randomInteger(10000))
                      .withCount(randomInteger(10).intValueExact()+1)
                      .withQuoteDate(DateUtils.dateToString(timestamp));
            if(marketInstrument instanceof Option) {
                Instrument underlyingInstrument = getUnderlyingInstrument();
                assert(underlyingInstrument != null);
                askBuilder.withExpirationType(getExpirationType((Option)marketInstrument));
                bidBuilder.withExpirationType(getExpirationType((Option)marketInstrument));
                askBuilder.withUnderlyingInstrument(underlyingInstrument);
                bidBuilder.withUnderlyingInstrument(underlyingInstrument);
            }
            if(marketInstrument instanceof Future) {
                askBuilder.withContractSize(100)
                          .withDeliveryType(DeliveryType.PHYSICAL)
                          .withStandardType(StandardType.STANDARD);
                bidBuilder.withContractSize(100)
                          .withDeliveryType(DeliveryType.PHYSICAL)
                          .withStandardType(StandardType.STANDARD);
            }
            // create the events
            process(askBuilder.create());
            process(bidBuilder.create());
        }
        /**
         * Get the book value.
         *
         * @return an <code>OrderBook</code> value
         */
        private OrderBook getBook()
        {
            return book;
        }
        /**
         * Get the value value.
         *
         * @return a <code>BigDecimal</code> value
         */
        private BigDecimal getValue()
        {
            return value;
        }
        /**
         * Sets the value value.
         *
         * @param a <code>BigDecimal</code> value
         */
        private void setValue(BigDecimal inValue)
        {
            value = inValue;
        }
        /**
         * Get the latestTrade value.
         *
         * @return a <code>TradeEvent</code> value
         */
        private TradeEvent getLatestTrade()
        {
            return latestTrade;
        }
        /**
         * Sets the latestTrade value.
         *
         * @param a <code>TradeEvent</code> value
         */
        private void setLatestTrade(TradeEvent inLatestTrade)
        {
            latestTrade = inLatestTrade;
        }
        /**
         * Get the instrument value.
         *
         * @return a <code>Instrument</code> value
         */
        private Instrument getInstrument()
        {
            return instrument;
        }
        // immutable state
        /**
         * the instrument of the book
         */
        private final Instrument instrument;
        /**
         * the order book itself
         */
        private final OrderBook book;
        // mutable state
        /**
         * the most recent value of the instrument
         */
        private volatile BigDecimal value;
        /**
         * the most recent trade of the instrument
         */
        private volatile TradeEvent latestTrade;
    }
    /**
     * Holds information common to all exchanges for a given <code>Instrument</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    @ThreadSafe
    @ClassVersion("$Id$")
    private static class SharedInstrumentInfo
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("%s [value: %s underlying: %s option chain: %s]", //$NON-NLS-1$
                                 instrument,
                                 mostRecentValue,
                                 underlyingInstrument,
                                 optionChain);
        }
        /**
         * Create a new SharedInstrumentInfo instance.
         *
         * @param inInstrument an <code>Instrument</code> value
         * @param inUnderlyingInstrument an <code>Instrument</code> value
         */
        private SharedInstrumentInfo(Instrument inInstrument,
                                     Instrument inUnderlyingInstrument)
        {
            instrument = inInstrument;
            underlyingInstrument = inUnderlyingInstrument;
            setMostRecentValue(randomDecimal(100).add(PENNY));
            // dividends may be issued for equities only
            List<DividendEvent> tempDividends = new ArrayList<DividendEvent>();
            if(inInstrument instanceof Equity) {
                // there is always a current (most recent) dividend
                long timestamp = System.currentTimeMillis();
                long oneDay = 1000 * 60 * 60 * 24;
                long oneQuarter = oneDay * 90; // approximate, not really important
                DividendEventBuilder builder = DividendEventBuilder.dividend().withEquity((Equity)inInstrument);
                tempDividends.add(builder.withAmount(randomDecimal(10).add(PENNY))
                                  .withEventType(EventType.UPDATE_PART)
                                  .withCurrency("USD") //$NON-NLS-1$
                                  .withDeclareDate(DateUtils.dateToString(new Date(timestamp - ((randomInteger(60).longValue() + 1) * oneDay)),
                                                                          DateUtils.DAYS))
                                  .withExecutionDate(DateUtils.dateToString(new Date(timestamp - ((randomInteger(60).longValue() + 1) * oneDay)),
                                                                            DateUtils.DAYS))
                                  .withFrequency(DividendFrequency.QUARTERLY)
                                  .withPaymentDate(DateUtils.dateToString(new Date(timestamp - ((randomInteger(60).longValue() + 1) * oneDay)),
                                                                          DateUtils.DAYS))
                                  .withRecordDate(DateUtils.dateToString(new Date(timestamp - ((randomInteger(60).longValue() + 1) * oneDay)),
                                                                         DateUtils.DAYS))
                                  .withStatus(DividendStatus.OFFICIAL)
                                  .withType(DividendType.CURRENT).create());
                // that establishes the current dividend
                // now create, say, 3 more UNOFFICIAL future dividends
                for(long quarterCounter=1;quarterCounter<=3;quarterCounter++) {
                    tempDividends.add(builder.withDeclareDate(null)
                                      .withEventType(EventType.UPDATE_PART)
                                      .withPaymentDate(null)
                                      .withRecordDate(null)
                                      .withExecutionDate(DateUtils.dateToString(new Date(timestamp + oneQuarter * quarterCounter),
                                                                                DateUtils.DAYS))
                                      .withStatus(DividendStatus.UNOFFICIAL)
                                      .withType(DividendType.FUTURE).create());
                }
            }
            dividends = ImmutableList.copyOf(tempDividends);
        }
        /**
         * Get the underlyingInstrument value.
         *
         * @return an <code>Instrument</code> value
         */
        private Instrument getUnderlyingInstrument()
        {
            return underlyingInstrument;
        }
        /**
         * Get the instrument value.
         *
         * @return a <code>Instrument</code> value
         */
        @SuppressWarnings("unused") // I know this isn't used, but it just feels wrong not to have the instrument on this object
        private Instrument getInstrument()
        {
            return instrument;
        }
        /**
         * Gets the current option chain for this instrument.
         *
         * @return a <code>SortedSet&lt;Instrument&gt;</code> value (may be empty)
         */
        private SortedSet<Instrument> getOptionChain()
        {
            return Collections.unmodifiableSortedSet(optionChain);
        }
        /**
         * Adds the given <code>Instrument</code> to this instrument's option chain.
         * 
         * <p>Adding the same <code>Instrument</code> more than once has no effect.
         *
         * @param inInstrument an <code>Instrument</code> value
         */
        private void addToOptionChain(Instrument inInstrument)
        {
            optionChain.add(inInstrument);
        }
        /**
         * Get the mostRecentValue value.
         *
         * @return a <code>BigDecimal</code> value
         */
        private BigDecimal getMostRecentValue()
        {
            return mostRecentValue;
        }
        /**
         * Sets the mostRecentValue value.
         *
         * @param a <code>BigDecimal</code> value
         */
        private void setMostRecentValue(BigDecimal inMostRecentValue)
        {
            mostRecentValue = inMostRecentValue;
        }
        /**
         * Gets the dividends for this <code>Instrument</code>, if any.
         *
         * @return a <code>List&lt;DividendEvent&gt;</code> value
         */
        private List<DividendEvent> getDividends()
        {
            return dividends;
        }
        /**
         * the instrument of the shared info
         */
        private final Instrument instrument;
        /**
         * the underlying instrument of the shared info (may be null)
         */
        private final Instrument underlyingInstrument;
        /**
         * most recent value seen at any exchange for the instrument
         */
        private volatile BigDecimal mostRecentValue;
        /**
         * option chain of this instrument (may be empty)
         */
        private final SortedSet<Instrument> optionChain = Collections.synchronizedSortedSet(new TreeSet<Instrument>(InstrumentComparator.INSTANCE));
        /**
         * contains the dividends, if any, issued for this instrument
         */
        private final List<DividendEvent> dividends;
    }
    /**
     * <code>ISubscriber</code> that filters publications to an enclosed <code>ISubscriber</code>
     * based on the original type of market data request and instrument.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.5.0
     */
    @ThreadSafe
    @ClassVersion("$Id$")
    private static class FilteringSubscriber
            implements ISubscriber
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("FilteringSubscriber for %s on %s watching %s", //$NON-NLS-1$
                                 type,
                                 exchange,
                                 instruments);
        }
        /**
         * Subscribes the given <code>ISubscriber</code> to market data updates of the given
         * type for the given instrument. 
         *
         * @param inOriginalSubscriber an <code>ISubscriber</code> value
         * @param inType a <code>Type</code> value
         * @param inInstruments a <code>Collection&lt;Instrument&gt;</code> value
         * @param inExchange a <code>SimulatedExchange</code> value containing the owning exchange
         * @param inExchangeRequest an <code>ExchangeRequest</code> value containing the request
         * @return a <code>Token</code> value representing the subscription
         */
        private static Token subscribe(ISubscriber inOriginalSubscriber,
                                       Type inType,
                                       Collection<Instrument> inInstruments,
                                       SimulatedExchange inExchange,
                                       ExchangeRequest inExchangeRequest)
        {
            FilteringSubscriber subscriber = new FilteringSubscriber(inOriginalSubscriber,
                                                                     inType,
                                                                     inInstruments,
                                                                     inExchange);
            // it's possible if this request is by underlying
            //  instrument that entries will be added to the underlying instrument's option chain later.
            //  results will be needed to be returned for these new entries, too.  obviously, the books aren't
            //  available before the symbol exists.  there needs to be a way for this subscriber to be notified
            //  when a new book is created for an option chain entry for the 
            // set up the subscriber to be notified if new option chain entries are added for the underlying
            if(inExchangeRequest.isForUnderlyingOnly()) {
                inExchange.optionChainSubscribers.put(inExchangeRequest.getUnderlyingInstrument(),
                                                      subscriber);
            }
            publisher.subscribe(subscriber);
            return subscriber.getToken();
        }
        /**
         * Create a new FilteringSubscriber instance.
         *
         * @param inSubscriber an <code>ISubscriber</code> value
         * @param inType a <code>Type</code> value
         * @param inInstruments a <code>Collection&lt;Instrument&gt;</code> value
         * @param inExchange a <code>SimulatedExchange</code> value containing the owning exchange
         */
        private FilteringSubscriber(ISubscriber inSubscriber,
                                    Type inType,
                                    Collection<Instrument> inInstruments,
                                    SimulatedExchange inExchange)
        {
            originalSubscriber = inSubscriber;
            type = inType;
            instruments.addAll(inInstruments);
            token = new Token(this);
            exchange = inExchange;
            Multimap<Instrument,DividendEvent> dividends = HashMultimap.create();
            lastKnownDividends = Multimaps.synchronizedMultimap(dividends);
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
                if(!instruments.contains(((HasInstrument)inData).getInstrument())) {
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "{} not interested in {}", //$NON-NLS-1$
                                           this,
                                           inData);
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
                case DIVIDENDS :
                    return inData instanceof DividendEvent;
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
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "Subscriber {} received {} to examine", //$NON-NLS-1$
                                   this,
                                   inData);
            if(type == Type.TOP_OF_BOOK) {
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} has subscribed to top-of-book - further analysis required", //$NON-NLS-1$
                                       this,
                                       inData);
                // top-of-book is a special case.  first, if we get this far, then the
                //  class of inData *should* be QuoteEvent, but let's not bank on that
                if(inData instanceof QuoteEvent) {
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "{} is a quote, continuing", //$NON-NLS-1$
                                           this);
                    QuoteEvent quoteEvent = (QuoteEvent)inData;
                    // a QuoteEvent for top-of-book is an opportunity for a new top-of-book state
                    //  for the exchange; an opportunity but *not* a guarantee
                    // first, check to see if there is, in fact, a new top-of-book state for the
                    //  exchange
                    TopOfBook newTopOfBook = null;
                    ExchangeRequestBuilder topOfBookRequestBuilder = ExchangeRequestBuilder.newRequest().withInstrument(quoteEvent.getInstrument());
                    if(quoteEvent instanceof OptionEvent) {
                        topOfBookRequestBuilder.withUnderlyingInstrument(((OptionEvent)quoteEvent).getUnderlyingInstrument());
                    }
                    newTopOfBook = makeTopOfBook(exchange.getTopOfBook(topOfBookRequestBuilder.create()));
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "New top-of-book is {}", //$NON-NLS-1$
                                           newTopOfBook);
                    TopOfBook lastKnownTopOfBook = lastKnownTopsOfBook.get(quoteEvent.getInstrument());
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "Last-known top-of-book is {}", //$NON-NLS-1$
                                           lastKnownTopOfBook);
                    // we are guaranteed that this object is non-null, but its components may be null
                    // check to see if the quote event caused a change in the top-of-book state
                    if(newTopOfBook.compareTo(lastKnownTopOfBook) != 0) {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "New top-of-book is different from last-known top-of-book"); //$NON-NLS-1$
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
                        publishCurrentSideIfNecessary(newTopOfBook.getBid(),
                                                      (lastKnownTopOfBook == null ? null : lastKnownTopOfBook.getBid()));
                        // has the ask changed?
                        publishCurrentSideIfNecessary(newTopOfBook.getAsk(),
                                                      (lastKnownTopOfBook == null ? null : lastKnownTopOfBook.getAsk()));
                    } else {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "New top-of-book is *not* different from last-known top-of-book, nothing to do"); //$NON-NLS-1$
                    }
                    lastKnownTopsOfBook.put(quoteEvent.getInstrument(),
                                            newTopOfBook);
                }
                return;
            }
            if(type == Type.DIVIDENDS) {
                SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                       "{} has subscribed to dividend - make sure that the dividend has not already been seen", //$NON-NLS-1$
                                       this);
                if(inData instanceof DividendEvent) {
                    DividendEvent dividendEvent = (DividendEvent)inData;
                    Collection<DividendEvent> dividends = lastKnownDividends.get(dividendEvent.getEquity());
                    if(dividends != null &&
                       dividends.contains(dividendEvent)) {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "{} has already seen {} - do nothing", //$NON-NLS-1$
                                               this,
                                               dividendEvent);
                    } else {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "{} has not yet seen {} - publish", //$NON-NLS-1$
                                               this,
                                               dividendEvent);
                        originalSubscriber.publishTo(inData);
                        lastKnownDividends.put(dividendEvent.getEquity(),
                                               dividendEvent);
                    }
                }
                return;
            }
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} has subscribed to something other than top-of-book or dividend - publish {}", //$NON-NLS-1$
                                   this,
                                   inData);
            originalSubscriber.publishTo(inData);
        }
        /**
         * Notifies this publisher of an option chain entry for an underlying
         * instrument in which this publisher has expressed an interest.
         *
         * @param inOptionChainEntry an <code>Instrument</code> value
         */
        private void noticeOfOptionChainEntry(Instrument inOptionChainEntry)
        {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "{} received notification of a potential new option chain entry: {}", //$NON-NLS-1$
                                   this,
                                   inOptionChainEntry);
            instruments.add(inOptionChainEntry);
        }
        /**
         * Publishes the side of the book implied by the type of the given <code>QuoteEvent</code>,
         * if necessary. 
         *
         * @param inCurrentTop an <code>E</code> indicating the most recent top of the given side
         * @param inLastTop an <code>E</code> indicating the last known top of the given side.
         */
        private <E extends QuoteEvent> void publishCurrentSideIfNecessary(E inCurrentTop,
                                                                          E inLastTop)
        {
            SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                   "Considering current {} and last {} to see if current needs to be published", //$NON-NLS-1$
                                   inCurrentTop,
                                   inLastTop);
            if(inCurrentTop == null) {
                // there is no current top quote, was there one before?
                if(inLastTop != null) {
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "Last ({}) needs to be removed, publish as a delete", //$NON-NLS-1$
                                           inLastTop);
                    // yes, there used to be a top quote, but it should go away now
                    QuoteEvent delete = QuoteEventBuilder.delete(inLastTop);
                    delete.setEventType(EventType.UPDATE_PART);
                    originalSubscriber.publishTo(delete);
                } else {
                    // there didn't used to be a top quote, so don't do anything
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "Both current and last are null - nothing to do"); //$NON-NLS-1$
                }
            } else {
                // there is a current top quote, compare it to the one that used to be here
                if(inLastTop == null) {
                    SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                           "Last is null, publish {}", //$NON-NLS-1$
                                           inCurrentTop);
                    // there didn't used to be a top quote, just add the new one
                    QuoteEvent add = QuoteEventBuilder.add(inCurrentTop);
                    add.setEventType(EventType.UPDATE_PART);
                    originalSubscriber.publishTo(add);
                } else {
                    // there used to be a top quote, check to see if it's different than the current one
                    // btw, we know that current quote and last known quote are both non-null
                    if(PriceAndSizeComparator.instance.compare(inLastTop,
                                                               inCurrentTop) != 0) {
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "Non-null current is different from non-null last: publish"); //$NON-NLS-1$
                        QuoteEvent add = QuoteEventBuilder.add(inCurrentTop);
                        add.setEventType(EventType.UPDATE_PART);
                        originalSubscriber.publishTo(add);
                    } else {
                        // the current and previous tops are identical, so don't do anything
                        SLF4JLoggerProxy.debug(SimulatedExchange.class,
                                               "Current and last are non-null but identical: do not publish"); //$NON-NLS-1$
                    }
                }
            }
        }
        /**
         * Gets the <code>Token</code> corresponding to this subscription.
         *
         * @return a <code>Token</code> value
         */
        private Token getToken()
        {
            return token;
        }
        /**
         * Creates a <code>TopOfBook</code> value from the given list of events, if possible. 
         *
         * @param inEvents a <code>List&lt;QuoteEvent&gt;</code> value
         * @return a <code>TopOfBook</code> value
         */
        private static TopOfBook makeTopOfBook(List<QuoteEvent> inEvents)
        {
            // to make a top out of a list, the following must be true:
            //  1) the list contains 0, 1, or 2 QuoteEvents
            //  2) if 2, the first event must be a bid, the second an ask
            //  3) if 1, the event may be either a bid or an ask
            assert(inEvents.size() <= 2);
            if(inEvents.isEmpty()) {
                return new TopOfBook(null,
                                     null);
            }
            // event list contains 1 or 2 events, don't know what type yet
            QuoteEvent event1 = inEvents.remove(0);
            QuoteEvent event2;
            if(inEvents.isEmpty()) {
                event2 = null;
            } else {
                event2 = inEvents.remove(0);
            }
            assert(inEvents.isEmpty());
            BidEvent bid = null;
            AskEvent ask = null;
            if(event1 instanceof BidEvent) {
                bid = (BidEvent)event1;
                if(event2 instanceof AskEvent) {
                    ask = (AskEvent)event2;
                }
            } else if(event1 instanceof AskEvent) {
                ask = (AskEvent)event1;
                assert(event2 == null);
            }
            return new TopOfBook(bid,
                                 ask);
        }
        /**
         * the last known top of the relevant book or <code>null</code> 
         */
        private final Map<Instrument,TopOfBook> lastKnownTopsOfBook = new ConcurrentHashMap<Instrument,TopOfBook>();
        /**
         * 
         */
        private final Multimap<Instrument,DividendEvent> lastKnownDividends;
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
        private final Set<Instrument> instruments = Collections.synchronizedSet(new HashSet<Instrument>());
        /**
         * the subscription token returned to the caller
         */
        private final Token token;
        /**
         * the exchange to which this subscription was targeted
         */
        private final SimulatedExchange exchange;
    }
    /**
     * Represents the top of a given book at a particular point in time.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    @Immutable
    @ClassVersion("$Id$")
    static class TopOfBook
            extends Pair<BidEvent,AskEvent>
            implements Comparable<TopOfBook>
    {
        /**
         * Create a new TopOfBook instance.
         *
         * @param inBidEvent a <code>BidEvent</code> value or <code>null</code>
         * @param inAskEvent an <code>AskEvent</code> value or <code>null</code>
         * @throws IllegalArgumentException if both the <code>BidEvent</code> and <code>AskEvent</code>
         *  are specified but are not events for the same <code>Instrument</code>
         */
        TopOfBook(BidEvent inBidEvent,
                  AskEvent inAskEvent)
        {
            super(inBidEvent,
                  inAskEvent);
            if(inBidEvent != null &&
               inAskEvent != null) {
                if(!inBidEvent.getInstrument().equals(inAskEvent.getInstrument())) {
                    throw new IllegalArgumentException();
                }
            }
        }
        /**
         * Gets the <code>BidEvent</code>.
         *
         * @return a <code>BidEvent</code> or <code>null</code>
         */
        public BidEvent getBid()
        {
            return getFirstMember();
        }
        /**
         * Gets the <code>AskEvent</code>.
         *
         * @return an <code>AskEvent</code> or <code>null</code>
         */
        public AskEvent getAsk()
        {
            return getSecondMember();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("%s -- %s", //$NON-NLS-1$
                                 getBid(),
                                 getAsk());
        }
        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(TopOfBook inOtherTop)
        {
            if(inOtherTop == null) {
                return -1;
            }
            if(getBid() == null) {
                // top1 bid is null
                if(inOtherTop.getBid() != null) {
                    // top2 bid is non-null
                    return 1;
                }
                // top1 bid is null and top2 bid is null
            } else {
                // top1 bid is non-null
                if(inOtherTop.getBid() == null) {
                    // top1 bid is non-null and top2 bid is null
                    return -1;
                }
                // top1 bid is non-null and top2 bid is non-null
                int result = getBid().getPrice().compareTo(inOtherTop.getBid().getPrice());
                if(result != 0) {
                    return result;
                }
                result = getBid().getSize().compareTo(inOtherTop.getBid().getSize());
                if(result != 0) {
                    return result;
                }
            }
            // top1 bid is equal (in size and price) to top2 bid and both are non-null, move on to asks
            if(getAsk() == null) {
                // top1 ask is null
                if(inOtherTop.getAsk() != null) {
                    // top2 ask is non-null
                    return 1;
                }
                // top1 ask is null and top2 ask is null
            } else {
                // top1 ask is non-null
                if(inOtherTop.getAsk() == null) {
                    // top1 ask is non-null and top2 ask is null
                    return -1;
                }
                // top1 ask is non-null and top2 ask is non-null
                int result = getAsk().getPrice().compareTo(inOtherTop.getAsk().getPrice());
                if(result != 0) {
                    return result;
                }
                result = getAsk().getSize().compareTo(inOtherTop.getAsk().getSize());
                if(result != 0) {
                    return result;
                }
            }
            return 0;
        }
    }
    /**
     * Unique identifier for a specific subscription request to the {@link SimulatedExchange}.
     * 
     * <p>This object is used to identify a market data request.  When
     * executing a subscription request, as in to
     * {@link Exchange#getDepthOfBook(ExchangeRequest, ISubscriber)}
     * for instance, a <code>Token</code> value will be returned.  Updates will be published to the
     * given {@link ISubscriber} until the exchange is stopped or the request is canceled via
     * {@link Token#cancel()}.
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
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("Simulated Exchange Token for %s", //$NON-NLS-1$
                                 subscriber);
        }
    }
}
