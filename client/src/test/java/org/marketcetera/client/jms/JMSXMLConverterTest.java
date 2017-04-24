package org.marketcetera.client.jms;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.client.ClientTest;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Currency;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.FIXResponse;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.TypesTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.tags.SessionId;

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
    }
    @Test
    public void verifyOrderSingle() throws Exception {
        for (Instrument instrument: sInstruments) {
            OrderSingle i = ClientTest.createOrderSingle();
            i.setInstrument(instrument);
            i.setCustomFields(generateCustomFields());
            DataEnvelope o=(DataEnvelope)roundTrip
                (new DataEnvelope(i,SESSION_ID));
            assertEquals(SESSION_ID,o.getSessionId());
            TypesTestBase.assertOrderSingleEquals
            (i,(OrderSingle)o.getOrder());
        }
    }

    @Test
    public void verifyOrderCancel() throws Exception {
        for (Instrument instrument: sInstruments) {
            OrderCancel i = ClientTest.createOrderCancel();
            i.setInstrument(instrument);
            i.setCustomFields(generateCustomFields());
            DataEnvelope o=(DataEnvelope)roundTrip
                (new DataEnvelope(i,SESSION_ID));
            assertEquals(SESSION_ID,o.getSessionId());
            TypesTestBase.assertOrderCancelEquals
            (i,(OrderCancel)o.getOrder());
        }
    }
    @Test
    public void verifyOrderReplace() throws Exception {
        for (Instrument instrument: sInstruments) {
            OrderReplace i = ClientTest.createOrderReplace();
            i.setInstrument(instrument);
            i.setCustomFields(generateCustomFields());
            DataEnvelope o=(DataEnvelope)roundTrip
                (new DataEnvelope(i,SESSION_ID));
            assertEquals(SESSION_ID,o.getSessionId());
            TypesTestBase.assertOrderReplaceEquals
            (i,(OrderReplace)o.getOrder());
        }
    }
    @Test
    public void verifyFIXOrder() throws Exception {
        FIXOrder i = ClientTest.createOrderFIX();
        DataEnvelope o=(DataEnvelope)roundTrip
            (new DataEnvelope(i,SESSION_ID));
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
    public void verifyFIXResponse() throws Exception {
        FIXResponse fixResponse = ClientTest.createFIXResponse();
        TypesTestBase.assertFIXResponseEquals(fixResponse,
                (FIXResponse) roundTrip(fixResponse));
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
        new ExpectedFailure<UnmarshalException>(){
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
    private static Instrument[] sInstruments = new Instrument[]{
            new Equity("sym"),
            new Option("sym", "20101010", BigDecimal.TEN, OptionType.Call),
            new Future("rama", FutureExpirationMonth.AUGUST,2010),
            new Currency("USD","GBP","","")
    };

    private static JMSXMLMessageConverter sConverter;
    private static final SessionId SESSION_ID=SessionId.generate();
}
