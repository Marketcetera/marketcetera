package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.ImbalanceEvent;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteEvent;
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
        Event event = EventTestBase.generateAskEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
        event = EventTestBase.generateBidEvent(new Equity("METC"));
        rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
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
    // aggregate event, top-of-book event, depth-of-book event?
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
        } else {
            throw new UnsupportedOperationException(inActualEvent.toString());
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
                   inExpectedEvent.getFarPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getFarPrice())) == 0);
        assertEquals(inExpectedEvent.getImbalanceType(),
                     MarketDataRpcUtil.getImbalanceType(inActualEvent.getImbalanceType()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getImbalanceVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getImbalanceVolume()),
                   inExpectedEvent.getImbalanceVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getImbalanceVolume())) == 0);
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        assertEquals(inExpectedEvent.isShortSaleRestricted(),
                     inActualEvent.getIsShortSaleRestricted());
        assertEquals(inExpectedEvent.getMarketStatus(),
                     MarketDataRpcUtil.getMarketStatus(inActualEvent.getMarketStatus()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getNearPrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getNearPrice()),
                   inExpectedEvent.getNearPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getNearPrice())) == 0);
        assertTrue("Expected: " + inExpectedEvent.getPairedVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getPairedVolume()),
                   inExpectedEvent.getPairedVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPairedVolume())) == 0);
        assertTrue("Expected: " + inExpectedEvent.getReferencePrice() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getReferencePrice()),
                   inExpectedEvent.getReferencePrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getReferencePrice())) == 0);
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
                   inExpectedEvent.getAmount().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getAmount())) == 0);
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
                   inExpectedEvent.getClose().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getClose())) == 0);
        assertEquals(inExpectedEvent.getCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getCloseExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getCloseExchange()).orElse(null));
        verifyRpcEvent(inExpectedEvent,
                       inActualEvent.getEvent());
        assertEquals(inExpectedEvent.getEventType(),
                     MarketDataRpcUtil.getEventType(inActualEvent.getEventType()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getHigh() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getHigh()),
                   inExpectedEvent.getHigh().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getHigh())) == 0);
        assertEquals(inExpectedEvent.getHighExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getHighExchange()).orElse(null));
        assertEquals(inExpectedEvent.getInstrument(),
                     TradeRpcUtil.getInstrument(inActualEvent.getInstrument()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getLow() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getLow()),
                   inExpectedEvent.getLow().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getLow())) == 0);
        assertEquals(inExpectedEvent.getLowExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getLowExchange()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getOpen() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getOpen()),
                   inExpectedEvent.getOpen().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getOpen())) == 0);
        assertEquals(inExpectedEvent.getOpenExchange(),
                     BaseRpcUtil.getStringValue(inActualEvent.getOpenExchange()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getPreviousClose() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getPreviousClose()),
                   inExpectedEvent.getPreviousClose().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPreviousClose())) == 0);
        assertEquals(inExpectedEvent.getPreviousCloseDate(),
                     BaseRpcUtil.getStringValue(inActualEvent.getPreviousCloseDate()).orElse(null));
        assertEquals(inExpectedEvent.getTradeHighTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeHighTime()).orElse(null));
        assertEquals(inExpectedEvent.getTradeLowTime(),
                     BaseRpcUtil.getStringValue(inActualEvent.getTradeLowTime()).orElse(null));
        assertTrue("Expected: " + inExpectedEvent.getValue() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getValue()),
                   inExpectedEvent.getValue().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getValue())) == 0);
        assertTrue("Expected: " + inExpectedEvent.getVolume() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getVolume()),
                   inExpectedEvent.getVolume().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getVolume())) == 0);
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
                   inExpectedEvent.getPrice().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getPrice())) == 0);
        assertEquals(inExpectedEvent.getProcessedTimestamp(),
                     inActualEvent.getProcessedTimestamp());
        assertEquals(inExpectedEvent.getReceivedTimestamp(),
                     inActualEvent.getReceivedTimestamp());
        assertTrue("Expected: " + inExpectedEvent.getSize() + " actual: " + BaseRpcUtil.getScaledQuantity(inActualEvent.getSize()),
                   inExpectedEvent.getSize().compareTo(BaseRpcUtil.getScaledQuantity(inActualEvent.getSize())) == 0);
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
