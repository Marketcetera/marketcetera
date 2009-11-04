package org.marketcetera.strategy.util;

import static org.marketcetera.strategy.Messages.WRONG_DIVIDEND_EQUITY_FOR_OPTION_CHAIN;
import static org.marketcetera.strategy.Messages.WRONG_EQUITY_FOR_OPTION_CHAIN;
import static org.marketcetera.strategy.Messages.WRONG_UNDERLYING_FOR_OPTION_CHAIN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.EquityEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.util.OptionContractPair.OptionContractPairKey;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the option chain of a given underlying instrument.
 * 
 * <p>This object maintains an in-memory representation of the option chain
 * of an underlying instrument and its market data.  To use <code>OptionChain</code>,
 * create a new <code>OptionChain</code> (may use any instrument as the underlying instrument
 * of the option chain):
 * <pre>
 * Equity theEquity = new Equity("GOOG");
 * OptionChain theChain = new OptionChain(theEquity);
 * </pre>
 * Set up market data for the underlying instrument as normal.  As market data events arrive,
 * pass the appropriate ones to the <code>OptionChain</code>:
 * <pre>
 * public void onAsk(AskEvent ask)
 * {
 *     theChain.process(ask);
 * }
 * </pre>
 * Note that if the <code>AskEvent</code> is not relevant to the <code>OptionChain</code>, it
 * will be discarded.  To take full advantage of the <code>OptionChain</code> object, add
 * similar code to <code>onBid</code>, <code>onTrade</code>, <code>onMarketstat</code>, and
 * <code>onDividend</code>.
 *
 * <p>The data stored in the <code>OptionChain</code> object can be retrieved as follows:
 * <pre>
 * List&lt;OptionContractPair&gt; optionChain = theChain.getOptionChain();
 * for(OptionContractPair contractPair : optionChain) {
 *     OptionContract putSide = contractPair.getPut();
 *     // do something with the put contract
 *     OptionContract callSide = contractPair.getCall();
 *     // do something with the call contract
 * }
 * </pre>
 * As new market data events come in, the option chain view is updated as the
 * events are added to the <code>OptionChain</code> object with {@link OptionChain#process(Event)}.
 * 
 * <p>Dividends for the underlying instrument of the <code>OptionChain</code> object are available in 
 * a similar fashion:
 * <pre>
 * List&lt;DividendEvent&gt; dividends = theChain.getDividends();
 * </pre>
 * The <code>OptionChain</code> also tracks market data for the underlying instrument and each 
 * <code>OptionContract</code>.
 * <pre>
 * // the latest ask for the option chain underlying instrument 
 * AskEvent ask = theChain.getLatestUnderlyingAsk();
 * for(OptionContractPair contractPair : optionChain) {
 *     // the latest ask for the put side of one of the entries in the option chain
 *     ask = contractPair.getPut().getLatestAsk();
 * }
 * </pre>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class OptionChain
{
    /**
     * Create a new OptionChain instance.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value indicating the <code>Instrument</code> for which
     *  to create the <code>OptionChain</code>
     * @throws NullPointerException if <code>inUnderlyingInstrument</code> is <code>null</code>
     */
    public OptionChain(Instrument inUnderlyingInstrument)
    {
        if(inUnderlyingInstrument == null) {
            throw new NullPointerException();
        }
        instrument = inUnderlyingInstrument;
    }
    /**
     * Gets a live, unmodifiable view of the option chain.
     * 
     * <p>Updates to the option chain will be visible in this view.  The
     * elements in the option chain will be sorted according to the
     * {@link OptionContractPair} <em>natural order</em>.
     * 
     * <p>This view is populated when {@link Event} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return a <code>Collection&lt;OptionContractPair&gt;</code> value
     */
    public Collection<OptionContractPair> getOptionChain()
    {
        // intentionally returning a live view of the option chain
        return Collections.unmodifiableCollection(optionChain.values());
    }
    /**
     * Gets a live, unmodifiable view of the dividends for the underlying instrument.
     *
     * <p>Updates to the dividend data for the underlying instrument will be
     * visible in this view.  The elements in the list are sorted in the order
     * that the corresponding <code>DividendEvent</code> objects are received.
     *
     * <p>This view is populated when {@link DividendEvent} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return a <code>List&lt;DividendEvent&gt;</code> value
     */
    public List<DividendEvent> getDividends()
    {
        // intentionally returning a live view of the dividends
        return Collections.unmodifiableList(dividends);
    }
    /**
     * Gets the underlying instrument for this <code>OptionChain</code>.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getUnderlyingInstrument()
    {
        return instrument;
    }
    /**
     * Gets the latest <code>Ask</code> for the underlying instrument.
     * 
     * <p>This data is populated when {@link AskEvent} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return an <code>AskEvent</code> or <code>null</code>
     */
    public AskEvent getLatestUnderlyingAsk()
    {
        return latestAsk;
    }
    /**
     * Gets the latest <code>Bid</code> for the underlying instrument.
     * 
     * <p>This data is populated when {@link BidEvent} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return a <code>BidEvent</code> or <code>null</code>
     */
    public BidEvent getLatestUnderlyingBid()
    {
        return latestBid;
    }
    /**
     * Gets the latest <code>Trade</code> for the underlying instrument.
     * 
     * <p>This data is populated when {@link TradeEvent} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return a <code>TradeEvent</code> or <code>null</code>
     */
    public TradeEvent getLatestUnderlyingTrade()
    {
        return latestTrade;
    }
    /**
     * Gets the latest <code>Marketstat</code> for the underlying instrument.
     * 
     * <p>This data is populated when {@link MarketstatEvent} objects are passed
     * to {@link OptionChain#process(Event)}.
     *
     * @return a <code>BidEvent</code> or <code>null</code>
     */
    public MarketstatEvent getLatestUnderlyingMarketstat()
    {
        return latestMarketstat;
    }
    /**
     * Attempts to apply the given event to this <code>OptionChain</code>.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>boolean</code> value which, if true, indicates that the given event was successfully applied
     *  to the option chain.  If false, the event was not applicable.
     */
    public boolean process(Event inEvent)
    {
        if(inEvent == null) {
            throw new NullPointerException();
        }
        if(inEvent instanceof AskEvent) {
            return processAskEvent((AskEvent)inEvent);
        }
        if(inEvent instanceof BidEvent) {
            return processBidEvent((BidEvent)inEvent);
        }
        if(inEvent instanceof DividendEvent) {
            return processDividendEvent((DividendEvent)inEvent);
        }
        if(inEvent instanceof MarketstatEvent) {
            return processMarketstatEvent((MarketstatEvent)inEvent);
        }
        if(inEvent instanceof TradeEvent) {
            return processTradeEvent((TradeEvent)inEvent);
        }
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        // renders the option chain as a human-readable table
        // TODO add column headers to the option chain
        // TODO render dividends more elegantly
        StringBuilder builder = new StringBuilder();
        String nl = SystemUtils.LINE_SEPARATOR;
        String none = "---";
        builder.append("Option Chain for ").append(getUnderlyingInstrument().getSymbol()).append(nl);
        builder.append("Bid:  ").append(getLatestUnderlyingBid() == null ? none : String.format("%s %s %s",
                                                                                               getLatestUnderlyingBid().getSize(),
                                                                                               getLatestUnderlyingBid().getPrice(),
                                                                                               getLatestUnderlyingBid().getExchange())).append(nl);
        builder.append("Ask:  ").append(getLatestUnderlyingAsk() == null ? none : String.format("%s %s %s",
                                                                                               getLatestUnderlyingAsk().getSize(),
                                                                                               getLatestUnderlyingAsk().getPrice(),
                                                                                               getLatestUnderlyingAsk().getExchange())).append(nl);
        builder.append("Last: ").append(getLatestUnderlyingTrade() == null ? none : String.format("%s %s %s",
                                                                                               getLatestUnderlyingTrade().getSize(),
                                                                                               getLatestUnderlyingTrade().getPrice(),
                                                                                               getLatestUnderlyingTrade().getExchange())).append(nl);
        builder.append("High: ").append(getLatestUnderlyingMarketstat() == null ? none : getLatestUnderlyingMarketstat().getHigh().toPlainString()).append(nl);
        builder.append("Low: ").append(getLatestUnderlyingMarketstat() == null ? none : getLatestUnderlyingMarketstat().getLow().toPlainString()).append(nl);
        Collection<OptionContractPair> chain = getOptionChain();
        if(chain.isEmpty()) {
            return builder.toString();
        }
        // generate row headers (symbol expiry strike)
        List<String> rowHeaders = new ArrayList<String>();
        List<String> putBidSizes = new ArrayList<String>();
        List<String> putBidPrices = new ArrayList<String>();
        List<String> putAskSizes = new ArrayList<String>();
        List<String> putAskPrices = new ArrayList<String>();
        List<String> lastPutSizes = new ArrayList<String>();
        List<String> lastPutPrices = new ArrayList<String>();
        List<String> callBidSizes = new ArrayList<String>();
        List<String> callBidPrices = new ArrayList<String>();
        List<String> callAskSizes = new ArrayList<String>();
        List<String> callAskPrices = new ArrayList<String>();
        List<String> lastCallSizes = new ArrayList<String>();
        List<String> lastCallPrices = new ArrayList<String>();
        for(OptionContractPair pair : chain) {
            OptionContract put = pair.getPut();
            OptionContract call = pair.getCall();
            String symbol;
            String expiry;
            String strike;
            if(put != null) {
                symbol = put.getInstrument().getSymbol();
                expiry = put.getInstrument().getExpiry();
                strike = put.getInstrument().getStrikePrice().toPlainString();
            } else if(call != null) {
                symbol = call.getInstrument().getSymbol();
                expiry = call.getInstrument().getExpiry();
                strike = call.getInstrument().getStrikePrice().toPlainString();
            } else {
                continue;
            }
            rowHeaders.add(String.format("%s %s %s",
                                         symbol,
                                         expiry,
                                         strike));
            if(put != null &&
               put.getLatestBid() != null) {
                BidEvent bid = put.getLatestBid();
                putBidSizes.add(bid.getSize().toPlainString());
                putBidPrices.add(String.format("%s %s",
                                               bid.getPrice().toPlainString(),
                                               bid.getExchange()));
            } else {
                putBidSizes.add(none);
                putBidPrices.add(none);
            }
            if(put != null &&
               put.getLatestAsk() != null) {
                AskEvent ask = put.getLatestAsk();
                putAskSizes.add(ask.getSize().toPlainString());
                putAskPrices.add(String.format("%s %s",
                                               ask.getPrice().toPlainString(),
                                               ask.getExchange()));
            } else {
                putAskSizes.add(none);
                putAskPrices.add(none);
            }
            if(put != null &&
               put.getLatestTrade() != null) {
                TradeEvent trade = put.getLatestTrade();
                lastPutSizes.add(trade.getSize().toPlainString());
                lastPutPrices.add(String.format("%s %s",
                                                trade.getPrice().toPlainString(),
                                                trade.getExchange()));
            } else {
                lastPutSizes.add(none);
                lastPutPrices.add(none);
            }
            if(call != null &&
               call.getLatestBid() != null) {
                BidEvent bid = call.getLatestBid();
                callBidSizes.add(bid.getSize().toPlainString());
                callBidPrices.add(String.format("%s %s",
                                                bid.getPrice().toPlainString(),
                                                bid.getExchange()));
            } else {
                callBidSizes.add(none);
                callBidPrices.add(none);
            }
            if(call != null &&
               call.getLatestAsk() != null) {
                AskEvent ask = call.getLatestAsk();
                callAskSizes.add(ask.getSize().toPlainString());
                callAskPrices.add(String.format("%s %s",
                                                ask.getPrice().toPlainString(),
                                                ask.getExchange()));
            } else {
                callAskSizes.add(none);
                callAskPrices.add(none);
            }
            if(call != null &&
               call.getLatestTrade() != null) {
                TradeEvent trade = call.getLatestTrade();
                lastCallSizes.add(trade.getSize().toPlainString());
                lastCallPrices.add(String.format("%s %s",
                                                 trade.getPrice().toPlainString(),
                                                 trade.getExchange()));
            } else {
                lastCallSizes.add(none);
                lastCallPrices.add(none);
            }
        }
        List<String> normalizedRowHeaders = makeColumn(rowHeaders);
        List<String> normalizedPutBidSizes = makeColumn(putBidSizes);
        List<String> normalizedPutBidPrices = makeColumn(putBidPrices);
        List<String> normalizedPutAskSizes = makeColumn(putAskSizes);
        List<String> normalizedPutAskPrices = makeColumn(putAskPrices);
        List<String> normalizedLastPutSizes = makeColumn(lastPutSizes);
        List<String> normalizedLastPutPrices = makeColumn(lastPutPrices);
        List<String> normalizedCallBidSizes = makeColumn(callBidSizes);
        List<String> normalizedCallBidPrices = makeColumn(callBidPrices);
        List<String> normalizedCallAskSizes = makeColumn(callAskSizes);
        List<String> normalizedCallAskPrices = makeColumn(callAskPrices);
        List<String> normalizedLastCallSizes = makeColumn(lastCallSizes);
        List<String> normalizedLastCallPrices = makeColumn(lastCallPrices);
        List<String> rawPutSizes = new ArrayList<String>();
        for(int counter=0;counter<normalizedRowHeaders.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedPutBidSizes.get(counter)).append(" | ");
            columnBuilder.append(normalizedPutAskSizes.get(counter)).append(" | ");
            columnBuilder.append(normalizedLastPutSizes.get(counter)).append(" | ");
            rawPutSizes.add(columnBuilder.toString());
        }
        List<String> normalizedPutSizes = makeColumn(rawPutSizes);
        List<String> rawPutPrices = new ArrayList<String>();
        for(int counter=0;counter<normalizedRowHeaders.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedPutBidPrices.get(counter)).append(" | ");
            columnBuilder.append(normalizedPutAskPrices.get(counter)).append(" | ");
            columnBuilder.append(normalizedLastPutPrices.get(counter)).append(" | ");
            rawPutPrices.add(columnBuilder.toString());
        }
        List<String> normalizedPutPrices = makeColumn(rawPutPrices);
        List<String> rawCallSizes = new ArrayList<String>();
        for(int counter=0;counter<normalizedRowHeaders.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedCallBidSizes.get(counter)).append(" | ");
            columnBuilder.append(normalizedCallAskSizes.get(counter)).append(" | ");
            columnBuilder.append(normalizedLastCallSizes.get(counter)).append(" | ");
            rawCallSizes.add(columnBuilder.toString());
        }
        List<String> normalizedCallSizes = makeColumn(rawCallSizes);
        List<String> rawCallPrices = new ArrayList<String>();
        for(int counter=0;counter<normalizedRowHeaders.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedCallBidPrices.get(counter)).append(" | ");
            columnBuilder.append(normalizedCallAskPrices.get(counter)).append(" | ");
            columnBuilder.append(normalizedLastCallPrices.get(counter)).append(" | ");
            rawCallPrices.add(columnBuilder.toString());
        }
        List<String> normalizedCallPrices = makeColumn(rawCallPrices);
        List<String> rawPuts = new ArrayList<String>();
        for(int counter=0;counter<normalizedPutSizes.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedPutSizes.get(counter));
            columnBuilder.append(normalizedPutPrices.get(counter));
            rawPuts.add(columnBuilder.toString());
        }
        List<String> normalizedPuts = makeColumn(rawPuts);
        List<String> rawCalls = new ArrayList<String>();
        for(int counter=0;counter<normalizedCallSizes.size();counter++) {
            StringBuilder columnBuilder = new StringBuilder();
            columnBuilder.append(normalizedCallSizes.get(counter));
            columnBuilder.append(normalizedCallPrices.get(counter));
            rawCalls.add(columnBuilder.toString());
        }
        List<String> normalizedCalls = makeColumn(rawCalls);
        for(int counter=0;counter<normalizedRowHeaders.size();counter++) {
            builder.append(normalizedRowHeaders.get(counter)).append(" | ");
            builder.append(normalizedPuts.get(counter));
            builder.append(normalizedCalls.get(counter));
            builder.append(nl);
        }
        return builder.toString();
    }
    /**
     * Processes the given <code>AskEvent</code>.
     *
     * @param inAsk an <code>AskEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>AskEvent</code> was successfully
     *  applied or not
     */
    private boolean processAskEvent(AskEvent inAsk)
    {
        if(!validate(inAsk)) {
            return false;
        }
        if(inAsk instanceof EquityEvent) {
            latestAsk = inAsk;
            return true;
        }
        if(inAsk instanceof OptionEvent) {
            return processEventForOptionChain((OptionEvent)inAsk);
        }
        return false;
    }
    /**
     * Processes the given <code>BidEvent</code>.
     *
     * @param inBid a <code>BidEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>BidEvent</code> was successfully
     *  applied or not
     */
    private boolean processBidEvent(BidEvent inBid)
    {
        if(!validate(inBid)) {
            return false;
        }
        if(inBid instanceof EquityEvent) {
            latestBid = inBid;
            return true;
        }
        if(inBid instanceof OptionEvent) {
            return processEventForOptionChain((OptionEvent)inBid);
        }
        return false;
    }
    /**
     * Processes the given <code>DividendEvent</code>.
     *
     * @param inDividend a <code>DividendEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>DividendEvent</code> was successfully
     *  applied or not
     */
    private boolean processDividendEvent(DividendEvent inDividend)
    {
        if(!validate(inDividend)) {
            return false;
        }
        dividends.add(inDividend);
        return true;
    }
    /**
     * Processes the given <code>MarketstatEvent</code>.
     *
     * @param inMarketstat a <code>MarketstatEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>MarketstatEvent</code> was successfully
     *  applied or not
     */
    private boolean processMarketstatEvent(MarketstatEvent inMarketstat)
    {
        if(!validate(inMarketstat)) {
            return false;
        }
        if(inMarketstat instanceof EquityEvent) {
            latestMarketstat = inMarketstat;
            return true;
        }
        if(inMarketstat instanceof OptionEvent) {
            return processEventForOptionChain((OptionEvent)inMarketstat);
        }
        return false;
    }
    /**
     * Processes the given <code>TradeEvent</code>.
     *
     * @param inTrade a <code>TradeEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>TradeEvent</code> was successfully
     *  applied or not
     */
    private boolean processTradeEvent(TradeEvent inTrade)
    {
        if(!validate(inTrade)) {
            return false;
        }
        if(inTrade instanceof EquityEvent) {
            latestTrade = inTrade;
            return true;
        }
        if(inTrade instanceof OptionEvent) {
            return processEventForOptionChain((OptionEvent)inTrade);
        }
        return false;
    }
    /**
     * Processes the given <code>OptionEvent</code> to update the option chain. 
     *
     * @param inOptionEvent an <code>OptionEvent</code> value
     * @return a <code>boolean</code> value indicating whether the <code>OptionEvent</code> was successfully
     *  applied or not
     */
    private synchronized boolean processEventForOptionChain(OptionEvent inOptionEvent)
    {
        // this method is synchronized in order to make put-if-absent atomic
        // create the object that will identify the correct contract pair in the option chain - the pair
        //  may or may not yet exist in the chain
        OptionContractPairKey key = OptionContractPair.getOptionContractPairKey(inOptionEvent.getInstrument());
        // retrieve the pair if it exists
        OptionContractPair contractPair = optionChain.get(key);
        if(contractPair == null) {
            // the pair does not yet exist, create the put/call contract pair for this event
            contractPair = new OptionContractPair(inOptionEvent);
            // add the new contract pair to the chain
            optionChain.put(key,
                            contractPair);
        }
        // the contract pair exists in the pair (is non-null, here, too) - process the option event
        return contractPair.process(inOptionEvent);
    }
    /**
     * Validates that the given event is applicable to this <code>OptionChain</code>.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>boolean</code> value indicating if the event is valid
     */
    private boolean validate(Event inEvent)
    {
        if(inEvent instanceof OptionEvent) {
            OptionEvent optionEvent = (OptionEvent)inEvent;
            if(!optionEvent.getUnderlyingInstrument().equals(getUnderlyingInstrument())) {
                WRONG_UNDERLYING_FOR_OPTION_CHAIN.warn(OptionChain.class,
                                                       optionEvent.getUnderlyingInstrument(),
                                                       getUnderlyingInstrument());
                return false;
            }
        }
        if(inEvent instanceof DividendEvent) {
            DividendEvent dividendEvent = (DividendEvent)inEvent;
            if(!dividendEvent.getInstrument().equals(getUnderlyingInstrument())) {
                WRONG_DIVIDEND_EQUITY_FOR_OPTION_CHAIN.warn(OptionChain.class,
                                                            dividendEvent.getInstrument(),
                                                            getUnderlyingInstrument());
                return false;
            }
        }
        if(inEvent instanceof EquityEvent) {
            EquityEvent equityEvent = (EquityEvent)inEvent;
            if(!equityEvent.getInstrument().equals(getUnderlyingInstrument())) {
                WRONG_EQUITY_FOR_OPTION_CHAIN.warn(OptionChain.class,
                                                   equityEvent.getInstrument(),
                                                   getUnderlyingInstrument());
                return false;
            }
        }
        return true;
    }
    /**
     * Takes the given list of strings and creates a new list of
     * strings of all the same width.
     *
     * @param inValues a <code>List&lt;String&gt;</code> value
     * @return a <code>List&lt;String&gt;</code> value
     */
    private static List<String> makeColumn(List<String> inValues)
    {
        List<String> input = new ArrayList<String>();
        input.addAll(inValues);
        List<String> output = new ArrayList<String>();
        int maxWidth = 0;
        for(String value : input) {
            maxWidth = Math.max(maxWidth,
                                value.length());
        }
        for(String value : input) {
            StringBuilder column = new StringBuilder().append(value);
            for(int count = 0;count < maxWidth-value.length();count++) {
                column.append(' ');
            }
            output.add(column.toString());
        }
        return output;
    }
    /**
     * the option chain - the collection of record for the option chain - made concurrent in order for it to be returned
     * outside the scope of this object and still predictably reflect updates, potentially in different threads
     */
    private final Map<OptionContractPairKey,OptionContractPair> optionChain = new ConcurrentSkipListMap<OptionContractPairKey,OptionContractPair>();
    /**
     * the live view of the dividend data - dividends should have relatively few writes but many more traversals.  copy-on-write makes
     * for expensive writes but allows concurrent reads 
     */
    private final List<DividendEvent> dividends = new CopyOnWriteArrayList<DividendEvent>();
    /**
     * the instrument for which to hold an option chain
     */
    private final Instrument instrument;
    /**
     * the latest ask for the option chain underlying instrument, may be <code>null</code> 
     */
    private volatile AskEvent latestAsk = null;
    /**
     * the latest bid for the option chain underlying instrument, may be <code>null</code> 
     */
    private volatile BidEvent latestBid = null;
    /**
     * the latest marketstat for the option chain underlying instrument, may be <code>null</code> 
     */
    private volatile MarketstatEvent latestMarketstat = null;
    /**
     * the latest trade for the option chain underlying instrument, may be <code>null</code> 
     */
    private volatile TradeEvent latestTrade = null;
}
