package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
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
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trade.Equity;
import org.marketcetera.trading.rpc.TradeRpcUtil;

/* $License$ */

/**
 * Tests {@link MarketDataRpcUtil}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcUtilTest
{
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link TradeEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcTradeEvent()
            throws Exception
    {
        Event event = EventTestBase.generateTradeEvent(new Equity("METC"));
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
    public void testGetRpcQuoteEvent()
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
//        Event newEvent = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
//        assertNotNull(newEvent);
//        verifyEvent(rpcEvent,
//                    newEvent);
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
    public void testGetRpcImbalanceEvent()
            throws Exception
    {
        Event event = EventTestBase.generateImbalanceEvent();
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link MarketstatEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcMarketstatEvent()
            throws Exception
    {
        Event event = EventTestBase.generateMarketstatEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link DepthOfBookEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetDepthOfBookEvent()
            throws Exception
    {
        Event event = EventTestBase.generateDepthOfBookEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link TopOfBookEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetTopOfBookEvent()
            throws Exception
    {
        Event event = EventTestBase.generateTopOfBookEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getMarketDataRequest(String, String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetMarketDataRequest()
            throws Exception
    {
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        requestBuilder.withContent(Content.TOP_OF_BOOK);
        requestBuilder.withSymbols("METC");
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
        assertTrue("Expected: " + inExpectedEvent.getClose() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getClose()),
                   inExpectedEvent.getClose().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getClose()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getCloseExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseExchange()).orElse(null));
        verifyRpcEvent(inExpectedEvent,
                       inActualEvent.getEvent());
        assertEquals(inExpectedEvent.getEventType(),
                     MarketDataRpcUtil.getEventType(inActualEvent.getEventType()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getHigh() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getHigh()),
                   inExpectedEvent.getHigh().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getHigh()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getHighExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getHighExchange()).orElse(null));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getLow() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getLow()),
                   inExpectedEvent.getLow().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getLow()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getLowExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getLowExchange()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getOpen() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getOpen()),
                   inExpectedEvent.getOpen().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getOpen()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getOpenExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getOpenExchange()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getPreviousClose() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getPreviousClose()),
                   inExpectedEvent.getPreviousClose().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPreviousClose()).orElse(null)) == 0);
        assertEquals(inExpectedEvent.getPreviousCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getPreviousCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getTradeHighTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeHighTime()).orElse(null));
        assertEquals(inExpectedEvent.getTradeLowTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeLowTime()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getValue() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getValue()),
                   inExpectedEvent.getValue().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getValue()).orElse(null)) == 0);
        assertTrue("Expected: " + inExpectedEvent.getVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getVolume()),
                   inExpectedEvent.getVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getVolume()).orElse(null)) == 0);
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
        assertTrue("Expected: " + inExpectedEvent.getPrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getPrice()),
                   inExpectedEvent.getPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPrice()).orElse(null)) == 0);
        if(inActualEvent.hasEventTimestamps()) {
            verifyRpcTimestamps(inExpectedEvent,
                                inActualEvent.getEventTimestamps());
        } else {
            verifyRpcTimestamps(inExpectedEvent,
                                null);
        }
        assertTrue("Expected: " + inExpectedEvent.getSize() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getSize()),
                   inExpectedEvent.getSize().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getSize()).orElse(null)) == 0);
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
}
