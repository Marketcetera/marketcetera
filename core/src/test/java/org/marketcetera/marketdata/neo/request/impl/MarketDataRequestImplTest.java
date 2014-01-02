package org.marketcetera.marketdata.neo.request.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.neo.Messages;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Tests {@link MarketDataRequestImpl}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRequestImplTest
{
    /**
     * Run once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        xmlContext = JAXBContext.newInstance(MarketDataRequestImpl.class);
    }
    /**
     * Tests the getters and setters.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGettersAndSetters()
            throws Exception
    {
        MarketDataRequestImpl request = new MarketDataRequestImpl();
        verifyRequest(request,
                      null,
                      null,
                      null,
                      null,
                      null,
                      null);
        String exchange = "Some kind of exchange";
        request.setExchange(exchange);
        verifyRequest(request,
                      null,
                      null,
                      null,
                      exchange,
                      null,
                      null);
        String provider = "Some kind of provider";
        request.setProvider(provider);
        verifyRequest(request,
                      null,
                      null,
                      null,
                      exchange,
                      provider,
                      null);
        String symbol1 = "METC-" + System.nanoTime();
        String symbol2 = "AAPL";
        String symbol3 = "IB/16Z";
        String symbol4 = "IB*";
        String symbol5 = "<spoof XML>";
        Set<String> expectedSymbols = new HashSet<String>();
        expectedSymbols.addAll(Arrays.asList(new String[] { symbol1,symbol2,symbol3,symbol4, symbol5 }));
        request.getSymbols().addAll(expectedSymbols);
        verifyRequest(request,
                      expectedSymbols,
                      null,
                      null,
                      exchange,
                      provider,
                      null);
        Set<String> expectedUnderlyingSymbols = new HashSet<String>();
        expectedUnderlyingSymbols.addAll(Arrays.asList(new String[] { "IBM","AAPL","MSFT","METC","*/*" }));
        request.getUnderlyingSymbols().addAll(expectedUnderlyingSymbols);
        verifyRequest(request,
                      expectedSymbols,
                      expectedUnderlyingSymbols,
                      null,
                      exchange,
                      provider,
                      null);
        Set<Content> expectedContent = EnumSet.allOf(Content.class);
        request.getContent().addAll(expectedContent);
        verifyRequest(request,
                      expectedSymbols,
                      expectedUnderlyingSymbols,
                      expectedContent,
                      exchange,
                      provider,
                      null);
        Map<String,String> expectedParameters = new HashMap<String,String>();
        expectedParameters.put("key1",
                               "value1");
        expectedParameters.put("key2",
                               "value2");
        expectedParameters.put("key3",
                               "value3");
        request.getParameters().putAll(expectedParameters);
        verifyRequest(request,
                      expectedSymbols,
                      expectedUnderlyingSymbols,
                      expectedContent,
                      exchange,
                      provider,
                      expectedParameters);
        request.getSymbols().clear();
        request.getUnderlyingSymbols().clear();
        request.getContent().clear();
        request.getParameters().clear();
        request.setExchange(null);
        request.setProvider(null);
        verifyRequest(request,
                      null,
                      null,
                      null,
                      null,
                      null,
                      null);
    }
    /**
     * Tests {@link MarketDataRequestImpl#validate()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testValidation()
            throws Exception
    {
        final MarketDataRequestImpl request = new MarketDataRequestImpl();
        new ExpectedFailure<IllegalArgumentException>(Messages.NO_SYMBOLS_OR_UNDERLYING_SYMBOLS.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                request.validate();
            }
        };
        request.getSymbols().add("METC");
        assertFalse(request.getSymbols().isEmpty());
        assertTrue(request.getUnderlyingSymbols().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(Messages.NO_CONTENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                request.validate();
            }
        };
        request.getSymbols().clear();
        request.getUnderlyingSymbols().add("METC");
        assertTrue(request.getSymbols().isEmpty());
        assertFalse(request.getUnderlyingSymbols().isEmpty());
        new ExpectedFailure<IllegalArgumentException>(Messages.NO_CONTENT.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                request.validate();
            }
        };
        request.getContent().add(Content.AGGREGATED_DEPTH);
        verifyRequest(request,
                      null,
                      new HashSet<String>(Arrays.asList(new String[] { "METC" })),
                      EnumSet.of(Content.AGGREGATED_DEPTH),
                      null,
                      null,
                      null);
    }
    /**
     * Conducts an XML marshalling/unmarshalling round-trip test.
     *
     * @param inRequest a <code>MarketDataRequestImpl</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doOneXmlTest(MarketDataRequestImpl inRequest)
            throws Exception
    {
        Marshaller marshaller = xmlContext.createMarshaller();
        Unmarshaller unmarshaller = xmlContext.createUnmarshaller();
        StringWriter writer = new StringWriter();
        marshaller.marshal(inRequest,
                           writer);
        String marshalledXml = writer.toString();
        SLF4JLoggerProxy.debug(this,
                               "{} marshalled to {}",
                               inRequest,
                               marshalledXml);
        StringReader reader = new StringReader(marshalledXml);
        MarketDataRequestImpl unmarshalledRequest = (MarketDataRequestImpl)unmarshaller.unmarshal(reader);
        doEqualityTest(unmarshalledRequest,
                       inRequest.getSymbols(),
                       inRequest.getUnderlyingSymbols(),
                       inRequest.getContent(),
                       inRequest.getExchange(),
                       inRequest.getProvider(),
                       inRequest.getParameters());
    }
    /**
     * Verifies that the given expected parameters match the corresponding attributes on the given actual value.
     *
     * @param inActualRequest a <code>MarketDataRequestImpl</code> value
     * @param inExpectedSymbols a <code>Set&lt;String&gt;</code> value or <code>null</code>
     * @param inExpectedUnderlyingSymbols a <code>Set&lt;String&gt;</code> value or <code>null</code>
     * @param inExpectedContent a <code>Set&lt;Content&gt;</code> value or <code>null</code>
     * @param inExchange a <code>String</code> value
     * @param inProvider a <code>String</code> value
     * @param inParameters a <code>Map&lt;String,String&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doEqualityTest(MarketDataRequestImpl inActualRequest,
                                Set<String> inExpectedSymbols,
                                Set<String> inExpectedUnderlyingSymbols,
                                Set<Content> inExpectedContent,
                                String inExchange,
                                String inProvider,
                                Map<String,String> inParameters)
            throws Exception
    {
        if(inExpectedSymbols == null) {
            assertTrue(inActualRequest.getSymbols().isEmpty());
        } else {
            assertEquals(inExpectedSymbols,
                         inActualRequest.getSymbols());
        }
        if(inExpectedUnderlyingSymbols == null) {
            assertTrue(inActualRequest.getUnderlyingSymbols().isEmpty());
        } else {
            assertEquals(inExpectedUnderlyingSymbols,
                         inActualRequest.getUnderlyingSymbols());
        }
        if(inExpectedContent == null) {
            assertTrue(inActualRequest.getContent().isEmpty());
        } else {
            assertEquals(inExpectedContent,
                         inActualRequest.getContent());
        }
        assertEquals(inExchange,
                     inActualRequest.getExchange());
        assertEquals(inProvider,
                     inActualRequest.getProvider());
        if(inParameters == null) {
            assertTrue(inActualRequest.getParameters().isEmpty());
        } else {
            assertEquals(inParameters,
                         inActualRequest.getParameters());
        }
    }
    /**
     * Verifies that the given expected parameters match the corresponding attributes on the given actual value.
     *
     * @param inActualRequest a <code>MarketDataRequestImpl</code> value
     * @param inExpectedSymbols a <code>Set&lt;String&gt;</code> value or <code>null</code>
     * @param inExpectedUnderlyingSymbols a <code>Set&lt;String&gt;</code> value or <code>null</code>
     * @param inExpectedContent a <code>Set&lt;Content&gt;</code> value or <code>null</code>
     * @param inExchange a <code>String</code> value
     * @param inProvider a <code>String</code> value
     * @param inParameters a <code>Map&lt;String,String&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyRequest(MarketDataRequestImpl inActualRequest,
                               Set<String> inExpectedSymbols,
                               Set<String> inExpectedUnderlyingSymbols,
                               Set<Content> inExpectedContent,
                               String inExchange,
                               String inProvider,
                               Map<String,String> inParameters)
            throws Exception
    {
        assertNotNull(inActualRequest.toString());
        doEqualityTest(inActualRequest,
                       inExpectedSymbols,
                       inExpectedUnderlyingSymbols,
                       inExpectedContent,
                       inExchange,
                       inProvider,
                       inParameters);
        doOneXmlTest(inActualRequest);
    }
    /**
     * XML marshalling/unmarshalling context
     */
    private static JAXBContext xmlContext;
}
