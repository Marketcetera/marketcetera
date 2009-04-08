package org.marketcetera.modules.cep.system;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;

/**
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
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
    protected Class getIncorrectQueryException() {
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
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] { bid1, trade1, trade2}),
                // System -> Sink: only get 2 trade events
                new DataRequest(TEST_URN, "select * from trade")
        });

        assertSame("Didn't receive right trade event", trade1, sSink.getReceived().take());
        assertSame("Didn't receive right trade event", trade2, sSink.getReceived().take());

        // check MXBean functionality
        assertEquals("Wrong number of received events", 3, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumReceived());
        assertEquals("Wrong number of emitted events", 2, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());

        sManager.cancel(flowID);
    }

    @Test(timeout=120000)
    public void testInvalidStringArrReqeust() throws Exception {
        new ExpectedTestFailure(UnsupportedRequestParameterType.class) {
            protected void execute() throws Exception {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 3 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                                new BidEvent(1, 2, new MSymbol("GOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                                new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                                new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(TEST_URN, new String[]{"select * from bob", "select * from fred"})
                });
            }
        }.run();
    }


    @Test(timeout=120000)
    public void testUnknownAlias() throws Exception {
        new ExpectedTestFailure(RequestDataException.class, Messages.UNSUPPORTED_TYPE.getText("bob")) {
            protected void execute() throws Exception {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 3 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                                new BidEvent(1, 2, new MSymbol("GOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                                new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                                new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(TEST_URN, "select * from bob")
                });
            }
        }.run();
    }
}
