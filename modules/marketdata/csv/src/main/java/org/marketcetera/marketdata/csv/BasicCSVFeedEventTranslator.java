package org.marketcetera.marketdata.csv;

import static org.marketcetera.marketdata.csv.Messages.CANNOT_GUESS_BIG_DECIMAL;
import static org.marketcetera.marketdata.csv.Messages.CANNOT_GUESS_DATE;
import static org.marketcetera.marketdata.csv.Messages.CANNOT_INTERPRET_DIVIDEND_FREQUENCY;
import static org.marketcetera.marketdata.csv.Messages.CANNOT_INTERPRET_DIVIDEND_STATUS;
import static org.marketcetera.marketdata.csv.Messages.CANNOT_INTERPRET_DIVIDEND_TYPE;
import static org.marketcetera.marketdata.csv.Messages.EMPTY_LINE;
import static org.marketcetera.marketdata.csv.Messages.INVALID_CFI_CODE;
import static org.marketcetera.marketdata.csv.Messages.LINE_MISSING_REQUIRED_FIELDS;
import static org.marketcetera.marketdata.csv.Messages.NOT_OSI_COMPLIANT;
import static org.marketcetera.marketdata.csv.Messages.UNABLE_TO_CONSTRUCT_DIVIDEND;
import static org.marketcetera.marketdata.csv.Messages.UNABLE_TO_CONSTRUCT_MARKETSTAT;
import static org.marketcetera.marketdata.csv.Messages.UNABLE_TO_CONSTRUCT_QUOTE;
import static org.marketcetera.marketdata.csv.Messages.UNABLE_TO_CONSTRUCT_TRADE;
import static org.marketcetera.marketdata.csv.Messages.UNKNOWN_BASIC_EVENT_TYPE;
import static org.marketcetera.marketdata.csv.Messages.UNKNOWN_SYMBOL_FORMAT;
import static org.marketcetera.marketdata.csv.Messages.UNSUPPORTED_CFI_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.Event;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.DividendEventBuilder;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.options.OptionUtils;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;

/* $License$ */

