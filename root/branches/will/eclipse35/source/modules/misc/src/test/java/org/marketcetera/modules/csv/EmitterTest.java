package org.marketcetera.modules.csv;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.net.URL;
import java.net.MalformedURLException;

/* $License$ */
/**
 * Tests the {@link CSVEmitter} module.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class EmitterTest extends ModuleTestBase {

    @BeforeClass
    public static void logSetup() {
        LoggerConfiguration.logSetup();
    }
    /**
     * Verifies failures when incorrect request parameters are provided.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void invalidRequests() throws Exception {
        new ExpectedFailure<IllegalRequestParameterValue>(
                org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE,
                CSVEmitterFactory.INSTANCE_URN.toString(), null){
            protected void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{
                        new DataRequest(CSVEmitterFactory.INSTANCE_URN,null)
                });
            }
        };
        final Object invalidParam = new Object();
        new ExpectedFailure<UnsupportedRequestParameterType>(
                org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE,
                CSVEmitterFactory.INSTANCE_URN.toString(),
                invalidParam.getClass().getName()){
            protected void run() throws Exception {
                mManager.createDataFlow(new DataRequest[]{
                        new DataRequest(CSVEmitterFactory.INSTANCE_URN,invalidParam)
                });
            }
        };
    }
    /**
     * Tests the error when invalid input file is provided.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test(timeout = 60000)
    public void invalidFile() throws Exception {
        assertTrue(mManager.getDataFlows(true).isEmpty());
        File csv = new File("does/not/exist");
        assertFalse(csv.exists());
        DataFlowID id = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CSVEmitterFactory.INSTANCE_URN,
                        csv.getAbsolutePath())
        });
        while(!mManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, id, 2, true, true, null,
                CSVEmitterFactory.INSTANCE_URN);
        DataFlowStep flowStep = info.getFlowSteps()[0];
        assertEquals(CSVEmitterFactory.INSTANCE_URN, flowStep.getModuleURN());
        assertEquals(true, flowStep.isEmitter());
        assertEquals(0, flowStep.getNumEmitted());
        assertEquals(1, flowStep.getNumEmitErrors());
        Pattern p = Pattern.compile(Messages.UNEXPECTED_ERROR.getText(".*"));
        assertTrue(flowStep.getLastEmitError(),
                p.matcher(flowStep.getLastEmitError()).matches());
        assertEquals(false, flowStep.isReceiver());
        assertEquals(0, flowStep.getNumReceived());
        assertEquals(0, flowStep.getNumReceiveErrors());
        assertEquals(null, flowStep.getLastReceiveError());
        assertEquals(CSVEmitterFactory.INSTANCE_URN,
                flowStep.getRequest().getRequestURN());
        assertEquals(csv.getAbsolutePath(), flowStep.getRequest().getData());
        assertFlowStep(info.getFlowSteps()[1], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 0, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
    }

    /**
     * Verifies the failure when there's insufficient input.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test(timeout = 60000)
    public void insufficientInput() throws Exception {
        assertTrue(INVALID_CSV_FILE.getAbsolutePath(),  INVALID_CSV_FILE.exists());
        DataFlowID id = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CSVEmitterFactory.INSTANCE_URN,
                        INVALID_CSV_FILE.getAbsolutePath())
        });
        //wait for data flow to complete
        while(!mManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        DataFlowInfo info = history.get(0);
        assertFlowInfo(info, id, 2, true, true, null,
                CSVEmitterFactory.INSTANCE_URN);
        assertFlowStep(info.getFlowSteps()[0], CSVEmitterFactory.INSTANCE_URN,
                true, 0, 1, Messages.INSUFFICIENT_DATA.getText(1), false, 0,
                0, null, CSVEmitterFactory.INSTANCE_URN,
                INVALID_CSV_FILE.getAbsolutePath());
        assertFlowStep(info.getFlowSteps()[1], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 0, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
    }
    /**
     * Verifies a data flow request with a file path string parameter.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVStringFile() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        //verify that creating a URL out of file path fails
        new ExpectedFailure<MalformedURLException>(null){
            protected void run() throws Exception {
                new URL(VALID_CSV_FILE.getAbsolutePath());
            }
        };
        //Try the file path
        checkEmitCSV(VALID_CSV_FILE.getAbsolutePath(), false);
    }
    /**
     * Verifies a reverse data flow request with a file path string parameter.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVStringFileReverse() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        //verify that creating a URL out of file path, with reverse prefix, fails
        new ExpectedFailure<MalformedURLException>(null){
            protected void run() throws Exception {
                new URL(CSVEmitter.PREFIX_REVERSE + VALID_CSV_FILE.getAbsolutePath());
            }
        };
        //Try reverse
        checkEmitCSV(CSVEmitter.PREFIX_REVERSE + VALID_CSV_FILE.getAbsolutePath(), true);
    }
    /**
     * Verifies a data flow request with a URL string parameter.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVStringURL() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        //Try the URL
        checkEmitCSV(VALID_CSV_FILE.toURI().toURL().toString(), false);
    }
    /**
     * Verifies a reverse data flow request with a URL string parameter.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVStringURLReverse() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        //Try reverse
        checkEmitCSV(CSVEmitter.PREFIX_REVERSE + VALID_CSV_FILE.toURI().
                toURL().toString(), true);
    }

    /**
     * Verifies a data flow request with a File parameter.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVFile() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        checkEmitCSV(VALID_CSV_FILE, false);
    }
    /**
     * Verifies a data flow request with a URL parameter.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test(timeout = 60000)
    public void emitCSVURL() throws Exception {
        assertTrue(VALID_CSV_FILE.getAbsolutePath(), VALID_CSV_FILE.exists());
        checkEmitCSV(VALID_CSV_FILE.toURI().toURL(), false);
    }

    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void info() throws Exception {
        assertProviderInfo(mManager, CSVEmitterFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(mManager, CSVEmitterFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false,
                true, false, true, false);
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

    /**
     * Runs a data flow with the supplied request parameter and verifies
     * that data is correctly emitted by the csv emitter.
     *
     * @param inRequestParam the request parameter to the emitter.
     *
     * @param inReverse
     * @throws Exception if there were errors
     */
    private void checkEmitCSV(Object inRequestParam, boolean inReverse)
            throws Exception {
        assertTrue(mManager.getDataFlows(true).isEmpty());
        FirstAndLastTracker tracker = new FirstAndLastTracker();
        mManager.addSinkListener(tracker);
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CSVEmitterFactory.INSTANCE_URN,
                        inRequestParam)
        });
        DataFlowInfo info = mManager.getDataFlowInfo(flowID);
        assertFlowInfo(info, flowID, 2, true, false, null, null);
        //Wait until data flow ends
        while(!mManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        List<DataFlowInfo> history = mManager.getDataFlowHistory();
        assertEquals(1, history.size());
        info = history.get(0);
        assertFlowInfo(info, flowID, 2, true, true, null,
                CSVEmitterFactory.INSTANCE_URN);
        assertFlowStep(info.getFlowSteps()[0], CSVEmitterFactory.INSTANCE_URN,
                true, NUM_ROWS, 1, Messages.NO_MORE_DATA.getText(), false, 0,
                0, null, CSVEmitterFactory.INSTANCE_URN, inRequestParam.toString());
        assertFlowStep(info.getFlowSteps()[1], SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, NUM_ROWS, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
        assertNotNull(tracker.getFirst());
        assertTrue(tracker.getFirst() instanceof Map);
        assertEquals(createMap("2008-10-02","16.77","16.85","15.54",
                "15.58","23416200","15.58"),inReverse? tracker.getLast(): tracker.getFirst());
        assertNotNull(tracker.getLast());
        assertTrue(tracker.getLast() instanceof Map);
        assertEquals(createMap("1996-04-12","25.25","43.00","24.50",
                "33.00","408720000","1.38"),inReverse? tracker.getFirst(): tracker.getLast());
    }

    /**
     * Creates a map with expected key value pairs in the test csv file.
     *
     * @return the map with expected key value pairs.
     */
    private Map createMap(String inDate, String inOpen, String inHigh,
                          String inLow, String inClose, String inVolume,
                          String inAdjClose) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("Date",inDate);
        map.put("Open",inOpen);
        map.put("High",inHigh);
        map.put("Low",inLow);
        map.put("Close",inClose);
        map.put("Volume",inVolume);
        map.put("Adj Close",inAdjClose);
        return map;
    }

    /**
     * A listener that tracks the first and the last objects that it
     * receives.
     */
    private static class FirstAndLastTracker implements SinkDataListener {
        public void receivedData(DataFlowID inFlowID, Object inData) {
            mLast = inData;
            if(mFirst == null) {
                mFirst = inData;
            }
        }

        public Object getFirst() {
            return mFirst;
        }

        public Object getLast() {
            return mLast;
        }

        private volatile Object mFirst;
        private volatile Object mLast;
    }
    private ModuleManager mManager;
    //Number or records in the csv file.
    private static final int NUM_ROWS = 3141;
    private static final File INPUTS_DIR = new File("src/test/sample_data","inputs");
    private static final File VALID_CSV_FILE = new File(INPUTS_DIR,"table.csv");
    private static final File INVALID_CSV_FILE = new File(INPUTS_DIR,"insufficient_rows.csv");
}
