package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.unicode.UnicodeFileWriter;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.module.*;
import static org.marketcetera.strategyagent.JarClassLoaderTest.*;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.lang.management.ManagementFactory;

/* $License$ */
/**
 * Tests {@link StrategyAgentTest}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class StrategyAgentTest extends TestCaseBase {
    @Before
    public void setupLogLevel() {
        Logger.getRootLogger().setLevel(Level.ERROR);
        setLevel(StrategyAgent.class.getName(), Level.INFO);
        setLevel(TestAgent.class.getName(), Level.INFO);
        setLevel(StrategyAgent.SINK_DATA, Level.INFO);
    }
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
        File conf = new File(JarClassLoaderTest.CONF_DIR, new StringBuilder().
                append(instanceURN.providerType()).
                append("_").append(instanceURN.providerName()).
                append(".properties").toString());
        FileOutputStream fos = new FileOutputStream(conf);
        properties.store(fos, "");
        fos.close();
        conf.deleteOnExit();
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
    @After
    public void cleanup() throws Exception {
        if(mRunner != null) {
            ModuleManager manager = mRunner.getManager();
            if(manager != null) {
                manager.stop();
            }
            mRunner = null;
        }
        if(mFile != null) {
            Deleter.apply(mFile);
            mFile = null;
        }
    }
    private static MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }
    private static void matchMessage(I18NMessage inMessage,
                                     LoggingEvent inMsg,
                                     Object... inMsgParams) {
        int i = inMessage.getParamCount();
        Object [] msgParams;
        boolean useRegex = false;
        if(inMsgParams.length < i) {
            //Use regex for parameters that are not supplied
            msgParams = Arrays.copyOf(inMsgParams, i);
            for(i = inMsgParams.length; i < msgParams.length; i++) {
                msgParams[i] = ".+";
                useRegex = true;
            }
        } else {
            msgParams = inMsgParams;
        }
        String expectedMsg = inMessage.getMessageProvider().getText(
                inMessage, msgParams);
        String actualMsg = inMsg.getMessage().toString();
        if (useRegex) {
            Pattern p = Pattern.compile(expectedMsg);
            assertTrue(actualMsg, p.matcher(actualMsg).matches());
        } else {
            assertEquals(expectedMsg, actualMsg);
        }
    }
    private File createFileWithText(String... inLines) throws IOException {
        File f = File.createTempFile("com",".txt");
        UnicodeFileWriter writer = new UnicodeFileWriter(f);
        BufferedWriter bufWriter = new BufferedWriter(writer);
        for(String line: inLines) {
            bufWriter.write(line);
            bufWriter.newLine();
        }
        bufWriter.close();
        writer.close();
        f.deleteOnExit();
        mFile = f;
        return f;
    }
    private LoggingEvent assertLastButXEvent(int inFromEnd,
                                                    Level inLevel,
                                                    String inLogger,
                                                    I18NMessage inMessage,
                                                    Object... inMsgParams) {
        LinkedList<LoggingEvent> events = getAppender().getEvents();
        assertTrue("" + events.size(), events.size() > inFromEnd);
        LoggingEvent event = events.get(events.size() - inFromEnd - 1);
        assertEvent(event, inLevel,  inLogger, null, null);
        matchMessage(inMessage, event, inMsgParams);
        return event;
    }

    private static void run(TestAgent inRunner, String... args) {
        StrategyAgent.run(inRunner, args);
    }
    private TestAgent createAgent(boolean inWaitForever) {
        mRunner = new TestAgent(inWaitForever);
        return mRunner;
    }

    /**
     * Creates a subclass of the supplied super class with the supplied name.
     * The subclass simply extends the super class and has a default constructor
     * that calls super's default constructor.
     *
     * @param inSuperClass the super class
     * @param inSubClassName the fully qualified name of the subclass.
     *
     * @return the array containing sub class byte code.
     */
    private static byte[] generateSubclass(
            Class inSuperClass, String inSubClassName) {
        ClassWriter cw = new ClassWriter(false);
        cw.visit(V1_6, ACC_PUBLIC,transformName(inSubClassName),null,
                transformName(inSuperClass.getName()),null);
        //Generate default constructor
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL,
                transformName(inSuperClass.getName()),"<init>","()V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(1,1);
        cw.visitEnd();
        return cw.toByteArray();
    }
    private static String transformName(String inName) {
        return inName.replace('.','/');
    }
    private TestAgent mRunner;
    private File mFile;
    private static final int NO_EXIT = -1;

    /**
     * A test agent that prevents the strategy agent from exiting the
     * process.
     */
    private static class TestAgent extends StrategyAgent {
        private TestAgent(boolean inWaitForever) {
            mWaitForever = inWaitForever;
            mExitCode = NO_EXIT;
        }

        /**
         * Returns the exit code of the agent.
         *
         * @return the exit code of the agent.
         */
        public int getExitCode() {
            return mExitCode;
        }
        @Override
        void exit(int inExitCode) {
            mExitCode = inExitCode;
        }

        @Override
        public void startWaitingForever() {
            if (mWaitForever) {
                super.startWaitingForever();
            }
        }

        private final boolean mWaitForever;
        private int mExitCode;
    }
}
