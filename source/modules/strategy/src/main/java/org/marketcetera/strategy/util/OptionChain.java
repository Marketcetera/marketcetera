package org.marketcetera.strategy.util;

import static org.marketcetera.strategy.Messages.WRONG_DIVIDEND_EQUITY_FOR_OPTION_CHAIN;
import static org.marketcetera.strategy.Messages.WRONG_EQUITY_FOR_OPTION_CHAIN;
import static org.marketcetera.strategy.Messages.WRONG_UNDERLYING_FOR_OPTION_CHAIN;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.SystemUtils;
import org.marketcetera.core.Pair;
import org.marketcetera.event.*;
import org.marketcetera.event.util.MarketstatEventCache;
import org.marketcetera.strategy.util.OptionContractPair.OptionContractPairKey;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;

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
        latestMarketstat = new MarketstatEventCache(instrument);
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
        return latestMarketstat.get();
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
        StringBuilder builder = new StringBuilder();
        builder.append(getUnderlyingInstrument().getSymbol()).append(nl); //$NON-NLS-1$
        builder.append(BID).append(getLatestUnderlyingBid() == null ? none : String.format("%s %s %s", //$NON-NLS-1$ //$NON-NLS-2$
                                                                                           getLatestUnderlyingBid().getSize(),
                                                                                           getLatestUnderlyingBid().getPrice(),
                                                                                           getLatestUnderlyingBid().getExchange())).append(nl);
        builder.append(ASK).append(getLatestUnderlyingAsk() == null ? none : String.format("%s %s %s", //$NON-NLS-1$ //$NON-NLS-2$
                                                                                           getLatestUnderlyingAsk().getSize(),
                                                                                           getLatestUnderlyingAsk().getPrice(),
                                                                                           getLatestUnderlyingAsk().getExchange())).append(nl);
        builder.append(LAST).append(getLatestUnderlyingTrade() == null ? none : String.format("%s %s %s", //$NON-NLS-1$ //$NON-NLS-2$
                                                                                              getLatestUnderlyingTrade().getSize(),
                                                                                              getLatestUnderlyingTrade().getPrice(),
                                                                                              getLatestUnderlyingTrade().getExchange())).append(nl);
        MarketstatEvent latestUnderlyingStats = getLatestUnderlyingMarketstat();
        builder.append(HIGH).append(latestUnderlyingStats == null ||
                                    latestUnderlyingStats.getHigh() == null ? none : latestUnderlyingStats.getHigh().toPlainString()).append(nl);
        builder.append(LOW).append(latestUnderlyingStats == null ||
                                   latestUnderlyingStats.getLow() == null ? none : latestUnderlyingStats.getLow().toPlainString()).append(nl);
        if(!dividends.isEmpty()) {
            // add dividends
            builder.append(DIVIDEND_HEADER).append(nl);
            Table table = new Table(dividendHeaders.length,
                                    BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                    ShownBorders.ALL,
                                    false);
            for(String header : dividendHeaders) {
                table.addCell(header,
                              headerStyle);
            }
            for(DividendEvent dividend : dividends) {
                table.addCell(dividend.getType() == null ? none : dividend.getType().toString());
                table.addCell(dividend.getAmount() == null ? none : String.format("%s (%s)", //$NON-NLS-1$
                                                                                  dividend.getAmount().toPlainString(),
                                                                                  dividend.getCurrency()));
                table.addCell(dividend.getExecutionDate() == null ? none : dividend.getExecutionDate());
                table.addCell(dividend.getDeclareDate() == null ? none : dividend.getDeclareDate());
                table.addCell(dividend.getPaymentDate() == null ? none : dividend.getPaymentDate());
                table.addCell(dividend.getRecordDate() == null ? none : dividend.getRecordDate());
                table.addCell(dividend.getStatus() == null ? none : dividend.getStatus().toString());
                table.addCell(dividend.getFrequency() == null ? none : dividend.getFrequency().toString());
            }
            builder.append(table.render());
            builder.append(nl);
        }
        Collection<OptionContractPair> chain = getOptionChain();
        if(chain.isEmpty()) {
            return builder.toString();
        }
        builder.append(OPTION_CHAIN_HEADER).append(nl);
        Table table = new Table(13,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        // add column headers
        for(Pair<String,Integer> header : chainHeaders) {
            table.addCell(header.getFirstMember(),
                          headerStyle,
                          header.getSecondMember());
        }
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
            table.addCell(String.format("%s %s %s", //$NON-NLS-1$
                                         symbol,
                                         expiry,
                                         strike));
            if(put != null &&
               put.getLatestBid() != null) {
                BidEvent bid = put.getLatestBid();
                table.addCell(bid.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            bid.getPrice().toPlainString(),
                                            bid.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
            if(put != null &&
               put.getLatestAsk() != null) {
                AskEvent ask = put.getLatestAsk();
                table.addCell(ask.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            ask.getPrice().toPlainString(),
                                            ask.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
            if(put != null &&
               put.getLatestTrade() != null) {
                TradeEvent trade = put.getLatestTrade();
                table.addCell(trade.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            trade.getPrice().toPlainString(),
                                            trade.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
            if(call != null &&
               call.getLatestBid() != null) {
                BidEvent bid = call.getLatestBid();
                table.addCell(bid.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            bid.getPrice().toPlainString(),
                                            bid.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
            if(call != null &&
               call.getLatestAsk() != null) {
                AskEvent ask = call.getLatestAsk();
                table.addCell(ask.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            ask.getPrice().toPlainString(),
                                            ask.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
            if(call != null &&
               call.getLatestTrade() != null) {
                TradeEvent trade = call.getLatestTrade();
                table.addCell(trade.getSize().toPlainString());
                table.addCell(String.format("%s %s", //$NON-NLS-1$
                                            trade.getPrice().toPlainString(),
                                            trade.getExchange()));
            } else {
                table.addCell(none);
                table.addCell(none);
            }
        }
        builder.append(table.render());
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
            latestMarketstat.cache(inMarketstat);
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
    private final MarketstatEventCache latestMarketstat;
    /**
     * the latest trade for the option chain underlying instrument, may be <code>null</code> 
     */
    private volatile TradeEvent latestTrade = null;
    // the following are constants used to display the option chain 
    private static final String nl = SystemUtils.LINE_SEPARATOR;
    private static final String none = "---"; //$NON-NLS-1$
    private static final CellStyle headerStyle = new CellStyle(HorizontalAlign.center);
    private static final String[] dividendHeaders = new String[] { "Type","Amount","Execution Date","Declare Date","Payment Date","Record Date","Status","Frequency" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    private static final List<Pair<String,Integer>> chainHeaders = new ArrayList<Pair<String,Integer>>();
    private static final String BID =  "Bid:  "; //$NON-NLS-1$
    private static final String ASK =  "Ask:  "; //$NON-NLS-1$
    private static final String LAST = "Last: "; //$NON-NLS-1$
    private static final String HIGH = "High: "; //$NON-NLS-1$
    private static final String LOW =  "Low:  "; //$NON-NLS-1$
    private static final String DIVIDEND_HEADER = "Dividends"; //$NON-NLS-1$
    private static final String OPTION_CHAIN_HEADER = "Option Chain"; //$NON-NLS-1$
    static {
        chainHeaders.add(new Pair<String,Integer>("", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Put", //$NON-NLS-1$
                                                  6));
        chainHeaders.add(new Pair<String,Integer>("Call", //$NON-NLS-1$
                                                  6));
        chainHeaders.add(new Pair<String,Integer>("", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Bid", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Ask", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Latest", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Bid", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Ask", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Latest", //$NON-NLS-1$
                                                  2));
        chainHeaders.add(new Pair<String,Integer>("Symbol/Expiry/Strike", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Size", //$NON-NLS-1$
                                                  1));
        chainHeaders.add(new Pair<String,Integer>("Price X", //$NON-NLS-1$
                                                  1));
    }
}
