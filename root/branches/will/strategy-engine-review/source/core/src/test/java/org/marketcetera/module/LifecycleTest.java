package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.AfterClass;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Properties;
import java.io.File;
import java.net.URL;
import java.net.URI;
import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * Tests Module provider and module lifecycle & information reporting.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class LifecycleTest extends ModuleTestBase {
    @BeforeClass
    public static void setup() throws Exception {
        SingletonModule.clearInstances();
        sManager = new ModuleManager();
        sManager.init();
    }
    @AfterClass
    public static void cleanup() throws Exception {
        sManager.stop();
    }
    @Before
    public void clear() throws Exception {
        MultipleModule.clearInstances();
    }

    /**
     * Tests get providers API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void getProviders() throws Exception {
        //Should see the following providers
        checkAllProviders(sManager.getProviders());
    }

    /**
     * Tests the get provider info API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void getProviderInfo() throws Exception {
        //Now verify the Provider Info for each of the providers
        assertProviderInfo(SinkModuleFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.SINK_MODULE_FACTORY_DESC.getText(),
                false,false);
        assertProviderInfo(SingleModuleFactory.PROVIDER_URN,
                new String[0], new Class[0],
                TestMessages.SINGLE_1_PROVIDER.getText(),
                false, false);
        assertProviderInfo(MultipleModuleFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName()},
                new Class[]{ModuleURN.class},
                TestMessages.MULTIPLE_1_PROVIDER.getText(),
                true, true);
        assertProviderInfo(ComplexModuleFactory.PROVIDER_URN,
                new String[]{String.class.getName(), File.class.getName(),
                        URL.class.getName(), Date.class.getName()},
                new Class[]{String.class, File.class,
                        URL.class, Date.class},
                TestMessages.MULTIPLE_2_PROVIDER.getText(),
                false, true);
        assertProviderInfo(JMXTestModuleFactory.PROVIDER_URN,
                new String[]{ModuleURN.class.getName(),
                        Boolean.class.getName(), Boolean.TYPE.getName(),
                        Byte.class.getName(), Byte.TYPE.getName(),
                        Character.class.getName(), Character.TYPE.getName(),
                        Short.class.getName(), Short.TYPE.getName(),
                        Integer.class.getName(), Integer.TYPE.getName(),
                        Float.class.getName(), Float.TYPE.getName(),
                        Long.class.getName(), Long.TYPE.getName(),
                        Double.class.getName(), Double.TYPE.getName(),
                        String.class.getName(),
                        BigDecimal.class.getName(),
                        BigInteger.class.getName(),
                        File.class.getName(),
                        URL.class.getName(),
                        Properties.class.getName()},
                new Class[]{ModuleURN.class,
                        Boolean.class, Boolean.TYPE,
                        Byte.class, Byte.TYPE,
                        Character.class, Character.TYPE,
                        Short.class, Short.TYPE,
                        Integer.class, Integer.TYPE,
                        Float.class, Float.TYPE,
                        Long.class, Long.TYPE,
                        Double.class, Double.TYPE,
                        String.class,
                        BigDecimal.class,
                        BigInteger.class,
                        File.class,
                        URL.class,
                        Properties.class},
                TestMessages.MULTIPLE_3_PROVIDER.getText(),
                false, true);
        final ModuleURN unknownProvider = new ModuleURN("metc:test:unknown");
        new ExpectedFailure<ProviderNotFoundException>(
                Messages.PROVIDER_NOT_FOUND,
                unknownProvider.toString()){
            protected void run() throws Exception {
                sManager.getProviderInfo(unknownProvider);
            }
        };
    }

    /**
     * Tests get module instances API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void getModuleInstances() throws Exception {
        assertContains(sManager.getModuleInstances(null),
                new ModuleURN[]{
                        SinkModuleFactory.INSTANCE_URN,
                        SingleModuleFactory.INSTANCE_URN});
        assertTrue(sManager.getModuleInstances(
                new ModuleURN("metc:not:exists")).isEmpty());
    }

    /**
     * Tests get module info API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void getModuleInfo() throws Exception {
        assertModuleInfo(SinkModuleFactory.INSTANCE_URN,
                ModuleState.STARTED,
                null,
                null,false,true, true, false, false);
        assertModuleInfo(SingleModuleFactory.INSTANCE_URN,
                ModuleState.CREATED,
                null,
                null,false,false, false, false, false);
        //Verify the singleton module instance.
        assertEquals(1,SingletonModule.getInstances());
        assertFalse(SingletonModule.getInstance().isStartInvoked());
        assertFalse(SingletonModule.getInstance().isStopInvoked());
    }

    /**
     * Tests that attempt to create another instance of singleton module fails.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void createSingletonModuleFail() throws Exception {
        new ExpectedFailure<ModuleCreationException>(
                Messages.CANNOT_CREATE_SINGLETON,
                SingleModuleFactory.PROVIDER_URN.toString(),
                SingleModuleFactory.INSTANCE_URN.toString()){
            protected void run() throws Exception {
                sManager.createModule(SingleModuleFactory.PROVIDER_URN,
                        "Whatever");
            }
        };
    }

    /**
     * Tests creation of a singleton module that can not be auto-created
     * as its creation requires parameters be supplied to it.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void createSingletonModule() throws Exception {
        //verify that the singleton module whose creation requires
        //parameters, doesn't exist.
        new ExpectedFailure<ModuleNotFoundException>(
                Messages.MODULE_NOT_FOUND,
                SingleParmModuleFactory.INSTANCE_URN.toString()){
            protected void run() throws Exception {
                sManager.getModuleInfo(SingleParmModuleFactory.INSTANCE_URN);
            }
        };
        //now create the module
        ModuleURN urn = sManager.createModule(
                SingleParmModuleFactory.PROVIDER_URN, new URI("http://blah"));
        assertEquals(SingleParmModuleFactory.INSTANCE_URN, urn);
        //try creating another instance and verify that it fails
        new ExpectedFailure<ModuleCreationException>(
                Messages.CANNOT_CREATE_SINGLETON,
                SingleParmModuleFactory.PROVIDER_URN.toString(),
                SingleParmModuleFactory.INSTANCE_URN.toString()){
            protected void run() throws Exception {
                sManager.createModule(SingleParmModuleFactory.PROVIDER_URN,
                        "http://whatever");
            }
        };
    }

    /**
     * Tests create module API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void createModule() throws Exception {
        new ExpectedFailure<ModuleCreationException>(
                Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_NUM,
                MultipleModuleFactory.PROVIDER_URN.toString(), 1, 0) {
            protected void run() throws Exception {
                sManager.createModule(MultipleModuleFactory.PROVIDER_URN);
            }
        };
        new ExpectedFailure<ModuleCreationException>(
                Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE,
                MultipleModuleFactory.PROVIDER_URN.toString(),
                0,ModuleURN.class.getName(), String.class.getName()) {
            protected void run() throws Exception {
                sManager.createModule(MultipleModuleFactory.PROVIDER_URN,
                        "metc:string:type:not:work");
            }
        };
        //verify that URN validation is happening.
        final ModuleURN urn1 = new ModuleURN("invalidURN");
        new ExpectedFailure<InvalidURNException>(
                Messages.INVALID_URN_SCHEME,
                urn1.scheme(), urn1.toString(), ModuleURN.SCHEME) {
            protected void run() throws Exception {
                sManager.createModule(MultipleModuleFactory.PROVIDER_URN, urn1);
            }
        };
        //verify the details on the module instance that got created
        assertEquals(1, MultipleModule.getNumInstances());
        assertModuleBase(urn1, false, false, true, false);
        //verify that we URN is validated to belong to the provider
        final ModuleURN urn2 = new ModuleURN("metc:test:wrong:myinstance");
        new ExpectedFailure<InvalidURNException>(
                Messages.INSTANCE_PROVIDER_URN_MISMATCH,
                urn2.toString(),
                    MultipleModuleFactory.PROVIDER_URN.toString()) {
            protected void run() throws Exception {
                sManager.createModule(MultipleModuleFactory.PROVIDER_URN, urn2);
            }
        };
        //verify the details on the module instance that got created
        assertEquals(2, MultipleModule.getNumInstances());
        assertModuleBase(urn2, false, false, true, false);

        ModuleURN moduleURN = new ModuleURN(MultipleModuleFactory.PROVIDER_URN,"myinstance");
        final ModuleURN urn3 = sManager.createModule(MultipleModuleFactory.PROVIDER_URN,
                moduleURN);
        assertEquals(moduleURN, urn3);
        assertContains(sManager.getModuleInstances(null), new ModuleURN[]{
                SinkModuleFactory.INSTANCE_URN,
                SingleModuleFactory.INSTANCE_URN, urn3});
        assertModuleInfo(urn3, ModuleState.STARTED,
                null, null, false, true, false, false, false);
        assertEquals(3, MultipleModule.getNumInstances());
        assertModuleBase(urn3, true, false, true, false);
        //Try creating a duplicate module and verify that fails
        new ExpectedFailure<ModuleCreationException>(
                Messages.DUPLICATE_MODULE_URN,urn3.toString()) {
            protected void run() throws Exception {
                sManager.createModule(MultipleModuleFactory.PROVIDER_URN, urn3);
            }
        };
    }

    /**
     * Tests a more complex invocation of createModule API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void createModuleComplex() throws Exception {
        //verify that various errors from the within the module
        //creation are caught.
        final String instanceName = "complicated";
        //test incorrect file path error from the module
        new ExpectedFailure<ModuleCreationException>(
                TestMessages.INCORRECT_FILE_PATH) {
            protected void run() throws Exception {
                sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                        instanceName, new File("yellow"),
                        new URL("http://what?"), new Date());
            }
        };
        //test incorrect url error
        new ExpectedFailure<ModuleCreationException>(
                TestMessages.INCORRECT_URL) {
            protected void run() throws Exception {
                sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                        instanceName, File.listRoots()[0],
                        new URL("ftp://what?"), new Date());
            }
        };
        //test date not supplied error.
        new ExpectedFailure<ModuleCreationException>(
                TestMessages.DATE_NOT_SUPPLIED) {
            protected void run() throws Exception {
                sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                        instanceName, File.listRoots()[0],
                        new URL("http://what?"), null);
            }
        };
        ModuleURN urn = sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                instanceName, File.listRoots()[0],
                new URL("http://what?"), new Date());
        assertEquals(instanceName, urn.instanceName());
        assertContains(sManager.getModuleInstances(
                ComplexModuleFactory.PROVIDER_URN),new ModuleURN[]{urn});
        assertModuleInfo(urn, ModuleState.CREATED,
                null, null, false, false, false, false, false);
        assertEquals(1, MultipleModule.getNumInstances());
        assertModuleBase(urn, false, false, false, false);
    }

    /**
     * Tests delete module API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void deleteModule() throws Exception {
        //verify URN validation
        new ExpectedFailure<InvalidURNException>(Messages.EMPTY_URN, "") {
            protected void run() throws Exception {
                sManager.deleteModule(null);
            }
        };
        final ModuleURN invalidURN = new ModuleURN("invalidURN");
        new ExpectedFailure<InvalidURNException>(Messages.INVALID_URN_SCHEME,
                invalidURN.scheme(), invalidURN.toString(), ModuleURN.SCHEME){
            protected void run() throws Exception {
                sManager.deleteModule(invalidURN);
            }
        };
        //verify non-existing module
        final ModuleURN notExist = new ModuleURN("metc:test:notexist:no");
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                notExist.toString()){
            protected void run() throws Exception {
                sManager.deleteModule(notExist);
            }
        };
        //verify that singleton modules cannot be deleted
        new ExpectedFailure<ModuleException>(Messages.CANNOT_DELETE_SINGLETON,
                SingleModuleFactory.INSTANCE_URN.toString()){
            protected void run() throws Exception {
                sManager.deleteModule(SingleModuleFactory.INSTANCE_URN);
            }
        };
        //create a new module and delete it
        final ModuleURN moduleURN = new ModuleURN(
                MultipleModuleFactory.PROVIDER_URN,"deleted");
        sManager.createModule(MultipleModuleFactory.PROVIDER_URN, moduleURN);
        assertModuleInfo(moduleURN, ModuleState.STARTED,
                null, null, false, true, false, false, false);
        //stop the module and delete it
        sManager.stop(moduleURN);
        assertModuleInfo(moduleURN, ModuleState.STOPPED,
                null, null, false, true, false, false, false);
        sManager.deleteModule(moduleURN);
        //verify that its deleted
        for(ModuleURN urn: sManager.getModuleInstances(moduleURN.parent())) {
            assertFalse(urn.equals(moduleURN));
        }
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                moduleURN.toString()){
            protected void run() throws Exception {
                sManager.getModuleInfo(moduleURN);
            }
        };
        //create the module again and delete it while its started.
        sManager.createModule(MultipleModuleFactory.PROVIDER_URN, moduleURN);
        assertModuleInfo(moduleURN, ModuleState.STARTED,
                null, null, false, true, false, false, false);
        //verify that delete fails if the module was started
        new ExpectedFailure<ModuleStateException>(
                Messages.DELETE_FAILED_MODULE_STATE_INCORRECT,
                moduleURN.toString(),
                ModuleState.STARTED,
                ModuleState.DELETABLE_STATES.toString()){
            protected void run() throws Exception {
                sManager.deleteModule(moduleURN);
            }
        };
        //Stop the module and verify that it can be deleted
        sManager.stop(moduleURN);
        //verify stop invocation.
        assertModuleBase(moduleURN,true, true, true, false);
        sManager.deleteModule(moduleURN);

        //verify that the module is deleted
        for(ModuleURN urn: sManager.getModuleInstances(moduleURN.parent())) {
            assertFalse(urn.equals(moduleURN));
        }
        new ExpectedFailure<ModuleNotFoundException>(Messages.MODULE_NOT_FOUND,
                moduleURN.toString()){
            protected void run() throws Exception {
                sManager.getModuleInfo(moduleURN);
            }
        };
    }

    /**
     * Tests start module API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void startModule() throws Exception {
        //Verify that URL validation is happening.
        new ExpectedFailure<InvalidURNException>(
                Messages.EMPTY_URN, ""){
            protected void run() throws Exception {
                sManager.start(null);
            }
        };
        final ModuleURN urn1 = new ModuleURN("invalidURN");
        new ExpectedFailure<InvalidURNException>(
                Messages.INVALID_URN_SCHEME, urn1.scheme(),
                urn1.toString(), ModuleURN.SCHEME){
            protected void run() throws Exception {
                sManager.start(urn1);
            }
        };

        //attempt starting a module instance that doesn't exist
        final ModuleURN urn2 = new ModuleURN("metc:does:not:exist");
        new ExpectedFailure<ModuleNotFoundException>(
                Messages.MODULE_NOT_FOUND,urn2.toString()){
            protected void run() throws Exception {
                sManager.start(urn2);
            }
        };

        //attempt starting an autocreate module instance that doesn't exist
        final ModuleURN urn3 = new ModuleURN(ComplexModuleFactory.PROVIDER_URN,"startTesting");
        new ExpectedFailure<ModuleNotFoundException>(
                Messages.MODULE_NOT_FOUND,urn3.toString()){
            protected void run() throws Exception {
                sManager.start(urn3);
            }
        };

        //create that module and then attempt starting it
        sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                urn3.instanceName(), File.listRoots()[0],
                new URL("http://what?"), new Date());
        assertModuleInfo(urn3, ModuleState.CREATED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn3, false, false, false, false);
        sManager.start(urn3);
        assertModuleInfo(urn3, ModuleState.STARTED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn3, true, false, false, false);

        //attempt starting a module that is already started
        new ExpectedFailure<ModuleStateException>(
                Messages.MODULE_NOT_STARTED_STATE_INCORRECT, urn3.toString(),
                ModuleState.STARTED, ModuleState.STARTABLE_STATES.toString()){
            protected void run() throws Exception {
                sManager.start(urn3);
            }
        };
    }

    /**
     * Tests failures when invoking the start module API.
     *
     * @throws Exception if there are unexpected errors.
     */

    @Test
    public void startFailure() throws Exception {
        //create a module
        final ModuleURN urn = new ModuleURN(ComplexModuleFactory.PROVIDER_URN, "startFail");
        sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                urn.instanceName(), File.listRoots()[0],
                new URL("http://what?"), new Date());
        ModuleInfo info = assertModuleInfo(urn, ModuleState.CREATED,
                null, null, false, false, false, false, false);
        //verify that there's no start failure message
        assertNull(info.getLastStartFailure());
        MultipleModule m = (MultipleModule) assertModuleBase(
                urn, false, false, false, false);
        m.setFailStart(true);
        new ExpectedFailure<ModuleException>(
                TestMessages.TEST_START_STOP_FAILURE){
            protected void run() throws Exception {
                sManager.start(urn);
            }
        };
        info = assertModuleInfo(urn, ModuleState.START_FAILED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn, true, false, false, false);
        //verify the start failure message
        assertEquals(TestMessages.TEST_START_STOP_FAILURE.getText(),
                info.getLastStartFailure());
        m.setFailStart(false);
        sManager.start(urn);
        info = assertModuleInfo(urn, ModuleState.STARTED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn, true, false, false, false);
        //verify that the start failure message is cleared
        assertNull(info.getLastStartFailure());
    }

    /**
     * Tests the stop module API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void stopModule() throws Exception {
        //Verify that URN validation is happening
        new ExpectedFailure<InvalidURNException>(
                Messages.EMPTY_URN, ""){
            protected void run() throws Exception {
                sManager.stop(null);
            }
        };
        final ModuleURN urn1 = new ModuleURN("invalidURN");
        //try stopping a module with an invalid URN
        new ExpectedFailure<InvalidURNException>(
                Messages.INVALID_URN_SCHEME, urn1.scheme(),
                urn1.toString(), ModuleURN.SCHEME){
            protected void run() throws Exception {
                sManager.stop(urn1);
            }
        };
        //attempt stopping a module that doesn't exist
        final ModuleURN urn2 = new ModuleURN("metc:does:not:exist");
        new ExpectedFailure<ModuleNotFoundException>(
                Messages.MODULE_NOT_FOUND,urn2.toString()){
            protected void run() throws Exception {
                sManager.stop(urn2);
            }
        };
        //Create a module for testing
        final ModuleURN urn3 = new ModuleURN(
                ComplexModuleFactory.PROVIDER_URN,"stopTesting");
        sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                urn3.instanceName(), File.listRoots()[0],
                new URL("http://what?"), new Date());
        assertModuleInfo(urn3, ModuleState.CREATED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn3, false, false, false, false);

        //try stopping a module that is not started
        new ExpectedFailure<ModuleStateException>(
                Messages.MODULE_NOT_STOPPED_STATE_INCORRECT, urn3.toString(),
                ModuleState.CREATED, ModuleState.STOPPABLE_STATES.toString()){
            protected void run() throws Exception {
                sManager.stop(urn3);
            }
        };
        //start the module and then try stopping it
        sManager.start(urn3);
        assertModuleInfo(urn3, ModuleState.STARTED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn3, true, false, false, false);
        //try stopping the module
        sManager.stop(urn3);
        assertModuleInfo(urn3, ModuleState.STOPPED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn3, true, true, false, false);
        //try stopping this module again and observe the failure
        new ExpectedFailure<ModuleStateException>(
                Messages.MODULE_NOT_STOPPED_STATE_INCORRECT, urn3.toString(),
                ModuleState.STOPPED, ModuleState.STOPPABLE_STATES.toString()){
            protected void run() throws Exception {
                sManager.stop(urn3);
            }
        };
    }

    /**
     * Tests for failures when invoking the stop module API.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void stopFailure() throws Exception {
        //create a module
        final ModuleURN urn = new ModuleURN(ComplexModuleFactory.PROVIDER_URN,"stopFail");
        sManager.createModule(ComplexModuleFactory.PROVIDER_URN,
                urn.instanceName(), File.listRoots()[0],
                new URL("http://what?"), new Date());
        ModuleInfo info = assertModuleInfo(urn, ModuleState.CREATED,
                null, null, false, false, false, false, false);
        //verify that there's no stop failure message
        assertNull(info.getLastStopFailure());
        assertModuleBase(urn, false, false, false, false);
        //now start the module.
        sManager.start(urn);
        info = assertModuleInfo(urn, ModuleState.STARTED,
                null, null, false, false, false, false, false);
        assertNull(info.getLastStopFailure());
        MultipleModule m = (MultipleModule) assertModuleBase(
                urn, true, false, false, false);
        //setup stop to fail
        m.setFailStop(true);
        new ExpectedFailure<ModuleException>(
                TestMessages.TEST_START_STOP_FAILURE){
            protected void run() throws Exception {
                sManager.stop(urn);
            }
        };
        info = assertModuleInfo(urn, ModuleState.STOP_FAILED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn, true, true, false, false);
        //verify the start failure message
        assertEquals(TestMessages.TEST_START_STOP_FAILURE.getText(),
                info.getLastStopFailure());
        m.setFailStop(false);
        sManager.stop(urn);
        info = assertModuleInfo(urn, ModuleState.STOPPED,
                null, null, false, false, false, false, false);
        assertModuleBase(urn, true, true, false, false);
        //verify that the start failure message is cleared
        assertNull(info.getLastStopFailure());
    }

    /**
     * Verifies that the sink module cannot be stopped.
     *
     * @throws Exception if there are unexpected errors.
     */
    @Test
    public void sinkStopFailure() throws Exception {
        //verify module's start.
        ModuleInfo info = assertModuleInfo(SinkModuleFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false, true, true,
                false, false);
        assertNull(info.getLastStartFailure());
        assertNull(info.getLastStopFailure());
        //attempt stopping it
        new ExpectedFailure<ModuleException>(
                Messages.CANNOT_STOP_SINK_MODULE){
            protected void run() throws Exception {
                sManager.stop(SinkModuleFactory.INSTANCE_URN);
            }
        };
        //verify the state again.
        info = assertModuleInfo(SinkModuleFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false, true, true,
                false, false);
        assertNull(info.getLastStartFailure());
        assertNull(info.getLastStopFailure());
    }


    private void assertProviderInfo(
            ModuleURN inURN,
            String[] parameterTypeNames,
            Class[] parameterTypes, String description,
            boolean autoInstantiate,
            boolean multipleInstances) throws Exception {
        assertProviderInfo(sManager, inURN, parameterTypeNames, parameterTypes,
                description, autoInstantiate, multipleInstances);
    }

    private ModuleInfo assertModuleInfo(ModuleURN inURN,
                                        ModuleState inState,
                                        DataFlowID[] inInitDataFlows,
                                        DataFlowID[] inParticipateDataFlows,
                                        boolean inAutocreated,
                                        boolean inAutostart,
                                        boolean inReceiver,
                                        boolean inEmitter,
                                        boolean inFlowRequester)
            throws Exception {
        return assertModuleInfo(sManager, inURN, inState,
                inInitDataFlows, inParticipateDataFlows, inAutocreated,
                inAutostart, inReceiver, inEmitter, inFlowRequester);
    }

    private static ModuleManager sManager = null;
}
