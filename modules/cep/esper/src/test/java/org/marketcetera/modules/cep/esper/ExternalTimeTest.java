package org.marketcetera.modules.cep.esper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.management.JMX;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.module.BlockingSinkDataListener;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleTestBase;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.SimpleSuggestionFactory;
import org.marketcetera.trade.Suggestion;

/**
 * Test the external time functionality
 *  
 * @author toli kuznets
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ExternalTimeTest extends ModuleTestBase {
    protected ModuleManager sManager;
    protected static BlockingSinkDataListener sSink;
    protected static Factory sFactory;

    private static ModuleURN TEST_URN = new ModuleURN(CEPEsperFactory.PROVIDER_URN, "toli");
    @Before
    public void before() throws Exception {
        sSink = new BlockingSinkDataListener();
        sManager = new ModuleManager();
        sManager.init();
        sManager.addSinkListener(sSink);
    }

    @After
    public void after() throws Exception {
        sManager.removeSinkListener(sSink);
        sManager.stop();
    }

    /** Setup an esper query with a time window of 10 days that gives the size of events in the window:
     * Send in 5 events, with different times apart
     * We should have the following output:
     * t+0: start of window, 1 event
     * t+5: 2 events (initial + 2nd event)
     * t+year: 0 events (window reset), immediately followed by 1 events (3rd event only in window)
     * t+year+1day: 2 events (3rd and 4th)
     * t+year+15days: 0 events (window reset), followed by 1 event (new window established)
     *
     */
    @Test(timeout = 2*60*1000)
    public void testExternalTime() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2977, 7, 8, 10, 30, 40);
        // first time event on 2977-7-8
        AskEvent ask1 = EventTestBase.generateEquityAskEvent(1, cal.getTimeInMillis(), new Equity("AB1"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        cal.set(2977, 7, 13, 10, 30, 40);
        // next time event on 2977-7-13
        AskEvent ask2 = EventTestBase.generateEquityAskEvent(1, cal.getTimeInMillis(), new Equity("AB2"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        cal.set(2978, 7, 13, 10, 30, 40);
        // 3rd event year later on 2978-7-13, should reset window
        AskEvent ask3 = EventTestBase.generateEquityAskEvent(1, cal.getTimeInMillis(), new Equity("AB3"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        cal.set(2978, 7, 14, 10, 30, 40);
        // 4th event day later than 3rd - should cause a hit
        AskEvent ask4 = EventTestBase.generateEquityAskEvent(1, cal.getTimeInMillis(), new Equity("AB4"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        cal.set(2978, 7, 29, 11, 15, 40);
        // 5th event 15 days later  - window should be empty, reset to 1
        AskEvent ask5 = EventTestBase.generateEquityAskEvent(1, cal.getTimeInMillis(), new Equity("AB5"), "nyse", new BigDecimal("23"), new BigDecimal("23"));

        sManager.createModule(CEPEsperFactory.PROVIDER_URN, TEST_URN);
        CEPEsperProcessorMXBean esperBean = JMX.newMXBeanProxy(
                ModuleTestBase.getMBeanServer(),
                TEST_URN.toObjectName(),
                CEPEsperProcessorMXBean.class);
        sManager.stop(TEST_URN);
        esperBean.setUseExternalTime(true);
        assertTrue("accessor doesn't return right value", esperBean.isUseExternalTime());
        sManager.start(TEST_URN);

        Suggestion sug1 = new SimpleSuggestionFactory().createOrderSingleSuggestion();
        sug1.setIdentifier("acura");

        DataFlowID flow = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send 2 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[] { ask1, ask2, ask3, ask4, ask5}),
                // ESPER -> Sink: pattern
                new DataRequest(TEST_URN, new String[] {"select * from ask.win:time(10 days).std:size()"})
        });

        assertEquals("wrong size event", 1L, sSink.getNextData());  // AB1
        assertEquals("wrong size event", 2L, sSink.getNextData());  // AB1 + AB2
        assertEquals("wrong size event", 0L, sSink.getNextData());  // AB3 comes in to reset clock 1 year later and gives 0 size
        assertEquals("wrong size event", 1L, sSink.getNextData());  // AB3 also triggers new window/size of 1
        assertEquals("wrong size event", 2L, sSink.getNextData());  // AB4 comes in after AB3 to give 2
        assertEquals("wrong size event", 0L, sSink.getNextData());  // AB5 comes in 15 days later, resets window -> 0 size
        assertEquals("wrong size event", 1L, sSink.getNextData());  // AB5 sets new window of size 1

        sManager.cancel(flow);
    }

    @Test(timeout = 2*60*1000)
    public void testCancelBeforeFlowIsOver() throws Exception {
        sManager.createModule(CEPEsperFactory.PROVIDER_URN, TEST_URN);
        CEPEsperProcessorMXBean esperBean = JMX.newMXBeanProxy(
                ModuleTestBase.getMBeanServer(),
                TEST_URN.toObjectName(),
                CEPEsperProcessorMXBean.class);
        sManager.stop(TEST_URN);
        esperBean.setUseExternalTime(true);
        sManager.start(TEST_URN);
        DataFlowID flow = sManager.createDataFlow(new DataRequest[] {
                // Copier -> Esper: send  events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[] { 1, 2, 3, 4, 5, "vishel zaichik pogulyat"}),
                // ESPER -> Sink: pattern
                new DataRequest(TEST_URN, new String[] {"select * bid"})
        });
        sManager.cancel(flow);
        assertEquals("shouldn't have any statements", 0, esperBean.getStatementNames().length);
    }
    @Test
    public void testCancelWithoutData() throws Exception {
        sManager.createModule(CEPEsperFactory.PROVIDER_URN, TEST_URN);
        CEPEsperProcessorMXBean esperBean = JMX.newMXBeanProxy(
                ModuleTestBase.getMBeanServer(),
                TEST_URN.toObjectName(),
                CEPEsperProcessorMXBean.class);
        sManager.stop(TEST_URN);
        esperBean.setUseExternalTime(true);
        sManager.start(TEST_URN);
        String[] query = {"select * bid"};
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                //don't send any data so that we remove unprocessed statements.
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[0]),
                new DataRequest(TEST_URN, query)
        });
        sManager.cancel(flowID);
        //There should be no warning when canceling the data flow.
    }
}
