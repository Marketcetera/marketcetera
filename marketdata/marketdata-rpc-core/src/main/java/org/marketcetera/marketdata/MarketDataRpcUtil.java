package org.marketcetera.marketdata;

import java.io.StringReader;
import java.io.StringWriter;

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
     * @return an <code>Event</code> value
     */
    public static Event getEvent(MarketDataRpc.EventsResponse inResponse)
    {
        try {
            return (Event)unmarshall(inResponse.getPayload());
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
        try {
            inBuilder.setPayload(marshall(inEvent));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Get the market data request from the given RPC request.
     *
     * @param inRequest a <code>String</code> value
     * @param inRequestId
     * @return a <code>MarketDataRequest</code> value
     */
    public static MarketDataRequest getMarketDataRequest(String inRequest,
                                                         String inRequestId)
    {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(inRequest);
        requestBuilder.append(':');
        requestBuilder.append(MarketDataRequestBuilder.REQUEST_ID_KEY).append('=').append(inRequestId);
        return MarketDataRequestBuilder.newRequestFromString(requestBuilder.toString());
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
