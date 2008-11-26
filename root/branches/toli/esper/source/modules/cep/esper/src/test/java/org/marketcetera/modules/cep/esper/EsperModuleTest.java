package org.marketcetera.modules.cep.esper;

import com.espertech.esper.client.EPStatement;
import static junit.framework.Assert.assertNull;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.modules.cep.system.CEPTestBase;
import org.marketcetera.modules.cep.system.CopierModuleFactory;
import org.marketcetera.trade.Factory;

import javax.management.JMX;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Test the Esper module functionality
 * @author toli@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
public class EsperModuleTest extends CEPTestBase {
    private static CEPEsperProcessorMXBean sEsperBean;
    private static ModuleURN TEST_URN = new ModuleURN(CEPEsperFactory.PROVIDER_URN, "toli");

    @BeforeClass
    public static void setup() throws Exception {
        factory = Factory.getInstance();
        sManager = new ModuleManager();
        sManager.init();
        sEsperBean = JMX.newMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                TEST_URN.toObjectName(),
                CEPEsperProcessorMXBean.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }

    @Override
    protected ModuleURN getModuleURN() {
        return TEST_URN;
    }

    /**
     * We have the following data flow:
     * CopierModule --> CEP --> Sink
     * Feed 3 events into copier (which just re-emits it), and then test that it goes through to the Sink via Esper
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void testBasicFlow() throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new TradeEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // Esper -> Sink: only get IBM trade events
                new DataRequest(TEST_URN, "select * from trade where symbol='IBM'")
        });

        Object obj = sink.getReceived().take();
        assertEquals("Didn't receive right trade event", TradeEvent.class, obj.getClass());
        TradeEvent theTrade = (TradeEvent) obj;
        assertEquals("Didn't receive right symbol event", "IBM", theTrade.getSymbol());
        assertEquals("Didn't receive right size event", new BigDecimal("200"), theTrade.getSize());

        assertNull("should not receive any more events", sink.getReceived().poll(5, TimeUnit.SECONDS));
        assertEquals("Sink should only receive one event", 0, sink.getReceived().size());

        // check MXBean functionality
        assertEquals("Wrong number of received events", 3, sEsperBean.getNumEventsReceived());
        assertEquals("Wrong number of emitted events", 1, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());

        // stop flow
        sManager.cancel(flowID);
    }

    /** Create a data flow where you subscribe to 2 types of events, but only the last one
     * should result in statements being received
     */
    @Test
    public void testOnlyLastStatementGetsSubscriber() throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new TradeEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // Esper -> Sink: only get IBM trade events
                new DataRequest(TEST_URN, new String[]{"select * from trade where symbol='IBM'", "select * from trade where symbol='JAVA'"})
        });

        // verify that we only get the event for java, not IBM
        assertEquals("JAVA", ((TradeEvent) sink.getReceived().take()).getSymbol());
        assertEquals("wrong # of emitted events from Esper", 1, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flowID);
    }

    /** Create one data flow, send events, make sure they come through
     * Then cancel it, create similar data flow, send same events, but make sure
     * only the laste subscriptions get hits
     */
    @Test
    public void testEsperCancel() throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new TradeEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // Esper -> Sink: only get IBM trade events
                new DataRequest(TEST_URN, "select * from trade where symbol='IBM'")
        });

        // verify we get 1 trade and then cancel
        assertEquals("IBM", ((TradeEvent) sink.getReceived().take()).getSymbol());
        assertEquals("wrong # of emitted events from Esper", 1, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flowID);

        DataFlowID flowID2 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new TradeEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // Esper -> Sink: only get IBM trade events
                new DataRequest(TEST_URN, "select * from trade where symbol='JAVA'")
        });
        // verify we only get 1 trade for Java
        assertEquals("JAVA", ((TradeEvent) sink.getReceived().take()).getSymbol());
        assertEquals("wrong # of emitted events from Esper", 1, sManager.getDataFlowInfo(flowID2).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flowID2);
    }

    @Test
    /** Verify the statements are treated correctly */
    public void testCreateStatements() throws Exception {
        CEPEsperProcessor esperPr = new CEPEsperProcessor(CEPEsperFactory.PROVIDER_URN);
        esperPr.preStart();
        ArrayList<EPStatement> stmts = esperPr.createStatements("select * from ask where symbol = 'entourage'",
                "p:every(spike=ask(exchange='sunday'))");
        junit.framework.Assert.assertEquals(2, stmts.size());
        assertFalse("Did not create a regular Esper statement", stmts.get(0).isPattern());
        assertTrue("did not create a pattern statement", stmts.get(1).isPattern());
    }

    @Test
    public void testJMX() throws Exception {
        sManager.createModule(CEPEsperFactory.PROVIDER_URN, TEST_URN);
        CEPEsperProcessorMXBean esperBean = JMX.newMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                TEST_URN.toObjectName(),
                CEPEsperProcessorMXBean.class);

        assertFalse("external time not set correctly", esperBean.isUseExternalTime());
        sManager.stop(TEST_URN);

        esperBean.setUseExternalTime(true);
        sManager.start(TEST_URN);
        assertTrue("external time not set correctly", esperBean.isUseExternalTime());

        sManager.stop(TEST_URN);
        sManager.deleteModule(TEST_URN);
    }

    @Test
    public void testPzattern() throws Exception {
        // do a pattern query and make sure we get something reasonable back
       // p:every ask(symbol="IBM", price>80) where timer:within(60 seconds)
    }

    @Test
    public void testExternalTime() throws Exception {
        
    }
}
