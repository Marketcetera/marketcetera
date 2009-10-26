package org.marketcetera.event;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.SecurityType;

/* $License$ */

/**
 * Provides services for event-based unit tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventTestBase
{
    /**
     * Generates an equity <code>AskEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>String</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(long inMessageId,
                                                  Date inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize,
                                                  String inExchangeTimestamp)
    {
        return QuoteEventBuilder.equityAskEvent().withMessageId(inMessageId)
                                                 .withTimestamp(inTimestamp)
                                                 .withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(inPrice)
                                                 .withSize(inSize)
                                                 .withQuoteDate(inExchangeTimestamp).create();
    }
    /**
     * Generates an equity <code>AskEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(long inMessageId,
                                                  long inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize)
    {
        return generateEquityAskEvent(inMessageId,
                                      new Date(inTimestamp),
                                      inInstrument,
                                      inExchange,
                                      inPrice,
                                      inSize,
                                      DateUtils.dateToString(new Date(inTimestamp)));
    }
    /**
     * Generates an equity <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                  String inExchange)
    {
        return QuoteEventBuilder.equityAskEvent().withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates an equity <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.equityAskEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates an equity <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.equityAskEvent().withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(inPrice)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates an option <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateOptionAskEvent(Option inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.optionAskEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withMultiplier(0)
                                                 .withUnderlyingInstrument(inInstrument).create();
    }
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>String</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(long inMessageId,
                                                  Date inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize,
                                                  String inExchangeTimestamp)
    {
        return QuoteEventBuilder.equityBidEvent().withMessageId(inMessageId)
                                                 .withTimestamp(inTimestamp)
                                                 .withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(inPrice)
                                                 .withSize(inSize)
                                                 .withQuoteDate(inExchangeTimestamp).create();
    }
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(long inMessageId,
                                                  long inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize)
    {
        return generateEquityBidEvent(inMessageId,
                                      new Date(inTimestamp),
                                      inInstrument,
                                      inExchange,
                                      inPrice,
                                      inSize,
                                      DateUtils.dateToString(new Date()));
    }
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                  String inExchange)
    {
        return generateEquityBidEvent(inInstrument,
                                      inExchange,
                                      generateDecimalValue());
    }
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.equityBidEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.equityBidEvent().withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(inPrice)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates an option <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateOptionBidEvent(Option inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.optionBidEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withMultiplier(0)
                                                 .withUnderlyingInstrument(inInstrument).create();
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
     */
    public static List<AskEvent> generateEquityAskEvents(Equity inInstrument,
                                                         String inExchange,
                                                         int inCount)
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
     */
    public static List<BidEvent> generateEquityBidEvents(Equity inInstrument,
                                                         String inExchange,
                                                         int inCount)
    {
        List<BidEvent> bids = new ArrayList<BidEvent>();
        for(int i=0;i<inCount;i++) {
            bids.add(generateEquityBidEvent(inInstrument,
                                            inExchange));
        }
        return bids;
    }
    /**
     * Generates an equity <code>TradeEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>String</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateEquityTradeEvent(long inMessageId,
                                                      Date inTimestamp,
                                                      Equity inInstrument,
                                                      String inExchange,
                                                      BigDecimal inPrice,
                                                      BigDecimal inSize,
                                                      String inExchangeTimestamp)
    {
        return TradeEventBuilder.equityTradeEvent().withMessageId(inMessageId)
                                                   .withTimestamp(inTimestamp)
                                                   .withInstrument(inInstrument)
                                                   .withExchange(inExchange)
                                                   .withPrice(inPrice)
                                                   .withSize(inSize)
                                                   .withTradeDate(inExchangeTimestamp).create();
    }
    /**
     * Generates an equity <code>TradeEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateEquityTradeEvent(long inMessageId,
                                                      long inTimestamp,
                                                      Equity inInstrument,
                                                      String inExchange,
                                                      BigDecimal inPrice,
                                                      BigDecimal inSize)
    {
        return generateEquityTradeEvent(inMessageId,
                                        new Date(inTimestamp),
                                        inInstrument,
                                        inExchange,
                                        inPrice,
                                        inSize,
                                        DateUtils.dateToString(new Date(inTimestamp)));
    }
    /**
     * Generates an equity <code>MarketstatEvent</code> value. 
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inOpenPrice a <code>BigDecimal</code> value
     * @param inHighPrice a <code>BigDecimal</code> value
     * @param inLowPrice a <code>BigDecimal</code> value
     * @param inClosePrice a <code>BigDecimal</code> value
     * @param inPreviousClosePrice a <code>BigDecimal</code> value
     * @param inVolume a <code>BigDecimal</code> value
     * @param inCloseDate a <code>String</code> value
     * @param inPreviousCloseDate a <code>String</code> value
     * @param inTradeHighTime a <code>String</code> value
     * @param inTradeLowTime a <code>String</code> value
     * @param inOpenExchange a <code>String</code> value
     * @param inHighExchange a <code>String</code> value
     * @param inLowExchange a <code>String</code> value
     * @param inCloseExchange a <code>String</code> value
     * @return a <code>MarketstatEvent</code> value
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
    {
        return MarketstatEventBuilder.equityMarketstat().withInstrument(inInstrument)
                                                        .withTimestamp(inTimestamp)
                                                        .withOpenPrice(inOpenPrice)
                                                        .withHighPrice(inHighPrice)
                                                        .withLowPrice(inLowPrice)
                                                        .withClosePrice(inClosePrice)
                                                        .withPreviousClosePrice(inPreviousClosePrice)
                                                        .withVolume(inVolume)
                                                        .withCloseDate(DateUtils.dateToString(inCloseDate))
                                                        .withPreviousCloseDate(DateUtils.dateToString(inPreviousCloseDate))
                                                        .withTradeHighTime(DateUtils.dateToString(inTradeHighTime))
                                                        .withTradeLowTime(DateUtils.dateToString(inTradeLowTime)).withOpenExchange(inOpenExchange)
                                                        .withHighExchange(inHighExchange).withLowExchange(inLowExchange).withCloseExchange(inCloseExchange).create();
    }
    /**
     * Generates an <code>Instrument</code> guaranteed to be of a
     * previously undiscovered type.
     *
     * @return an <code>Instrument</code> value
     */
    public static Instrument generateUnsupportedInstrument()
    {
        Instrument unsupportedInstrument = new Instrument() {
            private static final long serialVersionUID = 1L;
            private final Object object = new Object();
               @Override
               public boolean equals(Object inObj)
               {
                   return object.equals(inObj);
               }
               @Override
               public SecurityType getSecurityType()
               {
                   return SecurityType.Unknown;
               }
               @Override
               public String getSymbol()
               {
                   return object.toString();
               }
               @Override
               public int hashCode()
               {
                   return object.hashCode();
               }
           };
           assertFalse(unsupportedInstrument instanceof Equity);
           assertFalse(unsupportedInstrument instanceof Option);
           return unsupportedInstrument;
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
     * generates random values - used for test data
     */
    private static final Random random = new Random(System.nanoTime());
}
