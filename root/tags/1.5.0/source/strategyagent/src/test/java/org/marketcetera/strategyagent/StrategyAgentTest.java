package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;
import static org.marketcetera.strategyagent.JarClassLoaderTest.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;

import javax.management.JMX;
import javax.management.ObjectName;
import java.io.File;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * Tests {@link StrategyAgent}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class StrategyAgentTest extends StrategyAgentTestBase {
    @Test
    public void runNoArgs() {
        run(createAgent(false));
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertNoEvents();
    }
    @Test
    public void runInvalidArgs() {
        run(createAgent(false), "/doesnotexist");
        assertEquals(StrategyAgent.EXIT_START_ERROR, mRunner.getExitCode());
        assertLastButXEvent(0, Level.ERROR,
                StrategyAgent.class.getName(),
                Messages.LOG_ERROR_CONFIGURE_AGENT);
    }
    @Test
    public void runInvalidCmdSyntax() throws Exception {
        final String syntax = "this is invalid command syntax";
        File f = createFileWithText("#comment", syntax,"");
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(StrategyAgent.EXIT_CMD_PARSE_ERROR, mRunner.getExitCode());
        assertLastButXEvent(1, Level.ERROR,
                TestAgent.class.getName(),
                Messages.INVALID_COMMAND_SYNTAX, syntax, 2);
        assertLastButXEvent(0, Level.ERROR,
                StrategyAgent.class.getName(),
                Messages.LOG_COMMAND_PARSE_ERRORS, 1);
    }
    @Test
    public void runInvalidCmdName() throws Exception {
        File f = createFileWithText("badname;this one's bad",
                "#comment","    \t");
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(StrategyAgent.EXIT_CMD_PARSE_ERROR, mRunner.getExitCode());
        assertLastButXEvent(1, Level.ERROR,
                TestAgent.class.getName(),
                Messages.INVALID_COMMAND_NAME, "badname", 1);
        assertLastButXEvent(0, Level.ERROR,
                StrategyAgent.class.getName(),
                Messages.LOG_COMMAND_PARSE_ERRORS, 1);
    }
    @Test
    public void runWithMultipleErrors() throws Exception {
        final String syntax = "whoops bad syntax again";
        File f = createFileWithText("#comment 1",
                "badname;you give tests a bad name",
                "",
                "# comment 2",
                syntax,
                " \t ",
                "# A valid command",
                "createModule;metc:blah:zoo:gah",
                "# another bad egg",
                "createDataFlo;metc:blah:zoo:gah",
                "# another valid command",
                "createDataFlow;metc:blah:zoo:gah",
                "# yet another valid command",
                "startModule;metc:blah:zoo:gah",
                "# end");
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(StrategyAgent.EXIT_CMD_PARSE_ERROR, mRunner.getExitCode());
        assertLastButXEvent(3, Level.ERROR,
                TestAgent.class.getName(),
                Messages.INVALID_COMMAND_NAME, "badname", 2);
        assertLastButXEvent(2, Level.ERROR,
                TestAgent.class.getName(),
                Messages.INVALID_COMMAND_SYNTAX, syntax,  5);
        assertLastButXEvent(1, Level.ERROR,
                TestAgent.class.getName(),
                Messages.INVALID_COMMAND_NAME, "createDataFlo", 10);
        assertLastButXEvent(0, Level.ERROR,
                StrategyAgent.class.getName(),
                Messages.LOG_COMMAND_PARSE_ERRORS, 3);

    }
    @Test
    public void createModuleSyntaxError() throws Exception {
        File f = createFileWithText("createModule;metc:blah:zoo:gah");
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createModule",
                "metc:blah:zoo:gah");
        assertLastButXEvent(0, Level.WARN,
                TestAgent.class.getName(),
                Messages.LOG_ERROR_EXEC_CMD, "createModule",
                "metc:blah:zoo:gah",1, Messages.CREATE_MODULE_INVALID_SYNTAX.
                getText("metc:blah:zoo:gah"));
    }
    @Test
    public void createModule() throws Exception {
        ModuleURN factoryURN = ProcessorModuleFactory.PROVIDER_URN;
        ModuleURN instanceURN = new ModuleURN(factoryURN, "blah");
        String parameter = factoryURN + ";" + instanceURN;
        File f = createFileWithText("createModule;" + parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createModule",
                parameter);
        assertLastButXEvent(0, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_COMMAND_RUN_RESULT, "createModule",
                instanceURN.getValue());
    }
    @Test
    public void createModuleError() throws Exception {
        ModuleURN factoryURN = new ModuleURN("metc:test:notexist");
        ModuleURN instanceURN = new ModuleURN(factoryURN, "blah");
        String parameter = factoryURN + ";" + instanceURN;
        File f = createFileWithText("createModule;" + parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createModule",
                parameter);
        assertLastButXEvent(0, Level.WARN,
                TestAgent.class.getName(),
                Messages.LOG_ERROR_EXEC_CMD, "createModule", parameter, 1,
                org.marketcetera.module.Messages.PROVIDER_NOT_FOUND.getText(
                        factoryURN.getValue()));
    }
    @Test
    public void startModuleError() throws Exception {
        File f = createFileWithText("startModule;metc:does:not:exist");
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "startModule",
                "metc:does:not:exist");
        assertLastButXEvent(0, Level.WARN,
                TestAgent.class.getName(),
                Messages.LOG_ERROR_EXEC_CMD, "startModule",
                "metc:does:not:exist", 1,
                org.marketcetera.module.Messages.MODULE_NOT_FOUND.getText(
                        "metc:does:not:exist"));
    }
    @Test
    public void startModule() throws Exception {
        File f = createFileWithText("startModule;" +
                SingleModuleFactory.INSTANCE_URN);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "startModule",
                SingleModuleFactory.INSTANCE_URN);
        assertLastButXEvent(0, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_COMMAND_RUN_RESULT, "startModule",
                true);
    }

    @Test
    public void createDataFlowError() throws Exception {
        //setup a data flow, that will fail as one of the
        //modules cannot participate in the data flow.
        ModuleURN instanceURN = SingleModuleFactory.INSTANCE_URN;

        String parameter = EmitterModuleFactory.INSTANCE_URN +
                ";somestring^" + instanceURN;
        File f = createFileWithText("createDataFlow;" + parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createDataFlow",
                parameter);
        assertLastButXEvent(0, Level.WARN,
                TestAgent.class.getName(),
                Messages.LOG_ERROR_EXEC_CMD, "createDataFlow",
                parameter, 1,
                org.marketcetera.module.Messages.DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT.
                        getText(EmitterModuleFactory.INSTANCE_URN,
                        ModuleState.CREATED, EnumSet.of(ModuleState.STARTED,
                        ModuleState.STOP_FAILED).toString()));
    }
    @Test
    public void createDataFlow() throws Exception {
        //Supply a set of commands to create modules
        //and setup a data flow between them.
        //start emitter module
        ModuleURN factoryURN = ProcessorModuleFactory.PROVIDER_URN;
        ModuleURN instanceURN = new ModuleURN(factoryURN, "process");
        String parameter = EmitterModuleFactory.INSTANCE_URN +
                        ";somestring^"+instanceURN+";"+String.class.getName();
        File f = createFileWithText(
                "startModule;" + EmitterModuleFactory.INSTANCE_URN,
                "createDataFlow;" + parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(3, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "startModule",
                EmitterModuleFactory.INSTANCE_URN);
        assertLastButXEvent(2, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_COMMAND_RUN_RESULT, "startModule",
                true);
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createDataFlow",
                parameter);
        //Get the data flow ID
        List<DataFlowID> flows = mRunner.getManager().getDataFlows(true);
        assertEquals(1,flows.size());
        assertLastButXEvent(0, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_COMMAND_RUN_RESULT, "createDataFlow",
                flows.get(0));
    }
    @Test
    public void loading() throws Exception {
        //Create a subclass of ConfigurationProviderTestFactory into a
        //jar in the jars subdirectory.
        String newSubclass = getClass().getPackage().getName() + ".ProviderFactory";
        byte[] classBytes = generateSubclass(MyTestFactory.class,newSubclass);
        JarContents jc = new JarContents(transformName(newSubclass) + ".class",classBytes);
        //Create the factory file to load this factory via the service loader
        File jar = createJar("provider.jar",new JarContents[]{
                jc,
                new JarContents("META-INF/services/" +
                        ModuleFactory.class.getName(), newSubclass.getBytes())
        });
        ModuleURN instanceURN = new ModuleURN(MyTestFactory.PROVIDER_URN,
                "stratocaster");
        //Create the properties file for testing default parameter setting
        Properties properties = new Properties();
        properties.setProperty("MaxLimit","123456.123456");
        properties.setProperty(".Boolean","true");
        properties.setProperty("Decimal", "987.654");
        properties.setProperty(".Decimal","123.123");
        properties.setProperty(instanceURN.instanceName() + ".Decimal","123.123");
        properties.setProperty("whatever.Decimal","34234.234");
        properties.setProperty("wherever.Decimal","34234.234");
        properties.setProperty("String","yes");
        properties.setProperty(".File","/tmp/yes");
        properties.setProperty(instanceURN.instanceName() +
                ".FactoryAnnotation","annoDomini");
        properties.setProperty("int","312");
        properties.setProperty("whatever.PrimFloat","312");
        savePropertiesForProvider(instanceURN, properties);
        String parameter = MyTestFactory.PROVIDER_URN + ";" +
                instanceURN.getValue();
        File f = createFileWithText("createModule;" +
                parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        assertLastButXEvent(1, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_RUNNING_COMMAND, "createModule",
                parameter);
        assertLastButXEvent(0, Level.INFO,
                TestAgent.class.getName(),
                Messages.LOG_COMMAND_RUN_RESULT, "createModule",
                instanceURN.getValue());
        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(
                getMBeanServer(),
                MyTestFactory.PROVIDER_URN.toObjectName(),
                ConfigurationProviderFactoryMXBean.class);
        assertEquals(new BigDecimal("123456.123456"),factory.getMaxLimit());
        assertEquals(new BigInteger("1"),factory.getInstances());
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                instanceURN.toObjectName(), JMXTestModuleMXBean.class);
        //value for all instances
        assertEquals(true, module.getBoolean());
        //specific value for this instance
        assertEquals(new BigDecimal("123.123"), module.getDecimal());
        //value set for factory but not for this instance
        assertNull(module.getString());
        //value set for all instances
        assertEquals("/tmp/yes",module.getFile());
        //value only set for this instance
        assertEquals("annoDomini", module.getFactoryAnnotation());
        //value set but with incorrect property name case
        assertNull(module.getInt());
        //value set for a different instance
        assertEquals(0.0f, module.getPrimFloat(), 0.0f);
        //value not specified in the properties.
        assertNull(module.getURL());
    }
    @Test
    public void checkSample() throws Exception {
        File input = new File(new File(
                JarClassLoaderTest.SAMPLE_DATA_DIR, "inputs"),
                "sampleCommands.txt");
        assertTrue(input.getAbsolutePath(), input.isFile());
        run(createAgent(false), input.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
    }

    @Test(timeout = 5000)
    public void sinkLogging() throws Exception {
        //Disable other kinds of logging so that it doesn't interfere
        //with sink logging.
        setLevel(TestAgent.class.getName(), Level.ERROR);
        String parameter = EmitterModuleFactory.INSTANCE_URN +
                        ";somestring";
        File f = createFileWithText(
                "startModule;" + EmitterModuleFactory.INSTANCE_URN,
                "createDataFlow;" + parameter);
        run(createAgent(false), f.getAbsolutePath());
        assertEquals(NO_EXIT, mRunner.getExitCode());
        //Get the data flow ID
        List<DataFlowID> flows = mRunner.getManager().getDataFlows(true);
        assertEquals(1,flows.size());
        //Wait until we have one event in the log
        while(getAppender().getEvents().isEmpty()) {
            Thread.sleep(200);
        }
        assertLastButXEvent(0, Level.INFO,
                StrategyAgent.SINK_DATA,
                Messages.LOG_SINK_DATA, flows.get(0), 1,
                String.class.getName(),"somestring");
    }
    @Test
    public void initFail() throws Exception {
        //Register a bean with MM name to cause init to fail
        ObjectName on = new ObjectName(ModuleManager.MODULE_MBEAN_NAME);
        getMBeanServer().registerMBean(new JMXTestModule(
                new ModuleURN("metc:blah:goo:gah")), on);
        run(createAgent(false));
        assertEquals(StrategyAgent.EXIT_INIT_ERROR, mRunner.getExitCode());
        assertLastButXEvent(0, Level.ERROR,
                StrategyAgent.class.getName(),
                Messages.LOG_ERROR_INITIALIZING_AGENT);
    }

}
