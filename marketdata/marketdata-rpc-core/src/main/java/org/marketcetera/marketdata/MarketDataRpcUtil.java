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
        try {
            return Optional.of((Event)unmarshall(inResponse.getEvent().getPayload()));
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
