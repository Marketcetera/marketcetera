package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.trade.*;
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
 * @since $Release$
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
        OrderSingle orderSingle = ClientTest.createOrderSingle();
        orderSingle.setCustomFields(generateCustomFields());
        TypesTestBase.assertOrderSingleEquals(orderSingle,
                (OrderSingle) roundTrip(orderSingle));
    }

    @Test
    public void verifyOrderCancel() throws Exception {
        OrderCancel orderCancel = ClientTest.createOrderCancel();
        orderCancel.setCustomFields(generateCustomFields());
        TypesTestBase.assertOrderCancelEquals(orderCancel,
                (OrderCancel) roundTrip(orderCancel));
    }
    @Test
    public void verifyOrderReplace() throws Exception {
        OrderReplace orderReplace = ClientTest.createOrderReplace();
        orderReplace.setCustomFields(generateCustomFields());
        TypesTestBase.assertOrderReplaceEquals(orderReplace,
                (OrderReplace) roundTrip(orderReplace));
    }
    @Test
    public void verifyFIXOrder() throws Exception {
        FIXOrder fixOrder = ClientTest.createOrderFIX();
        TypesTestBase.assertOrderFIXEquals(fixOrder,
                (FIXOrder) roundTrip(fixOrder));
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
}
