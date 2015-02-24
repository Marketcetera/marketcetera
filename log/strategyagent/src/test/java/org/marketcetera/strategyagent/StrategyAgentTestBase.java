package org.marketcetera.strategyagent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.SAService;
import org.marketcetera.util.unicode.UnicodeFileWriter;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Base class for Strategy Agent tests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
public class StrategyAgentTestBase
{
    /**
     * Set the app dir property so that the properties files are picked up.
     */
    @BeforeClass
    public static void setupConfDirProperty()
    {
        initParams();
    }
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        initParams();
    }
    /**
     * Initializes the params used for SA tests.
     */
    protected static void initParams()
    {
        useWs = false;
        wsHostname = "localhost";
        wsPort = 9001;
        jmsPort = 61617;
        buildJmsUrl();
    }
    /**
     * Shuts down the SA used for tests.
     */
    protected static void shutdownSa()
    {
        if(moduleManager != null) {
            moduleManager.stop();
            moduleManager = null;
        }
        if(sa != null && sa.isRunning()) {
            try {
                sa.stop();
                sa = null;
            } catch (Exception ignored) {}
        }
        if(app != null) {
            app.stop();
            app = null;
        }
        if(server != null && server.isRunning()) {
            server.stop();
            server = null;
        }
    }
    /**
     * Creates and saves a properties file for the module with the given URN containing the given properties.
     *
     * @param inURN a <code>ModuleURN</code> value
     * @param inProperties a <code>Properties</code> value
     * @throws IOException if an error occurs saving the properties
     */
    protected static void savePropertiesForProvider(ModuleURN inURN,
                                                    Properties inProperties)
            throws IOException
    {
        File conf = new File(JarClassLoaderTest.CONF_DIR,
                             new StringBuilder().append(inURN.providerType()).append("_").append(inURN.providerName()).append(".properties").toString());
        FileOutputStream fos = new FileOutputStream(conf);
        inProperties.store(fos,
                           "");
        fos.close();
        conf.deleteOnExit();
    }
    /**
     * Gets the test MBean server.
     *
     * @return an <code>MBeanServer</code> value
     */
    protected static MBeanServer getMBeanServer()
    {
        return ManagementFactory.getPlatformMBeanServer();
    }
    /**
     * Sets the JMS URL test value based on the hostname and jms port value.
     */
    protected static void buildJmsUrl()
    {
        jmsUrl = "tcp://" + wsHostname + ":" + jmsPort;
    }
    /**
     * Creates a file containing the given lines.
     *
     * <p>The file will be deleted at the end of the test run.
     *
     * @param inLines a <code>String[]</code> value
     * @return a <code>File</code> value referring to a file containing the given content
     * @throws IOException if an error occurs writing the file
     */
    protected File createFileWithText(String... inLines)
            throws IOException
    {
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
        return f;
    }
    /**
     * Creates a running Strategy Agent using the given arguments.
     *
     * @param inArguments a <code>String[]</code> value
     * @throws Exception if an unexpected error occurs
     */
    protected static void createSaWith(String... inArguments)
            throws Exception
    {
        app = new MockApplicationContainer();
        app.setArguments(inArguments);
        app.start();
        sa = new StrategyAgent();
        loader = new JarClassLoader(new StaticStrategyAgentApplicationInfoProvider(),
                                    StrategyAgentTestBase.class.getClassLoader());
        AgentConfigurationProvider configurationProvider = new AgentConfigurationProvider(loader);
        Map<String,String> receiverProperties = Maps.newHashMap();
        receiverProperties.put("URL",
                               jmsUrl);
        receiverProperties.put("LogLevel",
                               "WARN");
        receiverProperties.put("SkipJAASConfiguration",
                               "false");
        configurationProvider.setReceiverProperties(receiverProperties);
        moduleManager = new ModuleManager(loader);
        moduleManager.setConfigurationProvider(configurationProvider);
        publisher = new PublisherEngine();
        sa.setModuleManager(moduleManager);
        sa.setLoader(loader);
        sa.setDataPublisher(publisher);
        sa.start();
        if(useWs) {
            Authenticator authenticator = new DefaultAuthenticator();
            ClientSessionFactory clientSessionFactory = new ClientSessionFactory();
            SessionManager<ClientSession> sessionManager = new SessionManager<ClientSession>(clientSessionFactory,
                                                                                             -1);
            servicesProvider = new SAServiceImpl(sessionManager,
                                                 moduleManager,
                                                 publisher);
            server = new StrategyAgentWebServicesProvider();
            server.setHostname(wsHostname);
            server.setPort(wsPort);
            server.setAuthenticator(authenticator);
            server.setSessionManager(sessionManager);
            server.setServiceProvider(servicesProvider);
            server.start();
        }
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
    protected static byte[] generateSubclass(Class<?> inSuperClass,
                                             String inSubClassName)
    {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(org.objectweb.asm.Opcodes.V1_6,
                org.objectweb.asm.Opcodes.ACC_PUBLIC,
                transformName(inSubClassName),
                null,
                transformName(inSuperClass.getName()),
                null);
        // generate default constructor
        MethodVisitor mv = cw.visitMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC,
                                          "<init>",
                                          "()V",
                                          null,
                                          null);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
        mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL,
                           transformName(inSuperClass.getName()),
                           "<init>",
                           "()V");
        mv.visitInsn(org.objectweb.asm.Opcodes.RETURN);
        mv.visitMaxs(1,1);
        cw.visitEnd();
        return cw.toByteArray();
    }
    /**
     * Creates a subclass of the supplied super class with the supplied name.
     * The subclass extends the super class and has a constructor
     * that accepts a parameter of type {@link ModuleURN} and supplies
     * it to the super class constructor.
     *
     * @param inSuperClass the super class
     * @param inSubClassName the fully qualified name of the subclass.
     *
     * @return the array containing sub class byte code.
     */
    protected static byte[] generateSubclassURNConstructor(
            Class<?> inSuperClass, String inSubClassName) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(org.objectweb.asm.Opcodes.V1_6,
                org.objectweb.asm.Opcodes.ACC_PUBLIC,
                transformName(inSubClassName),null,
                transformName(inSuperClass.getName()),null);
        //Generate default constructor
        MethodVisitor mv = cw.visitMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC,
                "<init>", "(Lorg/marketcetera/module/ModuleURN;)V", null, null);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 1);
        mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL,
                transformName(inSuperClass.getName()), "<init>",
                "(Lorg/marketcetera/module/ModuleURN;)V");
        mv.visitInsn(org.objectweb.asm.Opcodes.RETURN);
        mv.visitMaxs(2,2);
        cw.visitEnd();
        return cw.toByteArray();
    }
    /**
     * Transforms the given name, replacing '.' with '/'.
     *
     * @param inName a <code>String</code> value
     * @return a <code>String</code> value
     */
    protected static String transformName(String inName)
    {
        return inName.replace('.','/');
    }
    /**
     * test application value
     */
    protected static MockApplicationContainer app;
    /**
     * test data publisher value
     */
    protected static PublisherEngine publisher;
    /**
     * test class loader value
     */
    protected static ClassLoader loader;
    /**
     * test module manager value
     */
    protected static ModuleManager moduleManager;
    /**
     * test strategy agent value
     */
    protected static StrategyAgent sa;
    /**
     * 
     */
    protected static SAService servicesProvider;
    /**
     * 
     */
    protected static StrategyAgentWebServicesProvider server;
    /**
     * 
     */
    protected static String wsHostname;
    /**
     * 
     */
    protected static int wsPort;
    /**
     * 
     */
    protected static String jmsUrl;
    /**
     * 
     */
    protected static int jmsPort;
    /**
     * 
     */
    protected static boolean useWs;
}
