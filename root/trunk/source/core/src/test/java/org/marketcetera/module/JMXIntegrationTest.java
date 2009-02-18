package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.core.Util;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import javax.management.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.File;
import java.net.URL;
import java.net.URI;

/* $License$ */
/**
 * Tests JMX Integration. Tests all the Module Manager
 * services exposed via JMX. And the ability to expose Module
 * Factory and module mbean interfaces via JMX.
 * Also tests string to object conversion done as a part
 * of various jmx invocations.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class JMXIntegrationTest extends ModuleTestBase {
    @BeforeClass
    public static void setup() throws Exception {
        sManager = new ModuleManager();
        sManager.init();
        sManager.addSinkListener(sSink);
    }
    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }

    /**
     * Verifies the bean info for the module manager
     *
     * @throws Exception if there are errors
     */
    @Test
    public void managerBeanInfo() throws Exception {
        verifyBeanInfo(getMBeanServer().getMBeanInfo(getMMName()));
    }

    /**
     * Tests attributes of the module manager
     *
     * @throws Exception if there was an error
     */
    @Test
    public void managerAttributes() throws Exception {
        ModuleManagerMXBean mm = JMX.newMXBeanProxy(getMBeanServer(),
                getMMName(), ModuleManagerMXBean.class);
        assertContains(mm.getProviders(),
                SinkModuleFactory.PROVIDER_URN.getValue(),
                SingleModuleFactory.PROVIDER_URN.getValue(),
                MultipleModuleFactory.PROVIDER_URN.getValue(),
                ComplexModuleFactory.PROVIDER_URN.getValue(),
                JMXTestModuleFactory.PROVIDER_URN.getValue());
        assertContains(mm.getInstances(),
                SinkModuleFactory.INSTANCE_URN.getValue(),
                SingleModuleFactory.INSTANCE_URN.getValue()
        );
    }

    /**
     * Tests module manager operations.
     *
     * @throws Exception if there were errors
     */
    @Test(timeout = 60000)
    public void managerOperations() throws Exception {
        final ModuleManagerMXBean mm = JMX.newMXBeanProxy(getMBeanServer(),
                getMMName(), ModuleManagerMXBean.class);
        //Get module instances
        assertContains(mm.getModuleInstances(
                SingleModuleFactory.PROVIDER_URN.getValue()),
                SingleModuleFactory.INSTANCE_URN.getValue());
        final String invalidURN = "invalidURN";
        new ExpectedFailure<RuntimeException>(
                Messages.INVALID_URN_SCHEME.getText(invalidURN,
                        invalidURN,ModuleURN.SCHEME)){
            protected void run() throws Exception {
                mm.getModuleInstances(invalidURN);
            }
        };
        //Get Provider Info
        ModuleTestBase.assertProviderInfo(mm.getProviderInfo(
                SinkModuleFactory.PROVIDER_URN.getValue()),
                SinkModuleFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.SINK_MODULE_FACTORY_DESC.getText(),
                false,false);
        new ExpectedFailure<RuntimeException>(
                Messages.INVALID_URN_SCHEME.getText(invalidURN,
                        invalidURN, ModuleURN.SCHEME)){
            protected void run() throws Exception {
                mm.getProviderInfo(invalidURN);
            }
        };
        //Get Module Info
        ModuleTestBase.assertModuleInfo(mm.getModuleInfo(
                SingleModuleFactory.INSTANCE_URN.getValue()),
                SingleModuleFactory.INSTANCE_URN,
                ModuleState.CREATED,
                null, null, false, false, false, false, false);
        new ExpectedFailure<RuntimeException>(
                Messages.INVALID_URN_SCHEME.getText(invalidURN,
                        invalidURN, ModuleURN.SCHEME)){
            protected void run() throws Exception {
                mm.getModuleInfo(invalidURN);
            }
        };
        //create module
        final String urn = new ModuleURN(MultipleModuleFactory.PROVIDER_URN,
                "myinstance").toString();
        assertEquals(urn,mm.createModule(
                MultipleModuleFactory.PROVIDER_URN.getValue(), urn));
        new ExpectedFailure<RuntimeException>(
                Messages.INVALID_URN_SCHEME.getText(invalidURN,
                        invalidURN, ModuleURN.SCHEME)){
            protected void run() throws Exception {
                mm.createModule(invalidURN, null);
            }
        };
        //create module unsupported parameter conversion
        new ExpectedFailure<RuntimeException>(
                Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE.getText(
                        SingleParmModuleFactory.PROVIDER_URN.toString(), 0,
                        URI.class.getName(), String.class.getName())){
            protected void run() throws Exception {
                mm.createModule(SingleParmModuleFactory.PROVIDER_URN.getValue(),
                        "http://whatever");
            }
        };

        //stop module
        mm.stop(urn);
        new ExpectedFailure<RuntimeException>(
                Messages.MODULE_NOT_STOPPED_STATE_INCORRECT.getText(urn,
                        ModuleState.STOPPED,
                        ModuleState.STOPPABLE_STATES.toString())) {
            protected void run() throws Exception {
                mm.stop(urn);
            }
        };
        //start module
        mm.start(urn);
        new ExpectedFailure<RuntimeException>(
                Messages.MODULE_NOT_STARTED_STATE_INCORRECT.getText(urn,
                        ModuleState.STARTED,
                        ModuleState.STARTABLE_STATES.toString())) {
            protected void run() throws Exception {
                mm.start(urn);
            }
        };
        //stop module to be able to delete it
        mm.stop(urn);
        //delete module
        mm.deleteModule(urn);
        new ExpectedFailure<RuntimeException>(
                Messages.MODULE_NOT_FOUND.getText(urn)) {
            protected void run() throws Exception {
                mm.deleteModule(urn);
            }
        };
        //refresh
        mm.refresh();
        
        //test failures
        new ExpectedFailure<RuntimeException>(
                Messages.DATA_REQUEST_TOO_SHORT.getText(1)){
            protected void run() throws Exception {
                mm.createDataFlow(urn);
            }
        };
        new ExpectedFailure<RuntimeException>(
                Messages.DATA_REQUEST_TOO_SHORT.getText(1)){
            protected void run() throws Exception {
                mm.createDataFlow(urn, false);
            }
        };
        new ExpectedFailure<RuntimeException>(
                Messages.DATA_FLOW_NOT_FOUND.getText("noflow")) {
            protected void run() throws Exception {
                mm.getDataFlowInfo("noflow");
            }
        };
        new ExpectedFailure<RuntimeException>(
                Messages.DATA_FLOW_NOT_FOUND.getText("noflow")){
            protected void run() throws Exception {
                mm.cancel("noflow");
            }
        };
        //test data flow
        assertTrue(mm.getDataFlowHistory().isEmpty());
        ModuleURN procURN = new ModuleURN(
                ProcessorModuleFactory.PROVIDER_URN, "proc");
        //start the emitter.
        mm.start(EmitterModuleFactory.INSTANCE_URN.getValue());
        DataFlowID flowID = mm.createDataFlow(
                EmitterModuleFactory.INSTANCE_URN.getValue() + ";parameter" + "^" +
                        procURN.getValue() + ";passThru^");
        verifySimpleDataFlow(mm, procURN, flowID);
        flowID = mm.createDataFlow(
                EmitterModuleFactory.INSTANCE_URN.getValue() + ";parameter" + "^" +
                        procURN.getValue() + ";passThru^", true);
        verifySimpleDataFlow(mm, procURN, flowID);
        List<DataFlowInfo> infos = mm.getDataFlowHistory();
        assertTrue(String.valueOf(infos.size()), infos.size() > 0);
        //verify history
        assertEquals(ModuleManager.DEFAULT_MAX_FLOW_HISTORY,
                mm.getMaxFlowHistory());
        //wipe out history
        mm.setMaxFlowHistory(0);
        assertEquals(0, mm.getMaxFlowHistory());
        mm.setMaxFlowHistory(ModuleManager.DEFAULT_MAX_FLOW_HISTORY);
        assertTrue(mm.getDataFlowHistory().isEmpty());
    }

    /**
     * Tests parsing of string to data requests.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void parseDataRequests() throws Exception {
        new ExpectedFailure<RuntimeException>(
                Messages.EMPTY_STRING_DATA_REQUEST.getText()){
            protected void run() throws Exception {
                ModuleManagerMXBeanImpl.parseDataRequests(null);
            }
        };
        new ExpectedFailure<RuntimeException>(
                Messages.EMPTY_STRING_DATA_REQUEST.getText()){
            protected void run() throws Exception {
                ModuleManagerMXBeanImpl.parseDataRequests("");
            }
        };
        new ExpectedFailure<RuntimeException>(
                Messages.EMPTY_STRING_DATA_REQUEST.getText()){
            protected void run() throws Exception {
                ModuleManagerMXBeanImpl.parseDataRequests("   ");
            }
        };
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"))
        },"metc:a:b:c");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"))
        },"metc:a:b:c^");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"))
        },"metc:a:b:c;");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"),DataCoupling.ASYNC, null)
        },"metc:a:b:c;ASYNC");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"),DataCoupling.ASYNC, null)
        },"metc:a:b:c;ASYNC;");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"),
                        DataCoupling.ASYNC, " this ^ is ^ my ^ request")
        },"metc:a:b:c;ASYNC; this ^^ is ^^ my ^^ request");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c")),
                new DataRequest(new ModuleURN("metc:p:q:r"))
        },"metc:a:b:c^metc:p:q:r");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c")),
                new DataRequest(new ModuleURN("metc:p:q:r"))
        },"metc:a:b:c^metc:p:q:r^");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c")),
                new DataRequest(new ModuleURN("metc:p:q:r")),
                new DataRequest(new ModuleURN("metc:x:y:z"))
        },"metc:a:b:c^metc:p:q:r^metc:x:y:z");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"), "blah")
        },"metc:a:b:c;blah");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"), DataCoupling.SYNC,"blah")
        },"metc:a:b:c;SYNC;blah");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"), DataCoupling.ASYNC,"blah")
        },"metc:a:b:c;ASYNC;blah");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"), "sync;blah")
        },"metc:a:b:c;sync;blah");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"),"test'string;another"),
                new DataRequest(new ModuleURN("metc:p:q:r"),"another;test,string;"),
                new DataRequest(new ModuleURN("metc:x:y:z"),"thats;a,string.")
        },"metc:a:b:c;test'string;another^metc:p:q:r;another;test,string;^metc:x:y:z;thats;a,string.");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b:c"),"this ^ is special, request"),
                new DataRequest(new ModuleURN("metc:p:q:r"), " lots of ^, ^^, ^^^"),
                new DataRequest(new ModuleURN("metc:x:y:z"))
        },"metc:a:b:c;this ^^ is special, request^metc:p:q:r; lots of ^^, ^^^^, ^^^^^^^metc:x:y:z");
        verifyParsedRequests(new DataRequest[]{
                new DataRequest(new ModuleURN("metc:a:b"), " request "),
                new DataRequest(new ModuleURN("metc:p"), " another ")
        },"metc:a:b:; request ^ metc:p; another ^");
    }

    /**
     * Tests module factory's mbean registration
     *
     * @throws Exception if there were errors
     */
    @Test
    public void providerTests() throws Exception {
        // Verify that no bean exists for a factory that doesn't expose a
        // JMX Interface
        verifyFactory();
        verifyModule();
        //verify that updated value of factory attribute is correctly fetched
        assertEquals(2, JMX.newMXBeanProxy(getMBeanServer(),
                JMXTestModuleFactory.PROVIDER_URN.toObjectName(),
                JMXTestFactoryMXBean.class).getNumInstancesCreated());
    }

    /**
     * Verifies a simple data flow.
     *
     * @param inManager the module manager instance.
     * @param inProcURN the processor module instance URN.
     * @param inFlowID the data flow ID
     *
     * @throws InterruptedException if the test is interrupted
     */
    private void verifySimpleDataFlow(ModuleManagerMXBean inManager,
                                      ModuleURN inProcURN,
                                      DataFlowID inFlowID)
            throws InterruptedException {
        int nHistory = inManager.getDataFlowHistory().size();
        assertNotNull(inFlowID);
        //verify get data flows
        List<DataFlowID> ids = inManager.getDataFlows(true);
        assertEquals(1, ids.size());
        assertEquals(inFlowID, ids.get(0));
        //wait for the sink to receive data
        while(sSink.getData().length < 1)  {
            Thread.sleep(1000);
            SLF4JLoggerProxy.info(this," waiting for data to get to sink");
        }
        sSink.clear();
        //verify flow info
        DataFlowInfo info = inManager.getDataFlowInfo(inFlowID.getValue());
        DataFlowTest.assertFlowInfo(info, inFlowID, 3, true, false, null, null);
        verifyFlowSteps(inProcURN, info);
        //test data flow cancel
        inManager.cancel(inFlowID.getValue());
        //wait for the data flow to end.
        while(!inManager.getDataFlows(true).isEmpty()) {
            Thread.sleep(1000);
        }
        //verify the history
        List<DataFlowInfo> infos = inManager.getDataFlowHistory();
        assertEquals(nHistory + 1, infos.size());
        DataFlowTest.assertFlowInfo(infos.get(0), inFlowID, 3, true,
                true, null, null);
        verifyFlowSteps(inProcURN, info);
    }

    /**
     * Verifies data request parsing.
     *
     * @param inExpected the expected array of data requests.
     * @param inRequest the string to parse into data requests.
     */
    private void verifyParsedRequests(DataRequest[]inExpected, String inRequest) {
        DataRequest[] actual = ModuleManagerMXBeanImpl.parseDataRequests(inRequest);
        assertEquals(Arrays.asList(actual).toString(),inExpected.length,
                actual.length);
        for(int i = 0; i < inExpected.length; i++) {
            assertDataEquals(inExpected[i], actual[i]);
        }

    }

    /**
     * Asserts that the supplied data requests are equal.
     *
     * @param inExpected the expected data request.
     * @param inActual the actual data request.
     */
    private void assertDataEquals(DataRequest inExpected,
                                  DataRequest inActual) {
        assertEquals(inExpected.getRequestURN(), inActual.getRequestURN());
        assertEquals(inExpected.getCoupling(), inActual.getCoupling());
        assertEquals(inExpected.getData(), inActual.getData());
    }

    /**
     * Verifies the data flow steps for the supplied flow Info.
     *
     * @param inProcURN the processor module URN.
     * @param inInfo the data flow Info.
     */
    private void verifyFlowSteps(ModuleURN inProcURN, DataFlowInfo inInfo) {
        DataFlowTest.assertFlowStep(inInfo.getFlowSteps()[0],
                EmitterModuleFactory.INSTANCE_URN,
                true, 1, 0, null, false, 0, 0, null,
                EmitterModuleFactory.INSTANCE_URN,"parameter");
        DataFlowTest.assertFlowStep(inInfo.getFlowSteps()[1],
                inProcURN,
                true, 1, 0, null, true, 1, 0, null,
                inProcURN,"passThru");
        DataFlowTest.assertFlowStep(inInfo.getFlowSteps()[2],
                SinkModuleFactory.INSTANCE_URN,
                false, 0, 0, null, true, 1, 0, null,
                SinkModuleFactory.INSTANCE_URN, null);
    }

    /**
     * Verify module factory MXBean Features.
     *
     * @throws Exception if there were unexpected errors
     */
    private void verifyFactory() throws Exception {
        assertFalse(getMBeanServer().isRegistered(
                SingleModuleFactory.PROVIDER_URN.toObjectName()));
        //Find the factory bean for a factory that has a JMX Interface
        ObjectName name = JMXTestModuleFactory.PROVIDER_URN.toObjectName();
        assertTrue(getMBeanServer().isRegistered(name));
        //verify the bean info for the factory bean
        verifyBeanInfo(getMBeanServer().getMBeanInfo(name));
        JMXTestFactoryMXBean factory = JMX.newMXBeanProxy(getMBeanServer(),
                name, JMXTestFactoryMXBean.class);
        //invoke various operations to verify that they work
        assertEquals(0, factory.getNumInstancesCreated());
        assertEquals("default",factory.getNewInstanceAnnotation());
        String s = "test annotation";
        factory.setNewInstanceAnnotation(s);
        assertEquals(s,factory.getNewInstanceAnnotation());
    }

    /**
     * Verify module instance MXBean features.
     *
     * @throws Exception if there were unexpected errors
     */
    private void verifyModule() throws Exception {
        //verify that no bean exists for an instance that doesn't
        //expose a JMX Interface
        assertFalse(getMBeanServer().isRegistered(
                SingleModuleFactory.INSTANCE_URN.toObjectName()));

        // test string to type parameter translation when creating
        // a module
        ModuleURN urn = new ModuleURN(JMXTestModuleFactory.PROVIDER_URN,"testinstance");
        Boolean vBool = Boolean.TRUE;
        boolean vPBool = true;
        Byte vByte = (byte) 34;
        byte vPByte = 12;
        Character vChar = 'c';
        char vPChar = 'g';
        Short vShort = 14;
        short vPShort = 53;
        Integer vInt = 654;
        int vPInt = 433;
        Float vFloat = 34.34f;
        float vPFloat = 353.32f;
        Long vLong = 523423l;
        long vPLong = 2341234234l;
        Double vDouble = 342341.34;
        double vPDouble = 23465.34;
        String vString = "why?";
        BigDecimal vDecimal = new BigDecimal("3423412342698798.3432");
        BigInteger vInteger = new BigInteger("8909175843507945098345");
        File vFile = new File("/tmp/test");
        URL vURL = new URL("http://www.what.com");
        Properties vProperties = new Properties();
        vProperties.put("si","no");
        vProperties.put("go","no");

        final ModuleManagerMXBean mm = JMX.newMXBeanProxy(getMBeanServer(),
                getMMName(), ModuleManagerMXBean.class);

        //Create a new module with all the values specified as a
        //string parameter list
        String parameterList = getCreateParmString(urn, vBool, vPBool, vByte,
                vPByte, vChar, vPChar, vShort, vPShort, vInt, vPInt, vFloat,
                vPFloat, vLong, vPLong, vDouble, vPDouble, vString, vDecimal,
                vInteger, vFile, vURL, vProperties);
        assertEquals(urn.getValue(), mm.createModule(
                JMXTestModuleFactory.PROVIDER_URN.getValue(),
                parameterList));
        final ObjectName name = urn.toObjectName();
        assertTrue(getMBeanServer().isRegistered(name));
        //verify the bean info for the module instance bean
        verifyBeanInfo(getMBeanServer().getMBeanInfo(name));
        //get the JMX interface and verify all the values
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                name, JMXTestModuleMXBean.class);
        //verify getters and setters
        assertEquals(vBool, module.getBoolean());
        module.setBoolean(vBool = Boolean.FALSE);
        assertEquals(vBool, module.getBoolean());

        assertEquals(vPBool, module.isPrimBoolean());
        module.setPrimBoolean(vPBool = false);
        assertEquals(vPBool, module.isPrimBoolean());

        assertEquals(vByte, module.getByte());
        module.setByte(vByte = 64);
        assertEquals(vByte, module.getByte());

        assertEquals(vPByte,module.getPrimByte());
        module.setPrimByte(vPByte = 59);
        assertEquals(vPByte,module.getPrimByte());

        assertEquals(vChar,module.getCharacter());
        module.setCharacter(vChar = '9');
        assertEquals(vChar,module.getCharacter());

        assertEquals(vPChar,module.getPrimCharacter());
        module.setPrimCharacter(vPChar = ';');
        assertEquals(vPChar,module.getPrimCharacter());

        assertEquals(vShort,module.getShort());
        module.setShort(vShort = 3556);
        assertEquals(vShort,module.getShort());

        assertEquals(vPShort,module.getPrimShort());
        module.setPrimShort(vPShort = 7983);
        assertEquals(vPShort,module.getPrimShort());

        assertEquals(vInt,module.getInt());
        module.setInt(vInt = 98953);
        assertEquals(vInt,module.getInt());

        assertEquals(vPInt,module.getPrimInt());
        module.setPrimInt(vPInt = 94385);
        assertEquals(vPInt,module.getPrimInt());

        assertEquals(vFloat,module.getFloat());
        module.setFloat(vFloat = 90908.34f);
        assertEquals(vFloat,module.getFloat());

        assertEquals(vPFloat,module.getPrimFloat(),0.01);
        module.setPrimFloat(vPFloat = 984398.59f);
        assertEquals(vPFloat,module.getPrimFloat(),0.01);

        assertEquals(vLong,module.getLong());
        module.setLong(vLong = 905902345l);
        assertEquals(vLong,module.getLong());

        assertEquals(vPLong,module.getPrimLong());
        module.setPrimLong(vPLong = 8798437209l);
        assertEquals(vPLong,module.getPrimLong());

        assertEquals(vDouble,module.getDouble());
        module.setDouble(vDouble = 9089089842.9889);
        assertEquals(vDouble,module.getDouble());

        assertEquals(vPDouble,module.getPrimDouble(),0.01);
        module.setPrimDouble(vPDouble = 893423423.34534);
        assertEquals(vPDouble,module.getPrimDouble(),0.01);

        assertEquals(vString,module.getString());
        module.setString(vString = "theory");
        assertEquals(vString,module.getString());

        assertEquals(vDecimal,module.getDecimal());
        module.setDecimal(vDecimal = new BigDecimal("4906754982789345.4589970284"));
        assertEquals(vDecimal,module.getDecimal());

        assertEquals(vInteger,module.getInteger());
        module.setInteger(vInteger = new BigInteger("89237897058967038475"));
        assertEquals(vInteger,module.getInteger());

        assertEquals(vFile.toString(),module.getFile());
        module.setFile((vFile = new File("/opt/not/got")).toString());
        assertEquals(vFile.toString(),module.getFile());

        assertEquals(vURL.toString(),module.getURL());
        module.setURL((vURL = new URL("http://marketcetera.org")).toString());
        assertEquals(vURL.toString(),module.getURL());

        assertEquals(Util.propertiesToString(vProperties), module.getProperties());

        // verify that the updated factory annotation got copied to the instance
        assertEquals("test annotation", module.getFactoryAnnotation());

        //delete this module and verify that the bean is unregistered
        mm.deleteModule(urn.getValue());
        assertFalse(getMBeanServer().isRegistered(urn.toObjectName()));

        //try creating a module with extra parameters and verify that
        //the extra parameter is ignored.
        assertEquals(urn.getValue(), mm.createModule(
                JMXTestModuleFactory.PROVIDER_URN.getValue(),
                parameterList + ",extraParameter,another"));

        //now verify failure when invoking a mx bean method.
        new ExpectedFailure<RuntimeException>("error"){
            protected void run() throws Exception {
                JMX.newMXBeanProxy(getMBeanServer(), name,
                        JMXTestModuleMXBean.class).
                        setFactoryAnnotation("error");
            }
        };

        // Now verify that creating the module without specifying a value
        // for the primitive type fails.

        //boolean error
        verifyStringConvError(mm, false, (byte)53, 'e',(short)3434,34235, 98.34f,234234231l,1989492.342,2);
        //byte error
        verifyStringConvError(mm, true, (byte)0, 'e',(short)3434,34235, 98.34f,234234231l,1989492.342,4);
        //char error
        verifyStringConvError(mm, true, (byte)53, '\u0000',(short)3434,34235, 98.34f,234234231l,1989492.342,6);
        //short error
        verifyStringConvError(mm, true, (byte)53, 'e',(short)0,34235, 98.34f,234234231l,1989492.342,8);
        //int error
        verifyStringConvError(mm, true, (byte)53, 'e',(short)3434,0, 98.34f,234234231l,1989492.342,10);
        //float error
        verifyStringConvError(mm, true, (byte)53, 'e',(short)3434,34235, 0f,234234231l,1989492.342,12);
        //long error
        verifyStringConvError(mm, true, (byte)53, 'e',(short)3434,34235, 98.34f,0,1989492.342,14);
        //double error
        verifyStringConvError(mm, true, (byte)53, 'e',(short)3434,34235, 98.34f,234234231l,0,16);
    }

    /**
     * Verify module creation error due to string parsing errors when
     * converting the string parameter value to appropriate types needed
     * by the module factory.
     *
     * @param manager the module manager bean
     * @param vBool the boolean value
     * @param vByte the byte value
     * @param vChar the char value
     * @param vShort the short value
     * @param vInt the int value
     * @param vFloat the float value
     * @param vLong the long value
     * @param vDouble the double value
     * @param errIdx the error Idx.
     *
     * @throws Exception if there were unexpected errors
     */
    private static void verifyStringConvError(final ModuleManagerMXBean manager,
                                              final boolean vBool,
                                              final byte vByte,
                                              final char vChar,
                                              final short vShort,
                                              final int vInt,
                                              final float vFloat,
                                              final long vLong,
                                              final double vDouble,
                                              int errIdx)
            throws Exception {
        new ExpectedFailure<RuntimeException>(
                Messages.CANNOT_CREATE_MODULE_PARAM_CONVERT_ERROR.
                        getText(JMXTestModuleFactory.PROVIDER_URN.getValue(),
                        errIdx),false) {
            protected void run() throws Exception {
                manager.createModule(JMXTestModuleFactory.PROVIDER_URN.getValue(),
                        getCreateParmString(
                                new ModuleURN(JMXTestModuleFactory.PROVIDER_URN,
                                        "failinstance"),
                                Boolean.FALSE, vBool, (byte) 89,
                                vByte, 'm', vChar, (short) 6543, vShort, 43234,
                                vInt, 4324.43f, vFloat, 3123124212l, vLong, 2432432.43242,
                                vDouble, "whatever", new BigDecimal("65.45"),
                                new BigInteger("45398"), new File("/whatever"),
                                new URL("http://market.org"), new Properties()));
            }
        };
    }

    /**
     * Creates the string parameter to use for creating the module, given the
     * value of various parameters.
     */
    private static String getCreateParmString(ModuleURN inUrn,
                                              Boolean inVBool, boolean inVPBool,
                                              Byte inVByte, byte inVPByte,
                                              Character inVChar, char inVPChar,
                                              Short inVShort, short inVPShort,
                                              Integer inVInt, int inVPInt,
                                              Float inVFloat, float inVPFloat,
                                              Long inVLong, long inVPLong,
                                              Double inVDouble, double inVPDouble,
                                              String inVString,
                                              BigDecimal inVDecimal,
                                              BigInteger inVInteger,
                                              File inVFile, URL inVURL,
                                              Properties inVProperties) {
        final char c = ',';
        StringBuilder sb = new StringBuilder();
        sb.append(inUrn);
        sb.append(c).append(inVBool);
        sb.append(c);
        //conditionally assign primitive values to aid negative tests
        if(inVPBool) {
            sb.append(inVPBool);
        }
        sb.append(c).append(inVByte);
        sb.append(c);
        if(inVPByte != 0) {
            sb.append(inVPByte);
        }
        sb.append(c).append(inVChar);
        sb.append(c);
        if(inVPChar != 0) {
            sb.append(inVPChar);
        }
        sb.append(c).append(inVShort);
        sb.append(c);
        if(inVPShort != 0) {
            sb.append(inVPShort);
        }
        sb.append(c).append(inVInt);
        sb.append(c);
        if(inVPInt != 0) {
            sb.append(inVPInt);
        }
        sb.append(c).append(inVFloat);
        sb.append(c);
        if(inVPFloat != 0) {
            sb.append(inVPFloat);
        }
        sb.append(c).append(inVLong);
        sb.append(c);
        if(inVPLong != 0) {
            sb.append(inVPLong);
        }
        sb.append(c).append(inVDouble);
        sb.append(c);
        if(inVPDouble != 0) {
            sb.append(inVPDouble);
        }
        sb.append(c).append(inVString);
        sb.append(c).append(inVDecimal);
        sb.append(c).append(inVInteger);
        sb.append(c).append(inVFile);
        sb.append(c).append(inVURL);
        sb.append(c).append(Util.propertiesToString(inVProperties));
        return sb.toString();
    }

    /**
     * Returns module manager MBean's name.
     *
     * @return module manager MBean's name.
     *
     * @throws MalformedObjectNameException if there were errors
     */
    private ObjectName getMMName() throws MalformedObjectNameException {
        return new ObjectName(ModuleManager.MODULE_MBEAN_NAME);
    }


    private static void assertContains(Collection<String> inContainer,
                                       String... inContents) {
        List<String> urnList = Arrays.asList(inContents);
        HashSet<String> contents = new HashSet<String>(urnList);
        contents.removeAll(inContainer);
        assertTrue(contents.toString(), inContainer.containsAll(urnList));
    }

    private static ModuleManager sManager;
    private static Sink sSink = new Sink();

}
