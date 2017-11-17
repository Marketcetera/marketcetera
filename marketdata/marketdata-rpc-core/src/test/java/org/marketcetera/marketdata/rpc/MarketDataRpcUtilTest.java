package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.HasTimestamps;
import org.marketcetera.event.ImbalanceEvent;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.rpc.base.BaseRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trade.ConvertibleBond;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trading.rpc.TradeRpcUtil;

import com.google.common.collect.Lists;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/* $License$ */

/**
 * Tests {@link MarketDataRpcUtil}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(JUnitParamsRunner.class)
@Parameters(method="instrumentParameters")
public class MarketDataRpcUtilTest
{
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link TradeEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetRpcTradeEvent(Instrument inInstrument)
            throws Exception
    {
        Event event = EventTestBase.generateTradeEvent(inInstrument);
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link QuoteEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetRpcQuoteEvent(Instrument inInstrument)
            throws Exception
    {
        // test ask
        Event event = EventTestBase.generateAskEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
        // test bid
        event = EventTestBase.generateBidEvent(new Equity("METC"));
        rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link DividendEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcDividendEvent()
            throws Exception
    {
        Event event = EventTestBase.generateDividendEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link LogEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcLogEvent()
            throws Exception
    {
        Event event = EventTestBase.generateLogEvent();
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link ImbalanceEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetRpcImbalanceEvent(Instrument inInstrument)
            throws Exception
    {
        Event event = EventTestBase.generateImbalanceEvent(inInstrument);
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link MarketstatEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetRpcMarketstatEvent(Instrument inInstrument)
            throws Exception
    {
        Event event = EventTestBase.generateMarketstatEvent(inInstrument);
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link DepthOfBookEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetDepthOfBookEvent(Instrument inInstrument)
            throws Exception
    {
        Event event = EventTestBase.generateDepthOfBookEvent(inInstrument);
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link TopOfBookEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetTopOfBookEvent(Instrument inInstrument)
            throws Exception
    {
        Event event = EventTestBase.generateTopOfBookEvent(inInstrument);
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(newEvent);
        verifyEvent(rpcEvent,
                    newEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getMarketDataRequest(String, String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Parameters(method="instrumentParameters")
    public void testGetMarketDataRequest(Instrument inInstrument)
            throws Exception
    {
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        requestBuilder.withContent(Content.TOP_OF_BOOK);
        requestBuilder.withSymbols(inInstrument.getFullSymbol());
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(requestBuilder.create().toString(),
                                                           UUID.randomUUID().toString(),
                                                           null).isPresent());
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(requestBuilder.create().toString(),
                                                           null,
                                                           UUID.randomUUID().toString()).isPresent());
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(null,
                                                           UUID.randomUUID().toString(),
                                                           UUID.randomUUID().toString()).isPresent());
    }
    /**
     * Verify the given event matches the given RPC event.
     *
     * @param inExpectedEvent an <code>MarketDataTypesRpc.EventHolder</code> value
     * @param inActualEvent an <code>Event</code> value
     */
    public static void verifyEvent(MarketDataTypesRpc.EventHolder inExpectedEvent,
                                   Event inActualEvent)
    {
        if(inExpectedEvent.hasTradeEvent()) {
            assertTrue("Expected: " + TradeEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof TradeEvent);
            verifyTradeEvent(inExpectedEvent.getTradeEvent(),
                             (TradeEvent)inActualEvent);
        } else if(inExpectedEvent.hasQuoteEvent()) {
            assertTrue("Expected: " + QuoteEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof QuoteEvent);
            verifyQuoteEvent(inExpectedEvent.getQuoteEvent(),
                             (QuoteEvent)inActualEvent);
        } else if(inExpectedEvent.hasLogEvent()) {
            assertTrue("Expected: " + LogEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof LogEvent);
            verifyLogEvent(inExpectedEvent.getLogEvent(),
                           (LogEvent)inActualEvent);
        } else if(inExpectedEvent.hasDividendEvent()) {
            assertTrue("Expected: " + DividendEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof DividendEvent);
            verifyDividendEvent(inExpectedEvent.getDividendEvent(),
                                (DividendEvent)inActualEvent);
        } else if(inExpectedEvent.hasImbalanceEvent()) {
            assertTrue("Expected: " + ImbalanceEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof ImbalanceEvent);
            verifyImbalanceEvent(inExpectedEvent.getImbalanceEvent(),
                                (ImbalanceEvent)inActualEvent);
        } else if(inExpectedEvent.hasMarketstatEvent()) {
            assertTrue("Expected: " + MarketstatEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof MarketstatEvent);
            verifyMarketstatEvent(inExpectedEvent.getMarketstatEvent(),
                                  (MarketstatEvent)inActualEvent);
        } else if(inExpectedEvent.hasDepthOfBookEvent()) {
            assertTrue("Expected: " + DepthOfBookEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof DepthOfBookEvent);
            verifyDepthOfBookEvent(inExpectedEvent.getDepthOfBookEvent(),
                                  (DepthOfBookEvent)inActualEvent);
        } else if(inExpectedEvent.hasTopOfBookEvent()) {
            assertTrue("Expected: " + TopOfBookEvent.class.getSimpleName() + " Actual: " + inActualEvent.getClass().getSimpleName(),
                       inActualEvent instanceof TopOfBookEvent);
            verifyTopOfBookEvent(inExpectedEvent.getTopOfBookEvent(),
                                 (TopOfBookEvent)inActualEvent);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Verify the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent an <code>Event</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.EventHolder</code> value
     */
    public static void verifyRpcEventHolder(Event inExpectedEvent,
                                            MarketDataTypesRpc.EventHolder inActualEvent)
    {
        if(inActualEvent.hasTradeEvent()) {
            assertTrue("Expected: " + TradeEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof TradeEvent);
            verifyRpcTradeEvent((TradeEvent)inExpectedEvent,
                                inActualEvent.getTradeEvent());
        } else if(inActualEvent.hasQuoteEvent()) {
            assertTrue("Expected: " + QuoteEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof QuoteEvent);
            verifyRpcQuoteEvent((QuoteEvent)inExpectedEvent,
                                inActualEvent.getQuoteEvent());
        } else if(inActualEvent.hasDividendEvent()) {
            assertTrue("Expected: " + DividendEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof DividendEvent);
            verifyRpcDividendEvent((DividendEvent)inExpectedEvent,
                                   inActualEvent.getDividendEvent());
        } else if(inActualEvent.hasLogEvent()) {
            assertTrue("Expected: " + LogEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof LogEvent);
            verifyRpcLogEvent((LogEvent)inExpectedEvent,
                              inActualEvent.getLogEvent());
        } else if(inActualEvent.hasImbalanceEvent()) {
            assertTrue("Expected: " + ImbalanceEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof ImbalanceEvent);
            verifyRpcImbalanceEvent((ImbalanceEvent)inExpectedEvent,
                                    inActualEvent.getImbalanceEvent());
        } else if(inActualEvent.hasMarketstatEvent()) {
            assertTrue("Expected: " + MarketstatEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof MarketstatEvent);
            verifyRpcMarketstatEvent((MarketstatEvent)inExpectedEvent,
                                     inActualEvent.getMarketstatEvent());
        } else if(inActualEvent.hasDepthOfBookEvent()) {
            assertTrue("Expected: " + DepthOfBookEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof DepthOfBookEvent);
            verifyRpcDepthOfBookEvent((DepthOfBookEvent)inExpectedEvent,
                                      inActualEvent.getDepthOfBookEvent());
        } else if(inActualEvent.hasTopOfBookEvent()) {
            assertTrue("Expected: " + TopOfBookEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof TopOfBookEvent);
            verifyRpcTopOfBookEvent((TopOfBookEvent)inExpectedEvent,
                                    inActualEvent.getTopOfBookEvent());
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Verify that the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent an <code>ImbalanceEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.ImbalanceEvent</code> value
     */
    public static void verifyRpcImbalanceEvent(ImbalanceEvent inExpectedEvent,
                                               MarketDataTypesRpc.ImbalanceEvent inActualEvent)
    {
        assertEquals(inExpectedEvent.getAuctionType(),
                     MarketDataRpcUtil.getAuctionType(inActualEvent.getAuctionType()).orElse(null));
        verifyRpcEvent(inExpectedEvent,
                       inActualEvent.getEvent());
        assertEquals(inExpectedEvent.getEventType(),
                     MarketDataRpcUtil.getEventType(inActualEvent.getEventType()).orElse(null));
        assertEquals(inExpectedEvent.getExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getExchange()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getFarPrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getFarPrice()),
                   inExpectedEvent.getFarPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getFarPrice()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getImbalanceType(),
                     MarketDataRpcUtil.getImbalanceType(inActualEvent.getImbalanceType()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getImbalanceVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getImbalanceVolume()),
                   inExpectedEvent.getImbalanceVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getImbalanceVolume()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        assertEquals(inExpectedEvent.isShortSaleRestricted(),
                     inActualEvent.getIsShortSaleRestricted());
        assertEquals(inExpectedEvent.getMarketStatus(),
                     MarketDataRpcUtil.getMarketStatus(inActualEvent.getMarketStatus()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getNearPrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getNearPrice()),
                   inExpectedEvent.getNearPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getNearPrice()).orElse(null)) == 0);
        assertTrue("Expected: " + inExpectedEvent.getPairedVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getPairedVolume()),
                   inExpectedEvent.getPairedVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPairedVolume()).orElse(null)) == 0);
        assertTrue("Expected: " + inExpectedEvent.getReferencePrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getReferencePrice()),
                   inExpectedEvent.getReferencePrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getReferencePrice()).orElse(null)) == 0);
    }
    /**
     * Verify that the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent a <code>LogEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.LogEvent</code> value
     */
    public static void verifyRpcLogEvent(LogEvent inExpectedEvent,
                                         MarketDataTypesRpc.LogEvent inActualEvent)
    {
        if(inExpectedEvent.getException() == null) {
            assertFalse(inActualEvent.hasException());
        } else {
            Throwable rpcException = (Throwable)BaseRpcUtil.getObject(inActualEvent.getException()).orElse(null);
            assertEquals(inExpectedEvent.getException().getMessage(),
                         rpcException.getMessage());
        }
        assertEquals(inExpectedEvent.getLevel(),
                     MarketDataRpcUtil.getLogEventLevel(inActualEvent.getLogEventLevel()).orElse(null));
        assertEquals(inExpectedEvent.getMessage(),
                     BaseRpcUtil.getStringValue(inActualEvent.getMessage()).orElse(null));
    }
    /**
     * Verify the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent a <code>DividendEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.DividendEvent</code> value
     */
    public static void verifyRpcDividendEvent(DividendEvent inExpectedEvent,
                                              MarketDataTypesRpc.DividendEvent inActualEvent)
    {
        assertTrue("Expected: " + inExpectedEvent.getAmount() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getAmount()),
                   inExpectedEvent.getAmount().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getAmount()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getCurrency(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCurrency()).orElse(null));
        assertEquals(inExpectedEvent.getDeclareDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getDeclareDate()).orElse(null));
        assertEquals(inExpectedEvent.getExecutionDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getExecutionDate()).orElse(null));
        assertEquals(inExpectedEvent.getFrequency(),
                     MarketDataRpcUtil.getDividendFrequency(inActualEvent.getFrequency()).orElse(null));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        assertEquals(inExpectedEvent.getPaymentDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getPaymentDate()).orElse(null));
        assertEquals(inExpectedEvent.getRecordDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getRecordDate()).orElse(null));
        assertEquals(inExpectedEvent.getStatus(),
                     MarketDataRpcUtil.getDividendStatus(inActualEvent.getStatus()).orElse(null));
        assertEquals(inExpectedEvent.getType(),
                     MarketDataRpcUtil.getDividendType(inActualEvent.getType()).orElse(null));
    }
    /**
     * Verify the given RPC depth-of-book event matches the given expected event.
     *
     * @param inExpectedEvent a <code>DepthOfBookEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.DepthOfBookEvent</code> value
     */
    public static void verifyRpcDepthOfBookEvent(DepthOfBookEvent inExpectedEvent,
                                                 MarketDataTypesRpc.DepthOfBookEvent inActualEvent)
    {
        assertEquals(inExpectedEvent.getAsks().size(),
                     inActualEvent.getAsksCount());
        final Iterator<MarketDataTypesRpc.QuoteEvent> askIterator = inActualEvent.getAsksList().iterator();
        inExpectedEvent.getAsks().stream().forEach(ask->verifyRpcQuoteEvent(ask,
                                                                            askIterator.next()));
        assertEquals(inExpectedEvent.getBids().size(),
                     inActualEvent.getBidsCount());
        final Iterator<MarketDataTypesRpc.QuoteEvent> bidIterator = inActualEvent.getBidsList().iterator();
        inExpectedEvent.getBids().stream().forEach(bid->verifyRpcQuoteEvent(bid,
                                                                            bidIterator.next()));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
    }
    /**
     * Verify the given RPC top-of-book event matches the given expected event.
     *
     * @param inExpectedEvent a <code>TopOfBookEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.DepthOfBookEvent</code> value
     */
    public static void verifyRpcTopOfBookEvent(TopOfBookEvent inExpectedEvent,
                                               MarketDataTypesRpc.TopOfBookEvent inActualEvent)
    {
        if(inActualEvent.hasAsk()) {
            assertNotNull(inExpectedEvent.getAsk());
            verifyRpcQuoteEvent(inExpectedEvent.getAsk(),
                                inActualEvent.getAsk());
        } else {
            assertNull(inExpectedEvent.getAsk());
        }
        if(inActualEvent.hasBid()) {
            assertNotNull(inExpectedEvent.getBid());
            verifyRpcQuoteEvent(inExpectedEvent.getBid(),
                                inActualEvent.getBid());
        } else {
            assertNull(inExpectedEvent.getBid());
        }
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
    }
    /**
     * Verify the given RPC quote event matches the given expected event.
     *
     * @param inExpectedEvent a <code>QuoteEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.QuoteEvent</code> value
     */
    public static void verifyRpcQuoteEvent(QuoteEvent inExpectedEvent,
                                           MarketDataTypesRpc.QuoteEvent inActualEvent)
    {
        assertEquals(inExpectedEvent.getCount(),
                     inActualEvent.getCount());
        assertEquals(inExpectedEvent instanceof BidEvent,
                     inActualEvent.getIsBid());
        assertEquals(inExpectedEvent.isEmpty(),
                     inActualEvent.getIsEmpty());
        assertEquals(inExpectedEvent.getLevel(),
                     inActualEvent.getLevel());
        verifyRpcMarketDataEvent(inExpectedEvent,
                                 inActualEvent.getMarketDataEvent());
        assertEquals(inExpectedEvent.getAction(),
                     MarketDataRpcUtil.getQuoteAction(inActualEvent.getQuoteAction()).orElse(null));
    }
    /**
     * Verify the given RPC market stat event matches the given market stat event.
     *
     * @param inExpectedEvent a <code>MarketstatEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.MarketstatEvent</code> value
     */
    public static void verifyRpcMarketstatEvent(MarketstatEvent inExpectedEvent,
                                                MarketDataTypesRpc.MarketstatEvent inActualEvent)
    {
        verifyRpcQuantity(inExpectedEvent.getClose(),
                          inActualEvent.getClose());
        assertEquals(inExpectedEvent.getCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getCloseExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseExchange()).orElse(null));
        verifyRpcEvent(inExpectedEvent,
                       inActualEvent.getEvent());
        assertEquals(inExpectedEvent.getEventType(),
                     MarketDataRpcUtil.getEventType(inActualEvent.getEventType()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getHigh(),
                          inActualEvent.getHigh());
        assertEquals(inExpectedEvent.getHighExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getHighExchange()).orElse(null));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getLow(),
                          inActualEvent.getLow());
        assertEquals(inExpectedEvent.getLowExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getLowExchange()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getOpen(),
                          inActualEvent.getOpen());
        assertEquals(inExpectedEvent.getOpenExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getOpenExchange()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getPreviousClose(),
                          inActualEvent.getPreviousClose());
        assertEquals(inExpectedEvent.getPreviousCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getPreviousCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getTradeHighTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeHighTime()).orElse(null));
        assertEquals(inExpectedEvent.getTradeLowTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeLowTime()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getValue(),
                          inActualEvent.getValue());
        verifyRpcQuantity(inExpectedEvent.getVolume(),
                          inActualEvent.getVolume());
    }
    /**
     * Verify the given RPC market data event matches the given market data event.
     *
     * @param inExpectedEvent a <code>MarketDataEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.MarketDataEvent</code> value
     */
    public static void verifyRpcMarketDataEvent(MarketDataEvent inExpectedEvent,
                                                MarketDataTypesRpc.MarketDataEvent inActualEvent)
    {
        verifyRpcEvent(inExpectedEvent,
                       inActualEvent.getEvent());
        assertEquals(inExpectedEvent.getEventType(),
                     MarketDataRpcUtil.getEventType(inActualEvent.getEventType()).orElse(null));
        assertEquals(inExpectedEvent.getExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getExchange()).orElse(null));
        assertEquals(inExpectedEvent.getExchangeTimestamp(),
                     BaseRpcUtil.getDateValue(inActualEvent.getExchangeTimestamp()).orElse(null));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        verifyRpcQuantity(inExpectedEvent.getPrice(),
                          inActualEvent.getPrice());
        if(inActualEvent.hasEventTimestamps()) {
            verifyRpcTimestamps(inExpectedEvent,
                                inActualEvent.getEventTimestamps());
        } else {
            verifyRpcTimestamps(inExpectedEvent,
                                null);
        }
        verifyRpcQuantity(inExpectedEvent.getSize(),
                          inActualEvent.getSize());
    }
    /**
     * Verify the given timestamp holder matches the given RPC expected value.
     *
     * @param inExpectedTimestamps a <code>MarketDataTypesRpc.EventTimestamps</code> value or <code>null</code>
     * @param inActualTimestamps a <code>HasTimestamps</code> value
     */
    public static void verifyTimestamps(MarketDataTypesRpc.EventTimestamps inExpectedTimestamps,
                                        HasTimestamps inActualTimestamps)
    {
        if(inExpectedTimestamps == null) {
            assertEquals(0,
                         inActualTimestamps.getProcessedTimestamp());
            assertEquals(0,
                         inActualTimestamps.getReceivedTimestamp());
        } else {
            assertEquals(inExpectedTimestamps.getProcessedTimestamp(),
                         inActualTimestamps.getProcessedTimestamp());
            assertEquals(inExpectedTimestamps.getReceivedTimestamp(),
                         inActualTimestamps.getReceivedTimestamp());
        }
    }
    /**
     * Verify the given RPC timestamp holder matches the given expected value.
     *
     * @param inExpectedTimestamps a <code>HasTimestamps</code> value
     * @param inActualTimestamps a <code>MarketDataTypesRpc.EventTimestamps</code> value or <code>null</code>
     */
    public static void verifyRpcTimestamps(HasTimestamps inExpectedTimestamps,
                                           MarketDataTypesRpc.EventTimestamps inActualTimestamps)
    {
        if(inActualTimestamps == null) {
            assertEquals(0,
                         inExpectedTimestamps.getProcessedTimestamp());
            assertEquals(0,
                         inExpectedTimestamps.getReceivedTimestamp());
        } else {
            assertEquals(inActualTimestamps.getProcessedTimestamp(),
                         inExpectedTimestamps.getProcessedTimestamp());
            assertEquals(inActualTimestamps.getReceivedTimestamp(),
                         inExpectedTimestamps.getReceivedTimestamp());
        }
    }
    /**
     * Verify the given RPC market data event matches the given market data event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.MarketDataEvent</code> value
     * @param inActualEvent a <code>MarketDataEvent</code> value
     */
    public static void verifyMarketDataEvent(MarketDataTypesRpc.MarketDataEvent inExpectedEvent,
                                             MarketDataEvent inActualEvent)
    {
        verifyEvent(inExpectedEvent.getEvent(),
                    inActualEvent);
        assertEquals(MarketDataRpcUtil.getEventType(inExpectedEvent.getEventType()).orElse(null),
                     inActualEvent.getEventType());
        assertEquals(inExpectedEvent.getExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getExchange()).orElse(null));
        assertEquals(BaseRpcUtil.getDateValue(inExpectedEvent.getExchangeTimestamp()).orElse(null),
                     inActualEvent.getExchangeTimestamp());
        assertEquals(TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null),
                     inActualEvent.getInstrument());
        assertTrue("Expected: " + BaseRpcUtil.getScaledQuantity(inExpectedEvent.getPrice()) + " actual: " + inActualEvent.getPrice(),
                   BaseRpcUtil.getScaledQuantity(inExpectedEvent.getPrice()).orElse(null).compareTo(inActualEvent.getPrice()) == 0);
        if(inExpectedEvent.hasEventTimestamps()) {
            verifyTimestamps(inExpectedEvent.getEventTimestamps(),
                             inActualEvent);
        } else {
            verifyTimestamps(null,
                             inActualEvent);
        }
        assertTrue("Expected: " + BaseRpcUtil.getScaledQuantity(inExpectedEvent.getSize()) + " actual: " + inActualEvent.getSize(),
                   BaseRpcUtil.getScaledQuantity(inExpectedEvent.getSize()).orElse(null).compareTo(inActualEvent.getSize()) == 0);
        if(inExpectedEvent.hasOptionAttributes()) {
            verifyOptionAttributes(inExpectedEvent.getOptionAttributes(),
                                   inActualEvent);
        } else {
            verifyOptionAttributes(null,
                                   inActualEvent);
        }
    }
    /**
     * Verify the given RPC event matches the given event.
     *
     * @param inEvent a <code>MarketDataTypesRpc.Event</code> value
     * @param inActualEvent an <code>Event</code> value
     */
    public static void verifyEvent(MarketDataTypesRpc.Event inExpectedEvent,
                                   Event inActualEvent)
    {
        assertEquals(inExpectedEvent.getMessageId(),
                     inActualEvent.getMessageId());
        assertEquals(StringUtils.trimToNull(inExpectedEvent.getProvider()),
                     BaseRpcUtil.getStringValue(inActualEvent.getProvider()).orElse(null));
        assertEquals(inExpectedEvent.getRequestId(),
                     inActualEvent.getRequestId());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getSource()).orElse(null),
                     inActualEvent.getSource()==null?null:String.valueOf(inExpectedEvent.getSource()));
        assertEquals(BaseRpcUtil.getDateValue(inExpectedEvent.getTimestamp()).orElse(null),
                     inActualEvent.getTimestamp());
    }
    /**
     * Verify the given RPC event matches the given event.
     *
     * @param inExpectedEvent an <code>Event</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.Event</code> value
     */
    public static void verifyRpcEvent(Event inExpectedEvent,
                                      MarketDataTypesRpc.Event inActualEvent)
    {
        assertEquals(inExpectedEvent.getMessageId(),
                     inActualEvent.getMessageId());
        assertEquals(inExpectedEvent.getProvider(),
                     BaseRpcUtil.getStringValue(inActualEvent.getProvider()).orElse(null));
        assertEquals(inExpectedEvent.getRequestId(),
                     inActualEvent.getRequestId());
        assertEquals(inExpectedEvent.getSource()==null?null:String.valueOf(inExpectedEvent.getSource()),
                     BaseRpcUtil.getStringValue(inActualEvent.getSource()).orElse(null));
        assertEquals(inExpectedEvent.getTimestamp(),
                     BaseRpcUtil.getDateValue(inActualEvent.getTimestamp()).orElse(null));
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.MarketstatEvent</code> value
     * @param inActualEvent a <code>MarketstatEvent</code> value
     */
    public static void verifyMarketstatEvent(MarketDataTypesRpc.MarketstatEvent inExpectedEvent,
                                             MarketstatEvent inActualEvent)
    {
        verifyEvent(inExpectedEvent.getEvent(),
                    inActualEvent);
        verifyQuantity(inExpectedEvent.getClose(),
                       inActualEvent.getClose());
        verifyString(inExpectedEvent.getCloseDate(),
                     inActualEvent.getCloseDate());
        verifyString(inExpectedEvent.getCloseExchange(),
                     inActualEvent.getCloseExchange());
        assertEquals(MarketDataRpcUtil.getEventType(inExpectedEvent.getEventType()).orElse(null),
                     inActualEvent.getEventType());
        verifyQuantity(inExpectedEvent.getHigh(),
                       inActualEvent.getHigh());
        verifyString(inExpectedEvent.getHighExchange(),
                     inActualEvent.getHighExchange());
        assertEquals(TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null),
                     inActualEvent.getInstrument());
        verifyQuantity(inExpectedEvent.getLow(),
                       inActualEvent.getLow());
        verifyString(inExpectedEvent.getLowExchange(),
                     inActualEvent.getLowExchange());
        verifyQuantity(inExpectedEvent.getOpen(),
                       inActualEvent.getOpen());
        verifyString(inExpectedEvent.getOpenExchange(),
                     inActualEvent.getOpenExchange());
        verifyQuantity(inExpectedEvent.getPreviousClose(),
                       inActualEvent.getPreviousClose());
        verifyString(inExpectedEvent.getPreviousCloseDate(),
                     inActualEvent.getPreviousCloseDate());
        verifyString(inExpectedEvent.getTradeHighTime(),
                     inActualEvent.getTradeHighTime());
        verifyString(inExpectedEvent.getTradeLowTime(),
                     inActualEvent.getTradeLowTime());
        verifyQuantity(inExpectedEvent.getValue(),
                       inActualEvent.getValue());
        verifyQuantity(inExpectedEvent.getVolume(),
                       inActualEvent.getVolume());
        if(inExpectedEvent.hasOptionAttributes()) {
            verifyOptionAttributes(inExpectedEvent.getOptionAttributes(),
                                   inActualEvent);
        } else {
            verifyOptionAttributes(null,
                                   inActualEvent);
        }
    }
    /**
     * Verify the given expected string value matches the given actual string value.
     *
     * @param inExpectedValue a <code>String</code> value
     * @param inActualValue a <code>String</code> value
     */
    public static void verifyString(String inExpectedValue,
                                    String inActualValue)
    {
        assertEquals(StringUtils.trimToNull(inExpectedValue),
                     StringUtils.trimToNull(inActualValue));
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.ImbalanceEvent</code> value
     * @param inActualEvent an <code>ImbalanceEvent</code> value
     */
    public static void verifyImbalanceEvent(MarketDataTypesRpc.ImbalanceEvent inExpectedEvent,
                                            ImbalanceEvent inActualEvent)
    {
        verifyEvent(inExpectedEvent.getEvent(),
                    inActualEvent);
        assertEquals(MarketDataRpcUtil.getAuctionType(inExpectedEvent.getAuctionType()).orElse(null),
                     inActualEvent.getAuctionType());
        assertEquals(MarketDataRpcUtil.getEventType(inExpectedEvent.getEventType()).orElse(null),
                     inActualEvent.getEventType());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getExchange()).orElse(null),
                     inActualEvent.getExchange());
        verifyQuantity(inExpectedEvent.getFarPrice(),
                       inActualEvent.getFarPrice());
        assertEquals(MarketDataRpcUtil.getImbalanceType(inExpectedEvent.getImbalanceType()).orElse(null),
                     inActualEvent.getImbalanceType());
        verifyQuantity(inExpectedEvent.getImbalanceVolume(),
                       inActualEvent.getImbalanceVolume());
        assertEquals(TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null),
                     inActualEvent.getInstrument());
        assertEquals(MarketDataRpcUtil.getInstrumentStatus(inExpectedEvent.getInstrumentStatus()).orElse(null),
                     inActualEvent.getInstrumentStatus());
        assertEquals(inExpectedEvent.getIsShortSaleRestricted(),
                     inActualEvent.isShortSaleRestricted());
        assertEquals(MarketDataRpcUtil.getMarketStatus(inExpectedEvent.getMarketStatus()).orElse(null),
                     inActualEvent.getMarketStatus());
        verifyQuantity(inExpectedEvent.getNearPrice(),
                       inActualEvent.getNearPrice());
        verifyQuantity(inExpectedEvent.getPairedVolume(),
                       inActualEvent.getPairedVolume());
        verifyQuantity(inExpectedEvent.getReferencePrice(),
                       inActualEvent.getReferencePrice());
        if(inExpectedEvent.hasOptionAttributes()) {
            verifyOptionAttributes(inExpectedEvent.getOptionAttributes(),
                                   inActualEvent);
        } else {
            verifyOptionAttributes(null,
                                   inActualEvent);
        }
    }
    /**
     * Verify the given RPC option attributes match the option attributes on the given event, if any.
     *
     * @param inOptionAttributes a <code>MarketDataTypesRpc.OptionAttributes</code> value or <code>null</code>
     * @param inEvent an <code>Event</code> value
     */
    public static void verifyOptionAttributes(MarketDataTypesRpc.OptionAttributes inOptionAttributes,
                                              Event inEvent)
    {
        if(inOptionAttributes == null) {
            assertFalse(inEvent instanceof OptionEvent);
            return;
        }
        assertTrue("Expected: " + OptionEvent.class.getSimpleName() + " actual: " + inEvent.getClass().getSimpleName(),
                   inEvent instanceof OptionEvent);
        OptionEvent optionEvent = (OptionEvent)inEvent;
        assertEquals(MarketDataRpcUtil.getExpirationType(inOptionAttributes.getExpirationType()).orElse(null),
                     optionEvent.getExpirationType());
        verifyQuantity(inOptionAttributes.getMultiplier(),
                       optionEvent.getMultiplier());
        assertEquals(BaseRpcUtil.getStringValue(inOptionAttributes.getProviderSymbol()).orElse(null),
                     optionEvent.getProviderSymbol());
        assertEquals(TradeRpcUtil.getInstrument(inOptionAttributes.getUnderlyingInstrument()).orElse(null),
                     optionEvent.getUnderlyingInstrument());
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.DividendEvent</code> value
     * @param inActualEvent a <code>DividendEvent</code> value
     */
    public static void verifyDividendEvent(MarketDataTypesRpc.DividendEvent inExpectedEvent,
                                           DividendEvent inActualEvent)
    {
        verifyEvent(inExpectedEvent.getEvent(),
                    inActualEvent);
        verifyQuantity(inExpectedEvent.getAmount(),
                       inActualEvent.getAmount());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getCurrency()).orElse(null),
                     inActualEvent.getCurrency());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getDeclareDate()).orElse(null),
                     inActualEvent.getDeclareDate());
        assertEquals(MarketDataRpcUtil.getEventType(inExpectedEvent.getEventType()).orElse(null),
                     inActualEvent.getEventType());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getExecutionDate()).orElse(null),
                     inActualEvent.getExecutionDate());
        assertEquals(MarketDataRpcUtil.getDividendFrequency(inExpectedEvent.getFrequency()).orElse(null),
                     inActualEvent.getFrequency());
        assertEquals(TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null),
                     inActualEvent.getInstrument());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getPaymentDate()).orElse(null),
                     inActualEvent.getPaymentDate());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getRecordDate()).orElse(null),
                     inActualEvent.getRecordDate());
        assertEquals(MarketDataRpcUtil.getDividendStatus(inExpectedEvent.getStatus()).orElse(null),
                     inActualEvent.getStatus());
        assertEquals(MarketDataRpcUtil.getDividendType(inExpectedEvent.getType()).orElse(null),
                     inActualEvent.getType());
        if(inExpectedEvent.hasOptionAttributes()) {
            verifyOptionAttributes(inExpectedEvent.getOptionAttributes(),
                                   inActualEvent);
        } else {
            verifyOptionAttributes(null,
                                   inActualEvent);
        }
    }
    /**
     * Verify the given quantity matches the given expected RPC quantity.
     *
     * @param inExpectedQuantity a <code>BaseRpc.Qty</code> value
     * @param inActualQuantity a <code>BigDecimal</code> value
     */
    public static void verifyQuantity(BaseRpc.Qty inExpectedQuantity,
                                      BigDecimal inActualQuantity)
    {
        if(inExpectedQuantity == null) {
            assertNull(inActualQuantity);
            return;
        }
        assertNotNull(inActualQuantity);
        assertTrue("Expected: " + BaseRpcUtil.getScaledQuantity(inExpectedQuantity) + " actual: " + inActualQuantity,
                   BaseRpcUtil.getScaledQuantity(inExpectedQuantity).get().compareTo(inActualQuantity) == 0);
    }
    /**
     * Verify the given RPC quantity matches the given expected quantity.
     *
     * @param inExpectedQuantity a <code>BigDecimal</code> value
     * @param inActualQuantity a <code>BaseRpc.Qty</code> value
     */
    public static void verifyRpcQuantity(BigDecimal inExpectedQuantity,
                                         BaseRpc.Qty inActualQuantity)
    {
        if(inActualQuantity == null) {
            assertNull(inExpectedQuantity);
            return;
        }
        assertNotNull("Expected " + inExpectedQuantity + " to be non-null because " + inActualQuantity + " was non-null",
                      inExpectedQuantity);
        assertTrue("Expected: " + inExpectedQuantity + " actual: " + BaseRpcUtil.getScaledQuantity(inActualQuantity),
                   BaseRpcUtil.getScaledQuantity(inActualQuantity).get().compareTo(inExpectedQuantity) == 0);
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.LogEvent</code> value
     * @param inActualEvent a <code>LogEvent</code> value
     */
    public static void verifyLogEvent(MarketDataTypesRpc.LogEvent inExpectedEvent,
                                      LogEvent inActualEvent)
    {
        verifyEvent(inExpectedEvent.getEvent(),
                    inActualEvent);
        if(inExpectedEvent.hasException()) {
            assertEquals(((Throwable)BaseRpcUtil.getObject(inExpectedEvent.getException()).orElse(null)).getMessage(),
                         inActualEvent.getException().getMessage());
        } else {
            assertNull(inActualEvent.getException());
        }
        assertEquals(MarketDataRpcUtil.getLogEventLevel(inExpectedEvent.getLogEventLevel()).orElse(null),
                     inActualEvent.getLevel());
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getMessage()).orElse(null),
                     inActualEvent.getMessage());
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.QuoteEvent</code> value
     * @param inActualEvent a <code>QuoteEvent</code> value
     */
    public static void verifyQuoteEvent(MarketDataTypesRpc.QuoteEvent inExpectedEvent,
                                        QuoteEvent inActualEvent)
    {
        verifyMarketDataEvent(inExpectedEvent.getMarketDataEvent(),
                              inActualEvent);
        assertEquals(MarketDataRpcUtil.getQuoteAction(inExpectedEvent.getQuoteAction()).orElse(null),
                     inActualEvent.getAction());
        assertEquals(inExpectedEvent.getCount(),
                     inActualEvent.getCount());
        assertEquals(inExpectedEvent.getLevel(),
                     inActualEvent.getLevel());
        assertEquals(BaseRpcUtil.getDateValue(inExpectedEvent.getQuoteDate()).orElse(null),
                     inActualEvent.getQuoteDate());
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.TopOfBookEvent</code> value
     * @param inActualEvent a <code>TopOfBookEvent</code> value
     */
    public static void verifyTopOfBookEvent(MarketDataTypesRpc.TopOfBookEvent inExpectedEvent,
                                            TopOfBookEvent inActualEvent)
    {
        if(inExpectedEvent.hasAsk()) {
            verifyQuoteEvent(inExpectedEvent.getAsk(),
                             inActualEvent.getAsk());
        } else {
            assertNull(inActualEvent.getAsk());
        }
        if(inExpectedEvent.hasBid()) {
            verifyQuoteEvent(inExpectedEvent.getBid(),
                             inActualEvent.getBid());
        } else {
            assertNull(inActualEvent.getBid());
        }
        assertEquals(inActualEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null));
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.DepthOfBookEvent</code> value
     * @param inActualEvent a <code>DepthOfBookEvent</code> value
     */
    public static void verifyDepthOfBookEvent(MarketDataTypesRpc.DepthOfBookEvent inExpectedEvent,
                                              DepthOfBookEvent inActualEvent)
    {
        assertEquals(inExpectedEvent.getAsksCount(),
                     inActualEvent.getAsks().size());
        final Iterator<MarketDataTypesRpc.QuoteEvent> askIterator = inExpectedEvent.getAsksList().iterator();
        inActualEvent.getAsks().stream().forEach(ask->verifyQuoteEvent(askIterator.next(),
                                                                       ask));
        assertEquals(inActualEvent.getBids().size(),
                     inExpectedEvent.getBidsCount());
        final Iterator<MarketDataTypesRpc.QuoteEvent> bidIterator = inExpectedEvent.getBidsList().iterator();
        inActualEvent.getBids().stream().forEach(bid->verifyQuoteEvent(bidIterator.next(),
                                                                       bid));
        assertEquals(inActualEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inExpectedEvent.getInstrument()).orElse(null));
    }
    /**
     * Verify the given event matches the given expected RPC event.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.TradeEvent</code> value
     * @param inActualEvent a <code>TradeEvent</code> value
     */
    public static void verifyTradeEvent(MarketDataTypesRpc.TradeEvent inExpectedEvent,
                                        TradeEvent inActualEvent)
    {
        verifyMarketDataEvent(inExpectedEvent.getMarketDataEvent(),
                              inActualEvent);
        assertEquals(BaseRpcUtil.getStringValue(inExpectedEvent.getTradeCondition()).orElse(null),
                     inActualEvent.getTradeCondition());
        assertEquals(BaseRpcUtil.getDateValue(inExpectedEvent.getTradeDate()).orElse(null),
                     inActualEvent.getTradeDate());
    }
    /**
     * Verify that the given RPC trade event matches the given trade event.
     *
     * @param inExpectedEvent a <code>TradeEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.TradeEvent</code> value
     */
    public static void verifyRpcTradeEvent(TradeEvent inExpectedEvent,
                                           MarketDataTypesRpc.TradeEvent inActualEvent)
    {
        verifyRpcMarketDataEvent(inExpectedEvent,
                                 inActualEvent.getMarketDataEvent());
        assertEquals(inExpectedEvent.getTradeCondition(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeCondition()).orElse(null));
        assertEquals(inExpectedEvent.getTradeDate(),
                     BaseRpcUtil.getDateValue(inActualEvent.getTradeDate()).orElse(null));
    }
    /**
     * Get the instruments for test parameters.
     *
     * @return an <code>Object</code> value
     */
    protected Object instrumentParameters()
    {
        List<Object> results = Lists.newArrayList();
        for(Instrument instrument : getInstrumentList()) {
            results.add(new Object[] { instrument });
        }
        return results.toArray();
    }
    /**
     * Get list of instruments to use for testing.
     *
     * @return a <code>List&lt;Instrument&gt;</code> value
     */
    private List<Instrument> getInstrumentList()
    {
        List<Instrument> instruments = Lists.newArrayList();
        instruments.add(new Equity("METC"));
        instruments.add(org.marketcetera.trade.Future.fromString("METC-201811"));
        instruments.add(new Currency("USD/GBP"));
        instruments.add(new Option("METC","20181117",EventTestBase.generateDecimalValue(),OptionType.Put));
        instruments.add(new ConvertibleBond("FR0011453463"));
        return instruments;
    }
}
