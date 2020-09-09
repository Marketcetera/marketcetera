package org.marketcetera.event;

import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.marketcetera.event.impl.DepthOfBookEventBuilder;
import org.marketcetera.event.impl.DividendEventBuilder;
import org.marketcetera.event.impl.ImbalanceEventBuilder;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.OptionEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TopOfBookEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.util.time.DateService;

import com.google.common.collect.Lists;

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
     * Generate a depth-of-book-event for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>DepthOfBookEvent</code> value
     */
    public static DepthOfBookEvent generateDepthOfBookEvent(Instrument inInstrument)
    {
        DepthOfBookEventBuilder builder = DepthOfBookEventBuilder.depthOfBook();
        BigDecimal bidPrice = generateDecimalValue();
        BigDecimal askPrice = bidPrice.add(new BigDecimal("0.02"));
        int bidDepth = random.nextInt(20);
        int askDepth = random.nextInt(20);
        BigDecimal PENNY = new BigDecimal("0.01");
        List<BidEvent> bids = Lists.newArrayList();
        List<AskEvent> asks = Lists.newArrayList();
        for(int i=0;i<bidDepth;i++) {
            bidPrice = bidPrice.subtract(PENNY.multiply(new BigDecimal(i)));
            BidEvent bid = generateBidEvent(inInstrument,
                                            bidPrice);
            bid.setLevel(i+1);
            bids.add(bid);
        }
        for(int i=0;i<askDepth;i++) {
            askPrice = askPrice.add(PENNY.multiply(new BigDecimal(i)));
            AskEvent ask = generateAskEvent(inInstrument,
                                            askPrice);
            ask.setLevel(i+1);
            asks.add(ask);
        }
        builder.withAsks(asks).withBids(bids).withInstrument(inInstrument);
        return builder.create();
    }
    /**
     * Generate a top-of-book-event for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>TopOfBookEvent</code> value
     */
    public static TopOfBookEvent generateTopOfBookEvent(Instrument inInstrument)
    {
        TopOfBookEventBuilder builder = TopOfBookEventBuilder.topOfBookEvent();
        DepthOfBookEvent depthOfBookEvent = generateDepthOfBookEvent(inInstrument);
        if(!depthOfBookEvent.getAsks().isEmpty()) {
            builder.withAsk(depthOfBookEvent.getAsks().get(0));
        }
        if(!depthOfBookEvent.getBids().isEmpty()) {
            builder.withBid(depthOfBookEvent.getBids().get(0));
        }
        builder.withInstrument(inInstrument);
        return builder.create();
    }
    /**
     * Generates an <code>AskEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateAskEvent(Instrument inInstrument)
    {
        return generateAskEvent(inInstrument,
                                generateDecimalValue());
    }
    /**
     * Generates an <code>AskEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inQuoteDate a <code>LocalDateTime</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateAskEvent(Instrument inInstrument,
                                            LocalDateTime inQuoteDate)
    {
        return QuoteEventBuilder.askEvent(inInstrument)
                .withExchange(generateExchange())
                .withPrice(generateDecimalValue())
                .withSize(generateDecimalValue())
                .withQuoteDate(inQuoteDate).create();
    }
    /**
     * Generates an <code>AskEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inAskPrice a <code>BigDecimal</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateAskEvent(Instrument inInstrument,
                                            BigDecimal inAskPrice)
    {
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(inInstrument)
                .withExchange(generateExchange())
                .withPrice(inAskPrice)
                .withSize(generateDecimalValue())
                .withQuoteDate(generateQuoteDate());
        addOptionAttributes(builder,
                            inInstrument);
        return builder.create();
    }
    /**
     * Generates a <code>BidEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateBidEvent(Instrument inInstrument)
    {
        return generateBidEvent(inInstrument,
                                generateDecimalValue());
    }
    /**
     * Generates a <code>BidEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inQuoteDate a <code>LocalDateTime</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateBidEvent(Instrument inInstrument,
                                            LocalDateTime inQuoteDate)
    {
        return QuoteEventBuilder.bidEvent(inInstrument)
                .withExchange(generateExchange())
                .withPrice(generateDecimalValue())
                .withSize(generateDecimalValue())
                .withQuoteDate(inQuoteDate).create();
    }
    /**
     * Generates a <code>BidEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inBidPrice a <code>BigDecimal</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateBidEvent(Instrument inInstrument,
                                            BigDecimal inBidPrice)
    {
        QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(inInstrument)
                .withExchange(generateExchange())
                .withPrice(inBidPrice)
                .withSize(generateDecimalValue())
                .withQuoteDate(generateQuoteDate());
        addOptionAttributes(builder,
                            inInstrument);
        return builder.create();
    }
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
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>LocalDateTime</code> value
     * @return an <code>AskEvent</code> value
     */
    public static AskEvent generateEquityAskEvent(long inMessageId,
                                                  LocalDateTime inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize,
                                                  LocalDateTime inExchangeTimestamp)
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
     * @param inTimestamp a <code>LocalDateTime</code> value
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
                                      DateService.toLocalDateTime(inTimestamp),
                                      inInstrument,
                                      inExchange,
                                      inPrice,
                                      inSize,
                                      DateService.toLocalDateTime(inTimestamp));
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
    }
    
    
    /**
     * Generates an equity <code>BidEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>LocalDateTime</code> value
     * @return a <code>BidEvent</code> value
     */
    public static BidEvent generateEquityBidEvent(long inMessageId,
                                                  LocalDateTime inTimestamp,
                                                  Equity inInstrument,
                                                  String inExchange,
                                                  BigDecimal inPrice,
                                                  BigDecimal inSize,
                                                  LocalDateTime inExchangeTimestamp)
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
     * @param inTimestamp a <code>LocalDateTime</code> value
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
                                      DateService.toLocalDateTime(inTimestamp),
                                      inInstrument,
                                      inExchange,
                                      inPrice,
                                      inSize,
                                      LocalDateTime.now());
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now()).create();
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
                                                 .withQuoteDate(LocalDateTime.now())
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
     * Generates a <code>TradeEvent</code> with the given value.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateTradeEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Option) {
            return generateOptionTradeEvent((Option)inInstrument,
                                            new Equity(inInstrument.getSymbol()));
        }
        return TradeEventBuilder.tradeEvent(inInstrument)
                .withExchange(generateExchange())
                .withPrice(generateDecimalValue())
                .withSize(generateDecimalValue())
                .withTradeCondition("0xACAE")
                .withProvider("test")
                .withProviderSymbol(inInstrument.getFullSymbol())
                .withSource(EventTestBase.class)
                .withTradeDate(generateQuoteDate()).create();
    }
    /**                                                                                                                                                                                                     
     * Generates a <code>TradeEvent</code> with the given value.                                                                                                                                            
     *                                                                                                                                                                                                      
     * @param inInstrument an <code>Instrument</code> value                                                                                                                                                 
     * @param inExchange a <code>String</code> value                                                                                                                                                        
     * @return a <code>TradeEvent</code> value                                                                                                                                                              
     */
    public static TradeEvent generateTradeEvent(Instrument inInstrument,
                                                String inExchange)
    {
        return TradeEventBuilder.tradeEvent(inInstrument)
                .withExchange(inExchange)
                .withPrice(generateDecimalValue())
                .withSize(generateDecimalValue())
                .withTradeDate(generateQuoteDate()).create();
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
                                                   .withProviderSymbol(inInstrument.getFullSymbol())
                                                   .withTradeDate(LocalDateTime.now()).create();
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
                                                   .withTradeDate(LocalDateTime.now()).create();
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
                                                   .withTradeDate(LocalDateTime.now()).create();
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
                                                   .withTradeDate(LocalDateTime.now()).create();
    }
    /**
     * Generates an equity <code>TradeEvent</code> with the given values.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>LocalDateTime</code> value
     * @param inInstrument an <code>Equity</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inExchangeTimestamp a <code>LocalDateTime</code> value
     * @return a <code>TradeEvent</code> value
     */
    public static TradeEvent generateEquityTradeEvent(long inMessageId,
                                                      LocalDateTime inTimestamp,
                                                      Equity inInstrument,
                                                      String inExchange,
                                                      BigDecimal inPrice,
                                                      BigDecimal inSize,
                                                      LocalDateTime inExchangeTimestamp)
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
     * @param inTimestamp a <code>LocalDateTime</code> value
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
                                        DateService.toLocalDateTime(inTimestamp),
                                        inInstrument,
                                        inExchange,
                                        inPrice,
                                        inSize,
                                        DateService.toLocalDateTime(inTimestamp));
    }
    /**
     * Generates an equity <code>MarketstatEvent</code> value. 
     *
     * @param inInstrument an <code>Equity</code> value
     * @param inTimestamp a <code>LocalDateTime</code> value
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
                                                                LocalDateTime inTimestamp,
                                                                BigDecimal inOpenPrice,
                                                                BigDecimal inHighPrice,
                                                                BigDecimal inLowPrice,
                                                                BigDecimal inClosePrice,
                                                                BigDecimal inPreviousClosePrice,
                                                                BigDecimal inVolume,
                                                                LocalDateTime inCloseDate,
                                                                LocalDateTime inPreviousCloseDate,
                                                                LocalDateTime inTradeHighTime,
                                                                LocalDateTime inTradeLowTime,
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
                                                        .withCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withPreviousCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withTradeHighTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withTradeLowTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withOpenExchange("O")
                                                        .withHighExchange("H")
                                                        .withLowExchange("L")
                                                        .withCloseExchange("C").create();
    }
    /**
     * Generates a <code>MarketstatEvent</code> for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>MarketstatEvent</code>
     */
    public static MarketstatEvent generateMarketstatEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Option) {
            return generateOptionMarketstatEvent((Option)inInstrument,
                                                 new Equity("METC"));
        }
        long startMillis = System.currentTimeMillis();
        long oneDay = 1000 * 60 * 60 * 24;
        int counter = 0;
        return MarketstatEventBuilder.marketstat(inInstrument).withOpenPrice(generateDecimalValue())
                                                              .withHighPrice(generateDecimalValue())
                                                              .withLowPrice(generateDecimalValue())
                                                              .withClosePrice(generateDecimalValue())
                                                              .withPreviousClosePrice(generateDecimalValue())
                                                              .withVolume(generateDecimalValue())
                                                              .withCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                              .withPreviousCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                              .withTradeHighTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                              .withTradeLowTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                              .withValue(generateDecimalValue())
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
                                                        .withValue(generateDecimalValue())
                                                        .withVolume(generateDecimalValue())
                                                        .withCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withPreviousCloseDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withTradeHighTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withTradeLowTime(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                                        .withExpirationType(ExpirationType.AMERICAN)
                                                        .withMultiplier(BigDecimal.ZERO)
                                                        .withUnderlyingInstrument(inUnderlyingInstrument)
                                                        .withOpenExchange("O")
                                                        .withHighExchange("H")
                                                        .withLowExchange("L")
                                                        .withCloseExchange("C").create();
    }
    /**
     * Generate a <code>LogEvent</code> with random values.
     *
     * @return a <code>LogEvent</code> value
     */
    public static LogEvent generateLogEvent()
    {
        LogEventBuilder builder = LogEventBuilder.error();
        builder.withRequestId(System.nanoTime());
        builder.withException(new RuntimeException("This exception is expected"));
        builder.withSource(EventTestBase.class);
        builder.withMessage(Messages.VALIDATION_NULL_AMOUNT);
        return builder.create();
    }
    /**
     * Generate an <code>ImbalanceEvent</code> with random values.
     *
     * @return an <code>ImbalanceEvent</code> value
     */
    public static ImbalanceEvent generateImbalanceEvent()
    {
        return generateImbalanceEvent(new Equity("METC"));
    }
    /**
     * Generate an <code>ImbalanceEvent</code> with random values and the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @return an <code>ImbalanceEvent</code> value
     */
    public static ImbalanceEvent generateImbalanceEvent(Instrument inInstrument)
    {
        ImbalanceEventBuilder builder = ImbalanceEventBuilder.Imbalance(inInstrument);
        builder.withTimestamp(LocalDateTime.now());
        builder.withAmountOutstanding(EventTestBase.generateDecimalValue());
        builder.withAuctionType(AuctionType.values()[random.nextInt(AuctionType.values().length)]);
        builder.withExchange(UUID.randomUUID().toString());
        builder.withEventType(EventType.values()[random.nextInt(EventType.values().length)]);
        builder.withFarPrice(EventTestBase.generateDecimalValue());
        builder.withImbalanceVolume(EventTestBase.generateDecimalValue());
        builder.withInstrumentStatus(InstrumentStatus.values()[random.nextInt(InstrumentStatus.values().length)]);
        builder.withMarketStatus(MarketStatus.values()[random.nextInt(MarketStatus.values().length)]);
        builder.withNearPrice(EventTestBase.generateDecimalValue());
        builder.withPairedVolume(EventTestBase.generateDecimalValue());
        builder.withReferencePrice(EventTestBase.generateDecimalValue());
        builder.withImbalanceType(ImbalanceType.values()[random.nextInt(ImbalanceType.values().length)]);
        builder.withShortSaleRestricted(random.nextBoolean());
        addOptionAttributes(builder,
                            inInstrument);
        return builder.create();
    }
    /**
     * Add option attributes to the given builder, if appropriate.
     *
     * @param inBuilder an <code>OptionEventBuilder&lt;?&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     */
    public static void addOptionAttributes(OptionEventBuilder<?> inBuilder,
                                           Instrument inInstrument)
    {
        addOptionAttributes(inBuilder,
                            inInstrument,
                            new Equity("METC"));
    }
    /**
     * Add option attributes to the given builder, if appropriate.
     *
     * @param inBuilder an <code>OptionEventBuilder&lt;?&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inUnderlyingInstrument an <code>Instrument</code> value
     */
    public static void addOptionAttributes(OptionEventBuilder<?> inBuilder,
                                           Instrument inInstrument,
                                           Instrument inUnderlyingInstrument)
    {
        if(inInstrument instanceof Option) {
            inBuilder.withExpirationType(ExpirationType.AMERICAN);
            inBuilder.withMultiplier(EventTestBase.generateDecimalValue());
            inBuilder.withUnderlyingInstrument(inUnderlyingInstrument);
            inBuilder.withProviderSymbol(inInstrument.getFullSymbol());
        }
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
                                              .withDeclareDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                              .withExecutionDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                              .withFrequency(DividendFrequency.ANNUALLY)
                                              .withPaymentDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
                                              .withRecordDate(DateUtils.dateToString(DateService.toLocalDateTime(startMillis  + (counter++ * oneDay))))
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
     * @return a <code>LocalDateTime</code> value
     */
    public static LocalDateTime generateQuoteDate()
    {
        return LocalDateTime.now();
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
