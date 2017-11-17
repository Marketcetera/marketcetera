package org.marketcetera.marketdata.rpc;

import java.util.List;
import java.util.Optional;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.AuctionType;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasTimestamps;
import org.marketcetera.event.ImbalanceEvent;
import org.marketcetera.event.ImbalanceType;
import org.marketcetera.event.InstrumentStatus;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.MarketStatus;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.DepthOfBookEventBuilder;
import org.marketcetera.event.impl.DividendEventBuilder;
import org.marketcetera.event.impl.ImbalanceEventBuilder;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.OptionEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TopOfBookEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataProviderStatus;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trading.rpc.TradeRpcUtil;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides RPC utilities for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcUtil
{
    /**
     * Get the event from the given RPC event value.
     *
     * @param inRpcEvent a <code>MarketDataTypesRpc.EventHolder</code> value
     * @return an <code>Optional&lt;? extends Event&gt;</code> value
     */
    public static Optional<? extends Event> getEvent(MarketDataTypesRpc.EventHolder inRpcEvent)
    {
        if(inRpcEvent == null) {
            return Optional.empty();
        }
        if(inRpcEvent.hasDepthOfBookEvent()) {
            return getDepthOfBookEvent(inRpcEvent.getDepthOfBookEvent());
        } else if(inRpcEvent.hasDividendEvent()) {
            return getDividendEvent(inRpcEvent.getDividendEvent());
        } else if(inRpcEvent.hasImbalanceEvent()) {
            return getImbalanceEvent(inRpcEvent.getImbalanceEvent());
        } else if(inRpcEvent.hasLogEvent()) {
            return getLogEvent(inRpcEvent.getLogEvent());
        } else if(inRpcEvent.hasMarketstatEvent()) {
            return getMarketstatEvent(inRpcEvent.getMarketstatEvent());
        } else if(inRpcEvent.hasQuoteEvent()) {
            return getQuoteEvent(inRpcEvent.getQuoteEvent());
        } else if(inRpcEvent.hasTopOfBookEvent()) {
            return getTopOfBookEvent(inRpcEvent.getTopOfBookEvent());
        } else if(inRpcEvent.hasTradeEvent()) {
            return getTradeEvent(inRpcEvent.getTradeEvent());
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Get the RPC event holder from the given event.
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Optional&lt;MarketDataTypeRpc.EventHolder&gt;</code> value
     * @throws UnsupportedOperationException if the given event is not of a supported type
     */
    public static Optional<MarketDataTypesRpc.EventHolder> getRpcEventHolder(Event inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.EventHolder.Builder builder = MarketDataTypesRpc.EventHolder.newBuilder();
        if(inEvent instanceof TradeEvent) {
            getRpcTradeEvent((TradeEvent)inEvent).ifPresent(value->builder.setTradeEvent(value));
        } else if(inEvent instanceof QuoteEvent) {
            getRpcQuoteEvent((QuoteEvent)inEvent).ifPresent(value->builder.setQuoteEvent(value));
        } else if(inEvent instanceof DividendEvent) {
            getRpcDividendEvent((DividendEvent)inEvent).ifPresent(value->builder.setDividendEvent(value));
        } else if(inEvent instanceof LogEvent) {
            getRpcLogEvent((LogEvent)inEvent).ifPresent(value->builder.setLogEvent(value));
        } else if(inEvent instanceof ImbalanceEvent) {
            getRpcImbalanceEvent((ImbalanceEvent)inEvent).ifPresent(value->builder.setImbalanceEvent(value));
        } else if(inEvent instanceof MarketstatEvent) {
            getRpcMarketstatEvent((MarketstatEvent)inEvent).ifPresent(value->builder.setMarketstatEvent(value));
        } else if(inEvent instanceof TopOfBookEvent) {
            getRpcTopOfBookEvent((TopOfBookEvent)inEvent).ifPresent(value->builder.setTopOfBookEvent(value));
        } else if(inEvent instanceof DepthOfBookEvent) {
            getRpcDepthOfBookEvent((DepthOfBookEvent)inEvent).ifPresent(value->builder.setDepthOfBookEvent(value));
        }
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC option attributes from the given option event value.
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.OptionAttributes&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.OptionAttributes> getRpcOptionAttributes(Event inEvent)
    {
        if(inEvent == null || !(inEvent instanceof OptionEvent)) {
            return Optional.empty();
        }
        OptionEvent optionEvent = (OptionEvent)inEvent;
        MarketDataTypesRpc.OptionAttributes.Builder builder = MarketDataTypesRpc.OptionAttributes.newBuilder();
        TradeRpcUtil.getRpcInstrument(optionEvent.getUnderlyingInstrument()).ifPresent(value->builder.setUnderlyingInstrument(value));
        getRpcExpirationType(optionEvent.getExpirationType()).ifPresent(value->builder.setExpirationType(value));
        BaseRpcUtil.getRpcQty(optionEvent.getMultiplier()).ifPresent(value->builder.setMultiplier(value));
        builder.setHasDeliverable(optionEvent.hasDeliverable());
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC expiration type from the given expiration type value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value
     * @return an <code>Optional&lt;MarketDataTypes.ExpirationType&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.ExpirationType> getRpcExpirationType(ExpirationType inExpirationType)
    {
        if(inExpirationType == null) {
            return Optional.empty();
        }
        return Optional.of(MarketDataTypesRpc.ExpirationType.values()[(inExpirationType.ordinal())]);
    }
    /**
     * Get the expiration type from the given RPC expiration type value.
     *
     * @param inExpirationType a <code>MarketDataTypes.ExpirationType</code> value
     * @return an <code>Optional&lt;ExpirationType&gt;</code> value
     */
    public static Optional<ExpirationType> getExpirationType(MarketDataTypesRpc.ExpirationType inExpirationType)
    {
        if(inExpirationType == null) {
            return Optional.empty();
        }
        return Optional.of(ExpirationType.values()[(inExpirationType.ordinal())]);
    }
    /**
     * Get the RPC imbalance event from the given value.
     *
     * @param inImbalanceEvent an <code>ImbalanceEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.ImbalanceEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.ImbalanceEvent> getRpcImbalanceEvent(ImbalanceEvent inImbalanceEvent)
    {
        if(inImbalanceEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.ImbalanceEvent.Builder builder = MarketDataTypesRpc.ImbalanceEvent.newBuilder();
        getRpcAuctionType(inImbalanceEvent.getAuctionType()).ifPresent(value->builder.setAuctionType(value));
        getRpcEvent(inImbalanceEvent).ifPresent(value->builder.setEvent(value));
        getRpcEventType(inImbalanceEvent.getEventType()).ifPresent(eventType->builder.setEventType(eventType));
        BaseRpcUtil.getStringValue(inImbalanceEvent.getExchange()).ifPresent(exchange->builder.setExchange(exchange));
        BaseRpcUtil.getRpcQty(inImbalanceEvent.getFarPrice()).ifPresent(value->builder.setFarPrice(value));
        getRpcImbalanceType(inImbalanceEvent.getImbalanceType()).ifPresent(value->builder.setImbalanceType(value));
        BaseRpcUtil.getRpcQty(inImbalanceEvent.getImbalanceVolume()).ifPresent(value->builder.setImbalanceVolume(value));
        TradeRpcUtil.getRpcInstrument(inImbalanceEvent.getInstrument()).ifPresent(instrument->builder.setInstrument(instrument));
        getRpcOptionAttributes(inImbalanceEvent).ifPresent(value->builder.setOptionAttributes(value));
        getRpcInstrumentStatus(inImbalanceEvent.getInstrumentStatus()).ifPresent(value->builder.setInstrumentStatus(value));
        builder.setIsShortSaleRestricted(inImbalanceEvent.isShortSaleRestricted());
        getRpcMarketStatus(inImbalanceEvent.getMarketStatus()).ifPresent(value->builder.setMarketStatus(value));
        BaseRpcUtil.getRpcQty(inImbalanceEvent.getNearPrice()).ifPresent(value->builder.setNearPrice(value));
        BaseRpcUtil.getRpcQty(inImbalanceEvent.getPairedVolume()).ifPresent(value->builder.setPairedVolume(value));
        BaseRpcUtil.getRpcQty(inImbalanceEvent.getReferencePrice()).ifPresent(value->builder.setReferencePrice(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC market status from the given value.
     *
     * @param inMarketStatus a <code>MarketStatus</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.MarketStatus&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.MarketStatus> getRpcMarketStatus(MarketStatus inMarketStatus)
    {
        if(inMarketStatus == null) {
            return Optional.empty();
        }
        return Optional.of(MarketDataTypesRpc.MarketStatus.values()[inMarketStatus.ordinal()]);
    }
    /**
     * Get the market status from the given RPC value.
     *
     * @param inMarketStatus a <code>MarketDataTypesRpc.MarketStatus</code> value
     * @return an <code>Optional&lt;MarketStatus&gt;</code> value
     */
    public static Optional<MarketStatus> getMarketStatus(MarketDataTypesRpc.MarketStatus inMarketStatus)
    {
        if(inMarketStatus == null) {
            return Optional.empty();
        }
        return Optional.of(MarketStatus.values()[inMarketStatus.ordinal()]);
    }
    /**
     * Get the RPC instrument status from the given value.
     *
     * @param inInstrumentStatus an <code>InstrumentStatus</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.InstrumentStatus&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.InstrumentStatus> getRpcInstrumentStatus(InstrumentStatus inInstrumentStatus)
    {
        if(inInstrumentStatus == null) {
            return Optional.empty();
        }
        return Optional.of(MarketDataTypesRpc.InstrumentStatus.values()[inInstrumentStatus.ordinal()]);
    }
    /**
     * Get the instrument status from the given RPC value.
     *
     * @param inInstrumentStatus a <code>MarketDataTypesRpc.InstrumentStatus</code> value
     * @return an <code>Optional&lt;InstrumentStatus&gt;</code> value
     */
    public static Optional<InstrumentStatus> getInstrumentStatus(MarketDataTypesRpc.InstrumentStatus inInstrumentStatus)
    {
        if(inInstrumentStatus == null) {
            return Optional.empty();
        }
        return Optional.of(InstrumentStatus.values()[inInstrumentStatus.ordinal()]);
    }
    /**
     * Get the RPC imbalance type from the given value.
     *
     * @param inImbalanceType an <code>ImbalanceType</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.ImbalanceType&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.ImbalanceType> getRpcImbalanceType(ImbalanceType inImbalanceType)
    {
        if(inImbalanceType == null) {
            return Optional.empty();
        }
        return Optional.of(MarketDataTypesRpc.ImbalanceType.values()[inImbalanceType.ordinal()]);
    }
    /**
     * Get the imbalance type from the given RPC value.
     *
     * @param inImbalanceType a <code>MarketDataTypesRpc.ImbalanceType</code> value
     * @return an <code>Optional&lt;ImbalanceType&gt;</code> value
     */
    public static Optional<ImbalanceType> getImbalanceType(MarketDataTypesRpc.ImbalanceType inImbalanceType)
    {
        if(inImbalanceType == null) {
            return Optional.empty();
        }
        return Optional.of(ImbalanceType.values()[inImbalanceType.ordinal()]);
    }
    /**
     * Get the RPC auction type from the given value.
     *
     * @param inAuctionType an <code>AuctionType</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.AuctionType&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.AuctionType> getRpcAuctionType(AuctionType inAuctionType)
    {
        if(inAuctionType == null) {
            return Optional.empty();
        }
        return Optional.of(MarketDataTypesRpc.AuctionType.values()[inAuctionType.ordinal()]);
    }
    /**
     * Get the auction type from the given RPC value.
     *
     * @param inAuctionType a <code>MarketDataTypesRpc.AuctionType</code> value
     * @return an <code>Optional&lt;AuctionType&gt;</code> value
     */
    public static Optional<AuctionType> getAuctionType(MarketDataTypesRpc.AuctionType inAuctionType)
    {
        if(inAuctionType == null) {
            return Optional.empty();
        }
        return Optional.of(AuctionType.values()[inAuctionType.ordinal()]);
    }
    /**
     * Get the log event from the given RPC event.
     *
     * @param inLogEvent a <code>MarketDataTypesRpc.LogEvent</code> value
     * @return an <code>Optional&lt;LogEvent&gt;</code> value
     */
    public static Optional<LogEvent> getLogEvent(MarketDataTypesRpc.LogEvent inLogEvent)
    {
        if(inLogEvent == null) {
            return Optional.empty();
        }
        final LogEventBuilder builder;
        switch(inLogEvent.getLogEventLevel()) {
            case DEBUG_LOG_EVENT_LEVEL:
                builder = LogEventBuilder.debug();
                break;
            case ERROR_LOG_EVENT_LEVEL:
                builder = LogEventBuilder.error();
                break;
            case INFO_LOG_EVENT_LEVEL:
                builder = LogEventBuilder.info();
                break;
            case WARN_LOG_EVENT_LEVEL:
                builder = LogEventBuilder.warn();
                break;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(String.valueOf(inLogEvent.getLogEventLevel()));
        }
        BaseRpcUtil.getObject(inLogEvent.getException()).ifPresent(value->builder.withException((Throwable)value));
        builder.withMessage(Messages.MESSAGE_HOLDER,
                            inLogEvent.getMessage());
        builder.withMessageId(inLogEvent.getEvent().getMessageId());
        builder.withRequestId(inLogEvent.getEvent().getRequestId());
        BaseRpcUtil.getStringValue(inLogEvent.getEvent().getSource()).ifPresent(value->builder.withSource(value));
        BaseRpcUtil.getDateValue(inLogEvent.getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        return Optional.of(builder.create());
    }
    /**
     * Get the RPC log event from the given value.
     *
     * @param inLogEvent a <code>LogEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.LogEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.LogEvent> getRpcLogEvent(LogEvent inLogEvent)
    {
        if(inLogEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.LogEvent.Builder builder = MarketDataTypesRpc.LogEvent.newBuilder();
        BaseRpcUtil.getRpcObject(inLogEvent.getException()).ifPresent(value->builder.setException(value));
        getRpcLogEventLevel(inLogEvent.getLevel()).ifPresent(value->builder.setLogEventLevel(value));
        BaseRpcUtil.getStringValue(inLogEvent.getMessage()).ifPresent(value->builder.setMessage(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC log event level for the given value.
     *
     * @param inLevel a <code>LogEventLevel</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.LogEventLevel&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.LogEventLevel> getRpcLogEventLevel(LogEventLevel inLevel)
    {
        if(inLevel == null) {
            return Optional.empty();
        }
        switch(inLevel) {
            case DEBUG:
                return Optional.of(MarketDataTypesRpc.LogEventLevel.DEBUG_LOG_EVENT_LEVEL);
            case ERROR:
                return Optional.of(MarketDataTypesRpc.LogEventLevel.ERROR_LOG_EVENT_LEVEL);
            case INFO:
                return Optional.of(MarketDataTypesRpc.LogEventLevel.INFO_LOG_EVENT_LEVEL);
            case WARN:
                return Optional.of(MarketDataTypesRpc.LogEventLevel.WARN_LOG_EVENT_LEVEL);
            default:
                throw new UnsupportedOperationException(inLevel.name());
        }
    }
    /**
     * Get the log event level for the given RPC value.
     *
     * @param inLevel a <code>MarketDataTypesRpc.LogEventLevel</code> value
     * @return an <code>Optional&lt;LogEventLevel&gt;</code> value
     */
    public static Optional<LogEventLevel> getLogEventLevel(MarketDataTypesRpc.LogEventLevel inLevel)
    {
        if(inLevel == null) {
            return Optional.empty();
        }
        switch(inLevel) {
            case DEBUG_LOG_EVENT_LEVEL:
                return Optional.of(LogEventLevel.DEBUG);
            case ERROR_LOG_EVENT_LEVEL:
                return Optional.of(LogEventLevel.ERROR);
            case INFO_LOG_EVENT_LEVEL:
                return Optional.of(LogEventLevel.INFO);
            case WARN_LOG_EVENT_LEVEL:
                return Optional.of(LogEventLevel.WARN);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inLevel.name());
        }
    }
    /**
     * Get the RPC dividend event from the given value.
     *
     * @param inDividendEvent a <code>DividendEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.DividendEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.DividendEvent> getRpcDividendEvent(DividendEvent inDividendEvent)
    {
        if(inDividendEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.DividendEvent.Builder builder = MarketDataTypesRpc.DividendEvent.newBuilder();
        BaseRpcUtil.getRpcQty(inDividendEvent.getAmount()).ifPresent(value->builder.setAmount(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getCurrency()).ifPresent(value->builder.setCurrency(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getDeclareDate()).ifPresent(value->builder.setDeclareDate(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getExecutionDate()).ifPresent(value->builder.setExecutionDate(value));
        getRpcDividendFrequency(inDividendEvent.getFrequency()).ifPresent(value->builder.setFrequency(value));
        TradeRpcUtil.getRpcInstrument(inDividendEvent.getInstrument()).ifPresent(value->builder.setInstrument(value));
        getRpcOptionAttributes(inDividendEvent).ifPresent(value->builder.setOptionAttributes(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getPaymentDate()).ifPresent(value->builder.setPaymentDate(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getRecordDate()).ifPresent(value->builder.setRecordDate(value));
        getRpcDividendStatus(inDividendEvent.getStatus()).ifPresent(value->builder.setStatus(value));
        getRpcDividendType(inDividendEvent.getType()).ifPresent(value->builder.setType(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC dividend type from the given value.
     *
     * @param inType a <code>DividendType</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.DividendType&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.DividendType> getRpcDividendType(DividendType inType)
    {
        if(inType == null) {
            return Optional.empty();
        }
        switch(inType) {
            case CURRENT:
                return Optional.of(MarketDataTypesRpc.DividendType.CURRENT_DIVIDEND_TYPE);
            case FUTURE:
                return Optional.of(MarketDataTypesRpc.DividendType.FUTURE_DIVIDEND_TYPE);
            case SPECIAL:
                return Optional.of(MarketDataTypesRpc.DividendType.SPECIAL_DIVIDEND_TYPE);
            case UNKNOWN:
                return Optional.of(MarketDataTypesRpc.DividendType.UNKNOWN_DIVIDEND_TYPE);
            default:
                throw new UnsupportedOperationException(inType.name());
        }
    }
    /**
     * Get the dividend type from the given RPC value.
     *
     * @param inType a <code>MarketDataTypesRpc.DividendType</code> value
     * @return an <code>Optional&lt;DividendType&gt;</code> value
     */
    public static Optional<DividendType> getDividendType(MarketDataTypesRpc.DividendType inType)
    {
        if(inType == null) {
            return Optional.empty();
        }
        switch(inType) {
            case CURRENT_DIVIDEND_TYPE:
                return Optional.of(DividendType.CURRENT);
            case FUTURE_DIVIDEND_TYPE:
                return Optional.of(DividendType.FUTURE);
            case SPECIAL_DIVIDEND_TYPE:
                return Optional.of(DividendType.SPECIAL);
            case UNKNOWN_DIVIDEND_TYPE:
                return Optional.of(DividendType.UNKNOWN);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inType.name());
        }
    }
    /**
     * Get the RPC dividend status from the given value.
     *
     * @param inStatus a <code>DividendStatus</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.DividendStatus&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.DividendStatus> getRpcDividendStatus(DividendStatus inStatus)
    {
        if(inStatus == null) {
            return Optional.empty();
        }
        switch(inStatus) {
            case OFFICIAL:
                return Optional.of(MarketDataTypesRpc.DividendStatus.OFFICIAL_DIVIDEND_STATUS);
            case UNKNOWN:
                return Optional.of(MarketDataTypesRpc.DividendStatus.UNKNOWN_DIVIDEND_STATUS);
            case UNOFFICIAL:
                return Optional.of(MarketDataTypesRpc.DividendStatus.UNOFFICIAL_DIVIDEND_STATUS);
            default:
                throw new UnsupportedOperationException(inStatus.name());
        }
    }
    /**
     * Get the dividend status value from the given RPC value.
     *
     * @param inStatus a <code>MarketDataTypesRpc.DividendStatus</code> value
     * @return an <code>Optional&lt;DividendStatus&gt;</code> value
     */
    public static Optional<DividendStatus> getDividendStatus(MarketDataTypesRpc.DividendStatus inStatus)
    {
        if(inStatus == null) {
            return Optional.empty();
        }
        switch(inStatus) {
            case OFFICIAL_DIVIDEND_STATUS:
                return Optional.of(DividendStatus.OFFICIAL);
            case UNKNOWN_DIVIDEND_STATUS:
                return Optional.of(DividendStatus.UNKNOWN);
            case UNOFFICIAL_DIVIDEND_STATUS:
                return Optional.of(DividendStatus.UNOFFICIAL);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inStatus.name());
        }
    }
    /**
     * Get the RPC dividend frequency from the given value.
     *
     * @param inFrequency a <code>DividendFrequency</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.DividendFrequency&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.DividendFrequency> getRpcDividendFrequency(DividendFrequency inFrequency)
    {
        if(inFrequency == null) {
            return Optional.empty();
        }
        switch(inFrequency) {
            case ANNUALLY:
                return Optional.of(MarketDataTypesRpc.DividendFrequency.ANNUALLY_DIVIDEND_FREQUENCY);
            case MONTHLY:
                return Optional.of(MarketDataTypesRpc.DividendFrequency.MONTHLY_DIVIDEND_FREQUENCY);
            case OTHER:
                return Optional.of(MarketDataTypesRpc.DividendFrequency.OTHER_DIVIDEND_FREQUENCY);
            case QUARTERLY:
                return Optional.of(MarketDataTypesRpc.DividendFrequency.QUARTERLY_DIVIDEND_FREQUENCY);
            case SEMI_ANNUALLY:
                return Optional.of(MarketDataTypesRpc.DividendFrequency.SEMI_ANNUALLY_DIVIDEND_FREQUENCY);
            default:
                throw new UnsupportedOperationException(inFrequency.name());
        }
    }
    /**
     * Get the dividend frequency from the given RPC value.
     *
     * @param inFrequency a <code>MarketDataTypesRpc.DividendFrequency</code> value
     * @return an <code>Optional&lt;DividendFrequency&gt;</code> value
     */
    public static Optional<DividendFrequency> getDividendFrequency(MarketDataTypesRpc.DividendFrequency inFrequency)
    {
        if(inFrequency == null) {
            return Optional.empty();
        }
        switch(inFrequency) {
            case ANNUALLY_DIVIDEND_FREQUENCY:
                return Optional.of(DividendFrequency.ANNUALLY);
            case MONTHLY_DIVIDEND_FREQUENCY:
                return Optional.of(DividendFrequency.MONTHLY);
            case OTHER_DIVIDEND_FREQUENCY:
                return Optional.of(DividendFrequency.OTHER);
            case QUARTERLY_DIVIDEND_FREQUENCY:
                return Optional.of(DividendFrequency.QUARTERLY);
            case SEMI_ANNUALLY_DIVIDEND_FREQUENCY:
                return Optional.of(DividendFrequency.SEMI_ANNUALLY);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inFrequency.name());
        }
    }
    /**
     * Get the RPC depth-of-book event from the given depth-of-book value.
     *
     * @param inDepthOfBookEvent a <code>DepthOfBookEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.DepthOfBookEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.DepthOfBookEvent> getRpcDepthOfBookEvent(DepthOfBookEvent inDepthOfBookEvent)
    {
        if(inDepthOfBookEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.DepthOfBookEvent.Builder builder = MarketDataTypesRpc.DepthOfBookEvent.newBuilder();
        TradeRpcUtil.getRpcInstrument(inDepthOfBookEvent.getInstrument()).ifPresent(value->builder.setInstrument(value));
        inDepthOfBookEvent.getBids().stream().forEach(bid->getRpcQuoteEvent(bid).ifPresent(value->builder.addBids(value)));
        inDepthOfBookEvent.getAsks().stream().forEach(ask->getRpcQuoteEvent(ask).ifPresent(value->builder.addAsks(value)));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC top-of-book event from the given top-of-book value.
     *
     * @param inTopOfBookEvent a <code>TopOfBookEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.TopOfBookEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.TopOfBookEvent> getRpcTopOfBookEvent(TopOfBookEvent inTopOfBookEvent)
    {
        if(inTopOfBookEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.TopOfBookEvent.Builder builder = MarketDataTypesRpc.TopOfBookEvent.newBuilder();
        TradeRpcUtil.getRpcInstrument(inTopOfBookEvent.getInstrument()).ifPresent(value->builder.setInstrument(value));
        getRpcQuoteEvent(inTopOfBookEvent.getBid()).ifPresent(value->builder.setBid(value));
        getRpcQuoteEvent(inTopOfBookEvent.getAsk()).ifPresent(value->builder.setAsk(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC quote event from the given value.
     *
     * @param inQuoteEvent a <code>QuoteEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.QuoteEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.QuoteEvent> getRpcQuoteEvent(QuoteEvent inQuoteEvent)
    {
        if(inQuoteEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.QuoteEvent.Builder builder = MarketDataTypesRpc.QuoteEvent.newBuilder();
        builder.setCount(inQuoteEvent.getCount());
        builder.setIsBid(inQuoteEvent instanceof BidEvent);
        builder.setIsEmpty(inQuoteEvent.isEmpty());
        builder.setLevel(inQuoteEvent.getLevel());
        getRpcMarketDataEvent(inQuoteEvent).ifPresent(value->builder.setMarketDataEvent(value));
        getRpcQuoteAction(inQuoteEvent.getAction()).ifPresent(value->builder.setQuoteAction(value));
        BaseRpcUtil.getTimestampValue(inQuoteEvent.getQuoteDate()).ifPresent(value->builder.setQuoteDate(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC quote action from the given value.
     *
     * @param inAction a <code>QuoteAction</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.QuoteAction&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.QuoteAction> getRpcQuoteAction(QuoteAction inAction)
    {
        if(inAction == null) {
            return Optional.empty();
        }
        switch(inAction) {
            case ADD:
                return Optional.of(MarketDataTypesRpc.QuoteAction.ADD_QUOTE_ACTION);
            case CHANGE:
                return Optional.of(MarketDataTypesRpc.QuoteAction.CHANGE_QUOTE_ACTION);
            case DELETE:
                return Optional.of(MarketDataTypesRpc.QuoteAction.DELETE_QUOTE_ACTION);
            default:
                throw new UnsupportedOperationException(inAction.name());
        }
    }
    /**
     * Get the quote action from the given RPC value.
     *
     * @param inAction a <code>MarketDataTypesRpc.QuoteAction</code> value
     * @return an <code>Optional&lt;QuoteAction&gt;</code> value
     */
    public static Optional<QuoteAction> getQuoteAction(MarketDataTypesRpc.QuoteAction inAction)
    {
        if(inAction == null) {
            return Optional.empty();
        }
        switch(inAction) {
            case ADD_QUOTE_ACTION:
                return Optional.of(QuoteAction.ADD);
            case CHANGE_QUOTE_ACTION:
                return Optional.of(QuoteAction.CHANGE);
            case DELETE_QUOTE_ACTION:
                return Optional.of(QuoteAction.DELETE);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inAction.name());
        }
    }
    /**
     * Get the imbalance event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.ImbalanceEvent</code> value
     * @return an <code>Optional&lt;ImbalanceEvent&gt;</code> value
     */
    public static Optional<ImbalanceEvent> getImbalanceEvent(MarketDataTypesRpc.ImbalanceEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        ImbalanceEventBuilder builder = ImbalanceEventBuilder.Imbalance(instrument);
        builder.withMessageId(inEvent.getEvent().getMessageId());
        BaseRpcUtil.getStringValue(inEvent.getEvent().getProvider()).ifPresent(value->builder.withProvider(value));
        BaseRpcUtil.getStringValue(inEvent.getEvent().getSource()).ifPresent(value->builder.withSource(value));
        BaseRpcUtil.getDateValue(inEvent.getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        getAuctionType(inEvent.getAuctionType()).ifPresent(value->builder.withAuctionType(value));
        getEventType(inEvent.getEventType()).ifPresent(value->builder.withEventType(value));
        BaseRpcUtil.getStringValue(inEvent.getExchange()).ifPresent(value->builder.withExchange(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getFarPrice()).ifPresent(value->builder.withFarPrice(value));
        getImbalanceType(inEvent.getImbalanceType()).ifPresent(value->builder.withImbalanceType(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getImbalanceVolume()).ifPresent(value->builder.withImbalanceVolume(value));
        getInstrumentStatus(inEvent.getInstrumentStatus()).ifPresent(value->builder.withInstrumentStatus(value));
        builder.withShortSaleRestricted(inEvent.getIsShortSaleRestricted());
        getMarketStatus(inEvent.getMarketStatus()).ifPresent(value->builder.withMarketStatus(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getNearPrice()).ifPresent(value->builder.withNearPrice(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getPairedVolume()).ifPresent(value->builder.withPairedVolume(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getReferencePrice()).ifPresent(value->builder.withReferencePrice(value));
        if(inEvent.hasOptionAttributes()) {
            setOptionAttributes(inEvent.getOptionAttributes(),
                                builder);
        }
        return Optional.of(builder.create());
    }
    /**
     * Get the marketstat event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.MarketstatEvent</code> value
     * @return an <code>Optional&lt;MarketstatEvent&gt;</code> value
     */
    public static Optional<MarketstatEvent> getMarketstatEvent(MarketDataTypesRpc.MarketstatEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        MarketstatEventBuilder builder = MarketstatEventBuilder.marketstat(instrument);
        // event attributes
        builder.withMessageId(inEvent.getEvent().getMessageId());
        BaseRpcUtil.getStringValue(inEvent.getEvent().getProvider()).ifPresent(value->builder.withProvider(value));
        BaseRpcUtil.getStringValue(inEvent.getEvent().getSource()).ifPresent(value->builder.withSource(value));
        BaseRpcUtil.getDateValue(inEvent.getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        // marketstat attributes
        BaseRpcUtil.getScaledQuantity(inEvent.getClose()).ifPresent(value->builder.withClosePrice(value));
        BaseRpcUtil.getStringValue(inEvent.getCloseDate()).ifPresent(value->builder.withCloseDate(value));
        BaseRpcUtil.getStringValue(inEvent.getCloseExchange()).ifPresent(value->builder.withCloseExchange(value));
        getEventType(inEvent.getEventType()).ifPresent(value->builder.withEventType(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getHigh()).ifPresent(value->builder.withHighPrice(value));
        BaseRpcUtil.getStringValue(inEvent.getHighExchange()).ifPresent(value->builder.withHighExchange(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getLow()).ifPresent(value->builder.withLowPrice(value));
        BaseRpcUtil.getStringValue(inEvent.getLowExchange()).ifPresent(value->builder.withLowExchange(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getOpen()).ifPresent(value->builder.withOpenPrice(value));
        BaseRpcUtil.getStringValue(inEvent.getOpenExchange()).ifPresent(value->builder.withOpenExchange(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getPreviousClose()).ifPresent(value->builder.withPreviousClosePrice(value));
        BaseRpcUtil.getStringValue(inEvent.getPreviousCloseDate()).ifPresent(value->builder.withPreviousCloseDate(value));
        BaseRpcUtil.getStringValue(inEvent.getTradeHighTime()).ifPresent(value->builder.withTradeHighTime(value));
        BaseRpcUtil.getStringValue(inEvent.getTradeLowTime()).ifPresent(value->builder.withTradeLowTime(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getValue()).ifPresent(value->builder.withValue(value));
        BaseRpcUtil.getScaledQuantity(inEvent.getVolume()).ifPresent(value->builder.withVolume(value));
        if(inEvent.hasOptionAttributes()) {
            setOptionAttributes(inEvent.getOptionAttributes(),
                                builder);
        }
        return Optional.of(builder.create());
    }
    /**
     * Get the dividend event from the given RPC value.
     *
     * @param inDividendEvent a <code>DividendEvent</code> value
     * @return an <code>Optional&lt;DividendEvent&gt;</code> value
     */
    public static Optional<DividendEvent> getDividendEvent(MarketDataTypesRpc.DividendEvent inDividendEvent)
    {
        if(inDividendEvent == null) {
            return Optional.empty();
        }
        DividendEventBuilder builder = DividendEventBuilder.dividend();
        BaseRpcUtil.getScaledQuantity(inDividendEvent.getAmount()).ifPresent(value->builder.withAmount(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getCurrency()).ifPresent(value->builder.withCurrency(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getDeclareDate()).ifPresent(value->builder.withDeclareDate(value));
        TradeRpcUtil.getInstrument(inDividendEvent.getInstrument()).ifPresent(value->builder.withEquity((Equity)value));
        getEventType(inDividendEvent.getEventType()).ifPresent(value->builder.withEventType(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getExecutionDate()).ifPresent(value->builder.withExecutionDate(value));
        getDividendFrequency(inDividendEvent.getFrequency()).ifPresent(value->builder.withFrequency(value));
        builder.withMessageId(inDividendEvent.getEvent().getMessageId());
        BaseRpcUtil.getStringValue(inDividendEvent.getPaymentDate()).ifPresent(value->builder.withPaymentDate(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getRecordDate()).ifPresent(value->builder.withRecordDate(value));
        BaseRpcUtil.getStringValue(inDividendEvent.getEvent().getSource()).ifPresent(value->builder.withSource(value));
        getDividendStatus(inDividendEvent.getStatus()).ifPresent(value->builder.withStatus(value));
        BaseRpcUtil.getDateValue(inDividendEvent.getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        getDividendType(inDividendEvent.getType()).ifPresent(value->builder.withType(value));
        return Optional.of(builder.create());
    }
    /**
     * Get the top-of-book event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.TopOfBookEvent</code> value
     * @return an <code>Optional&lt;TopOfBookEvent&gt;</code> value
     */
    public static Optional<TopOfBookEvent> getTopOfBookEvent(MarketDataTypesRpc.TopOfBookEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        TopOfBookEventBuilder builder = TopOfBookEventBuilder.topOfBookEvent();
        builder.withInstrument(instrument);
        if(inEvent.hasAsk()) {
            getQuoteEvent(inEvent.getAsk()).ifPresent(value->builder.withAsk((AskEvent)value));
        }
        if(inEvent.hasBid()) {
            getQuoteEvent(inEvent.getBid()).ifPresent(value->builder.withBid((BidEvent)value));
        }
        return Optional.of(builder.create());
    }
    /**
     * Get the depth-of-book event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.DepthOfBookEvent</code> value
     * @return an <code>Optional&lt;DepthOfBookEvent&gt;</code> value
     */
    public static Optional<DepthOfBookEvent> getDepthOfBookEvent(MarketDataTypesRpc.DepthOfBookEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        DepthOfBookEventBuilder builder = DepthOfBookEventBuilder.depthOfBook();
        builder.withInstrument(instrument);
        List<AskEvent> asks = Lists.newArrayList();
        for(MarketDataTypesRpc.QuoteEvent ask : inEvent.getAsksList()) {
            getQuoteEvent(ask).ifPresent(value->asks.add((AskEvent)value));
        }
        builder.withAsks(asks);
        List<BidEvent> bids = Lists.newArrayList();
        for(MarketDataTypesRpc.QuoteEvent bid : inEvent.getBidsList()) {
            getQuoteEvent(bid).ifPresent(value->bids.add((BidEvent)value));
        }
        builder.withBids(bids);
        return Optional.of(builder.create());
    }
    /**
     * Get the quote event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.QuoteEvent</code> value
     * @return an <code>Optional&lt;QuoteEvent&gt;</code> value
     */
    public static Optional<? extends QuoteEvent> getQuoteEvent(MarketDataTypesRpc.QuoteEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getMarketDataEvent().getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        final QuoteEventBuilder<? extends QuoteEvent> builder;
        if(inEvent.getIsBid()) {
            builder = QuoteEventBuilder.bidEvent(instrument);
        } else {
            builder = QuoteEventBuilder.askEvent(instrument);
        }
        getQuoteAction(inEvent.getQuoteAction()).ifPresent(value->builder.withAction(value));
        builder.withCount(inEvent.getCount());
        getEventType(inEvent.getMarketDataEvent().getEventType()).ifPresent(value->builder.withEventType(value));
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getExchange()).ifPresent(value->builder.withExchange(value));
        builder.withLevel(inEvent.getLevel());
        builder.withMessageId(inEvent.getMarketDataEvent().getEvent().getMessageId());
        BaseRpcUtil.getScaledQuantity(inEvent.getMarketDataEvent().getPrice()).ifPresent(value->builder.withPrice(value));
        builder.withProcessedTimestamp(inEvent.getMarketDataEvent().getEventTimestamps().getProcessedTimestamp());
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getEvent().getProvider()).ifPresent(value->builder.withProvider(value));
        BaseRpcUtil.getDateValue(inEvent.getQuoteDate()).ifPresent(value->builder.withQuoteDate(value));
        builder.withReceivedTimestamp(inEvent.getMarketDataEvent().getEventTimestamps().getReceivedTimestamp());
        BaseRpcUtil.getScaledQuantity(inEvent.getMarketDataEvent().getSize()).ifPresent(value->builder.withSize(value));
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getEvent().getSource()).ifPresent(value->builder.withSource(value));
        BaseRpcUtil.getDateValue(inEvent.getMarketDataEvent().getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        if(inEvent.getMarketDataEvent().hasOptionAttributes()) {
            setOptionAttributes(inEvent.getMarketDataEvent().getOptionAttributes(),
                                builder);
        }
        return Optional.of(builder.create());
    }
    /**
     * Get the trade event from the given RPC value.
     *
     * @param inEvent a <code>MarketDataTypesRpc.TradeEvent</code> value
     * @return an <code>Optional&lt;TradeEvent&gt;</code> value
     */
    public static Optional<TradeEvent> getTradeEvent(MarketDataTypesRpc.TradeEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        Instrument instrument = TradeRpcUtil.getInstrument(inEvent.getMarketDataEvent().getInstrument()).orElse(null);
        if(instrument == null) {
            return Optional.empty();
        }
        TradeEventBuilder<TradeEvent> builder = TradeEventBuilder.tradeEvent(instrument);
        getEventType(inEvent.getMarketDataEvent().getEventType()).ifPresent(value->builder.withEventType(value));
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getExchange()).ifPresent(value->builder.withExchange(value));
        builder.withMessageId(inEvent.getMarketDataEvent().getEvent().getMessageId());
        BaseRpcUtil.getScaledQuantity(inEvent.getMarketDataEvent().getPrice()).ifPresent(value->builder.withPrice(value));
        builder.withProcessedTimestamp(inEvent.getMarketDataEvent().getEventTimestamps().getProcessedTimestamp());
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getEvent().getProvider()).ifPresent(value->builder.withProvider(value));
        builder.withReceivedTimestamp(inEvent.getMarketDataEvent().getEventTimestamps().getReceivedTimestamp());
        BaseRpcUtil.getScaledQuantity(inEvent.getMarketDataEvent().getSize()).ifPresent(value->builder.withSize(value));
        BaseRpcUtil.getStringValue(inEvent.getMarketDataEvent().getEvent().getSource()).ifPresent(value->builder.withSource(value));
        BaseRpcUtil.getDateValue(inEvent.getMarketDataEvent().getEvent().getTimestamp()).ifPresent(value->builder.withTimestamp(value));
        BaseRpcUtil.getStringValue(inEvent.getTradeCondition()).ifPresent(value->builder.withTradeCondition(value));
        BaseRpcUtil.getDateValue(inEvent.getTradeDate()).ifPresent(value->builder.withTradeDate(value));
        if(inEvent.getMarketDataEvent().hasOptionAttributes()) {
            setOptionAttributes(inEvent.getMarketDataEvent().getOptionAttributes(),
                                builder);
        }
        return Optional.of(builder.create());
    }
    /**
     * Set the values from the given RPC option attributes on the given option event builder.
     *
     * @param inOptionAttributes a <code>MarketDataTypesRpc.OptionAttributes</code> value
     * @param inBuilder an <code>OptionEventBuilder&lt;?&gt;</code> value
     */
    public static void setOptionAttributes(MarketDataTypesRpc.OptionAttributes inOptionAttributes,
                                           OptionEventBuilder<?> inBuilder)
    {
        getExpirationType(inOptionAttributes.getExpirationType()).ifPresent(value->inBuilder.withExpirationType(value));
        BaseRpcUtil.getScaledQuantity(inOptionAttributes.getMultiplier()).ifPresent(value->inBuilder.withMultiplier(value));
        BaseRpcUtil.getStringValue(inOptionAttributes.getProviderSymbol()).ifPresent(value->inBuilder.withProviderSymbol(value));
        TradeRpcUtil.getInstrument(inOptionAttributes.getUnderlyingInstrument()).ifPresent(value->inBuilder.withUnderlyingInstrument(value));
    }
    /**
     * Get the RPC trade event from the given value.
     *
     * @param inTradeEvent a <code>TradeEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.TradeEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.TradeEvent> getRpcTradeEvent(TradeEvent inTradeEvent)
    {
        if(inTradeEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.TradeEvent.Builder builder = MarketDataTypesRpc.TradeEvent.newBuilder();
        getRpcMarketDataEvent(inTradeEvent).ifPresent(value->builder.setMarketDataEvent(value));
        getTradeCondition(inTradeEvent).ifPresent(value->builder.setTradeCondition(value));
        getTradeDate(inTradeEvent).ifPresent(value->builder.setTradeDate(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the trade date from the given trade event.
     *
     * @param inTradeEvent a <code>TradeEvent</code> value
     * @return an <code>Optional&lt;com.google.protobuf.Timestamp&gt;</code> value
     */
    public static Optional<com.google.protobuf.Timestamp> getTradeDate(TradeEvent inTradeEvent)
    {
        return BaseRpcUtil.getTimestampValue(inTradeEvent.getTradeDate());
    }
    /**
     * Get the trade condition from the given trade event.
     *
     * @param inTradeEvent a <code>TradeEvent</code> value
     * @return an <code>Optional&lt;String&gt;</code> value
     */
    public static Optional<String> getTradeCondition(TradeEvent inTradeEvent)
    {
        return BaseRpcUtil.getStringValue(inTradeEvent.getTradeCondition());
    }
    /**
     * Get the RPC market stat event from the given value.
     *
     * @param inEvent a <code>MarketstatEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.MarketstatEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.MarketstatEvent> getRpcMarketstatEvent(MarketstatEvent inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.MarketstatEvent.Builder builder = MarketDataTypesRpc.MarketstatEvent.newBuilder();
        BaseRpcUtil.getRpcQty(inEvent.getClose()).ifPresent(qty->builder.setClose(qty));
        BaseRpcUtil.getStringValue(inEvent.getCloseDate()).ifPresent(value->builder.setCloseDate(value));
        BaseRpcUtil.getStringValue(inEvent.getCloseExchange()).ifPresent(exchange->builder.setCloseExchange(exchange));
        getRpcEvent(inEvent).ifPresent(value->builder.setEvent(value));
        getRpcEventType(inEvent.getEventType()).ifPresent(eventType->builder.setEventType(eventType));
        BaseRpcUtil.getRpcQty(inEvent.getHigh()).ifPresent(qty->builder.setHigh(qty));
        BaseRpcUtil.getStringValue(inEvent.getHighExchange()).ifPresent(exchange->builder.setHighExchange(exchange));
        TradeRpcUtil.getRpcInstrument(inEvent.getInstrument()).ifPresent(instrument->builder.setInstrument(instrument));
        getRpcOptionAttributes(inEvent).ifPresent(value->builder.setOptionAttributes(value));
        BaseRpcUtil.getRpcQty(inEvent.getLow()).ifPresent(qty->builder.setLow(qty));
        BaseRpcUtil.getStringValue(inEvent.getLowExchange()).ifPresent(exchange->builder.setLowExchange(exchange));
        BaseRpcUtil.getRpcQty(inEvent.getOpen()).ifPresent(qty->builder.setOpen(qty));
        BaseRpcUtil.getStringValue(inEvent.getOpenExchange()).ifPresent(exchange->builder.setOpenExchange(exchange));
        BaseRpcUtil.getRpcQty(inEvent.getPreviousClose()).ifPresent(qty->builder.setPreviousClose(qty));
        BaseRpcUtil.getStringValue(inEvent.getPreviousCloseDate()).ifPresent(value->builder.setPreviousCloseDate(value));
        BaseRpcUtil.getStringValue(inEvent.getTradeHighTime()).ifPresent(value->builder.setTradeHighTime(value));
        BaseRpcUtil.getStringValue(inEvent.getTradeLowTime()).ifPresent(value->builder.setTradeLowTime(value));
        BaseRpcUtil.getRpcQty(inEvent.getValue()).ifPresent(qty->builder.setValue(qty));
        BaseRpcUtil.getRpcQty(inEvent.getVolume()).ifPresent(qty->builder.setVolume(qty));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC market data event from the given value.
     *
     * @param inMarketDataEvent a <code>MarketDataEvent</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.MarketDataEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.MarketDataEvent> getRpcMarketDataEvent(MarketDataEvent inMarketDataEvent)
    {
        if(inMarketDataEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.MarketDataEvent.Builder builder = MarketDataTypesRpc.MarketDataEvent.newBuilder();
        getRpcEvent(inMarketDataEvent).ifPresent(value->builder.setEvent(value));
        getRpcEventType(inMarketDataEvent.getEventType()).ifPresent(eventType->builder.setEventType(eventType));
        BaseRpcUtil.getStringValue(inMarketDataEvent.getExchange()).ifPresent(exchange->builder.setExchange(exchange));
        BaseRpcUtil.getTimestampValue(inMarketDataEvent.getExchangeTimestamp()).ifPresent(exchangeTimestamp->builder.setExchangeTimestamp(exchangeTimestamp));
        TradeRpcUtil.getRpcInstrument(inMarketDataEvent.getInstrument()).ifPresent(instrument->builder.setInstrument(instrument));
        getRpcOptionAttributes(inMarketDataEvent).ifPresent(value->builder.setOptionAttributes(value));
        BaseRpcUtil.getRpcQty(inMarketDataEvent.getPrice()).ifPresent(qty->builder.setPrice(qty));
        getRpcEventTimestamps(inMarketDataEvent).ifPresent(value->builder.setEventTimestamps(value));
        BaseRpcUtil.getRpcQty(inMarketDataEvent.getSize()).ifPresent(qty->builder.setSize(qty));
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC timestamp value from the given value.
     *
     * @param inHasTimestamps a <code>HasTimestamps</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.EventTimestamps&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.EventTimestamps> getRpcEventTimestamps(HasTimestamps inHasTimestamps)
    {
        if(inHasTimestamps == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.EventTimestamps.Builder builder = MarketDataTypesRpc.EventTimestamps.newBuilder();
        builder.setProcessedTimestamp(inHasTimestamps.getProcessedTimestamp());
        builder.setReceivedTimestamp(inHasTimestamps.getReceivedTimestamp());
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC event type from the given value.
     *
     * @param inEventType an <code>EventType</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.EventType&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.EventType> getRpcEventType(EventType inEventType)
    {
        if(inEventType == null) {
            return Optional.empty();
        }
        switch(inEventType) {
            case SNAPSHOT_FINAL:
                return Optional.of(MarketDataTypesRpc.EventType.SNAPSHOT_FINAL);
            case SNAPSHOT_PART:
                return Optional.of(MarketDataTypesRpc.EventType.SNAPSHOT_PART);
            case UPDATE_FINAL:
                return Optional.of(MarketDataTypesRpc.EventType.UPDATE_FINAL);
            case UPDATE_PART:
                return Optional.of(MarketDataTypesRpc.EventType.UPDATE_PART);
            case UNKNOWN:
                return Optional.of(MarketDataTypesRpc.EventType.UNKNOWN_EVENT_TYPE);
            default:
                throw new UnsupportedOperationException(inEventType.name());
        }
    }
    /**
     * Get the event type from the given RPC value.
     *
     * @param inEventType a <code>MarketDataTypesRpc.EventType</code> value
     * @return an <code>Optional&lt;EventType&gt;</code> value
     */
    public static Optional<EventType> getEventType(MarketDataTypesRpc.EventType inEventType)
    {
        if(inEventType == null) {
            return Optional.empty();
        }
        switch(inEventType) {
            case SNAPSHOT_FINAL:
                return Optional.of(EventType.SNAPSHOT_FINAL);
            case SNAPSHOT_PART:
                return Optional.of(EventType.SNAPSHOT_PART);
            case UNKNOWN_EVENT_TYPE:
                return Optional.of(EventType.UNKNOWN);
            case UPDATE_FINAL:
                return Optional.of(EventType.UPDATE_FINAL);
            case UPDATE_PART:
                return Optional.of(EventType.UPDATE_PART);
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inEventType.name());
        }
    }
    /**
     * Get the RPC event from the given value.
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.Event&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.Event> getRpcEvent(Event inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.Event.Builder builder = MarketDataTypesRpc.Event.newBuilder();
        builder.setMessageId(inEvent.getMessageId());
        BaseRpcUtil.getStringValue(inEvent.getProvider()).ifPresent(value->builder.setProvider(value));
        builder.setRequestId(inEvent.getRequestId());
        BaseRpcUtil.getStringValue(inEvent.getSource()==null?null:String.valueOf(inEvent.getSource())).ifPresent(value->builder.setSource(value));
        BaseRpcUtil.getTimestampValue(inEvent.getTimestamp()).ifPresent(value->builder.setTimestamp(value));
        return Optional.of(builder.build());
    }
    /**
     * Get the market data request from the given RPC request.
     *
     * @param inRequest a <code>String</code> value
     * @param inServerRequestId a <code>String</code> value
     * @param inClientRequestId a <code>String</code> value
     * @return an <code>Optional&lt;MarketDataRequest&gt;</code> value
     */
    public static Optional<MarketDataRequest> getMarketDataRequest(String inRequest,
                                                                   String inServerRequestId,
                                                                   String inClientRequestId)
    {
        if(inRequest == null || inServerRequestId == null || inClientRequestId == null) {
            return Optional.empty();
        }
        inRequest = inRequest.replace(inClientRequestId,
                                      inServerRequestId);
        return Optional.of(MarketDataRequestBuilder.newRequestFromString(inRequest));
    }
    /**
     * Get the RPC market data status value from the given market data status.
     *
     * @param inMarketDataStatus a <code>MarketDataStatus</code> value
     * @return an <code>Optional&lt;MarketDataRpc.MarketDataStatus&gt;</code> value
     */
    public static Optional<MarketDataRpc.MarketDataStatus> getRpcMarketDataStatus(MarketDataStatus inMarketDataStatus)
    {
        if(inMarketDataStatus == null) {
            return Optional.empty();
        }
        MarketDataRpc.MarketDataStatus.Builder builder = MarketDataRpc.MarketDataStatus.newBuilder();
        builder.setFeedStatus(getRpcFeedStatus(inMarketDataStatus.getFeedStatus()));
        builder.setProvider(inMarketDataStatus.getProvider());
        return Optional.of(builder.build());
    }
    /**
     * Get the RPC feed status value from the given feed status.
     *
     * @param inFeedStatus a <code>FeedStatus</code> value
     * @return a <code>MarketDataTypesRpc.FeedStatus</code> value
     */
    public static MarketDataTypesRpc.FeedStatus getRpcFeedStatus(FeedStatus inFeedStatus)
    {
        switch(inFeedStatus) {
            case AVAILABLE:
                return MarketDataTypesRpc.FeedStatus.AVAILABLE_FEED_STATUS;
            case ERROR:
                return MarketDataTypesRpc.FeedStatus.ERROR_FEED_STATUS;
            case OFFLINE:
                return MarketDataTypesRpc.FeedStatus.OFFLINE_FEED_STATUS;
            case UNKNOWN:
            default:
                return MarketDataTypesRpc.FeedStatus.UNKNOWN_FEED_STATUS;
        }
    }
    /**
     * Get the content value for the given RPC content.
     *
     * @param inContent a <code>MarketDataTypesRpc.ContentAndCapability</code> value
     * @return a <code>Content</code> value
     */
    public static Content getContent(MarketDataTypesRpc.ContentAndCapability inContent)
    {
        switch(inContent) {
            case AGGREGATED_DEPTH:
                return Content.AGGREGATED_DEPTH;
            case BBO10:
                return Content.BBO10;
            case DIVIDEND:
                return Content.DIVIDEND;
            case IMBALANCE:
                return Content.IMBALANCE;
            case LATEST_TICK:
                return Content.LATEST_TICK;
            case LEVEL_2:
                return Content.LEVEL_2;
            case MARKET_STAT:
                return Content.MARKET_STAT;
            case NBBO:
                return Content.NBBO;
            case OPEN_BOOK:
                return Content.OPEN_BOOK;
            case TOP_OF_BOOK:
                return Content.TOP_OF_BOOK;
            case TOTAL_VIEW:
                return Content.TOTAL_VIEW;
            case UNAGGREGATED_DEPTH:
                return Content.UNAGGREGATED_DEPTH;
            case EVENT_BOUNDARY:
            case UNKNOWN:
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException("Unsupported content :" + inContent);
        }
    }
    /**
     * Get the RPC content from the given value.
     *
     * @param inContent a <code>Content</code> value
     * @return a <code>MarketDataTypesRpc.ContentAndCapability</code> value
     */
    public static MarketDataTypesRpc.ContentAndCapability getRpcContent(Content inContent)
    {
        switch(inContent) {
            case AGGREGATED_DEPTH:
                return MarketDataTypesRpc.ContentAndCapability.AGGREGATED_DEPTH;
            case BBO10:
                return MarketDataTypesRpc.ContentAndCapability.BBO10;
            case DIVIDEND:
                return MarketDataTypesRpc.ContentAndCapability.DIVIDEND;
            case IMBALANCE:
                return MarketDataTypesRpc.ContentAndCapability.IMBALANCE;
            case LATEST_TICK:
                return MarketDataTypesRpc.ContentAndCapability.LATEST_TICK;
            case LEVEL_2:
                return MarketDataTypesRpc.ContentAndCapability.LEVEL_2;
            case MARKET_STAT:
                return MarketDataTypesRpc.ContentAndCapability.MARKET_STAT;
            case NBBO:
                return MarketDataTypesRpc.ContentAndCapability.NBBO;
            case OPEN_BOOK:
                return MarketDataTypesRpc.ContentAndCapability.OPEN_BOOK;
            case TOP_OF_BOOK:
                return MarketDataTypesRpc.ContentAndCapability.TOP_OF_BOOK;
            case TOTAL_VIEW:
                return MarketDataTypesRpc.ContentAndCapability.TOTAL_VIEW;
            case UNAGGREGATED_DEPTH:
                return MarketDataTypesRpc.ContentAndCapability.UNAGGREGATED_DEPTH;
            default:
                return MarketDataTypesRpc.ContentAndCapability.UNKNOWN;
        }
    }
    /**
     * Get the market data status from the given RPC value.
     *
     * @param inMarketDataStatus a <code>MarketDataRpc.MarketDataStatus</code> value
     * @return a <code>MarketDataStatus</code> value
     */
    public static MarketDataStatus getMarketDataStatus(MarketDataRpc.MarketDataStatus inMarketDataStatus)
    {
        MarketDataProviderStatus status = new MarketDataProviderStatus();
        status.setFeedStatus(getFeedStatus(inMarketDataStatus.getFeedStatus()));
        status.setProvider(inMarketDataStatus.getProvider());
        return status;
    }
    /**
     * Get the feed status value from the given RPC value.
     *
     * @param inFeedStatus a <code>MarketDataTypesRpc.FeedStatus</code> value
     * @return a <code>FeedStatus</code> value
     */
    public static FeedStatus getFeedStatus(MarketDataTypesRpc.FeedStatus inFeedStatus)
    {
        switch(inFeedStatus) {
            case AVAILABLE_FEED_STATUS:
                return FeedStatus.AVAILABLE;
            case ERROR_FEED_STATUS:
                return FeedStatus.ERROR;
            case OFFLINE_FEED_STATUS:
                return FeedStatus.OFFLINE;
            case UNKNOWN_FEED_STATUS:
            case UNRECOGNIZED:
            default:
                return FeedStatus.UNKNOWN;
        }
    }
}