/**
 * Basic CSV Event Translator.
 * 
 * <p>Translates CSV files to {@link Event} objects based on a set of assumptions about the content of the CSV files.
 * 
 * <p>This translator provides support for top-of-book and latest-tick data for equities and options.  Note that
 * the support for options is somewhat weak as {@link #guessExpirationType(CSVQuantum, Option)}, {@link #guessHasDeliverable(CSVQuantum, Option)},
 * and {@link #guessMultiplier(CSVQuantum, Option)} all yield default values.  A more robust interpretation would add support for extra fields
 * for options.  Option symbols must all be OSI-compliant.
 * 
 * <p>This class is designed to be extended for specialization as necessary.  Any of the methods may be overridden by a subclass to provide
 * more detailed behavior.
 * 
 * <p>The default implementation expects data in the following format:
 * <table>
 *   <tr><td>Column0</td><td><strong>BID</strong></td><td><strong>ASK</strong></td><td><strong>TRADE</strong></td><td><strong>DIVIDEND</strong></td><td><strong>STAT</strong></td></tr>
 *   <tr><td>Column1</td><td><strong>Timestamp</strong></td><td><strong>Timestamp</strong></td><td><strong>Timestamp</strong></td><td><strong>Timestamp</strong></td><td><strong>Timestamp</strong></td></tr>
 *   <tr><td>Column2</td><td><strong>Symbol</strong></td><td><strong>Symbol</strong></td><td><strong>Symbol</strong></td><td><strong>Equity Symbol</strong></td><td><strong>Symbol</strong></td></tr>
 *   <tr><td>Column3</td><td><strong>QuoteDate</strong></td><td><strong>QuoteDate</strong></td><td><strong>TradeDate</strong></td><td><strong>Amount<strong></td><td><em>Open Price</em></td></tr>
 *   <tr><td>Column4</td><td><strong>Exchange</strong></td><td><strong>Exchange</strong></td><td><strong>Exchange</strong></td><td><strong>Currency</strong></td><td><em>High Price</em></td></tr>
 *   <tr><td>Column5</td><td><strong>Price</strong></td><td><strong>Price</strong></td><td><strong>Price</strong></td><td><strong>Type</strong></td><td><em>Low Price</em></td></tr>
 *   <tr><td>Column6</td><td><strong>Size</strong></td><td><strong>Size</strong></td><td><strong>Size</strong></td><td><strong>Frequency</strong></td><td><em>Close Price</em></td></tr>
 *   <tr><td>Column7</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><strong>Status</strong></td><td><em>Previous Close Price</em></td></tr>
 *   <tr><td>Column8</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><strong>Execution Date</strong></td><td><em>Volume</em></td></tr>
 *   <tr><td>Column9</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Record Date</em></td><td><em>Close Date</em></td></tr>
 *   <tr><td>Column10</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Payment Date</em></td><td><em>Previous Close Date</em></td></tr>
 *   <tr><td>Column11</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Declare Date</em></td><td><em>Trade High Time</em></td></tr>
 *   <tr><td>Column12</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Trade Low Time</em></td></tr>
 *   <tr><td>Column13</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Open Exchange</em></td></tr>
 *   <tr><td>Column14</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>High Exchange</em></td></tr>
 *   <tr><td>Column15</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Low Exchange</em></td></tr>
 *   <tr><td>Column16</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td><em>Close Exchange</em></td></tr>
 * </table>
 * 
 * <p>Fields may be omitted by skipping: <code>value,value,,value</code>.  Some fields are <strong>mandatory</strong>, some are <em>optional</em>
 * depending on the type of event being created.  <code>Column0</code> contains the type.
 * 
 * <p>This object is stateless.  All subclasses must also be reentrant.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
@Immutable
public class BasicCSVFeedEventTranslator
        extends CSVFeedEventTranslator
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedEventTranslator#toEvent(java.lang.Object, java.lang.String)
     */
    @Override
    public List<Event> toEvent(Object inData,
                               String inHandle)
            throws CoreException
    {
        List<Event> events = new ArrayList<Event>();
        CSVQuantum data = (CSVQuantum)inData;
        // the first element is the type, must be one of: {BID,ASK,TRADE,DIVIDEND,STAT} (case-insensitive)
        if(data.getLine().length == 0) {
            throw new CoreException(EMPTY_LINE);
        }
        EventType type = guessEventType(data);
        if(type.equals(EventType.BID)) {
            validateBid(data);
            events.add(processBid(data));
        } else if(type.equals(EventType.ASK)) {
            validateAsk(data);
            events.add(processAsk(data));
        } else if(type.equals(EventType.TRADE)) {
            validateTrade(data);
            events.add(processTrade(data));
        } else if(type.equals(EventType.DIVIDEND)) {
            validateDividend(data);
            events.add(processDividend(data));
        } else if(type.equals(EventType.STAT)) {
            validateMarketstat(data);
            events.add(processMarketstat(data));
        } else {
            throw new CoreException(new I18NBoundMessage2P(UNKNOWN_BASIC_EVENT_TYPE,
                                                           data.toString(),
                                                           type));
        }
        return events;
    }
    /**
     * Validates the given line as a market statistic. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateMarketstat(CSVQuantum inData)
            throws CoreException
    {
        validateRequiredFields(inData,
                               requiredMarketstatFields);
    }
    /**
     * Validates the given line as a dividend. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateDividend(CSVQuantum inData)
            throws CoreException
    {
        validateRequiredFields(inData,
                               requiredDividendFields);
    }
    /**
     * Validates the given line as a bid. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateBid(CSVQuantum inData)
            throws CoreException
    {
        validateQuote(inData);
    }
    /**
     * Validates the given line as an ask. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateAsk(CSVQuantum inData)
            throws CoreException
    {
        validateQuote(inData);
    }
    /**
     * Validates the given line as a quote. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateQuote(CSVQuantum inData)
            throws CoreException
    {
        validateRequiredFields(inData,
                               requiredQuoteFields);
    }
    /**
     * Validates the given line as a trade. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateTrade(CSVQuantum inData)
            throws CoreException
    {
        validateRequiredFields(inData,
                               requiredTradeFields);
    }
    /**
     * Confirms that the given fields are included in the given line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inRequiredFields a <code>Set&lt;Integer&gt;</code> value
     * @throws CoreException if the required fields are not present
     */
    protected void validateRequiredFields(CSVQuantum inData,
                                          Set<Integer> inRequiredFields)
            throws CoreException
    {
        for(int field : inRequiredFields) {
            if(field > inData.getLine().length-1) {
                throw new CoreException(new I18NBoundMessage2P(LINE_MISSING_REQUIRED_FIELDS,
                                                               inData.toString(),
                                                               inRequiredFields.toString()));
            }
        }
    }
    /**
     * Processes the given data line as a market stat. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>MarketstatEvent</code> value
     * @throws CoreException if an error occurs processing the line as a market stat
     */
    protected MarketstatEvent processMarketstat(CSVQuantum inData)
            throws CoreException
    {
        if(inData == null) {
            throw new NullPointerException();
        }
        try {
            Instrument instrument = guessInstrument(inData);                // 2
            MarketstatEventBuilder builder = MarketstatEventBuilder.marketstat(instrument);
            builder.withTimestamp(guessEventTimestamp(inData))              // 1
                   .withOpenPrice(guessOpenPrice(inData))                   // 3
                   .withHighPrice(guessHighPrice(inData))                   // 4
                   .withLowPrice(guessLowPrice(inData))                     // 5
                   .withClosePrice(guessClosePrice(inData))                 // 6
                   .withPreviousClosePrice(guessPreviousClosePrice(inData)) // 7
                   .withVolume(guessVolume(inData))                         // 8
                   .withCloseDate(guessCloseDate(inData))                   // 9
                   .withPreviousCloseDate(guessPreviousCloseDate(inData))   // 10
                   .withTradeHighTime(guessTradeHighTime(inData))           // 11
                   .withTradeLowTime(guessTradeLowTime(inData))             // 12
                   .withOpenExchange(guessOpenExchange(inData))             // 13
                   .withHighExchange(guessHighExchange(inData))             // 14
                   .withLowExchange(guessLowExchange(inData))               // 15
                   .withCloseExchange(guessCloseExchange(inData));          // 16
            if(instrument instanceof Option) {
                Option option = (Option)instrument;
                builder.withExpirationType(guessExpirationType(inData,
                                                               option))
                       .withMultiplier(guessMultiplier(inData,
                                                       option))
                       .withUnderlyingInstrument(guessUnderlyingInstrument(inData,
                                                                           option))
                       .hasDeliverable(guessHasDeliverable(inData,
                                                           option));
            }
            return builder.create();
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(UNABLE_TO_CONSTRUCT_MARKETSTAT,
                                                           String.valueOf(inData)));
        }
    }
    /**
     * Processes the given data line as a dividend. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>DividendEvent</code> value
     * @throws CoreException if an error occurs processing the line as a dividend
     */
    protected DividendEvent processDividend(CSVQuantum inData)
            throws CoreException
    {
        if(inData == null) {
            throw new NullPointerException();
        }
        DividendEventBuilder builder = DividendEventBuilder.dividend();
        try {
            builder.withAmount(guessDividendAmount(inData))
                   .withCurrency(guessDividendCurrency(inData))
                   .withDeclareDate(guessDividendDeclareDate(inData))
                   .withExecutionDate(guessDividendExecutionDate(inData))
                   .withFrequency(guessDividendFrequency(inData))
                   .withEquity(guessDividendEquity(inData))
                   .withPaymentDate(guessDividendPaymentDate(inData))
                   .withRecordDate(guessDividendRecordDate(inData))
                   .withStatus(guessDividendStatus(inData))
                   .withType(guessDividendType(inData))
                   .withTimestamp(guessEventTimestamp(inData));
            return builder.create();
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(UNABLE_TO_CONSTRUCT_DIVIDEND,
                                                           String.valueOf(inData)));
        }
    }
    /**
     * Processes the given data line as a bid. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BidEvent</code> value
     * @throws CoreException if an error occurs processing the line as a bid
     */
    protected BidEvent processBid(CSVQuantum inData)
            throws CoreException
    {
        if(inData == null) {
            throw new NullPointerException();
        }
        try {
            Instrument instrument = guessInstrument(inData);
            return (BidEvent)processQuote(inData,
                                          QuoteEventBuilder.bidEvent(instrument),
                                          instrument);
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(UNABLE_TO_CONSTRUCT_QUOTE,
                                                           String.valueOf(inData)));
        }
    }
    /**
     * Processes the given data line as an ask. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return an <code>AskEvent</code> value
     * @throws CoreException if an error occurs processing the line as an ask
     */
    protected AskEvent processAsk(CSVQuantum inData)
            throws CoreException
    {
        if(inData == null) {
            throw new NullPointerException();
        }
        try {
            Instrument instrument = guessInstrument(inData);
            return (AskEvent)processQuote(inData,
                                          QuoteEventBuilder.askEvent(instrument),
                                          instrument);
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(UNABLE_TO_CONSTRUCT_QUOTE,
                                                           String.valueOf(inData)));
        }
    }
    /**
     * Processes the given data line as a trade. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>TradeEvent</code> value
     * @throws CoreException if an error occurs processing the line as a trade
     */
    protected TradeEvent processTrade(CSVQuantum inData)
            throws CoreException
    {
        if(inData == null) {
            throw new NullPointerException();
        }
        try {
            Instrument instrument = guessInstrument(inData);
            TradeEventBuilder<? extends TradeEvent> builder = TradeEventBuilder.tradeEvent(instrument);
            builder.withTradeDate(guessTradeDate(inData))
                   .withExchange(guessExchange(inData))
                   .withPrice(guessPrice(inData))
                   .withSize(guessSize(inData))
                   .withTimestamp(guessEventTimestamp(inData))
                   .withReceivedTimestamp(inData.getReceivedTimestamp());
            if(instrument instanceof Option) {
                Option option = (Option)instrument;
                builder.withExpirationType(guessExpirationType(inData,
                                                               option))
                       .withMultiplier(guessMultiplier(inData,
                                                       option))
                       .withUnderlyingInstrument(guessUnderlyingInstrument(inData,
                                                                           option))
                       .hasDeliverable(guessHasDeliverable(inData,
                                                           option));
            }
            return builder.create();
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(UNABLE_TO_CONSTRUCT_TRADE,
                                                           String.valueOf(inData)));
        }
    }
    /**
     * Processes the given data line as a quote. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inBuilder a <code>QuoteEventBuilder&lt;? extends QuoteEvent&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>QuoteEvent</code> value
     * @throws CoreException if an error occurs processing the line as a quote
     */
    protected QuoteEvent processQuote(CSVQuantum inData,
                                      QuoteEventBuilder<? extends QuoteEvent> inBuilder,
                                      Instrument inInstrument)
            throws CoreException
    {
        inBuilder.withAction(guessQuoteAction(inData))        // optional
                 .withExchange(guessExchange(inData))         // required
                 .withMessageId(guessMessageId(inData))       // required
                 .withPrice(guessPrice(inData))               // required
                 .withQuoteDate(guessQuoteDate(inData))       // required
                 .withSize(guessSize(inData))                 // required
                 .withReceivedTimestamp(inData.getReceivedTimestamp())
                 .withTimestamp(guessEventTimestamp(inData)); // required
        // that should do it for your basic stuff, now, if necessary, add the option-specific works
        if(inInstrument instanceof Option) {
            Option option = (Option)inInstrument;
            inBuilder.withExpirationType(guessExpirationType(inData,               // optional
                                                             option))
                     .withMultiplier(guessMultiplier(inData,                       // optional
                                                     option))
                     .withUnderlyingInstrument(guessUnderlyingInstrument(inData,   // optional
                                                                         option))
                     .hasDeliverable(guessHasDeliverable(inData,                   // optional
                                                         option));
        }
        return inBuilder.create();
    }
    /**
     * Guesses the event type from the given line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>Type</code> value
     * @throws CoreException if the type cannot be determined
     */
    protected EventType guessEventType(CSVQuantum inData)
            throws CoreException
    {
        String type = guessString(inData,
                                  0);
        try {
            return EventType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new CoreException(new I18NBoundMessage2P(UNKNOWN_BASIC_EVENT_TYPE,
                                                           inData.toString(),
                                                           type));
        }
    }
    /**
     * Guesses the dividend type from the given data line.
     *
     * <p>The dividend type is assumed to be the sixth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>DividendType</code> value
     * @throws CoreException if the given data cannot be interpreted as a dividend type
     */
    protected DividendType guessDividendType(CSVQuantum inData)
            throws CoreException
    {
        String type = guessString(inData,
                                  5);
        if(type == null) {
            return null;
        }
        try {
            return DividendType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage2P(CANNOT_INTERPRET_DIVIDEND_TYPE,
                                                           inData.toString(),
                                                           type));
        }
    }
    /**
     * Guesses the dividend status from the given data line.
     *
     * <p>The dividend status is assumed to be the eighth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>DividendStatus</code> value
     * @throws CoreException if the given data cannot be interpreted as a dividend status
     */
    protected DividendStatus guessDividendStatus(CSVQuantum inData)
            throws CoreException
    {
        String value = guessString(inData,
                                   7);
        if(value == null) {
            return null;
        }
        try {
            return DividendStatus.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage2P(CANNOT_INTERPRET_DIVIDEND_STATUS,
                                                           inData.toString(),
                                                           value));
        }
    }
    /**
     * Guesses the dividend record date from the given data line.
     *
     * <p>The dividend payment date is assumed to be the tenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a dividend record date
     */
    protected String guessDividendRecordDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           9);
    }
    /**
     * Guesses the dividend payment date from the given data line.
     *
     * <p>The dividend payment date is assumed to be the eleventh element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a dividend payment date
     */
    protected String guessDividendPaymentDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           10);
    }
    /**
     * Guesses the dividend equity from the given data line. 
     *
     * <p>The dividend equity is assumed to be the third element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return an <code>Equity</code> value
     * @throws CoreException if the given data cannot be interpreted as a dividend <code>Equity</code> value
     */
    protected Equity guessDividendEquity(CSVQuantum inData)
            throws CoreException
    {
        String symbol = guessString(inData,
                                    2);
        if(symbol == null) {
            return null;
        }
        return new Equity(symbol);
    }
    /**
     * Guesses the dividend frequency from the given data line. 
     *
     * <p>The dividend frequency is assumed to be the seventh element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>DividendFrequency</code> value
     * @throws CoreException if the given data cannot be interpreted as a <code>DividendFrequency</code> value
     */
    protected DividendFrequency guessDividendFrequency(CSVQuantum inData)
            throws CoreException
    {
        String frequencyValue = guessString(inData,
                                            6);
        if(frequencyValue == null) {
            return null;
        }
        try {
            return DividendFrequency.valueOf(frequencyValue.toUpperCase());
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage2P(CANNOT_INTERPRET_DIVIDEND_FREQUENCY,
                                                           inData.toString(),
                                                           frequencyValue));
        }
    }
    /**
     * Guesses the dividend execution date from the given data line.
     *
     * <p>The dividend execution date is assumed to be the ninth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if an error occurs retrieving the dividend execution date
     */
    protected String guessDividendExecutionDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           8);
    }
    /**
     * Guesses the dividend declare date from the given data line.
     *
     * <p>The dividend declare date is assumed to be the twelfth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if an error occurs retrieving the dividend declare date
     */
    protected String guessDividendDeclareDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           11);
    }
    /**
     * Guesses the dividend currency from the given data line.
     *
     * <p>The dividend currency is assumed to be the fifth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if an error occurs retrieving the dividend currency
     */
    protected String guessDividendCurrency(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           4);
    }
    /**
     * Guesses the dividend amount from the given data line. 
     *
     * <p>The dividend amount is assumed to be the fourth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value
     * @throws CoreException if an error occurs retrieving the dividend amount
     */
    protected BigDecimal guessDividendAmount(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               3);
    }
    /**
     * Guesses the trade date of the given data line. 
     *
     * <p>The trade date is assumed to be the fourth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if an error occurs retrieving the trade date
     */
    protected String guessTradeDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           3);
    }
    /**
     * Guesses whether the contract of the data line has deliverables. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>boolean</code> value
     * @throws CoreException if the given data yields a contract that has deliverables
     */
    protected boolean guessHasDeliverable(CSVQuantum inData,
                                          Option inOption)
            throws CoreException
    {
        return true; 
    }
    /**
     * Guesses the underlying instrument of the given data line.
     * 
     * <p>Assumes that the underlying instrument is an {@link Equity} and that
     * the option is OSI-compatible.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inOption an <code>Option</code> value
     * @return an <code>Instrument</code> value
     * @throws CoreException if the given data does not yield an underlying instrument
     */
    protected Instrument guessUnderlyingInstrument(CSVQuantum inData,
                                                   Option inOption)
            throws CoreException
    {
        // this is suboptimal as underlyings can be other types of instruments
        return new Equity(inOption.getSymbol());
    }
    /**
     * Guesses the contract multiplier of the given data line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inOption an <code>Option</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a <code>BigDecimal</code> value
     */
    protected BigDecimal guessMultiplier(CSVQuantum inData,
                                         Option inOption)
            throws CoreException
    {
        return BigDecimal.ONE;
    }
    /**
     * Guesses the expiration type of the given data line.
     * 
     * @param inData a <code>CSVQuantum</code> value
     * @param inOption an <code>Option</code> value
     * @return an <code>ExpirationType</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as an <code>ExpirationType</code>
     */
    protected ExpirationType guessExpirationType(CSVQuantum inData,
                                                 Option inOption)
            throws CoreException
    {
        return ExpirationType.UNKNOWN;
    }
    /**
     * Guesses the size of the given data line. 
     *
     * <p>The size is assumed to be the seventh element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the data cannot be interpreted as a size
     */
    protected BigDecimal guessSize(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               6);
    }
    /**
     * Guesses the quote date from the given data line. 
     *
     * <p>The quote date is assumed to be the fourth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if an error occurs
     */
    protected String guessQuoteDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           3);
    }
    /**
     * Guesses the price of the given data line.
     * 
     * <p>The price is assumed to be the sixth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the data cannot be interpreted as a price
     */
    protected BigDecimal guessPrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               5);
    }
    /**
     * Guesses the message ID from the given data line. 
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>long</code> value
     * @throws CoreException if the data cannot be interpreted as a long
     */
    protected long guessMessageId(CSVQuantum inData)
            throws CoreException
    {
        return counter.incrementAndGet();
    }
    /**
     * Guesses the <code>Instrument</code> type from the symbol.
     * 
     * <p>This method makes one large assumption: the character <code>:</code> can be used only to separate CFI Code (ISO 10962) and the
     * symbol.  A symbol that otherwise includes <code>:</code> will not be processed correctly.  This method does not support escaped
     * <code>:</code> values.
     * 
     * <p>The line must contain at least three elements with the symbol in the third element.
     * 
     * @param inData a <code>CSVQuantum</code> value containing an element of data from the CSV file
     * @return an <code>Instrument</code> value
     * @throws CoreException if an error occurs parsing the symbol string
     * @throws ArrayIndexOutOfBoundsException if the given data does not contain symbol information at the expected position
     */
    protected Instrument guessInstrument(CSVQuantum inData)
            throws CoreException
    {
        String symbol = guessString(inData,
                                    2);
        if(symbol == null) {
            return null;
        }
        Instrument instrument = ClientManager.getInstance().resolveSymbol(symbol);
        if(instrument != null) {
            return instrument;
        }
        if(symbol.contains(":")) { //$NON-NLS-1$
            // assume the symbol contains a CFI (ISO10962) code
            String[] chunks = symbol.split(":"); //$NON-NLS-1$
            if(chunks.length != 2) {
                // unknown symbol format
                throw new CoreException(new I18NBoundMessage1P(UNKNOWN_SYMBOL_FORMAT,
                                                               symbol));
            }
            String cfiCodeChunk = chunks[0].toUpperCase();
            String symbolChunk = chunks[1];
            if(UNSUPPORTED_CFI_CODES.contains(cfiCodeChunk)) {
                // CFI code valid but not supported by METC
                throw new CoreException(new I18NBoundMessage3P(UNSUPPORTED_CFI_CODE,
                                                               symbol,
                                                               cfiCodeChunk,
                                                               SUPPORTED_CFI_CODES.toString()));
            }
            if(!SUPPORTED_CFI_CODES.contains(cfiCodeChunk)) {
                // CFI code invalid
                throw new CoreException(new I18NBoundMessage3P(INVALID_CFI_CODE,
                                                               symbol,
                                                               cfiCodeChunk,
                                                               SUPPORTED_CFI_CODES.toString()));
            }
            if(cfiCodeChunk.equals("E")) { //$NON-NLS-1$
                return new Equity(symbolChunk);
            } else if(cfiCodeChunk.equals("O")) { //$NON-NLS-1$
                // this must be an option, assume it's in OSI format
                try {
                    return OptionUtils.getOsiOptionFromString(symbolChunk);
                } catch (IllegalArgumentException e) {
                    // the option is not in OSI format
                    throw new CoreException(new I18NBoundMessage2P(NOT_OSI_COMPLIANT,
                                                                   symbol,
                                                                   symbolChunk)); 
                }
            }
            // this is a programming mistake - the code is alleged to be supported (contained by SUPPORTED_CFI_CODES)
            //  but is neither E nor O.  therefore, someone added a code to SUPPORTED_CFI_CODES but did not add a new if clause
            throw new UnsupportedOperationException();
        }
        // the symbol does not contain ":", therefore we can assume it's a symbol on its own
        // we cannot assume the symbol is an Equity, so, first try it on as an Option, but don't get discouraged if it doesn't work
        try {
            return OptionUtils.getOsiOptionFromString(symbol);
        } catch (IllegalArgumentException e) {
            // s'ok, this just must be an equity - note, this is a limiting assumption, when we activate additional classes, this will need to be expanded
        }
        return new Equity(symbol);
    }
    /**
     * Guesses the quote type from the data line.
     * 
     * <p>A <code>QuoteAction</code> indicates if the corresponding quote is an add (new quote) or if it's
     * a replacement for an existing quote.  All top-of-book quotes should be of type {@link QuoteAction#ADD}.
     * Depth-of-book quotes may of any type.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>QuoteAction</code> value or <code>null</code>
     */
    protected QuoteAction guessQuoteAction(CSVQuantum inData)
            throws CoreException
    {
        return QuoteAction.ADD;
    }
    /**
     * Guesses the exchange from the data line.
     *
     * <p>The exchange is assumed to be the fifth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    protected String guessExchange(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           4);
    }
    /**
     * Guesses the event timestamp from the data line.
     * 
     * <p>Note that the event timestamp is the time the event carries.  This is <em>not necessarily</em>
     * the same time as when the event occurred, i.e., it is not the exchange timestamp.
     *
     * <p>The event timestamp is assumed to be the second element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>Date</code> value containing the timestamp for the event or <code>null</code>
     * @throws CoreException if the event could not be parsed
     */
    protected Date guessEventTimestamp(CSVQuantum inData)
            throws CoreException
    {
        return guessDate(inData,
                         1);
    }
    /**
     * Guesses the close exchange from the given data line.
     * 
     * <p>The close exchange is assumed to be the seventeenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a close exchange
     */
    protected String guessCloseExchange(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          16);
    }
    /**
     * Guesses the low exchange from the given data line.
     * 
     * <p>The low exchange is assumed to be the sixteenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a low exchange
     */
    protected String guessLowExchange(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          15);
    }
    /**
     * Guesses the high exchange from the given data line.
     * 
     * <p>The high exchange is assumed to be the fifteenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a high exchange
     */
    protected String guessHighExchange(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          14);
    }
    /**
     * Guesses the open exchange from the given data line.
     * 
     * <p>The open exchange is assumed to be the fourteenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as an open exchange
     */
    protected String guessOpenExchange(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          13);
    }
    /**
     * Guesses the previous trade low time from the given data line.
     * 
     * <p>The trade low time is assumed to be the thirteenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a trade low time
     */
    protected String guessTradeLowTime(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          12);
    }
    /**
     * Guesses the previous trade high time from the given data line.
     * 
     * <p>The trade high time is assumed to be the twelfth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a trade high time
     */
    protected String guessTradeHighTime(CSVQuantum inData)
            throws CoreException
    {
       return guessString(inData,
                          11); 
    }
    /**
     * Guesses the previous close date from the given data line.
     * 
     * <p>The previous close date is assumed to be the eleventh element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a close close date
     */
    protected String guessPreviousCloseDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           10);
    }
    /**
     * Guesses the low price from the given data line.
     * 
     * <p>The low price is assumed to be the sixth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a low price
     */
    protected BigDecimal guessLowPrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               5);
    }
    /**
     * Guesses the high price from the given data line.
     * 
     * <p>The high price is assumed to be the fifth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a high price
     */
    protected BigDecimal guessHighPrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               4);
    }
    /**
     * Guesses the close price from the given data line.
     * 
     * <p>The close price is assumed to be the seventh element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a close price
     */
    protected BigDecimal guessClosePrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               6);
    }
    /**
     * Guesses the volume from the given data line. 
     *
     * <p>The volume is assumed to be the ninth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a volume
     */
    protected BigDecimal guessVolume(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               8);
    }
    /**
     * Guesses the previous close price from the given data line. 
     *
     * <p>The previous close price is assumed to be the eighth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a close price
     */
    protected BigDecimal guessPreviousClosePrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               7);
    }
    /**
     * Guesses the open price from the given data line. 
     *
     * <p>The open price is assumed to be the fourth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as an open price
     */
    protected BigDecimal guessOpenPrice(CSVQuantum inData)
            throws CoreException
    {
        return guessBigDecimal(inData,
                               3);
    }
    /**
     * Guesses the close date from the given data line.
     * 
     * <p>The close date is assumed to be the tenth element in the line.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @return a <code>String</code> value or <code>null</code>
     * @throws CoreException if the given data cannot be interpreted as a close date
     */
    protected String guessCloseDate(CSVQuantum inData)
            throws CoreException
    {
        return guessString(inData,
                           9);
    }
    /**
     * Interprets the given <code>String</code> as a <code>String</code> value.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inIndex an <code>int</code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    protected String guessString(CSVQuantum inData,
                                 int inIndex)
    {
        if(inData == null) {
            return null;
        }
        if(inIndex >= inData.getLine().length) {
            return null;
        }
        return StringUtils.trimToNull(inData.getLine()[inIndex]);
    }
    /**
     * Interprets the given <code>String</code> as a <code>Date</code> value.
     * 
     * <p>Note that this value is not interpreted exactly as a <code>Date</code>.  The value is interpreted
     * as a long, and then transformed to a <code>Date</code>.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inIndex an <code>int</code> value
     * @return a <code>Date</code> value or <code>null</code>
     * @throws CoreException if the value cannot be interpreted as a <code>Date</code>
     */
    protected Date guessDate(CSVQuantum inData,
                             int inIndex)
            throws CoreException
    {
        String dataChunk = guessString(inData,
                                       inIndex);
        if(dataChunk == null) {
            return null;
        }
        try {
            return new Date(Long.parseLong(dataChunk));
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(CANNOT_GUESS_DATE,
                                                           dataChunk));
        }
    }
    /**
     * Interprets the given <code>String</code> as a <code>BigDecimal</code> value.
     *
     * @param inData a <code>CSVQuantum</code> value
     * @param inIndex an <code>int</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code>
     * @throws CoreException if the value cannot be interpreted as a <code>BigDecimal</code>
     */
    protected BigDecimal guessBigDecimal(CSVQuantum inData,
                                         int inIndex)
            throws CoreException
    {
        String dataChunk = guessString(inData,
                                       inIndex);
        if(dataChunk == null) {
            return null;
        }
        try {
            return new BigDecimal(dataChunk);
        } catch (Exception e) {
            throw new CoreException(e,
                                    new I18NBoundMessage1P(CANNOT_GUESS_BIG_DECIMAL,
                                                           dataChunk));
        }
    }
    /**
     * used to uniquely identify events
     */
    protected static final AtomicLong counter = new AtomicLong(0);
    /**
     * CFI codes of supported instruments
     */
    protected static final Set<String> SUPPORTED_CFI_CODES = new HashSet<String>(Arrays.asList(new String[] { "E","O" } )); //$NON-NLS-1$ //$NON-NLS-2$
    /**
     * valid but unsupported CFI codes
     */
    protected static final Set<String> UNSUPPORTED_CFI_CODES = new HashSet<String>(Arrays.asList(new String[] { "D","R","F","M" } )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    /**
     * default fields required for quotes
     */
    protected static final Set<Integer> requiredQuoteFields = new HashSet<Integer>(Arrays.asList(new Integer[] { 0,1,2,3,4,5,6 }));
    /**
     * default fields required for trades
     */
    protected static final Set<Integer> requiredTradeFields = new HashSet<Integer>(Arrays.asList(new Integer[] { 0,1,2,3,4,5,6 }));
    /**
     * default fields required for dividends
     */
    protected static final Set<Integer> requiredDividendFields = new HashSet<Integer>(Arrays.asList(new Integer[] { 0,1,2,3,4,5,6,7,8 }));
    /**
     * default fields required for market stat events
     */
    protected static final Set<Integer> requiredMarketstatFields = new HashSet<Integer>(Arrays.asList(new Integer[] { 0,1,2 }));
    /**
     * The event type of a CSV event.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.0
     */
    public static enum EventType
    {
        BID,
        ASK,
        TRADE,
        DIVIDEND,
        STAT;
    }
}
