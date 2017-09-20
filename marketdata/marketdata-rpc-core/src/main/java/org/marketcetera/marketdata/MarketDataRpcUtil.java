package org.marketcetera.marketdata;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;

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
     * Get the event from the given RPC message.
     *
     * @param inResponse a <code>MarketDataRpc.EventsResponse</code> value
     * @return an <code>Optional&lt;Event&gt;</code> value
     */
    public static Optional<Event> getEvent(MarketDataRpc.EventsResponse inResponse)
    {
        if(!inResponse.hasEvent()) {
            return Optional.empty();
        }
        return Optional.of(getEvent(inResponse.getEvent()));
    }
    /**
     * Get the event from the given RPC event value.
     *
     * @param inRpcEvent a <code>MarketDataRpc.Event</code> value
     * @return an <code>Event</code> value
     */
    public static Event getEvent(MarketDataRpc.Event inRpcEvent)
    {
        try {
            return (Event)unmarshall(inRpcEvent.getPayload());
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Set the given event on the given builder.
     *
     * @param inEvent an <code>Event</code> value
     * @param inBuilder a <code>MarketDataRpc.EventsResponse.Builder</code> value
     */
    public static void setEvent(Event inEvent,
                                MarketDataRpc.EventsResponse.Builder inBuilder)
    {
        inBuilder.setEvent(getRpcEvent(inEvent));
    }
    /**
     * Get the RPC event for the given event.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>MarketDataRpc.Event</code>
     */
    public static MarketDataRpc.Event getRpcEvent(Event inEvent)
    {
        try {
            return MarketDataRpc.Event.newBuilder().setPayload(marshall(inEvent)).build();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the market data request from the given RPC request.
     *
     * @param inRequest a <code>String</code> value
     * @param inServerRequestId a <code>String</code> value
     * @param inClientRequestId a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest getMarketDataRequest(String inRequest,
                                                         String inServerRequestId,
                                                         String inClientRequestId)
    {
        inRequest = inRequest.replace(inClientRequestId,
                                      inServerRequestId);
        return MarketDataRequestBuilder.newRequestFromString(inRequest);
    }
    /**
     * Set the given market data status on the given RPC builder.
     *
     * @param inMarketDataStatus a <code>MarketDataStatus</code> value
     * @param inBuilder a <code>MarketDataRpc.MarketDataStatusListenerResponse.Builder</code> value
     */
    public static void setMarketDataStatus(MarketDataStatus inMarketDataStatus,
                                           MarketDataRpc.MarketDataStatusListenerResponse.Builder inBuilder)
    {
        if(inMarketDataStatus == null) {
            return;
        }
        inBuilder.setMarketDataStatus(getRpcMarketDataStatus(inMarketDataStatus));
    }
    /**
     * Get the RPC market data status value from the given market data status.
     *
     * @param inMarketDataStatus a <code>MarketDataStatus</code> value
     * @return a <code>MarketDataRpc.MarketDataStatus</code> value
     */
    public static MarketDataRpc.MarketDataStatus getRpcMarketDataStatus(MarketDataStatus inMarketDataStatus)
    {
        MarketDataRpc.MarketDataStatus.Builder builder = MarketDataRpc.MarketDataStatus.newBuilder();
        builder.setFeedStatus(getRpcFeedStatus(inMarketDataStatus.getFeedStatus()));
        builder.setProvider(inMarketDataStatus.getProvider());
        return builder.build();
    }
    /**
     * Get the RPC feed status value from the given feed status.
     *
     * @param inFeedStatus a <code>FeedStatus</code> value
     * @return a <code>MarketDataRpc.FeedStatus</code> value
     */
    public static MarketDataRpc.FeedStatus getRpcFeedStatus(FeedStatus inFeedStatus)
    {
        switch(inFeedStatus) {
            case AVAILABLE:
                return MarketDataRpc.FeedStatus.AVAILABLE_FEED_STATUS;
            case ERROR:
                return MarketDataRpc.FeedStatus.ERROR_FEED_STATUS;
            case OFFLINE:
                return MarketDataRpc.FeedStatus.OFFLINE_FEED_STATUS;
            case UNKNOWN:
            default:
                return MarketDataRpc.FeedStatus.UNKNOWN_FEED_STATUS;
        }
    }
    /**
     * Get the content value for the given RPC content.
     *
     * @param inContent a <code>MarketDataRpc.ContentAndCapability</code> value
     * @return a <code>Content</code> value
     */
    public static Content getContent(MarketDataRpc.ContentAndCapability inContent)
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
     * @return a <code>MarketDataRpc.ContentAndCapability</code> value
     */
    public static MarketDataRpc.ContentAndCapability getRpcCntent(Content inContent)
    {
        switch(inContent) {
            case AGGREGATED_DEPTH:
                return MarketDataRpc.ContentAndCapability.AGGREGATED_DEPTH;
            case BBO10:
                return MarketDataRpc.ContentAndCapability.BBO10;
            case DIVIDEND:
                return MarketDataRpc.ContentAndCapability.DIVIDEND;
            case IMBALANCE:
                return MarketDataRpc.ContentAndCapability.IMBALANCE;
            case LATEST_TICK:
                return MarketDataRpc.ContentAndCapability.LATEST_TICK;
            case LEVEL_2:
                return MarketDataRpc.ContentAndCapability.LEVEL_2;
            case MARKET_STAT:
                return MarketDataRpc.ContentAndCapability.MARKET_STAT;
            case NBBO:
                return MarketDataRpc.ContentAndCapability.NBBO;
            case OPEN_BOOK:
                return MarketDataRpc.ContentAndCapability.OPEN_BOOK;
            case TOP_OF_BOOK:
                return MarketDataRpc.ContentAndCapability.TOP_OF_BOOK;
            case TOTAL_VIEW:
                return MarketDataRpc.ContentAndCapability.TOTAL_VIEW;
            case UNAGGREGATED_DEPTH:
                return MarketDataRpc.ContentAndCapability.UNAGGREGATED_DEPTH;
            default:
                return MarketDataRpc.ContentAndCapability.UNKNOWN;
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
     * @param inFeedStatus a <code>MarketDataRpc.FeedStatus</code> value
     * @return a <code>FeedStatus</code> value
     */
    public static FeedStatus getFeedStatus(MarketDataRpc.FeedStatus inFeedStatus)
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
