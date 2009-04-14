package org.marketcetera.client.jms;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.trade.*;
import org.marketcetera.client.ClientTest;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.client.brokers.BrokerStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * Tests {@link JMSXMLMessageConverter}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class JMSXMLConverterTest {
    @BeforeClass
    public static void setup() throws Exception {
        sConverter = new JMSXMLMessageConverter();
        LoggerConfiguration.logSetup();
    }
    @Test
    public void verifyOrderSingle() throws Exception {
        OrderSingle i = ClientTest.createOrderSingle();
        i.setCustomFields(generateCustomFields());
        OrderEnvelope o=(OrderEnvelope)roundTrip
            (new OrderEnvelope(i,SESSION_ID));
        assertEquals(SESSION_ID,o.getSessionId());
        TypesTestBase.assertOrderSingleEquals
            (i,(OrderSingle)o.getOrder());
    }

    @Test
    public void verifyOrderCancel() throws Exception {
        OrderCancel i = ClientTest.createOrderCancel();
        i.setCustomFields(generateCustomFields());
        OrderEnvelope o=(OrderEnvelope)roundTrip
            (new OrderEnvelope(i,SESSION_ID));
        assertEquals(SESSION_ID,o.getSessionId());
        TypesTestBase.assertOrderCancelEquals
            (i,(OrderCancel)o.getOrder());
    }
    @Test
    public void verifyOrderReplace() throws Exception {
        OrderReplace i = ClientTest.createOrderReplace();
        i.setCustomFields(generateCustomFields());
        OrderEnvelope o=(OrderEnvelope)roundTrip
            (new OrderEnvelope(i,SESSION_ID));
        assertEquals(SESSION_ID,o.getSessionId());
        TypesTestBase.assertOrderReplaceEquals
            (i,(OrderReplace)o.getOrder());
    }
    @Test
    public void verifyFIXOrder() throws Exception {
        FIXOrder i = ClientTest.createOrderFIX();
        OrderEnvelope o=(OrderEnvelope)roundTrip
            (new OrderEnvelope(i,SESSION_ID));
        assertEquals(SESSION_ID,o.getSessionId());
        TypesTestBase.assertOrderFIXEquals
            (i,(FIXOrder)o.getOrder());
    }
    @Test
    public void verifyExecReport() throws Exception {
        ExecutionReport executionReport = ClientTest.createExecutionReport();
        TypesTestBase.assertExecReportEquals(executionReport,
                (ExecutionReport) roundTrip(executionReport));
    }
    @Test
    public void verifyCancelReject() throws Exception {
        OrderCancelReject cancelReject = ClientTest.createCancelReject();
        TypesTestBase.assertCancelRejectEquals(cancelReject,
                (OrderCancelReject) roundTrip(cancelReject));
    }
    @Test
    public void verifyBrokerStatus() throws Exception {
        BrokerStatus i = new BrokerStatus("me", new BrokerID("broke"), false);
        BrokerStatus o = (BrokerStatus) roundTrip(i);
        assertEquals(i.getName(), o.getName());
        assertEquals(i.getId(), o.getId());
        assertEquals(i.getLoggedOn(), o.getLoggedOn());
    }
    @Test
    public void marshallFailure() throws Exception {
        new ExpectedFailure<JAXBException>(NotMarshallable.class.getName(), false){
            protected void run() throws Exception {
                roundTrip(new NotMarshallable());
            }
        };
    }
    @Test
    public void unmarshallFailure() throws Exception {
        new ExpectedFailure<UnmarshalException>(null){
            protected void run() throws Exception {
                sConverter.fromXML("This is not XML");
            }
        };
    }
    static Object roundTrip(Object inObject) throws Exception {
        String xml = sConverter.toXML(inObject);
        SLF4JLoggerProxy.debug("XML", xml);
        return sConverter.fromXML(xml);
    }

    private Map<String, String> generateCustomFields() {
        Map<String, String> value = new HashMap<String, String>();
        value.put("key1", "value1");
        value.put("key2", "value2");
        return value;
    }
    private static class NotMarshallable {
        
    }

    private static JMSXMLMessageConverter sConverter;
    private static final SessionId SESSION_ID=SessionId.generate();
}
