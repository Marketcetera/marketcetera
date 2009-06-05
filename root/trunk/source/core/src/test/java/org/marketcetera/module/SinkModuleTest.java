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
import java.util.List;
import java.util.ArrayList;
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

    /**
     * Tests {@link BlockingSinkDataListener}.
     *
     * @throws Exception if there was an error.
     */
    @Test(timeout = 10000)
    public void blockingSinkListenerTest() throws Exception {
        final BlockingSinkDataListener listener = new BlockingSinkDataListener();
        assertEquals(0 ,listener.size());
        final List<Object> list = new ArrayList<Object>();
        Thread thread = new Thread("testThread"){
            @Override
            public void run() {
                try {
                    list.add(listener.getNextData());
                } catch (InterruptedException ignore) {
                }
            }
        };
        thread.start();
        //wait until thread is blocked
        while(thread.getState() != Thread.State.WAITING) {
            Thread.sleep(1000);
        }
        assertTrue(list.isEmpty());
        //Add an item to the listener
        Object data = "data";
        listener.receivedData(null, data);
        //wait for the thread to terminate
        while(thread.getState() != Thread.State.TERMINATED) {
            Thread.sleep(1000);
        }
        //verify that the data is received.
        assertFalse(list.toString(), list.isEmpty());
        assertEquals(1, list.size());
        assertEquals(data, list.get(0));
        assertEquals(0 ,listener.size());
        //Now test it with the sink
        sManager.addSinkListener(listener);
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, data)
        });
        //wait until the data is received
        assertEquals(data, listener.getNextData());
        //Terminate the data flow
        sManager.cancel(flowID);
        sManager.removeSinkListener(listener);
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
