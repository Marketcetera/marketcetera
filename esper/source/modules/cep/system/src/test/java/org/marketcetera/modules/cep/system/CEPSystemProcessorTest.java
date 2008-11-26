package org.marketcetera.modules.cep.system;

import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.AfterClass;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.*;
import org.marketcetera.modules.cep.system.CEPTestBase;
import org.marketcetera.modules.cep.system.CopierModuleFactory;
import org.marketcetera.trade.Factory;

import javax.management.JMX;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;

/**
 * @author admin
 * @version $Id$
 * @since $Release$
 */
public class CEPSystemProcessorTest extends CEPTestBase {
    private static CEPSystemProcessorMXBean sSystemBean;
    private static ModuleURN TEST_URN = new ModuleURN(CEPSystemFactory.PROVIDER_URN, "toli");

    @BeforeClass
    public static void setup() throws Exception {
        factory = Factory.getInstance();
        sManager = new ModuleManager();
        sManager.init();
        sSystemBean = JMX.newMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                TEST_URN.toObjectName(),
                CEPSystemProcessorMXBean.class);
    }

    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }

    @Override
    protected ModuleURN getModuleURN() {
        return TEST_URN;
    }

    @Test
    public void testBasicFlow() throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new TradeEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // System -> Sink: only get 2 trade events
                new DataRequest(TEST_URN, "select * from trade")
        });

        Object obj = sink.getReceived().take();
        assertEquals("Didn't receive right trade event", TradeEvent.class, obj.getClass());
        TradeEvent theTrade = (TradeEvent) obj;
        assertEquals("Didn't receive right symbol event", "IBM", theTrade.getSymbol());

        obj = sink.getReceived().take();
        assertEquals("Didn't receive right trade event", TradeEvent.class, obj.getClass());
        theTrade = (TradeEvent) obj;
        assertEquals("Didn't receive right symbol event", "JAVA", theTrade.getSymbol());
        assertEquals("Sink should only receive one event", 0, sink.getReceived().size());

        // check MXBean functionality
        assertEquals("Wrong number of received events", 3, sSystemBean.getNumEventsReceived());
        assertEquals("Wrong number of emitted events", 2, sSystemBean.getNumEventsEmitted());

        sManager.cancel(flowID);
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Test
    public void testUnknownAlias() throws Exception {
        new ExpectedTestFailure(RequestDataException.class, Messages.UNSUPPORTED_TYPE.getText("bob")) {
            protected void execute() throws Throwable {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 3 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                                new BidEvent(1, 2, "GOOG", "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                                new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                                new AskEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(TEST_URN, "select * from bob")
                });
            }
        }.run();
    }
}
