package org.marketcetera.modules.cep.system;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.event.Event;
import org.marketcetera.core.event.EventTestBase;
import org.marketcetera.core.marketdata.DateUtils;
import org.marketcetera.core.module.*;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.Factory;

/**
 * @version $Id: CEPSystemProcessorTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class CEPSystemProcessorTest extends CEPTestBase {
    private static ModuleURN TEST_URN = new ModuleURN(CEPSystemFactory.PROVIDER_URN, "toli");

    @BeforeClass
    public static void setup() throws Exception {
        sFactory = Factory.getInstance();
    }

    @Override
    protected ModuleURN getModuleURN() {
        return TEST_URN;
    }

    @Override
    protected Class<?> getIncorrectQueryException() {
        return RequestDataException.class;
    }

    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Ignore   // no idea what this is supposed to test and how
    public void info() throws Exception {
        assertProviderInfo(sManager, CEPSystemFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(sManager, CEPSystemFactory.PROVIDER_URN,
                ModuleState.STARTED, null, null, false,
                true, false, true, false);
    }


    @Test(timeout=120000)
    public void testBasicFlow() throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Event[] { bid1, trade1, trade2}),
                // System -> Sink: only get 2 trade events
                new DataRequest(TEST_URN, "select * from trade")
        });

        assertSame("Didn't receive right trade event", trade1, sSink.getNextData());
        assertSame("Didn't receive right trade event", trade2, sSink.getNextData());

        // check MXBean functionality
        assertEquals("Wrong number of received events", 3, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumReceived());
        assertEquals("Wrong number of emitted events", 2, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());

        sManager.cancel(flowID);
    }

    @Test(timeout=120000)
    public void testInvalidStringArrReqeust() throws Exception {
        new ExpectedFailure<UnsupportedRequestParameterType>() {
            @Override
            protected void run()
                    throws Exception
            {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 3 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new Event[] {
                                EventTestBase.generateEquityBidEvent(1, new Date(2), new Equity("GOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100"), DateUtils.dateToString(new Date())),
                                EventTestBase.generateEquityTradeEvent(3, new Date(4), new Equity("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200"), DateUtils.dateToString(new Date())),
                                EventTestBase.generateEquityAskEvent(5, new Date(6), new Equity("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"), DateUtils.dateToString(new Date()))
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(TEST_URN, new String[]{"select * from bob", "select * from fred"})
                });
            }
        };
    }


    @Test(timeout=120000)
    public void testUnknownAlias() throws Exception {
        new ExpectedFailure<RequestDataException>(Messages.UNSUPPORTED_TYPE,
                                                  "bob") {
            @Override
            protected void run()
                    throws Exception
            {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 3 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new Event[] {
                                EventTestBase.generateEquityBidEvent(1, new Date(2), new Equity("GOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100"), DateUtils.dateToString(new Date())),
                                EventTestBase.generateEquityTradeEvent(3, new Date(4), new Equity("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200"), DateUtils.dateToString(new Date())),
                                EventTestBase.generateEquityAskEvent(5, new Date(6), new Equity("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"), DateUtils.dateToString(new Date()))
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(TEST_URN, "select * from bob")
                });
            }
        };
    }
}
