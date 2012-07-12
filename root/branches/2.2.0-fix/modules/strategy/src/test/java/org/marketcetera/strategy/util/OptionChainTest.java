package org.marketcetera.strategy.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.TestMessages;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.*;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.strategy.util.OptionContractPair.OptionContractPairKey;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests {@link OptionChain}, [@link OptionContractPair}, and {@link OptionContract}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class OptionChainTest
        implements TestMessages
{
    /**
     * Tests {@link OptionChain#OptionChain(org.marketcetera.trade.Instrument)}.
     *
     * @throws Exception
     */
    @Test
    public void optionChainConstructor()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new OptionChain(null);
            }
        };
        new ExpectedFailure<UnsupportedOperationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                Instrument other = EventTestBase.generateUnsupportedInstrument();
                verifyOptionChain(new OptionChain(other),
                                  other,
                                  null,
                                  null,
                                  null,
                                  null,
                                  new ArrayList<DividendEvent>(),
                                  new ArrayList<OptionContractPair>());
            }
        };
        verifyOptionChain(new OptionChain(equity),
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        verifyOptionChain(new OptionChain(callOption),
                          callOption,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#getOptionChain()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getOptionChain()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        // empty option chain
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // add an element to the option chain
        AskEvent callAsk = QuoteEventBuilder.optionAskEvent().withUnderlyingInstrument(equity)
                                                             .hasDeliverable(false)
                                                             .withExchange("B")
                                                             .withExpirationType(ExpirationType.AMERICAN)
                                                             .withInstrument(callOption)
                                                             .withMultiplier(BigDecimal.ZERO)
                                                             .withPrice(EventTestBase.generateDecimalValue())
                                                             .withQuoteDate(DateUtils.dateToString(new Date()))
                                                             .withSize(EventTestBase.generateDecimalValue()).create();
        assertTrue(chain.process(callAsk));
        // create expected result
        OptionContractPair entry = new OptionContractPair((OptionEvent)callAsk);
        assertTrue(entry.process((OptionEvent)callAsk));
        // this test verifies only the contents of the option chain, doesn't yet dig into verifying the contents of the option chain entries
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
        // add a bid for the call side of the same bid
        BidEvent callBid = QuoteEventBuilder.optionBidEvent().withUnderlyingInstrument(equity)
                                                             .withExchange("B")
                                                             .withExpirationType(ExpirationType.AMERICAN)
                                                             .withInstrument(callOption)
                                                             .withPrice(EventTestBase.generateDecimalValue())
                                                             .withQuoteDate(DateUtils.dateToString(new Date()))
                                                             .withSize(EventTestBase.generateDecimalValue()).create();
        assertTrue(chain.process(callBid));
        // update expected result
        assertTrue(entry.process((OptionEvent)callBid));
        // verify again
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
        // add something for the put side of the same contract
        AskEvent putAsk = QuoteEventBuilder.optionAskEvent().withUnderlyingInstrument(equity)
                                                            .hasDeliverable(false)
                                                            .withExchange("X")
                                                            .withExpirationType(ExpirationType.AMERICAN)
                                                            .withInstrument(putOption)
                                                            .withMultiplier(BigDecimal.ZERO)
                                                            .withPrice(EventTestBase.generateDecimalValue())
                                                            .withQuoteDate(DateUtils.dateToString(new Date()))
                                                            .withSize(EventTestBase.generateDecimalValue()).create();
        assertTrue(chain.process(putAsk));
        // update expected result
        assertTrue(entry.process((OptionEvent)putAsk));
        // verify
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
        // now, get and hold the option chain view and make sure it gets updated without having to get a new version of it
        Collection<OptionContractPair> optionChain = chain.getOptionChain();
        assertEquals(1,
                     optionChain.size());
        assertEquals(entry,
                     optionChain.iterator().next());
        // add a bid for the same contract on the put side
        BidEvent putBid = QuoteEventBuilder.optionBidEvent().withUnderlyingInstrument(equity)
                                                            .withExchange("X")
                                                            .withExpirationType(ExpirationType.AMERICAN)
                                                            .withInstrument(putOption)
                                                            .withPrice(EventTestBase.generateDecimalValue())
                                                            .withQuoteDate(DateUtils.dateToString(new Date()))
                                                            .withSize(EventTestBase.generateDecimalValue()).create();
        assertTrue(chain.process(putBid));
        // update expected result
        assertTrue(entry.process((OptionEvent)putBid));
        // verify
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
        // check the updated option chain view
        assertEquals(1,
                     optionChain.size());
        assertEquals(entry,
                     optionChain.iterator().next());
        // add a trade for the put side
        TradeEvent putTrade = TradeEventBuilder.optionTradeEvent().withUnderlyingInstrument(equity)
                                               .withExchange("X")
                                               .withExpirationType(ExpirationType.AMERICAN)
                                               .withInstrument(putOption)
                                               .withPrice(EventTestBase.generateDecimalValue())
                                               .withTradeDate(DateUtils.dateToString(new Date()))
                                               .withSize(EventTestBase.generateDecimalValue()).create();
        assertTrue(chain.process(putTrade));
        // update expected result
        assertTrue(entry.process((OptionEvent)putTrade));
        // verify
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
        // check the updated option chain view (not using verifyOptionChain from here on in order to use the same optionChain collection,
        //  making sure it gets updated rather than getting a new view each time)
        assertEquals(1,
                     optionChain.size());
        assertEquals(entry,
                     optionChain.iterator().next());
        // create an event for a new OptionContract (same symbol and expiry, different (greater) strike)
        Option newCallOption = new Option(callOption.getSymbol(),
                                          callOption.getExpiry(),
                                          callOption.getStrikePrice().add(EventTestBase.generateDecimalValue()),
                                          OptionType.Call);
        callAsk = QuoteEventBuilder.optionAskEvent().withUnderlyingInstrument(equity)
                                                    .withExchange("Q")
                                                    .withExpirationType(ExpirationType.AMERICAN)
                                                    .withInstrument(newCallOption)
                                                    .withPrice(EventTestBase.generateDecimalValue())
                                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                                    .withSize(EventTestBase.generateDecimalValue()).create();
        // process the event (creates the new pair in the chain)
        assertTrue(chain.process(callAsk));
        assertNotNull(optionChain.toString());
        // make sure the new entry gets added
        assertEquals(2,
                     optionChain.size());
        OptionContractPair newEntry = new OptionContractPair((OptionEvent)callAsk);
        Iterator<OptionContractPair> iterator = optionChain.iterator();
        assertEquals(entry,
                     iterator.next());
        assertEquals(newEntry,
                     iterator.next());
        // create an event for yet another new OptionContract (same symbol and expiry, different (even greater) strike)
        Option newerCallOption = new Option(newCallOption.getSymbol(),
                                            newCallOption.getExpiry(),
                                            newCallOption.getStrikePrice().add(EventTestBase.generateDecimalValue()),
                                            OptionType.Call);
        callBid = QuoteEventBuilder.optionBidEvent().withUnderlyingInstrument(equity)
                                                    .withExchange("Q")
                                                    .withExpirationType(ExpirationType.AMERICAN)
                                                    .withInstrument(newerCallOption)
                                                    .withPrice(EventTestBase.generateDecimalValue())
                                                    .withQuoteDate(DateUtils.dateToString(new Date()))
                                                    .withSize(EventTestBase.generateDecimalValue()).create();
        // process the event (creates the new pair in the chain)
        assertTrue(chain.process(callBid));
        assertNotNull(chain.toString());
        // add a trade for the same call
        TradeEvent callTrade = TradeEventBuilder.optionTradeEvent().withUnderlyingInstrument(equity)
                                                                   .withExchange("Q")
                                                                   .withExpirationType(ExpirationType.AMERICAN)
                                                                   .withInstrument(newerCallOption)
                                                                   .withPrice(EventTestBase.generateDecimalValue())
                                                                   .withTradeDate(DateUtils.dateToString(new Date()))
                                                                   .withSize(EventTestBase.generateDecimalValue()).create();
        // process the event (creates the new pair in the chain)
        assertTrue(chain.process(callTrade));
        assertNotNull(chain.toString());
        // make sure the new entry gets added
        assertEquals(3,
                     optionChain.size());
        OptionContractPair newerEntry = new OptionContractPair((OptionEvent)callTrade);
        iterator = optionChain.iterator();
        assertEquals(entry,
                     iterator.next());
        assertEquals(newEntry,
                     iterator.next());
        assertEquals(newerEntry,
                     iterator.next());
    }
    /**
     * Tests {@link OptionChain#getDividends()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getDividends()
            throws Exception
    {
       OptionChain chain = new OptionChain(equity);
       // empty dividends
       verifyOptionChain(chain,
                         equity,
                         null,
                         null,
                         null,
                         null,
                         new ArrayList<DividendEvent>(),
                         new ArrayList<OptionContractPair>());
       // add a dividend
       Date date = new Date();
       DividendEventBuilder builder = DividendEventBuilder.dividend().withAmount(new BigDecimal("123.45"))
                                                                     .withCurrency("US Dollars")
                                                                     .withDeclareDate(DateUtils.dateToString(date))
                                                                     .withEquity(equity)
                                                                     .withExecutionDate(DateUtils.dateToString(date))
                                                                     .withFrequency(DividendFrequency.ANNUALLY)
                                                                     .withPaymentDate(DateUtils.dateToString(date))
                                                                     .withRecordDate(DateUtils.dateToString(date))
                                                                     .withStatus(DividendStatus.OFFICIAL)
                                                                     .withType(DividendType.FUTURE);
       DividendEvent event = builder.create();
       assertTrue(chain.process(event));
       verifyOptionChain(chain,
                         equity,
                         null,
                         null,
                         null,
                         null,
                         Arrays.asList(new DividendEvent[] { event }),
                         new ArrayList<OptionContractPair>());
       // add a new dividend making sure the list gets updated
       builder.withAmount(new BigDecimal("2345.67"));
       DividendEvent event2 = builder.create();
       assertFalse(event.equals(event2));
       assertTrue(chain.process(event2));
       verifyOptionChain(chain,
                         equity,
                         null,
                         null,
                         null,
                         null,
                         Arrays.asList(new DividendEvent[] { event, event2 }),
                         new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#getLatestUnderlyingAsk()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getLatestUnderlyingAsk()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        // no latest ask yet
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // create an ask for the underlying symbol
        AskEvent equityAsk = EventTestBase.generateEquityAskEvent(equity,
                                                                  QuoteAction.ADD);
        assertTrue(chain.process(equityAsk));
        verifyOptionChain(chain,
                          equity,
                          null,
                          equityAsk,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // send in a new ask
        AskEvent equityAsk2 = EventTestBase.generateEquityAskEvent(equity,
                                                                   QuoteAction.ADD);
        assertFalse(equityAsk.equals(equityAsk2));
        assertTrue(chain.process(equityAsk2));
        verifyOptionChain(chain,
                          equity,
                          null,
                          equityAsk2,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#getLatestUnderlyingBid()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getLatestUnderlyingBid()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        // no latest bid yet
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // create an ask for the underlying symbol
        BidEvent equityBid = EventTestBase.generateEquityBidEvent(equity,
                                                                  QuoteAction.ADD);
        assertTrue(chain.process(equityBid));
        verifyOptionChain(chain,
                          equity,
                          equityBid,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // send in a new ask
        BidEvent equityBid2 = EventTestBase.generateEquityBidEvent(equity,
                                                                   QuoteAction.ADD);
        assertFalse(equityBid.equals(equityBid2));
        assertTrue(chain.process(equityBid2));
        verifyOptionChain(chain,
                          equity,
                          equityBid2,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#getLatestUnderlyingTrade()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getLatestUnderlyingTrade()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        // no latest trade yet
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // create an ask for the underlying symbol
        TradeEvent equityTrade = EventTestBase.generateEquityTradeEvent(equity);
        assertTrue(chain.process(equityTrade));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          equityTrade,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // send in a new ask
        TradeEvent equityTrade2 = EventTestBase.generateEquityTradeEvent(equity);
        assertFalse(equityTrade.equals(equityTrade2));
        assertTrue(chain.process(equityTrade2));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          equityTrade2,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#getLatestUnderlyingMarketstat()}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getLatestUnderlyingMarketstat()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        // no latest marketstat yet
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // create an ask for the underlying symbol
        MarketstatEvent equityMarketstat = EventTestBase.generateEquityMarketstatEvent(equity);
        assertTrue(chain.process(equityMarketstat));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          equityMarketstat,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // send in a new ask
        MarketstatEvent equityMarketstat2 = EventTestBase.generateEquityMarketstatEvent(equity);
        assertFalse(equityMarketstat.equals(equityMarketstat2));
        assertTrue(chain.process(equityMarketstat2));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          equityMarketstat2,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // add an option event (creates an entry in the option chain)
        MarketstatEvent optionMarketstat = EventTestBase.generateOptionMarketstatEvent(putOption,
                                                                                       equity);
        assertTrue(chain.process(optionMarketstat));
        OptionContractPair entry = new OptionContractPair((OptionEvent)optionMarketstat);
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          equityMarketstat2,
                          new ArrayList<DividendEvent>(),
                          Arrays.asList(new OptionContractPair[] { entry }));
    }
    /**
     * Tests {@link OptionChain#process(org.marketcetera.event.Event)}. 
     *
     * <p>Note that this test uses the process capability of {@link OptionContractPair}
     * to prepare the expected data, which means that it merely tests {@link OptionContractPair}'s
     * ability to pass events through.  Another test is required to make sure that {@link OptionContract}
     * does the right thing.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void process()
            throws Exception
    {
        final OptionChain chain = new OptionChain(equity);
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                chain.process(null);
            }
        };
        AskEvent ask = EventTestBase.generateEquityAskEvent(equity,
                                                            QuoteAction.ADD);
        BidEvent bid = EventTestBase.generateEquityBidEvent(equity,
                                                            QuoteAction.ADD);
        TradeEvent trade = EventTestBase.generateEquityTradeEvent(equity);
        MarketstatEvent marketstat = EventTestBase.generateEquityMarketstatEvent(equity);
        DividendEvent dividend = EventTestBase.generateDividendEvent();
        LogEvent log = LogEventBuilder.error().withMessage(MESSAGE_0P).create();
        assertTrue(chain.process(ask));
        assertTrue(chain.process(bid));
        assertTrue(chain.process(trade));
        assertTrue(chain.process(marketstat));
        assertTrue(chain.process(dividend));
        assertFalse(chain.process(log));
        verifyOptionChain(chain,
                          equity,
                          bid,
                          ask,
                          trade,
                          marketstat,
                          Arrays.asList(new DividendEvent[] { dividend }),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * Tests {@link OptionChain#process(org.marketcetera.event.Event)} with error conditions.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void processErrorCases()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // generate events for a different underlying instrument
        Equity otherEquity = new Equity("GOOG");
        assertFalse(otherEquity.equals(equity));
        AskEvent ask = EventTestBase.generateEquityAskEvent(otherEquity,
                                                            QuoteAction.ADD);
        BidEvent bid = EventTestBase.generateEquityBidEvent(otherEquity,
                                                            QuoteAction.ADD);
        TradeEvent trade = EventTestBase.generateEquityTradeEvent(otherEquity);
        MarketstatEvent marketstat = EventTestBase.generateEquityMarketstatEvent(otherEquity);
        DividendEvent dividend = EventTestBase.generateDividendEvent(otherEquity);
        assertFalse(chain.process(ask));
        assertFalse(chain.process(bid));
        assertFalse(chain.process(trade));
        assertFalse(chain.process(marketstat));
        assertFalse(chain.process(dividend));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
        // same thing for option events (wrong symbol on events)
        ask = EventTestBase.generateOptionAskEvent(callOption,
                                                   QuoteAction.ADD);
        bid = EventTestBase.generateOptionBidEvent(callOption,
                                                   QuoteAction.ADD);
        trade = EventTestBase.generateOptionTradeEvent(callOption,
                                                       otherEquity);
        marketstat = EventTestBase.generateOptionMarketstatEvent(callOption,
                                                                 otherEquity);
        assertFalse(chain.process(ask));
        assertFalse(chain.process(bid));
        assertFalse(chain.process(trade));
        assertFalse(chain.process(marketstat));
        verifyOptionChain(chain,
                          equity,
                          null,
                          null,
                          null,
                          null,
                          new ArrayList<DividendEvent>(),
                          new ArrayList<OptionContractPair>());
    }
    /**
     * This test verifies that the {@link OptionContractPair} correct handles
     * events intended to modify the component {@link OptionContract} objects.
     * 
     * <p>Note that this test uses the process capability of {@link OptionContract}
     * to prepare the expected data, which means that it merely tests {@link OptionContractPair}'s
     * ability to pass events through.  Another test is required to make sure that {@link OptionContract}
     * does the right thing.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionChainPairProcessing()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        Collection<OptionContractPair> optionChain = chain.getOptionChain();
        // prepare expected contracts
        OptionContract expectedCall = new OptionContract(equity,
                                                         callOption,
                                                         OptionType.Call,
                                                         ExpirationType.AMERICAN,
                                                         true,
                                                         BigDecimal.TEN,
                                                         callOption.getSymbol());
        OptionContract expectedPut = new OptionContract(equity,
                                                        putOption,
                                                        OptionType.Put,
                                                        ExpirationType.AMERICAN,
                                                        true,
                                                        BigDecimal.TEN,
                                                        null);
        // create a few events for a given option contract
        assertTrue(optionChain.isEmpty());
        QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.optionAskEvent();
        QuoteEventBuilder<BidEvent> bidBuilder = QuoteEventBuilder.optionBidEvent();
        AskEvent ask = askBuilder.withExchange("Q")
                                 .withExpirationType(ExpirationType.AMERICAN)
                                 .withInstrument(callOption)
                                 .withMultiplier(BigDecimal.TEN)
                                 .hasDeliverable(true)
                                 .withPrice(EventTestBase.generateDecimalValue())
                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                 .withSize(EventTestBase.generateDecimalValue())
                                 .withProviderSymbol(callOption.getSymbol())
                                 .withUnderlyingInstrument(equity).create();
        BidEvent bid = bidBuilder.withExchange("X")
                                 .withExpirationType(ExpirationType.AMERICAN)
                                 .withInstrument(callOption)
                                 .withMultiplier(BigDecimal.TEN)
                                 .hasDeliverable(true)
                                 .withPrice(EventTestBase.generateDecimalValue())
                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                 .withSize(EventTestBase.generateDecimalValue())
                                 .withProviderSymbol(callOption.getSymbol())
                                 .withUnderlyingInstrument(equity).create();
        TradeEvent trade = EventTestBase.generateOptionTradeEvent(callOption,
                                                                  equity);
        MarketstatEvent marketstat = EventTestBase.generateOptionMarketstatEvent(callOption,
                                                                                 equity);
        assertTrue(chain.process(ask));
        OptionContractPair pair = optionChain.iterator().next();
        // prepare expected result
        expectedCall.process((OptionEvent)ask);
        verifyOptionContractPair(pair,
                                 null,
                                 expectedCall);
        assertTrue(chain.process(bid));
        expectedCall.process((OptionEvent)bid);
        verifyOptionContractPair(pair,
                                 null,
                                 expectedCall);
        assertTrue(chain.process(trade));
        expectedCall.process((OptionEvent)trade);
        verifyOptionContractPair(pair,
                                 null,
                                 expectedCall);
        assertTrue(chain.process(marketstat));
        expectedCall.process((OptionEvent)marketstat);
        verifyOptionContractPair(pair,
                                 null,
                                 expectedCall);
        assertEquals(1,
                     optionChain.size());
        // repeat tests for put side 
        ask = askBuilder.withExchange("Q")
                        .withExpirationType(ExpirationType.AMERICAN)
                        .withInstrument(putOption)
                        .withMultiplier(BigDecimal.TEN)
                        .hasDeliverable(true)
                        .withPrice(EventTestBase.generateDecimalValue())
                        .withQuoteDate(DateUtils.dateToString(new Date()))
                        .withSize(EventTestBase.generateDecimalValue())
                        .withProviderSymbol(putOption.getSymbol())
                        .withUnderlyingInstrument(equity).create();
        bid = bidBuilder.withExchange("X")
                        .withExpirationType(ExpirationType.AMERICAN)
                        .withInstrument(putOption)
                        .withMultiplier(BigDecimal.TEN)
                        .hasDeliverable(true)
                        .withPrice(EventTestBase.generateDecimalValue())
                        .withQuoteDate(DateUtils.dateToString(new Date()))
                        .withSize(EventTestBase.generateDecimalValue())
                        .withProviderSymbol(putOption.getSymbol())
                        .withUnderlyingInstrument(equity).create();
        trade = EventTestBase.generateOptionTradeEvent(putOption,
                                                       equity);
        marketstat = EventTestBase.generateOptionMarketstatEvent(putOption,
                                                                 equity);
        assertTrue(chain.process(ask));
        expectedPut.process((OptionEvent)ask);
        verifyOptionContractPair(pair,
                                 expectedPut,
                                 expectedCall);
        assertTrue(chain.process(bid));
        expectedPut.process((OptionEvent)bid);
        verifyOptionContractPair(pair,
                                 expectedPut,
                                 expectedCall);
        assertTrue(chain.process(trade));
        expectedPut.process((OptionEvent)trade);
        verifyOptionContractPair(pair,
                                 expectedPut,
                                 expectedCall);
        assertTrue(chain.process(marketstat));
        expectedPut.process((OptionEvent)marketstat);
        verifyOptionContractPair(pair,
                                 expectedPut,
                                 expectedCall);
        assertEquals(1,
                     optionChain.size());
    }
    /**
     * This test verifies that <code>OptionContractPair</code> objects are maintained
     * in the correct sort order. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void optionContractOrder()
            throws Exception
    {
        OptionChain chain = new OptionChain(equity);
        Collection<OptionContractPair> optionChain = chain.getOptionChain();
        assertTrue(optionChain.isEmpty());
        // we're going to need several different options for this test, all for the same underlying symbol
        Option o1 = new Option("ABC",
                               "20100101",
                               BigDecimal.ONE,
                               OptionType.Put);
        // this value has a higher strike price than o1, all other details the same
        Option o2 = new Option(o1.getSymbol(),
                               o1.getExpiry(),
                               o1.getStrikePrice().add(BigDecimal.ONE),
                               OptionType.Put);
        // this value has a higher expiry than o1, all other details the same
        Option o3 = new Option(o1.getSymbol(),
                               "20110101",
                               o1.getStrikePrice(),
                               OptionType.Put);
        // this value has a lexicographically higher value than o1, all other details the same
        Option o4 = new Option("ABCD",
                               o1.getExpiry(),
                               o1.getStrikePrice(),
                               OptionType.Put);
        // verify the data is set up correctly (asserting that the above comments are true)
        assertTrue(o1.getStrikePrice().compareTo(o2.getStrikePrice()) == -1);
        assertTrue(o1.getExpiry().compareTo(o3.getExpiry()) == -1);
        assertTrue(o1.getSymbol().compareTo(o4.getSymbol()) == -1);
        // we expect the order to end up being: o1, o2, o3, o4.  to prove that the contract pairs are sorted and not
        //  FIFO or FILO, we'll add them as follows: o2, o3, o1, o4.  remember that to create contract pairs, we
        //  add events for them
        // first, create one contract pair (we're just going to use a single event type to simplify the test.
        //  in this case, the type of event doesn't matter)
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.optionAskEvent();
        builder.withExchange("Q")
               .withExpirationType(ExpirationType.AMERICAN)
               .withPrice(EventTestBase.generateDecimalValue())
               .withSize(EventTestBase.generateDecimalValue())
               .withUnderlyingInstrument(equity)
               .withQuoteDate(DateUtils.dateToString(new Date()));
        assertTrue(chain.process(builder.withInstrument(o2).create()));
        assertEquals(1,
                     optionChain.size());
        assertEquals(o2,
                     optionChain.iterator().next().getPut().getInstrument());
        // add the next contract
        assertTrue(chain.process(builder.withInstrument(o3).create()));
        assertEquals(2,
                     optionChain.size());
        Iterator<OptionContractPair> iterator = optionChain.iterator();
        assertEquals(o2,
                     iterator.next().getPut().getInstrument());
        assertEquals(o3,
                     iterator.next().getPut().getInstrument());
        // next
        assertTrue(chain.process(builder.withInstrument(o1).create()));
        assertEquals(3,
                     optionChain.size());
        iterator = optionChain.iterator();
        assertEquals(o1,
                     iterator.next().getPut().getInstrument());
        assertEquals(o2,
                     iterator.next().getPut().getInstrument());
        assertEquals(o3,
                     iterator.next().getPut().getInstrument());
        // last
        assertTrue(chain.process(builder.withInstrument(o4).create()));
        assertEquals(4,
                     optionChain.size());
        iterator = optionChain.iterator();
        assertEquals(o1,
                     iterator.next().getPut().getInstrument());
        assertEquals(o2,
                     iterator.next().getPut().getInstrument());
        assertEquals(o3,
                     iterator.next().getPut().getInstrument());
        assertEquals(o4,
                     iterator.next().getPut().getInstrument());
    }
    /**
     * This test tests {@link OptionContractPair#equals(Object)} and {@link OptionContractPair#hashCode()}.
     * 
     * <p>Most of the relevant testing of <code>equals(Object)</code> is done above.  This method tests
     * the boundary cases not exercised by the use of the <code>OptionChain</code> <code>Collection</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionContractPairEqualsHashCode()
            throws Exception
    {
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.optionAskEvent();
        builder.withExchange("Q")
               .withExpirationType(ExpirationType.AMERICAN)
               .withPrice(EventTestBase.generateDecimalValue())
               .withQuoteDate(DateUtils.dateToString(new Date()))
               .withSize(EventTestBase.generateDecimalValue())
               .withUnderlyingInstrument(equity);
        OptionContractPair pair = new OptionContractPair((OptionEvent)builder.withInstrument(putOption).create());
        OptionContractPair equalPair = new OptionContractPair((OptionEvent)builder.create());
        OptionContractPair unequalPair = new OptionContractPair((OptionEvent)builder.withInstrument(new Option(putOption.getSymbol(),
                                                                                                               putOption.getExpiry(),
                                                                                                               putOption.getStrikePrice().add(BigDecimal.ONE),
                                                                                                               OptionType.Put)).create());
        EqualityAssert.assertEquality(pair,
                                      equalPair,
                                      unequalPair,
                                      this,
                                      null);
    }
    /**
     * Tests {@link OptionContractPair#equals(Object)} and {@link OptionContractPair#hashCode()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void contractPairHashCodeAndEquals()
            throws Exception
    {
        Option otherOptionStrike = new Option(callOption.getSymbol(),
                                              callOption.getExpiry(),
                                              callOption.getStrikePrice().add(BigDecimal.ONE),
                                              OptionType.Call);
        Option otherOptionExpiry = new Option(callOption.getSymbol(),
                                              "20110319",
                                              callOption.getStrikePrice(),
                                              OptionType.Call);
        Option otherOptionSymbol = new Option("COLIN",
                                              callOption.getExpiry(),
                                              callOption.getStrikePrice(),
                                              OptionType.Call);
        assertFalse(callOption.getSymbol().equals(otherOptionSymbol.getSymbol()));
        assertFalse(callOption.getExpiry().equals(otherOptionExpiry.getExpiry()));
        assertFalse(callOption.getStrikePrice().equals(otherOptionStrike.getStrikePrice()));
        OptionContractPairKey key1 = OptionContractPair.getOptionContractPairKey(callOption);
        OptionContractPairKey key2 = OptionContractPair.getOptionContractPairKey(callOption);
        OptionContractPairKey key3 = OptionContractPair.getOptionContractPairKey(otherOptionStrike);
        OptionContractPairKey key4 = OptionContractPair.getOptionContractPairKey(otherOptionExpiry);
        OptionContractPairKey key5 = OptionContractPair.getOptionContractPairKey(otherOptionSymbol);
        EqualityAssert.assertEquality(key1,
                                      key2,
                                      key3,
                                      key4,
                                      key5,
                                      this,
                                      null);
    }
    /**
     * Verifies that the given <code>OptionContractPair</code> contains the
     * given expected results.
     *
     * @param inActualContractPair an <code>OptionContractPair</code> value
     * @param inExpectedPut an <code>OptionContract</code> value
     * @param inExpectedCall an <code>OptionContract</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyOptionContractPair(OptionContractPair inActualContractPair,
                                                 OptionContract inExpectedPut,
                                                 OptionContract inExpectedCall)
            throws Exception
    {
        assertNotNull(inActualContractPair);
        if(inExpectedPut == null) {
            assertNull(inActualContractPair.getPut());
        } else {
            verifyOptionContract(inActualContractPair.getPut(),
                                 inExpectedPut.getUnderlyingInstrument(),
                                 inExpectedPut.getInstrument(),
                                 inExpectedPut.getInstrument().getSymbol(),
                                 inExpectedPut.getExpirationType(),
                                 inExpectedPut.getMultiplier(),
                                 inExpectedPut.hasDeliverable(),
                                 inExpectedPut.getLatestBid(),
                                 inExpectedPut.getLatestAsk(),
                                 inExpectedPut.getLatestTrade(),
                                 inExpectedPut.getLatestMarketstat());
        }
        if(inExpectedCall == null) {
            assertNull(inActualContractPair.getCall());
        } else {
            verifyOptionContract(inActualContractPair.getCall(),
                                 inExpectedCall.getUnderlyingInstrument(),
                                 inExpectedCall.getInstrument(),
                                 inExpectedCall.getInstrument().getSymbol(),
                                 inExpectedCall.getExpirationType(),
                                 inExpectedCall.getMultiplier(),
                                 inExpectedCall.hasDeliverable(),
                                 inExpectedCall.getLatestBid(),
                                 inExpectedCall.getLatestAsk(),
                                 inExpectedCall.getLatestTrade(),
                                 inExpectedCall.getLatestMarketstat());
        }
    }
    /**
     * Verifies that the given <code>OptionContract</code> contains the given
     * expected values.
     *
     * @param inActualContract an <code>OptionContract</code> value
     * @param inExpectedUnderlyingInstrument an <code>Instrument</code> value
     * @param inExpectedOption an <code>Option</code> value
     * @param inExpectedProviderSymbol a <code>String</code> value
     * @param inExpectedExpirationType an <code>ExpirationType</code> value
     * @param inExpectedMultiplier a <code>BigDecimal</code> value
     * @param inExpectedHasDeliverable a <code>boolean</code> value
     * @param inExpectedLatestBid a <code>BidEvent</code> value
     * @param inExpectedLatestAsk an <code>AskEvent</code> value
     * @param inExpectedLatestTrade a <code>TradeEvent</code> value
     * @param inExpectedLatestMarketstat a <code>MarketstatEvent</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyOptionContract(OptionContract inActualContract,
                                             Instrument inExpectedUnderlyingInstrument,
                                             Option inExpectedOption,
                                             String inExpectedProviderSymbol,
                                             ExpirationType inExpectedExpirationType,
                                             BigDecimal inExpectedMultiplier,
                                             boolean inExpectedHasDeliverable,
                                             BidEvent inExpectedLatestBid,
                                             AskEvent inExpectedLatestAsk,
                                             TradeEvent inExpectedLatestTrade,
                                             MarketstatEvent inExpectedLatestMarketstat)
            throws Exception
    {
        assertEquals(inExpectedUnderlyingInstrument,
                     inActualContract.getUnderlyingInstrument());
        assertEquals(inExpectedOption,
                     inActualContract.getInstrument());
        assertEquals(inExpectedExpirationType,
                     inActualContract.getExpirationType());
        assertEquals(inExpectedMultiplier,
                     inActualContract.getMultiplier());
        assertEquals(inExpectedHasDeliverable,
                     inActualContract.hasDeliverable());
        assertEquals(inExpectedLatestBid,
                     inActualContract.getLatestBid());
        assertEquals(inExpectedLatestAsk,
                     inActualContract.getLatestAsk());
        assertEquals(inExpectedLatestTrade,
                     inActualContract.getLatestTrade());
        assertEquals(inExpectedLatestMarketstat,
                     inActualContract.getLatestMarketstat());
        assertEquals(inExpectedProviderSymbol,
                     inActualContract.getProviderSymbol());
    }
    /**
     * Verifies that the given <code>OptionChain</code> object contains the given expected values.
     *
     * @param inActualOptionChain an <code>OptionChain</code> value
     * @param inExpectedUnderlyingInstrument an <code>Instrument</code> value
     * @param inExpectedLatestBid a <code>BidEvent</code> value
     * @param inExpectedLatestAsk an <code>AskEvent</code> value
     * @param inExpectedLatestTrade a <code>TradeEvent</code> value
     * @param inExpectedLatestMarketstat a <code>Marketstat</code> value
     * @param inExpectedDividends a <code>List&lt;DividendEvent&gt;</code> value
     * @param inExpectedOptionChainContents a <code>Collection&lt;OptionContractPair&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyOptionChain(OptionChain inActualOptionChain,
                                          Instrument inExpectedUnderlyingInstrument,
                                          BidEvent inExpectedLatestBid,
                                          AskEvent inExpectedLatestAsk,
                                          TradeEvent inExpectedLatestTrade,
                                          MarketstatEvent inExpectedLatestMarketstat,
                                          List<DividendEvent> inExpectedDividends,
                                          Collection<OptionContractPair> inExpectedOptionChainContents)
            throws Exception
    {
        assertNotNull(inActualOptionChain);
        assertNotNull(inActualOptionChain.toString());
        assertEquals(inExpectedUnderlyingInstrument,
                     inActualOptionChain.getUnderlyingInstrument());
        assertEquals(inExpectedLatestBid,
                     inActualOptionChain.getLatestUnderlyingBid());
        assertEquals(inExpectedLatestAsk,
                     inActualOptionChain.getLatestUnderlyingAsk());
        assertEquals(inExpectedLatestTrade,
                     inActualOptionChain.getLatestUnderlyingTrade());
        assertEquals(inExpectedLatestMarketstat,
                     inActualOptionChain.getLatestUnderlyingMarketstat());
        // convert to arrays for the equality test because otherwise the type of the collection
        //  is included in the test, which isn't relevant (LinkedList vs. ArrayList or ImmutableCollection vs. List, that kind of thing - just test
        //  the contents)
        assertTrue(Arrays.equals(inExpectedOptionChainContents.toArray(),
                                 inActualOptionChain.getOptionChain().toArray()));
        assertEquals(inExpectedDividends,
                     inActualOptionChain.getDividends());
    }
    /**
     * test instrument
     */
    private final Equity equity = new Equity("METC");
    /**
     * test option
     */
    private final Option callOption = new Option("MSFT",
                                                 "20100319",
                                                 BigDecimal.ONE,
                                                 OptionType.Call);
    /**
     * test option
     */
    private final Option putOption = new Option("MSFT",
                                                "20100319",
                                                BigDecimal.ONE,
                                                OptionType.Put);
}
