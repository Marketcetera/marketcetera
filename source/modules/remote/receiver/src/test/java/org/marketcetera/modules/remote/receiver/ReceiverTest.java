package org.marketcetera.modules.remote.receiver;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;
import org.marketcetera.event.LogEvent;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;

import javax.management.JMX;
import javax.security.auth.login.Configuration;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * Tests {@link ReceiverModule}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ReceiverTest extends ModuleTestBase {
    /**
     * Verifies the provider and module infos.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void info() throws Exception {
        initManager();
        assertProviderInfo(mManager, ReceiverFactory.PROVIDER_URN,
                new String[0], new Class[0],
                Messages.PROVIDER_DESCRIPTION.getText(),false, false);
        assertModuleInfo(mManager, ReceiverFactory.INSTANCE_URN,
                ModuleState.STARTED, null, null, false,
                true, true, false, false);
    }

    /**
     * Verifies failures that prevent module from getting started.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void initFailures() throws Exception {
        //Do not initialize the URL
        new ExpectedFailure<ModuleException>(Messages.START_FAIL_NO_URL){
            protected void run() throws Exception {
                initManager(new MockConfigProvider());
            }
        };
        //Specify incorrect URL
        new ExpectedFailure<ModuleException>(Messages.ERROR_STARTING_MODULE){
            protected void run() throws Exception {
                initManager(configProviderWithURLValue(
                        "this is not a valid URL"));
            }
        };
        //Verify that we can start up with correct configuration
        //after all these failures
        info();
    }

    /**
     * Verifies participation of the module in data flows.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void dataFlowSuccess() throws Exception {
        initManager();
        Object[]objs = new Object[]{
                Integer.MAX_VALUE,
                BigInteger.TEN,
                BigDecimal.TEN,
                new LinkedList(),
                new HashMap()
        };
        CopierModule.SynchronousRequest req = new CopierModule.SynchronousRequest(objs);
        //exhaust all the permits
        req.semaphore.acquire();
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, req),
                new DataRequest(ReceiverFactory.PROVIDER_URN)
        });
        //Wait for all the objects to get emitted
        req.semaphore.acquire();
        DataFlowInfo flowInfo = mManager.getDataFlowInfo(flowID);
        assertFlowInfo(flowInfo, flowID, 2,
                true, false, null, null);
        assertFlowStep(flowInfo.getFlowSteps()[0],
                CopierModuleFactory.INSTANCE_URN, true, 5, 0, null,
                false, 0, 0, null, CopierModuleFactory.INSTANCE_URN, req.toString());
        assertFlowStep(flowInfo.getFlowSteps()[1],
                ReceiverFactory.INSTANCE_URN, false, 0, 0, null,
                true, 5, 0, null, ReceiverFactory.PROVIDER_URN, null);
    }

    /**
     * Verifies data flow behavior with null and non-serializable values.
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void dataFlowFailures() throws Exception {
        initManager();
        Object[]objs = new Object[]{
                Integer.MAX_VALUE,
                null, //ignored, causes no errors
                Arrays.asList(BigInteger.TEN),
                new Object(), //not serializable, causes errors
                BigDecimal.TEN,
                Arrays.asList(new Object()) //not serializable, causes errors
        };
        CopierModule.SynchronousRequest req = new CopierModule.SynchronousRequest(objs);
        //exhaust all the permits
        req.semaphore.acquire();
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, req),
                new DataRequest(ReceiverFactory.PROVIDER_URN)
        });
        //Wait for all the objects to get emitted
        req.semaphore.acquire();
        DataFlowInfo flowInfo = mManager.getDataFlowInfo(flowID);
        assertFlowInfo(flowInfo, flowID, 2,
                true, false, null, null);
        assertFlowStep(flowInfo.getFlowSteps()[0],
                CopierModuleFactory.INSTANCE_URN, true, 6, 0, null,
                false, 0, 0, null, CopierModuleFactory.INSTANCE_URN, req.toString());
        assertFlowStep(flowInfo.getFlowSteps()[1],
                ReceiverFactory.INSTANCE_URN, false, 0, 0, null,
                true, 6, 2, Messages.ERROR_WHEN_TRANSMITTING.getText(
                        String.valueOf(objs[objs.length - 1])),
                ReceiverFactory.PROVIDER_URN, null);
    }

    /**
     * Tests MXBean functions.
     *
     * @throws Exception if there were errors
     */
    @Test
    public void jmx() throws Exception {
        initManager();
        verifyBeanInfo(getMBeanServer().getMBeanInfo(
                ReceiverFactory.INSTANCE_URN.toObjectName()));
        final ReceiverModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                ReceiverFactory.INSTANCE_URN.toObjectName(),
                ReceiverModuleMXBean.class);
        //Verify setters and getters
        assertEquals(DEFAULT_URL, bean.getURL());
        assertEquals(false, bean.isSkipJAASConfiguration());
        assertLogLevel(bean, LogEvent.Level.WARN);
        //url cannot be updated when the module is running.
        new ExpectedFailure<IllegalStateException>(
                Messages.ILLEGAL_STATE_SET_URL.getText()){
            protected void run() throws Exception {
                bean.setURL("blah");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(
                Messages.ILLEGAL_STATE_SET_SKIP_JAAS.getText()){
            protected void run() throws Exception {
                bean.setSkipJAASConfiguration(true);
            }
        };
        //log level can be updated any time.
        LogEvent.Level logLevel = LogEvent.Level.INFO;
        bean.setLogLevel(logLevel);
        assertLogLevel(bean, logLevel);
        //Stop the module
        mManager.stop(ReceiverFactory.INSTANCE_URN);
        //Verify setters and getters
        assertEquals(DEFAULT_URL, bean.getURL());
        assertLogLevel(bean, logLevel);
        //Verify that we can set the URL
        String url = "myURL";
        bean.setURL(url);
        assertEquals(url, bean.getURL());
        //that we can set it to null
        bean.setURL(null);
        assertEquals(null, bean.getURL());
        //verify that we can set the log level as well
        verifyLogLevels(bean);
        logLevel = LogEvent.Level.ERROR;
        bean.setLogLevel(logLevel);
        assertLogLevel(bean, logLevel);
        //Verify log level validations
        new ExpectedFailure<IllegalArgumentException>(
                Messages.NULL_LEVEL_VALUE.getText(EnumSet.allOf(
                        LogEvent.Level.class))){
            protected void run() throws Exception {
                bean.setLogLevel(null);
            }
        };
        //Verify that we still can not set skip JAAS
        new ExpectedFailure<IllegalArgumentException>(
                Messages.ILLEGAL_STATE_SET_SKIP_JAAS.getText()){
            protected void run() throws Exception {
                bean.setSkipJAASConfiguration(true);
            }
        };
        //Verify module start failure with an invalid URL
        bean.setURL("invalidURL");
        new ExpectedFailure<ModuleException>(Messages.ERROR_STARTING_MODULE){
            protected void run() throws Exception {
                mManager.start(ReceiverFactory.INSTANCE_URN);
            }
        };
        //verify module state
        assertModuleInfo(mManager, ReceiverFactory.INSTANCE_URN,
                ModuleState.START_FAILED, null, null, false, true,
                true, false, false);
        //Verify module starts when a valid URL is specified
        bean.setURL(DEFAULT_URL);
        mManager.start(ReceiverFactory.INSTANCE_URN);
    }

    /**
     * Verifies that JAAS Configuration is skipped when the
     * corresponding module attribute is turned on.
     *
     * @throws Exception if there was an exception
     */
    @Test(timeout = 10000)
    public void skipJaasConfig() throws Exception {
        MockConfigProvider prov = configProviderWithURLValue(DEFAULT_URL);
        prov.addDefault(ReceiverFactory.INSTANCE_URN, "SkipJAASConfiguration", "true");
        //The module doesn't fail to start if the credentials are incorrect
        //as JMS doesn't generate failure unless we attempt to send some data.
        initManager(prov);
        final ReceiverModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                ReceiverFactory.INSTANCE_URN.toObjectName(),
                ReceiverModuleMXBean.class);
        assertEquals(true, bean.isSkipJAASConfiguration());
        //Setup a data flow to force a connection and see what happens
        final int data = Integer.MAX_VALUE;
        DataFlowID flowID = mManager.createDataFlow(new DataRequest[]{
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[]{
                        data
                }),
                new DataRequest(ReceiverFactory.PROVIDER_URN)
        });
        DataFlowInfo flowInfo = mManager.getDataFlowInfo(flowID);
        assertFlowInfo(flowInfo, flowID, 2, true, false, null, null);
        assertEquals(2, flowInfo.getFlowSteps().length);
        while(flowInfo.getFlowSteps()[1].getLastReceiveError() == null) {
            Thread.sleep(100);
            flowInfo = mManager.getDataFlowInfo(flowID);
        }
        assertFlowStep(flowInfo.getFlowSteps()[1],
                ReceiverFactory.INSTANCE_URN, false, 0, 0, null,
                true, 1, 1, Messages.ERROR_WHEN_TRANSMITTING.getText(
                String.valueOf(data)), ReceiverFactory.PROVIDER_URN, null);
        //cancel the flow
        mManager.cancel(flowID);
    }

    /**
     * Verifies that the log level can be overridden by supplying a default
     * value.
     *
     * @throws Exception if there were unexpected failures.
     */
    @Test
    public void specifyDefaultLogLevel() throws Exception {
        MockConfigProvider prov = configProviderWithURLValue(DEFAULT_URL);
        prov.addDefault(ReceiverFactory.INSTANCE_URN, "LogLevel", "DEBUG");
        initManager(prov);
        final ReceiverModuleMXBean bean = JMX.newMXBeanProxy(getMBeanServer(),
                ReceiverFactory.INSTANCE_URN.toObjectName(),
                ReceiverModuleMXBean.class);
        assertLogLevel(bean, LogEvent.Level.DEBUG);
    }

    /**
     * Stops the module manager.
     *
     * @throws Exception if there were failures.
     */
    @After
    public void stopManager() throws Exception {
        if (mManager != null) {
            mManager.stop();
            mManager = null;
        }
    }

    /**
     * This is needed for {@link #skipJaasConfig()} test to pass.
     */
    @Before
    public void resetJAASConfig() {
        Configuration.setConfiguration(null);
    }

    /**
     * Tests all supported log levels and their translation to log4j levels.
     *
     * @param inBean the bean for the module
     */
    private void verifyLogLevels(ReceiverModuleMXBean inBean) {
        for(LogEvent.Level level: LogEvent.Level.values()) {
            inBean.setLogLevel(level);
            assertLogLevel(inBean, level);
        }
    }

    /**
     * Verifies that the supplied loglevel matches the bean current
     * attribute value and the current log4j user messages logger
     * configuration.
     *
     * @param inMBean the receiver's bean proxy.
     * @param inLogLevel the expected log level.
     */
    private void assertLogLevel(ReceiverModuleMXBean inMBean,
                                LogEvent.Level inLogLevel) {
        assertEquals(inLogLevel,  inMBean.getLogLevel());
        Level level = LogManager.getLogger(
                org.marketcetera.core.Messages.USER_MSG_CATEGORY).getLevel();
        assertNotNull(level);
        assertEquals(inLogLevel.toString(), level.toString());
    }

    /**
     * Initializes the module manager with the default URL value for the
     * receiver module.
     *
     * @throws Exception if there were errors.
     */
    private void initManager() throws Exception {
        MockConfigProvider prov = configProviderWithURLValue(DEFAULT_URL);
        initManager(prov);
    }

    /**
     * Initializes the module manager with the supplied configuration provider.
     *
     * @param inProvider the configuration provider instance.
     *
     * @throws Exception if there were errors.
     */
    private void initManager(MockConfigProvider inProvider) throws Exception {
        mManager = new ModuleManager();
        mManager.setConfigurationProvider(inProvider);
        mManager.init();
    }

    /**
     * Creates a configuration provider instance with the supplied URL
     * value as the default URL value for the receiver module.
     *
     * @param inUrl the URL value.
     *
     * @return the configuration provider instance.
     */
    private MockConfigProvider configProviderWithURLValue(String inUrl) {
        MockConfigProvider prov = new MockConfigProvider();
        prov.addDefault(ReceiverFactory.INSTANCE_URN, "URL", inUrl);
        return prov;
    }

    private ModuleManager mManager;
    /**
     * The default URL value to run the receiver's embedded broker on.
     */
    private static final String DEFAULT_URL = "tcp://localhost:61617";
}
