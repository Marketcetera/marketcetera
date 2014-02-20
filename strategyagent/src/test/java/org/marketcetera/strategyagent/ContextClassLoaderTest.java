package org.marketcetera.strategyagent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.strategyagent.ContextCLTestFactoryBase.*;
import static org.marketcetera.strategyagent.ContextCLTestModuleBase.*;
import static org.marketcetera.strategyagent.JarClassLoaderTest.createJar;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.management.JMX;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Test;
import org.marketcetera.module.*;

/* $License$ */
/**
 * Verifies that the context class loader is correctly set when
 * invoking various factory / module methods within strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
public class ContextClassLoaderTest
        extends StrategyAgentTestBase
{
    /**
     * Runs after each test.
     *
     * @throws Exception if there were unexpected errors.
     */
    @After
    public void cleanup()
            throws Exception
    {
        shutdownSa();
    }
    /**
     * Verifies that the context class loader is correctly setup when
     * the module methods are invoked
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void contextLoading()
            throws Exception
    {
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        // Verify initial factory state
        assertCLEquals(null,
                       sConstructClassLoader,
                       sCreateClassLoader,
                       sFactoryGetAttributeLoader,
                       sFactorySetAttributeLoader,
                       sFactoryOperationLoader);
        assertCLEquals(null,
                       sStartLoader,
                       sStopLoader,
                       sRequestLoader,
                       sCancelLoader,
                       sReceiveLoader,
                       sGetAttributeLoader,
                       sSetAttributeLoader,
                       sOperationLoader,
                       sFlowSupportLoader);
        // Create a subclass of ContextCLTestFactoryBase into a jar in the jars subdirectory
        String newSubclass = getClass().getPackage().getName() + ".ContextCLFactory";
        byte[] classBytes = generateSubclass(ContextCLTestFactoryBase.class,
                                             newSubclass);
        JarClassLoaderTest.JarContents jc = new JarClassLoaderTest.JarContents(transformName(newSubclass) + ".class",
                                                                               classBytes);
        // Create a subclass of ContextCLTestModuleBase into the jar as well
        String newModuleSubclass = ContextCLTestFactoryBase.MODULE_SUBCLASS_NAME;
        byte[] moduleClassBytes = generateSubclassURNConstructor(ContextCLTestModuleBase.class,
                                                                 newModuleSubclass);
        JarClassLoaderTest.JarContents moduleClass = new JarClassLoaderTest.JarContents(transformName(newModuleSubclass) + ".class",
                                                                                        moduleClassBytes);
        // Create the factory file to load this factory via the service loader
        createJar("clprovider.jar",
                  new JarClassLoaderTest.JarContents[] { jc, moduleClass, new JarClassLoaderTest.JarContents("META-INF/services/" + ModuleFactory.class.getName(), newSubclass.getBytes()) });
        final ModuleURN instanceURN = new ModuleURN(ContextCLTestFactoryBase.PROVIDER_URN,
                                                    "con");
        // Create the properties file for testing default parameter setting
        Properties properties = new Properties();
        properties.setProperty("Attribute",
                               "does not matter");
        properties.setProperty(".Attribute",
                               "does not matter");
        savePropertiesForProvider(instanceURN,
                                  properties);
        String parameter = ContextCLTestFactoryBase.PROVIDER_URN + ";" + instanceURN.getValue();
        File f = createFileWithText("createModule;" + parameter);
        // verify that the context classloader is same as the current classloader
        assertSame(getClass().getClassLoader(),
                   Thread.currentThread().getContextClassLoader());
        createSaWith(f.getAbsolutePath());
        assertTrue(sa.isRunning());
        // verify that the context classloader is now different
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        assertNotSame(getClass().getClassLoader(),
                      contextLoader);
        // verify factory class loaders
        assertCLEquals(contextLoader,
                       sConstructClassLoader,
                       sCreateClassLoader,
                       sFactorySetAttributeLoader);
        assertEquals(null,
                     sFactoryGetAttributeLoader,
                     sFactoryOperationLoader);
        sFactorySetAttributeLoader = null;
        final ContextCLFactoryMXBean factoryBean = JMX.newMXBeanProxy(getMBeanServer(),
                                                                      ContextCLTestFactoryBase.PROVIDER_URN.toObjectName(),
                                                                      ContextCLFactoryMXBean.class);
        invokeClearCtxCL(new Callable<Object>() {
            public Object call()
                    throws Exception
            {
                factoryBean.operation();
                factoryBean.setAttribute("value");
                return factoryBean.getAttribute();
            }
        });
        assertCLEquals(contextLoader,
                       sFactoryGetAttributeLoader,
                       sFactoryOperationLoader,
                       sFactorySetAttributeLoader);
        final ModuleManagerMXBean mmBean = JMX.newMXBeanProxy(getMBeanServer(),
                                                              new ObjectName(ModuleManager.MODULE_MBEAN_NAME),
                                                              ModuleManagerMXBean.class);
        //verify autostarted module
        verifyModuleCtxLoaders(mmBean,
                               instanceURN);
        // Clear all module loaders
        sStartLoader = sStopLoader = sRequestLoader = sCancelLoader = sReceiveLoader = sGetAttributeLoader = sSetAttributeLoader = sOperationLoader = sFlowSupportLoader = null;
        // Clear factory create loader
        sCreateClassLoader = null;
        // delete, recreate & restart the module
        invokeClearCtxCL(new Callable<Object>() {
            public Object call()
                    throws Exception
            {
                mmBean.deleteModule(instanceURN.toString());
                return mmBean.createModule(instanceURN.parent().toString(),
                                           instanceURN.toString());
            }
        });
        // verify factory create loader
        assertEquals(contextLoader,
                     sCreateClassLoader);
        verifyModuleCtxLoaders(mmBean,
                               instanceURN);
    }
    /**
     * Verifies that the given module functions and the context classloader is correct.
     *
     * @param inMgrBean a <code>ModuleManagerMXBean</code> value
     * @param inInstanceURN a <code>ModuleURN</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyModuleCtxLoaders(final ModuleManagerMXBean inMgrBean,
                                        final ModuleURN inInstanceURN)
            throws Exception
    {
        ClassLoader contextLoader = loader;
        // run the module through data flows
        invokeClearCtxCL(new Callable<Object>() {
            public Object call()
                    throws Exception
            {
                //as an emitter
                DataFlowID flowID = inMgrBean.createDataFlow(inInstanceURN.toString());
                inMgrBean.cancel(flowID.toString());
                // as a receiver
                flowID = inMgrBean.createDataFlow(CopierModuleFactory.INSTANCE_URN + ";a string^" + inInstanceURN);
                // wait until the data has flowed
                DataFlowInfo flowInfo;
                do {
                    Thread.sleep(500);
                    flowInfo = inMgrBean.getDataFlowInfo(flowID.toString());
                    assertEquals(3,
                                 flowInfo.getFlowSteps().length);
                } while (flowInfo.getFlowSteps()[0].getNumEmitted() < 0);
                inMgrBean.cancel(flowID.toString());
                // stop the module
                inMgrBean.stop(inInstanceURN.toString());
                return null;
            }
        });
        // verify module class loaders
        assertCLEquals(contextLoader,
                       sStartLoader,
                       sStopLoader,
                       sRequestLoader,
                       sCancelLoader,
                       sReceiveLoader,
                       sFlowSupportLoader,
                       sSetAttributeLoader);
        final ContextCLModuleMXBean moduleBean = JMX.newMXBeanProxy(getMBeanServer(),
                                                                    inInstanceURN.toObjectName(),
                                                                    ContextCLModuleMXBean.class);
        invokeClearCtxCL(new Callable<Object>() {
            public Object call()
                    throws Exception
            {
                moduleBean.operation();
                moduleBean.setAttribute("value");
                return moduleBean.getAttribute();
            }
        });
        assertCLEquals(contextLoader,
                       sStartLoader,
                       sStopLoader,
                       sRequestLoader,
                       sCancelLoader,
                       sReceiveLoader,
                       sGetAttributeLoader,
                       sSetAttributeLoader,
                       sOperationLoader,
                       sFlowSupportLoader);
    }
    /**
     * Verifies that the given expected classloader matches each of the supplied actual classloaders.
     *
     * @param inExpected a <code>ClassLoader</code> value
     * @param inActuals a <code>ClassLoader[]</code> value
     */
    private static void assertCLEquals(ClassLoader inExpected,
                                       ClassLoader... inActuals)
    {
        for(int i = 0; i < inActuals.length; i++) {
            ClassLoader actual = inActuals[i];
            assertEquals("Expected<" + inExpected + ">Actual [" + i + "]<" + actual + ">",
                         inExpected,
                         actual);
        }
    }
    /**
     * Invokes the given block of code using the current context classloader.
     *
     * @param inCallable a <code>Callable&lt;T&gt;</code> value
     * @return a <code>T</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static <T> T invokeClearCtxCL(final Callable<T> inCallable)
            throws Exception
    {
        Thread thread = Thread.currentThread();
        ClassLoader loader = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(inCallable.getClass().getClassLoader());
            return inCallable.call();
        } finally {
            thread.setContextClassLoader(loader);
        }
    }
}
