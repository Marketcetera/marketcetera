package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.StringReader;
import java.io.StringWriter;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.marketdata.MarketDataContextClassProvider;
import org.marketcetera.marketdata.core.rpc.MarketDataRpc;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Tests {@link MarketDataRpcUtil}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRpcUtilTest
{
    /**
     * Test {@link MarketDataRpcUtil#getEvent(org.marketcetera.marketdata.core.rpc.MarketDataRpc.Event)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetEvent()
            throws Exception
    {
        assertFalse(MarketDataRpcUtil.getEvent(null).isPresent());
        MarketDataRpc.Event rpcEvent = generateRpcEvent();
        Event event = MarketDataRpcUtil.getEvent(rpcEvent).orElse(null);
        assertNotNull(event);
        verifyEvent(rpcEvent,
                   event);
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                MarketDataRpcUtil.getEvent(generateRpcEvent(""));
            }
        };
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEvent(Event)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcEvent()
            throws Exception
    {
        assertFalse(MarketDataRpcUtil.getRpcEvent(null).isPresent());
        Event event = EventTestBase.generateAskEvent(new Equity("METC"));
        MarketDataRpc.Event rpcEvent = MarketDataRpcUtil.getRpcEvent(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEvent(event,
                       rpcEvent);
    }
    /**
     * Verify the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent an <code>Event</code> value
     * @param inActualEvent a <code>MarketDataRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static void verifyRpcEvent(Event inExpectedEvent,
                                      MarketDataRpc.Event inActualEvent)
            throws Exception
    {
        assertEquals(inExpectedEvent,
                     unmarshall(inActualEvent.getPayload()));
    }
    /**
     * Generate an RPC event with random values.
     *
     * @return a <code>MarketDataRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static MarketDataRpc.Event generateRpcEvent()
            throws Exception
    {
        return generateRpcEvent(marshall(EventTestBase.generateAskEvent(new Equity("METC"))));
    }
    /**
     * Generate an RPC event with the given XML payload.
     *
     * @param inPayload 
     * @return a <code>MarketDataRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static MarketDataRpc.Event generateRpcEvent(String inPayload)
            throws Exception
    {
        MarketDataRpc.Event.Builder builder = MarketDataRpc.Event.newBuilder();
        builder.setPayload(inPayload);
        return builder.build();
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedEvent a <code>MarketDataRpc.Event</code> value
     * @param inActualEvent an <code>Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static void verifyEvent(MarketDataRpc.Event inExpectedEvent,
                                   Event inActualEvent)
            throws Exception
    {
        Event expectedEventBody = unmarshall(inExpectedEvent.getPayload());
        assertEquals(expectedEventBody,
                     inActualEvent);
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
