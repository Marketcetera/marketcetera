package org.marketcetera.marketdata.rpc;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataContextClassProvider;
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
        if(inRpcEvent == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(unmarshall(inRpcEvent.getPayload()));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the RPC event for the given event.
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.Event&gt;</code>
     */
    public static Optional<MarketDataTypesRpc.Event> getRpcEvent(Event inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(MarketDataTypesRpc.Event.newBuilder().setPayload(marshall(inEvent)).build());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
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
        } else {
            throw new UnsupportedOperationException(inEvent.getClass().getSimpleName());
        }
        return Optional.of(builder.build());
    }
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
        getRpcNewEvent(inMarketDataEvent).ifPresent(value->builder.setEvent(value));
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
     * Get the RPC event from the given value.
     *
     * @param inEvent an <code>Event</code> value
     * @return an <code>Optional&lt;MarketDataTypesRpc.NewEvent&gt;</code> value
     */
    public static Optional<MarketDataTypesRpc.NewEvent> getRpcNewEvent(Event inEvent)
    {
        if(inEvent == null) {
            return Optional.empty();
        }
        MarketDataTypesRpc.NewEvent.Builder builder = MarketDataTypesRpc.NewEvent.newBuilder();
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
    /**
     * Marshals the given object to an XML stream.
     *
     * @param inObject an <code>Object</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if an error occurs marshalling the data
     */
    private static String marshall(Object inObject)
            throws JAXBException
    {
        StringWriter output = new StringWriter();
        synchronized(contextLock) {
            marshaller.marshal(inObject,
                               output);
        }
        return output.toString();
    }
    /**
     * Unmarshals an object from the given XML stream.
     *
     * @param inData a <code>String</code> value
     * @return a <code>Clazz</code> value
     * @throws JAXBException if an error occurs unmarshalling the data
     */
    @SuppressWarnings("unchecked")
    private static <Clazz> Clazz unmarshall(String inData)
            throws JAXBException
    {
        synchronized(contextLock) {
            return (Clazz)unmarshaller.unmarshal(new StringReader(inData));
        }
    }
    /**
     * guards access to JAXB context objects
     */
    private static final Object contextLock = new Object();
    /**
     * context used to serialize and unserialize messages as necessary
     */
    @GuardedBy("contextLock")
    private static JAXBContext context;
    /**
     * marshals messages
     */
    @GuardedBy("contextLock")
    private static Marshaller marshaller;
    /**
     * unmarshals messages
     */
    @GuardedBy("contextLock")
    private static Unmarshaller unmarshaller;
    static {
        try {
            synchronized(contextLock) {
                context = JAXBContext.newInstance(new MarketDataContextClassProvider().getContextClasses());
                marshaller = context.createMarshaller();
                unmarshaller = context.createUnmarshaller();
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
