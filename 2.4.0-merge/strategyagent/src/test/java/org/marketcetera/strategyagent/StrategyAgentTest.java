package org.marketcetera.strategyagent;

import static org.junit.Assert.*;
import static org.marketcetera.strategyagent.JarClassLoaderTest.createJar;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import javax.management.JMX;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Test;
import org.marketcetera.module.*;
import org.marketcetera.strategyagent.JarClassLoaderTest.JarContents;

/* $License$ */
/**
 * Tests {@link StrategyAgent}
 *
 * @author anshul@marketcetera.com
 */
public class StrategyAgentTest
        extends StrategyAgentTestBase
{
    @After
    public void cleanup()
    {
        shutdownSa();
    }
    /**
     * Tests running the SA with no arguments.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void runNoArgs()
            throws Exception
    {
        createSaWith();
        assertTrue(sa.isRunning());
    }
    /**
     * Tests running the SA with invalid arguments.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void runInvalidArgs()
            throws Exception
    {
        new ExpectedFailure<RuntimeException>("java.io.FileNotFoundException: /doesnotexist (No such file or directory)") {
            @Override
            protected void run()
                    throws Exception
            {
                createSaWith("/doesnotexist");
            }
        };
        assertFalse(sa.isRunning());
    }
    /**
     * Tests running the SA with invalid command syntax.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void runInvalidCmdSyntax()
            throws Exception
    {
        final String syntax = "this is invalid command syntax";
        new ExpectedFailure<RuntimeException>("java.lang.IllegalArgumentException: " + Messages.LOG_COMMAND_PARSE_ERRORS.getText(1)) {
            @Override
            protected void run()
                    throws Exception
            {
                createSaWith(createFileWithText("#comment",
                                                syntax,
                                                "").getAbsolutePath());
            }
        };
        assertFalse(sa.isRunning());
    }
    /**
     * Tests running the SA with an invalid command name.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void runInvalidCmdName()
            throws Exception
    {
        new ExpectedFailure<RuntimeException>("java.lang.IllegalArgumentException: " + Messages.LOG_COMMAND_PARSE_ERRORS.getText(1)) {
            @Override
            protected void run()
                    throws Exception
            {
                createSaWith(createFileWithText("badname;this one's bad",
                                                "#comment",
                                                "    \t").getAbsolutePath());
            }
        };
        assertFalse(sa.isRunning());
    }
    @Test
    public void runWithMultipleErrors()
            throws Exception
    {
        final String syntax = "whoops bad syntax again";
        final File f = createFileWithText("#comment 1",
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
        new ExpectedFailure<RuntimeException>("java.lang.IllegalArgumentException: " + Messages.LOG_COMMAND_PARSE_ERRORS.getText(3)) {
            @Override
            protected void run()
                    throws Exception
            {
                createSaWith(f.getAbsolutePath());
            }
        };
        assertFalse(sa.isRunning());
    }
    /**
     * Tests creating a module with a syntax error in the command.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void createModuleSyntaxError()
            throws Exception
    {
        final File f = createFileWithText("createModule;metc:blah:zoo:gah");
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
    }
    /**
     * Tests running the SA with a valid module start command.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void createModule()
            throws Exception
    {
        ModuleURN factoryURN = ProcessorModuleFactory.PROVIDER_URN;
        ModuleURN instanceURN = new ModuleURN(factoryURN, "blah");
        String parameter = factoryURN + ";" + instanceURN;
        File f = createFileWithText("createModule;" + parameter);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        assertFalse(moduleManager.getModuleInfo(instanceURN) == null);
    }
    /**
     * Tests running the SA with a valid module create command for an invalid module.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void createModuleError()
            throws Exception
    {
        ModuleURN factoryURN = new ModuleURN("metc:test:notexist");
        final ModuleURN instanceURN = new ModuleURN(factoryURN,
                                                    "blah");
        String parameter = factoryURN + ";" + instanceURN;
        File f = createFileWithText("createModule;" + parameter);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        new ExpectedFailure<ModuleNotFoundException>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.getModuleInfo(instanceURN);
            }
        };
    }
    /**
     * Tests running the SA with a valid module start command for an invalid module.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void startModuleError()
            throws Exception
    {
        File f = createFileWithText("startModule;metc:does:not:exist");
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        new ExpectedFailure<ModuleNotFoundException>() {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.getModuleInfo(new ModuleURN("metc:does:not:exist"));
            }
        };
    }
    /**
     * Tests running the SA with a valid module start command for a valid module.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void startModule()
            throws Exception
    {
        File f = createFileWithText("startModule;" + SingleModuleFactory.INSTANCE_URN);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        ModuleInfo moduleInfo = moduleManager.getModuleInfo(SingleModuleFactory.INSTANCE_URN);
        assertNotNull(moduleInfo.getStarted());
    }
    /**
     * Tests running the SA with a valid command that generates a data flow error.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void createDataFlowError()
            throws Exception
    {
        // setup a data flow, that will fail as one of the modules cannot participate in the data flow
        ModuleURN instanceURN = SingleModuleFactory.INSTANCE_URN;
        String parameter = EmitterModuleFactory.INSTANCE_URN + ";somestring^" + instanceURN;
        File f = createFileWithText("createDataFlow;" + parameter);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        ModuleInfo moduleInfo = moduleManager.getModuleInfo(instanceURN);
        assertNull(moduleInfo.getStarted());
    }
    /**
     * Tests running the SA with a valid command that generates a valid data flow.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void createDataFlow()
            throws Exception
    {
        // supply a set of commands to create modules and setup a data flow between them
        // start emitter module
        ModuleURN factoryURN = ProcessorModuleFactory.PROVIDER_URN;
        ModuleURN instanceURN = new ModuleURN(factoryURN, "process");
        String parameter = EmitterModuleFactory.INSTANCE_URN + ";somestring^"+instanceURN+";"+String.class.getName();
        File f = createFileWithText("startModule;" + EmitterModuleFactory.INSTANCE_URN,
                                    "createDataFlow;" + parameter);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        // get the data flow ID
        List<DataFlowID> flows = moduleManager.getDataFlows(true);
        assertEquals(1,
                     flows.size());
    }
    /**
     * Tests running the SA with a custom class loader.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void loading()
            throws Exception
    {
        // create a subclass of ConfigurationProviderTestFactory into a jar in the jars subdirectory.
        String newSubclass = getClass().getPackage().getName() + ".ProviderFactory";
        byte[] classBytes = generateSubclass(MyTestFactory.class,
                                             newSubclass);
        JarContents jc = new JarContents(transformName(newSubclass) + ".class",
                                         classBytes);
        // create the factory file to load this factory via the service loader
        createJar("provider.jar",
                  new JarContents[] { jc, new JarContents("META-INF/services/" + ModuleFactory.class.getName(), newSubclass.getBytes()) });
        ModuleURN instanceURN = new ModuleURN(MyTestFactory.PROVIDER_URN,
                                              "stratocaster");
        // create the properties file for testing default parameter setting
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
        properties.setProperty(instanceURN.instanceName() + ".FactoryAnnotation","annoDomini");
        properties.setProperty("int","312");
        properties.setProperty("whatever.PrimFloat","312");
        savePropertiesForProvider(instanceURN,
                                  properties);
        String parameter = MyTestFactory.PROVIDER_URN + ";" + instanceURN.getValue();
        File f = createFileWithText("createModule;" + parameter);
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(getMBeanServer(),
                                                                        MyTestFactory.PROVIDER_URN.toObjectName(),
                                                                        ConfigurationProviderFactoryMXBean.class);
        assertEquals(new BigDecimal("123456.123456"),
                     factory.getMaxLimit());
        assertEquals(new BigInteger("1"),factory.getInstances());
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                                                        instanceURN.toObjectName(),
                                                        JMXTestModuleMXBean.class);
        //value for all instances
        assertEquals(true,
                     module.getBoolean());
        //specific value for this instance
        assertEquals(new BigDecimal("123.123"),
                     module.getDecimal());
        //value set for factory but not for this instance
        assertNull(module.getString());
        //value set for all instances
        assertEquals("/tmp/yes",
                     module.getFile());
        //value only set for this instance
        assertEquals("annoDomini",
                     module.getFactoryAnnotation());
        //value set but with incorrect property name case
        assertNull(module.getInt());
        //value set for a different instance
        assertEquals(0.0f,
                     module.getPrimFloat(),
                     0.0f);
        //value not specified in the properties.
        assertNull(module.getURL());
    }
    /**
     * Tests running the SA with sample commands.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void checkSample()
            throws Exception
    {
        File input = new File(new File(JarClassLoaderTest.SAMPLE_DATA_DIR,
                                       "inputs"),
                              "sampleCommands.txt");
        assertTrue(input.getAbsolutePath(),
                   input.isFile());
        createSaWith(input.getAbsolutePath());
        assertTrue(sa.isRunning());
    }
    /**
     * Tests running the SA where it will fail during init.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void initFail()
            throws Exception
    {
        // Register a bean with MM name to cause init to fail
        ObjectName on = new ObjectName(ModuleManager.MODULE_MBEAN_NAME);
        getMBeanServer().registerMBean(new JMXTestModule(new ModuleURN("metc:blah:goo:gah")),
                                       on);
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                createSaWith();
            }
        };
        assertFalse(sa.isRunning());
    }
}
