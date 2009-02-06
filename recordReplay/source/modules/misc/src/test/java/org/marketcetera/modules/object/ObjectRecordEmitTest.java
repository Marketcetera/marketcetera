package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.file.CopyBytesUtils;
import org.marketcetera.util.file.CopyCharsUtils;
import org.marketcetera.module.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertTrue;import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

/* $License$ */
/**
 * Tests {@link ObjectEmitter} & {@link ObjectRecorder}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ObjectRecordEmitTest extends ModuleTestBase {
    /**
     * Verifies the emitter provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void emitterInfo() throws Exception {
        assertProviderInfo(mManager, ObjectEmitterFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.EMITTER_PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(mManager, ObjectEmitterFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false,
                true, false, true, false);
    }
    /**
     * Verifies the receiver provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void receiverInfo() throws Exception {
        assertProviderInfo(mManager, ObjectRecorderFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.RECORDER_PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(mManager, ObjectRecorderFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false,
                true, true, true, false);
    }
    /**
     * Verifies failures when incorrect request parameters are provided to
     * the emitter.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void invalidRequests() throws Exception {
        new ExpectedFailure<IllegalRequestParameterValue>(
                org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                ObjectEmitterFactory.INSTANCE_URN.toString(), null){
            protected void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{
                        new DataRequest(ObjectEmitterFactory.INSTANCE_URN,null)
                });
            }
        };
        final Object invalidParam = new Object();
        new ExpectedFailure<UnsupportedRequestParameterType>(
                org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE,
                ObjectEmitterFactory.INSTANCE_URN.toString(),
                invalidParam.getClass().getName()){
            protected void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{
                        new DataRequest(ObjectEmitterFactory.INSTANCE_URN,invalidParam)
                });
            }
        };
    }

    /**
     * Tests the data round trip from recorder to file and from file through
     * the emitter back.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void roundtrip() throws Exception {
        //Have an array of data to shove into the file
        Object[]objs = new Object[]{
                Integer.MAX_VALUE,
                BigInteger.TEN,
                BigDecimal.TEN,
                new LinkedList(),
                new HashMap()
        };
        File tmpFile = File.createTempFile("test",".obj");
        tmpFile.deleteOnExit();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpFile));
        for(Object obj: objs) {
            oos.writeObject(obj);
        }
        oos.close();
        //Add a sink listener
        Sink sink = new Sink();
        mManager.addSinkListener(sink);
        //Wire the emitter and recorder in a data flow
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(ObjectEmitterFactory.PROVIDER_URN, tmpFile.getAbsolutePath()),
                new DataRequest(ObjectRecorderFactory.PROVIDER_URN)
        });
        //Wait until the flow completes
        while(!mManager.getDataFlows(false).isEmpty()) {
            Thread.sleep(500);
        }
        //Verify the output file
        File output = new File(System.getProperty("java.io.tmpdir"),
                "flow-"+flowID.toString()+".obj");
        output.deleteOnExit();
        assertTrue(output.getAbsolutePath(), output.isFile());
        //verify the file contents
        assertArrayEquals(CopyBytesUtils.copy(tmpFile.getAbsolutePath()),
                CopyBytesUtils.copy(output.getAbsolutePath()));
        //verify the piped output
        Object[] data = sink.getData();
        assertEquals(objs.length, data.length);
        int i = 0;
        for(Object obj:objs) {
            assertEquals(obj, data[i++]);
        }
    }

    /**
     * Tests the the emitter stops the data flow with the appropriate error
     * when it's unable to read data from the input file.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void emitterIncorrectInput() throws Exception {
        File tmpFile = File.createTempFile("test",".obj");
        CopyCharsUtils.copy("whatever".toCharArray(), tmpFile.getAbsolutePath());
        tmpFile.deleteOnExit();
        //Add a sink listener
        Sink sink = new Sink();
        mManager.addSinkListener(sink);
        //Wire the emitter and recorder in a data flow
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(ObjectEmitterFactory.PROVIDER_URN, tmpFile.getAbsolutePath())
        });
        //Wait until the flow completes
        while(!mManager.getDataFlows(false).isEmpty()) {
            Thread.sleep(500);
        }
        //Verify we didn't receive any data
        assertEquals(0, sink.getData().length);
        //Verify flow failure
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, flowID, 2, true, true, null, ObjectEmitterFactory.INSTANCE_URN);
        assertFlowStep(info.getFlowSteps()[0],
                ObjectEmitterFactory.INSTANCE_URN, true, 0, 1,
                Messages.UNEXPECTED_ERROR.getText(""),
                false, 0, 0, null, ObjectEmitterFactory.PROVIDER_URN,
                tmpFile.getAbsolutePath());
    }
    private static class Sink implements SinkDataListener {
        public void receivedData(DataFlowID inFlowID, Object inData) {
            mObjects.add(inData);
        }
        public Object[] getData() {
            return mObjects.toArray();
        }
        private List<Object> mObjects = new LinkedList<Object>();
    }
    @Before
    public void setup() throws Exception {
        mManager = new ModuleManager();
        mManager.init();
    }
    @After
    public void cleanup() throws Exception {
        mManager.stop();
        mManager = null;
    }
    private ModuleManager mManager;
}
