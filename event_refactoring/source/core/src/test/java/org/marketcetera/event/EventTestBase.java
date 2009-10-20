package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.event.impl.DepthOfBookEventBuilder;
import org.marketcetera.event.impl.EventValidationException;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TopOfBookEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventTestBase
{
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inExchangeTimestamp
     * @return
     * @throws Exception
     */
    public static AskEvent generateEquityAskEvent(long inMessageId,
                                                          Date inTimestamp,
                                                          Equity inInstrument,
                                                          String inExchange,
                                                          BigDecimal inPrice,
                                                          BigDecimal inSize,
                                                          String inExchangeTimestamp)
          throws Exception
    {
        return QuoteEventBuilder.newEquityAskEvent().withMessageId(inMessageId).withTimestamp(inTimestamp).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).withQuoteDate(inExchangeTimestamp).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static AskEvent generateEquityAskEvent(long inMessageId,
                                                          long inTimestamp,
                                                          Equity inInstrument,
                                                          String inExchange,
                                                          BigDecimal inPrice,
                                                          BigDecimal inSize)
          throws Exception
    {
        return QuoteEventBuilder.newEquityAskEvent().withMessageId(inMessageId).withTimestamp(new Date(inTimestamp)).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).withQuoteDate(DateUtils.dateToString(new Date(inTimestamp))).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                          String inExchange)
          throws Exception
    {
        return QuoteEventBuilder.newEquityAskEvent().withMessageId(counter.incrementAndGet()).withTimestamp(new Date()).withInstrument(inInstrument)
            .withExchange(inExchange).withPrice(generateDecimalValue()).withSize(generateDecimalValue()).withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice)
          throws Exception
    {
        return QuoteEventBuilder.newEquityAskEvent().withMessageId(counter.incrementAndGet()).withTimestamp(new Date()).withInstrument(inInstrument)
            .withExchange(inExchange).withPrice(inPrice).withSize(generateDecimalValue()).withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inExchangeTimestamp
     * @return
     * @throws Exception
     */
    public static BidEvent generateEquityBidEvent(long inMessageId,
                                                  Date inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize,
                                                  String inExchangeTimestamp)
          throws Exception
    {
        return QuoteEventBuilder.newEquityBidEvent().withMessageId(inMessageId).withTimestamp(inTimestamp).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).withQuoteDate(inExchangeTimestamp).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static BidEvent generateEquityBidEvent(long inMessageId,
                                                          long inTimestamp,
                                                          Equity inInstrument,
                                                          String inExchange,
                                                          BigDecimal inPrice,
                                                          BigDecimal inSize)
          throws Exception
    {
        return QuoteEventBuilder.newEquityBidEvent().withMessageId(inMessageId).withTimestamp(new Date(inTimestamp)).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).withQuoteDate(DateUtils.dateToString(new Date(inTimestamp))).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                  String inExchange)
          throws Exception
    {
        return QuoteEventBuilder.newEquityBidEvent().withMessageId(counter.incrementAndGet()).withTimestamp(new Date()).withInstrument(inInstrument)
            .withExchange(inExchange).withPrice(generateDecimalValue()).withSize(generateDecimalValue()).withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                          String inExchange,
                                                          BigDecimal inPrice)
          throws Exception
    {
        return QuoteEventBuilder.newEquityBidEvent().withMessageId(counter.incrementAndGet()).withTimestamp(new Date()).withInstrument(inInstrument)
            .withExchange(inExchange).withPrice(inPrice).withSize(generateDecimalValue()).withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates the given number of <code>AskEvent</code> objects with the given attributes.
     * 
     * <p>Unspecified attributes are randomized except for the date, which is set to the
     * current time.  The timestamp is not guaranteed to be unique or consistent for
     * all the events in the list.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @param inCount an <code>int</code> value
     * @return a <code>List&lt;AskEvent&gt;</code> value
     * @throws Exception 
     */
    public static List<AskEvent> generateEquityAskEvents(Equity inInstrument,
                                                           String inExchange,
                                                           int inCount)
           throws Exception
    {
        List<AskEvent> asks = new ArrayList<AskEvent>();
        for(int i=0;i<inCount;i++) {
            asks.add(generateEquityAskEvent(inInstrument,
                                            inExchange));
        }
        return asks;
    }
    /**
     * Generates the given number of <code>BidEvent</code> objects with the given attributes.
     * 
     * <p>Unspecified attributes are randomized except for the date, which is set to the
     * current time.  The timestamp is not guaranteed to be unique or consistent for
     * all the events in the list. 
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @param inCount an <code>int</code> value
     * @return a <code>List&lt;BidEvent&gt;</code> value
     * @throws Exception 
     */
    public static List<BidEvent> generateEquityBidEvents(Equity inInstrument,
                                                           String inExchange,
                                                           int inCount)
            throws Exception
    {
        List<BidEvent> bids = new ArrayList<BidEvent>();
        for(int i=0;i<inCount;i++) {
            bids.add(generateEquityBidEvent(inInstrument,
                                            inExchange));
        }
        return bids;
    }
   /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inExchangeTimestamp
     * @return
     * @throws Exception
     */
    public static TradeEvent generateEquityTradeEvent(long inMessageId,
                                                              Date inTimestamp,
                                                              Equity inInstrument,
                                                              String inExchange,
                                                              BigDecimal inPrice,
                                                              BigDecimal inSize,
                                                              String inExchangeTimestamp)
              throws Exception
    {
        return TradeEventBuilder.newEquityTradeEvent().withMessageId(inMessageId).withTimestamp(inTimestamp).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).atQuoteDate(inExchangeTimestamp).create();
    }
    /**
     * 
     *
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @return
     * @throws Exception
     */
    public static TradeEvent generateEquityTradeEvent(long inMessageId,
                                                              long inTimestamp,
                                                              Equity inInstrument,
                                                              String inExchange,
                                                              BigDecimal inPrice,
                                                              BigDecimal inSize)
              throws Exception
    {
        return TradeEventBuilder.newEquityTradeEvent().withMessageId(inMessageId).withTimestamp(new Date(inTimestamp)).withInstrument(inInstrument).withExchange(inExchange)
            .withPrice(inPrice).withSize(inSize).atQuoteDate(DateUtils.dateToString(new Date(inTimestamp))).create();
    }
    /**
     * 
     *
     *
     * @param inInstrument
     * @param inTimestamp
     * @param inOpenPrice
     * @param inHighPrice
     * @param inLowPrice
     * @param inClosePrice
     * @param inPreviousClosePrice
     * @param inVolume
     * @param inCloseDate
     * @param inPreviousCloseDate
     * @param inTradeHighTime
     * @param inTradeLowTime
     * @param inOpenExchange
     * @param inHighExchange
     * @param inLowExchange
     * @param inCloseExchange
     * @return
     * @throws EventValidationException
     */
    public static MarketstatEvent generateEquityMarketstatEvent(Equity inInstrument,
                                                                        Date inTimestamp,
                                                                        BigDecimal inOpenPrice,
                                                                        BigDecimal inHighPrice,
                                                                        BigDecimal inLowPrice,
                                                                        BigDecimal inClosePrice,
                                                                        BigDecimal inPreviousClosePrice,
                                                                        BigDecimal inVolume,
                                                                        Date inCloseDate,
                                                                        Date inPreviousCloseDate,
                                                                        Date inTradeHighTime,
                                                                        Date inTradeLowTime,
                                                                        String inOpenExchange,
                                                                        String inHighExchange,
                                                                        String inLowExchange,
                                                                        String inCloseExchange)
        throws EventValidationException
    {
        return MarketstatEventBuilder.newEquityMarketstatEvent().withInstrument(inInstrument)
            .withTimestamp(inTimestamp).withOpenPrice(inOpenPrice).withHighPrice(inHighPrice)
            .withLowPrice(inLowPrice).withClosePrice(inClosePrice).withPreviousClosePrice(inPreviousClosePrice)
            .withVolume(inVolume).withCloseDate(DateUtils.dateToString(inCloseDate))
            .withPreviousCloseDate(DateUtils.dateToString(inPreviousCloseDate))
            .withTradeHighTime(DateUtils.dateToString(inTradeHighTime))
            .withTradeLowTime(DateUtils.dateToString(inTradeLowTime)).withOpenExchange(inOpenExchange)
            .withHighExchange(inHighExchange).withLowExchange(inLowExchange).withCloseExchange(inCloseExchange)
            .create();
    }
    /**
     * 
     *
     *
     * @param inBid
     * @param inAsk
     * @param inTimestamp
     * @param inInstrument
     * @return
     * @throws Exception
     */
    public static TopOfBookEvent generateEquityTopOfBookEvent(BidEvent inBid,
                                                                 AskEvent inAsk,
                                                                 Date inTimestamp)
            throws Exception
    {
        return TopOfBookEventBuilder.newTopOfBook().withBid(inBid).withAsk(inAsk).withTimestamp(inTimestamp).withMessageId(System.nanoTime()).create();
    }
    /**
     * 
     *
     *
     * @param inBids
     * @param inAsks
     * @param inTimestamp
     * @param inInstrument
     * @return
     * @throws EventValidationException
     */
    public static DepthOfBookEvent generateEquityDepthOfBookEvent(List<BidEvent> inBids,
                                                                  List<AskEvent> inAsks,
                                                                  Date inTimestamp,
                                                                  Equity inInstrument)
        throws EventValidationException
    {
        return DepthOfBookEventBuilder.equityDepthOfBook().withMessageId(counter.incrementAndGet()).withTimestamp(inTimestamp)
            .withBids(inBids).withAsks(inAsks).create();
    }
    /**
     * Create a new EventTestBase instance.
     *
     */
    private EventTestBase()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    private static BigDecimal generateDecimalValue()
    {
        return new BigDecimal(String.format("%d.%d",
                                            random.nextInt(10000),
                                            random.nextInt(100)));
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    private static BigDecimal generateIntegerValue()
    {
        return new BigDecimal(random.nextInt(10000));
    }
    private static final Random random = new Random(System.nanoTime());
    private static final AtomicLong counter = new AtomicLong(0);
}
