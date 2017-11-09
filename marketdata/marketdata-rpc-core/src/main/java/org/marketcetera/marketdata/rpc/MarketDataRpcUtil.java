package org.marketcetera.marketdata.rpc;

import java.util.Optional;

import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.DividendFrequency;
import org.marketcetera.event.DividendStatus;
import org.marketcetera.event.DividendType;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataProviderStatus;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataStatus;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
import org.marketcetera.rpc.base.BaseRpcUtil;
import org.marketcetera.trading.rpc.TradeRpcUtil;

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
     * @param inRpcEvent a <code>MarketDataTypesRpc.Event</code> value
     * @return an <code>Optional&lt;Event&gt;</code> value
     */
    public static Optional<Event> getEvent(MarketDataTypesRpc.Event inRpcEvent)
    {
        throw new UnsupportedOperationException();
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
        } else {
            throw new UnsupportedOperationException(inEvent.getClass().getSimpleName());
        }
        return Optional.of(builder.build());
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
//        return Optional.of(builder.build());
        throw new UnsupportedOperationException(); // TODO
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
        BaseRpcUtil.getRpcQty(inMarketDataEvent.getPrice()).ifPresent(qty->builder.setPrice(qty));
        builder.setProcessedTimestamp(inMarketDataEvent.getProcessedTimestamp());
        builder.setReceivedTimestamp(inMarketDataEvent.getReceivedTimestamp());
        BaseRpcUtil.getRpcQty(inMarketDataEvent.getSize()).ifPresent(qty->builder.setSize(qty));
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
