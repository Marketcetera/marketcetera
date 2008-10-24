package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.management.JMX;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Date;
import java.math.BigInteger;
import java.math.BigDecimal;

/* $License$ */
/**
 * Tests sink module features.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class SinkModuleTest extends ModuleTestBase {
    @BeforeClass
    public static void setup() throws Exception {
        sManager = new ModuleManager();
        sManager.init();
        sSinkBean = JMX.newMXBeanProxy(
                ManagementFactory.getPlatformMBeanServer(),
                SinkModuleFactory.INSTANCE_URN.toObjectName(),
                SinkModuleMXBean.class);
    }
    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }

    /**
     * Tests sink module reset feature.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void reset() throws Exception {
        sSinkBean.resetStats();
        Map<DataFlowID,Integer> flowStats = sSinkBean.getDataFlowStats();
        assertTrue(flowStats.toString(), flowStats.isEmpty());
        Map<String, Integer> typeStats = sSinkBean.getTypeStats();
        assertTrue(typeStats.toString(), typeStats.isEmpty());
    }

    /**
     * Tests sink module functionality within a data flow.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void flow() throws Exception {
        Sink sink1 = new Sink();
        //Get sink1 to throw exceptions to verify that any exceptions
        //thrown by it are ignored and do not affect delivery to other
        //listeners.
        sink1.setThrowException(true);
        Sink sink2 = new Sink();
        Sink sink3 = new Sink();
        sManager.addSinkListener(sink1);
        sManager.addSinkListener(sink2);
        sManager.addSinkListener(sink3);
        assertTrue(sManager.removeSinkListener(sink2));
        assertFalse(sManager.removeSinkListener(sink2));
        Object[] data = new Object[]{
                (byte)123,
                'e',
                (short)3432,
                543423,
                3423.43f,
                34324239849l,
                4323422.342343,
                new Date(),
                new BigInteger("34234234234234"),
                new BigDecimal("23423423432690809.234890734"),
                "This is a test string",
                true,
                false
        };
        ModuleURN procURN = new ModuleURN(ProcessorModuleFactory.PROVIDER_URN,
                "sink");
        sManager.start(EmitterModuleFactory.INSTANCE_URN);
        final DataRequest[] requests = {
                new DataRequest(EmitterModuleFactory.INSTANCE_URN, data),
                new DataRequest(procURN, "passThru")
        };
        DataFlowID flowID = sManager.createDataFlow(requests);
        //wait for the data to reach the first listener
        sink1.waitUntilTerminator();
        sManager.cancel(flowID);
        //verify that the listener that was removed didn't receive any data
        assertEquals(0, sink2.getData().length);
        //verify the data received by each of the listeners
        verify(flowID, data, sink1.getData());
        verify(flowID, data, sink3.getData());
        verifyJMXStats(data, flowID);
        sink1.clear();
        sink3.clear();
        sSinkBean.resetStats();
        //now remove the other and carry out the data flow again
        sManager.removeSinkListener(sink3);
        flowID = sManager.createDataFlow(requests);
        sink1.waitUntilTerminator();
        sManager.cancel(flowID);
        assertEquals(0, sink2.getData().length);
        assertEquals(0, sink3.getData().length);
        verify(flowID, data, sink1.getData());
        verifyJMXStats(data, flowID);
    }

    private void verifyJMXStats(Object[] inData, DataFlowID inFlowID) {
        Map<DataFlowID, Integer> flowStats = sSinkBean.getDataFlowStats();
        assertEquals(1, flowStats.size());
        assertEquals(inData.length, (int)flowStats.get(inFlowID));
        Map<String, Integer> typeStats = sSinkBean.getTypeStats();
        assertEquals(inData.length - 1 , typeStats.size());
        for(String c: typeStats.keySet()) {
            assertEquals(c,
                    Boolean.class.getName().equals(c)
                            ? 2
                            : 1,
                    (int)typeStats.get(c));
        }
    }

    private void verify(DataFlowID inID, Object[] inExpected, FlowData[] inData) {
        assertEquals(inExpected.length - 1, inData.length);
        for(int i = 0; i < inData.length; i++) {
            assertEquals(inID, inData[i].getFirstMember());
            assertEquals(inExpected[i], inData[i].getSecondMember());
        }
    }
    private static ModuleManager sManager;
    private static SinkModuleMXBean sSinkBean;
}
