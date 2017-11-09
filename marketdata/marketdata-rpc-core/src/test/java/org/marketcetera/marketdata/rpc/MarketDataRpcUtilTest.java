package org.marketcetera.marketdata.rpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.annotation.concurrent.GuardedBy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataContextClassProvider;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc;
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
     * Test {@link MarketDataRpcUtil#getEvent(org.marketcetera.marketdata.core.rpc.MarketDataTypesRpc.Event)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Ignore@Test
    public void testGetEvent()
            throws Exception
    {
        assertFalse(MarketDataRpcUtil.getEvent(null).isPresent());
        MarketDataTypesRpc.Event rpcEvent = generateRpcEvent();
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
    @Ignore@Test
    public void testGetRpcEvent()
            throws Exception
    {
        assertFalse(MarketDataRpcUtil.getRpcEvent(null).isPresent());
        Event event = EventTestBase.generateAskEvent(new Equity("METC"));
        MarketDataTypesRpc.Event rpcEvent = MarketDataRpcUtil.getRpcEvent(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEvent(event,
                       rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getRpcEventHolder(Event)} for {@link TradeEvent} types.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetRpcTradeEvent()
            throws Exception
    {
        Event event = EventTestBase.generateTradeEvent(new Equity("METC"));
        assertFalse(MarketDataRpcUtil.getRpcEventHolder(null).isPresent());
        MarketDataTypesRpc.EventHolder rpcEvent = MarketDataRpcUtil.getRpcEventHolder(event).orElse(null);
        assertNotNull(rpcEvent);
        verifyRpcEventHolder(event,
                             rpcEvent);
    }
    /**
     * Test {@link MarketDataRpcUtil#getMarketDataRequest(String, String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Ignore@Test
    public void testGetMarketDataRequest()
            throws Exception
    {
        MarketDataRequestBuilder requestBuilder = MarketDataRequestBuilder.newRequest();
        requestBuilder.withContent(Content.TOP_OF_BOOK);
        requestBuilder.withSymbols("METC");
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(requestBuilder.create().toString(),
                                                           UUID.randomUUID().toString(),
                                                           null).isPresent());
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(requestBuilder.create().toString(),
                                                           null,
                                                           UUID.randomUUID().toString()).isPresent());
        assertFalse(MarketDataRpcUtil.getMarketDataRequest(null,
                                                           UUID.randomUUID().toString(),
                                                           UUID.randomUUID().toString()).isPresent());
    }
    /**
     * Verify the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent an <code>Event</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.EventHolder</code> value
     */
    public static void verifyRpcEventHolder(Event inExpectedEvent,
                                            MarketDataTypesRpc.EventHolder inActualEvent)
    {
        if(inActualEvent.hasTradeEvent()) {
            assertTrue("Expected: " + TradeEvent.class.getSimpleName() + " Actual: " + inExpectedEvent.getClass().getSimpleName(),
                       inExpectedEvent instanceof TradeEvent);
            verifyRpcTradeEvent((TradeEvent)inExpectedEvent,
                                inActualEvent.getTradeEvent());
        } else {
            throw new UnsupportedOperationException(inActualEvent.toString());
        }
    }
    /**
     *
     *
     * @param inExpectedEvent a <code>TradeEvent</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.TradeEvent</code> value
     */
    public static void verifyRpcTradeEvent(TradeEvent inExpectedEvent,
                                            MarketDataTypesRpc.TradeEvent inTradeEvent)
    {
    }
    /**
     * Verify the given RPC event matches the given expected event.
     *
     * @param inExpectedEvent an <code>Event</code> value
     * @param inActualEvent a <code>MarketDataTypesRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static void verifyRpcEvent(Event inExpectedEvent,
                                      MarketDataTypesRpc.Event inActualEvent)
            throws Exception
    {
        assertEquals(inExpectedEvent,
                     unmarshall(inActualEvent.getPayload()));
    }
    /**
     * Generate an RPC event with random values.
     *
     * @return a <code>MarketDataTypesRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static MarketDataTypesRpc.Event generateRpcEvent()
            throws Exception
    {
        return generateRpcEvent(marshall(EventTestBase.generateAskEvent(new Equity("METC"))));
    }
    /**
     * Generate an RPC event with the given XML payload.
     *
     * @param inPayload 
     * @return a <code>MarketDataTypesRpc.Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static MarketDataTypesRpc.Event generateRpcEvent(String inPayload)
            throws Exception
    {
        MarketDataTypesRpc.Event.Builder builder = MarketDataTypesRpc.Event.newBuilder();
        builder.setPayload(inPayload);
        return builder.build();
    }
    /**
     * Verify the given actual user has the given expected values.
     *
     * @param inExpectedEvent a <code>MarketDataTypesRpc.Event</code> value
     * @param inActualEvent an <code>Event</code> value
     * @throws Exception if an unexpected error occurs
     */
    public static void verifyEvent(MarketDataTypesRpc.Event inExpectedEvent,
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
