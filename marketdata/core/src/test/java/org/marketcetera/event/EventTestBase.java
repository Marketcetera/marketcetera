package org.marketcetera.event;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.marketcetera.event.impl.DividendEventBuilder;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.SecurityType;

/* $License$ */

/**
 * Provides services for event-based unit tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EventTestBase
{
    /**
     * Generates an <code>AskEvent</code> for the given <code>Equity</code>.
     *
     * @param inInstrument an <code>Equity</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument)
    {
        return QuoteEventBuilder.equityAskEvent().withInstrument(inInstrument)
                                                 .withExchange(generateExchange())
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(generateQuoteDate()).create();
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
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(Equity inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.equityAskEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
                                                 .withPrice(inPrice)
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
                                                 .withMultiplier(BigDecimal.ZERO)
                                                 .withUnderlyingInstrument(inInstrument).create();
    }
    /**
     * Generates an option <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateOptionAskEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument)
    {
        return QuoteEventBuilder.optionAskEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
    }
    /**
     * Generates an option <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateOptionAskEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument,
                                                  String inExchange)
    {
        return QuoteEventBuilder.optionAskEvent().withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
    }
    /**
     * Generates an option <code>AskEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateOptionAskEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.optionAskEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
                                                 .withPrice(inPrice)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
    }
    
    /**
     * Generates a currency <code>BidEvent</code> with the given values.
     *
     * @param inInstrument a <code>Currency</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateCurrencyBidEvent(Currency inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.currencyBidEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("TEST")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withContractSize(1)
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a currency <code>BidEvent</code> with the given values.
     *
     * @param inInstrument a <code>Currency</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateCurrencyBidEvent(Currency inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.currencyBidEvent().withInstrument(inInstrument)
                                                 .withAction(QuoteAction.ADD)
                                                 .withExchange("TEST")
                                                 .withPrice(inPrice)
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a currency <code>AskEvent</code> with the given values.
     *
     * @param inInstrument a <code>Currency</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateCurrencyAskEvent(Currency inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.currencyAskEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("TEST")
                                                 .withPrice(generateDecimalValue())
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a currency <code>AskEvent</code> with the given values.
     *
     * @param inInstrument a <code>Currency</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateCurrencyAskEvent(Currency inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.currencyAskEvent().withInstrument(inInstrument)
                                                 .withAction(QuoteAction.ADD)
                                                 .withExchange("TEST")
                                                 .withPrice(inPrice)
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    
    /**
     * Generates a future <code>BidEvent</code> with the given values.
     *
     * @param inInstrument a <code>future</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateFutureBidEvent(Future inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.futureBidEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withContractSize(1)
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a future <code>BidEvent</code> with the given values.
     *
     * @param inInstrument a <code>Future</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateFutureBidEvent(Future inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.futureBidEvent().withInstrument(inInstrument)
                                                 .withAction(QuoteAction.ADD)
                                                 .withExchange("exchange")
                                                 .withPrice(inPrice)
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a future <code>AskEvent</code> with the given values.
     *
     * @param inInstrument a <code>Future</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateFutureAskEvent(Future inInstrument,
                                                  QuoteAction inAction)
    {
        return QuoteEventBuilder.futureAskEvent().withInstrument(inInstrument)
                                                 .withAction(inAction)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a future <code>AskEvent</code> with the given values.
     *
     * @param inInstrument a <code>Future</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateFutureAskEvent(Future inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.futureAskEvent().withInstrument(inInstrument)
                                                 .withAction(QuoteAction.ADD)
                                                 .withExchange("exchange")
                                                 .withPrice(inPrice)
                                                 .withContractSize(1)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date())).create();
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
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(Equity inInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.equityBidEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
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
                                                 .withMultiplier(BigDecimal.ZERO)
                                                 .withUnderlyingInstrument(inInstrument).create();
    }
    /**
     * Generates an option <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateOptionBidEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument)
    {
        return QuoteEventBuilder.optionBidEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
    }
    /**
     * Generates an option <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateOptionBidEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument,
                                                  String inExchange)
    {
        return QuoteEventBuilder.optionBidEvent().withInstrument(inInstrument)
                                                 .withExchange(inExchange)
                                                 .withPrice(generateDecimalValue())
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
    }
    /**
     * Generates an option <code>BidEvent</code> with the given values.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateOptionBidEvent(Option inInstrument,
                                                  Instrument inUnderlyingInstrument,
                                                  BigDecimal inPrice)
    {
        return QuoteEventBuilder.optionBidEvent().withInstrument(inInstrument)
                                                 .withExchange("exchange")
                                                 .withPrice(inPrice)
                                                 .withSize(generateDecimalValue())
                                                 .withQuoteDate(DateUtils.dateToString(new Date()))
                                                 .withExpirationType(ExpirationType.AMERICAN)
                                                 .withUnderlyingInstrument(inUnderlyingInstrument).create();
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
     * Generates an equity <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument an <code>Equity</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateEquityTradeEvent(Equity inInstrument)
    {
        return generateEquityTradeEvent(System.nanoTime(),
                                        System.currentTimeMillis(),
                                        inInstrument,
                                        "Q",
                                        generateDecimalValue(),
                                        generateDecimalValue());
    }
    /**
     * Generates an equity <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateEquityTradeEvent(Equity inInstrument,
                                                      BigDecimal inPrice)
    {
        return generateEquityTradeEvent(System.nanoTime(),
                                        System.currentTimeMillis(),
                                        inInstrument,
                                        "Q",
                                        inPrice,
                                        generateDecimalValue());
    }
    /**
     * Generates an option <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateOptionTradeEvent(Option inInstrument,
                                                      Instrument inUnderlyingInstrument)
    {
        return TradeEventBuilder.optionTradeEvent().withInstrument(inInstrument)
                                                   .withExchange("Q")
                                                   .withPrice(generateDecimalValue())
                                                   .withSize(generateDecimalValue())
                                                   .withExpirationType(ExpirationType.AMERICAN)
                                                   .withMultiplier(BigDecimal.ZERO)
                                                   .withUnderlyingInstrument(inUnderlyingInstrument)
                                                   .withTradeDate(DateUtils.dateToString(new Date())).create();
    }
    /**
     * Generates a future <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument a <code>Future</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateFutureTradeEvent(Future inInstrument,
                                                      BigDecimal inPrice)
    {
        return TradeEventBuilder.futureTradeEvent().withInstrument(inInstrument)
                                                   .withExchange("Q")
                                                   .withPrice(inPrice)
                                                   .withContractSize(1)
                                                   .withSize(generateDecimalValue())
                                                   .withTradeDate(DateUtils.dateToString(new Date())).create();
    }
    
    /**
     * Generates a currency <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument a <code>Currency</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateCurrencyTradeEvent(Currency inInstrument,
                                                      BigDecimal inPrice)
    {
        return TradeEventBuilder.currencyTradeEvent().withInstrument(inInstrument)
                                                   .withExchange("Q")
                                                   .withPrice(inPrice)
                                                   .withContractSize(1)
                                                   .withSize(generateDecimalValue())
                                                   .withTradeDate(DateUtils.dateToString(new Date())).create();
    }
    
    /**
     * Generates an option <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateOptionTradeEvent(Option inInstrument,
                                                      Instrument inUnderlyingInstrument,
                                                      BigDecimal inPrice)
    {
        return TradeEventBuilder.optionTradeEvent().withInstrument(inInstrument)
                                                   .withExchange("Q")
                                                   .withPrice(inPrice)
                                                   .withSize(generateDecimalValue())
                                                   .withExpirationType(ExpirationType.AMERICAN)
                                                   .withMultiplier(BigDecimal.ZERO)
                                                   .withUnderlyingInstrument(inUnderlyingInstrument)
                                                   .withTradeDate(DateUtils.dateToString(new Date())).create();
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
     * Generates an equity <code>MarketstatEvent</code> value. 
     *
     * @param inInstrument an <code>Equity</code> value
     * @return a <code>MarketstatEvent</code> value
     */
    public static MarketstatEvent generateEquityMarketstatEvent(Equity inInstrument)
    {
        long startMillis = System.currentTimeMillis();
        long oneDay = 1000 * 60 * 60 * 24;
        int counter = 0;
        return MarketstatEventBuilder.equityMarketstat().withInstrument(inInstrument)
                                                        .withOpenPrice(generateDecimalValue())
                                                        .withHighPrice(generateDecimalValue())
                                                        .withLowPrice(generateDecimalValue())
                                                        .withClosePrice(generateDecimalValue())
                                                        .withPreviousClosePrice(generateDecimalValue())
                                                        .withVolume(generateDecimalValue())
                                                        .withCloseDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withPreviousCloseDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withTradeHighTime(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withTradeLowTime(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withOpenExchange("O")
                                                        .withHighExchange("H")
                                                        .withLowExchange("L")
                                                        .withCloseExchange("C").create();
    }
    /**
     * Generates an option <code>MarketstatEvent</code> value. 
     *
     * @param inInstrument an <code>Option</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     * @return a <code>MarketstatEvent</code> value
     */
    public static MarketstatEvent generateOptionMarketstatEvent(Option inInstrument,
                                                                Instrument inUnderlyingInstrument)
    {
        long startMillis = System.currentTimeMillis();
        long oneDay = 1000 * 60 * 60 * 24;
        int counter = 0;
        return MarketstatEventBuilder.optionMarketstat().withInstrument(inInstrument)
                                                        .withOpenPrice(generateDecimalValue())
                                                        .withHighPrice(generateDecimalValue())
                                                        .withLowPrice(generateDecimalValue())
                                                        .withClosePrice(generateDecimalValue())
                                                        .withPreviousClosePrice(generateDecimalValue())
                                                        .withVolume(generateDecimalValue())
                                                        .withCloseDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withPreviousCloseDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withTradeHighTime(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withTradeLowTime(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                                        .withExpirationType(ExpirationType.AMERICAN)
                                                        .withMultiplier(BigDecimal.ZERO)
                                                        .withUnderlyingInstrument(inUnderlyingInstrument)
                                                        .withOpenExchange("O")
                                                        .withHighExchange("H")
                                                        .withLowExchange("L")
                                                        .withCloseExchange("C").create();
    }
    /**
     * Generates a <code>DividendEvent</code> with preset values.
     *
     * @return a <code>DividendEvent</code> value
     */
    public static DividendEvent generateDividendEvent()
    {
        return generateDividendEvent(new Equity("METC"));
    }
    /**
     * Generates a <code>DividendEvent</code> with the given and preset values.
     *
     * @param inEquity an <code>Equity</code> value
     * @return a <code>DividendEvent</code> value
     */
    public static DividendEvent generateDividendEvent(Equity inEquity)
    {
        long startMillis = System.currentTimeMillis();
        long oneDay = 1000 * 60 * 60 * 24;
        int counter = 0;
        return DividendEventBuilder.dividend().withEquity(inEquity)
                                              .withAmount(generateDecimalValue())
                                              .withCurrency("US Dollars")
                                              .withDeclareDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                              .withExecutionDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                              .withFrequency(DividendFrequency.ANNUALLY)
                                              .withPaymentDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                              .withRecordDate(DateUtils.dateToString(new Date(startMillis  + (counter++ * oneDay))))
                                              .withStatus(DividendStatus.OFFICIAL)
                                              .withType(DividendType.CURRENT).create();
    }
    /**
     * Generates an <code>Instrument</code> guaranteed to be of a
     * previously undiscovered type.
     *
     * @return an <code>Instrument</code> value
     */
    public static Instrument generateUnsupportedInstrument()
    {
        Instrument unsupportedInstrument = new UnsupportedInstrument();
        assertFalse(unsupportedInstrument instanceof Equity);
        assertFalse(unsupportedInstrument instanceof Option);
        return unsupportedInstrument;
    }
    /**
     * Generates a quote date value.
     *
     * @return a <code>String</code> value
     */
    public static String generateQuoteDate()
    {
        return DateUtils.dateToString(new Date());
    }
    /**
     * Generates an exchange value.
     *
     * @return a <code>String</code> value
     */
    public static String generateExchange()
    {
        return "Exchange-"+random.nextInt();
    }
    /**
     * Generates a random value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal generateDecimalValue()
    {
        return new BigDecimal(String.format("%d.%d",
                                            random.nextInt(10000),
                                            random.nextInt(100)));
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
     * generates random values - used for test data
     */
    private static final Random random = new Random(System.nanoTime());
    /**
     * <code>Instrument</code> guaranteed to not be of the same type as any other
     * <code>Instrument</code> in the code base.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    private static class UnsupportedInstrument
            extends Instrument
    {
        /* (non-Javadoc)
         * @see org.marketcetera.trade.Instrument#getSecurityType()
         */
        @Override
        public SecurityType getSecurityType()
        {
            return SecurityType.Unknown;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.trade.Instrument#getSymbol()
         */
        @Override
        public String getSymbol()
        {
            return String.valueOf(System.identityHashCode(this));
        }
        private static final long serialVersionUID = 1L;
    }
}
