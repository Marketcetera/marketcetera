package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.core.Util;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnectionTestBase;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.except.I18NException;

import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link EmbeddedConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class EmbeddedConnectionTest extends
        AbstractStrategyEngineConnectionTestBase {

    private ModuleManager mModuleManager;
    private MBeanServerConnection mMBeanServer;
    protected StrategyEngine mEngine;

    @Before
    public void before() throws Exception {
        super.before();
        mModuleManager = ModuleSupport.getModuleManager();
        mMBeanServer = ModuleSupport.getMBeanServerConnection();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
        mEngine = createEngine("Embedded");
    }

    @After
    public void after() throws Exception {
        super.after();
        for (ModuleURN urn : mModuleManager
                .getModuleInstances(StrategyModuleFactory.PROVIDER_URN)) {
            try {
                mModuleManager.stop(urn);
            } catch (Exception e) {
                // ignore
            } finally {
                try {
                    mModuleManager.deleteModule(urn);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    @Test
    public void testConstructorValidation() throws Exception {
        new ExpectedNullArgumentFailure("engine") {
            @Override
            protected void run() throws Exception {
                new EmbeddedConnection(null, mock(ExecutorService.class), null);
            }
        };
        new ExpectedNullArgumentFailure("guiExecutor") {
            @Override
            protected void run() throws Exception {
                new EmbeddedConnection(createEngine(""), null, null);
            }
        };
    }

    @Override
    public void testDeploy() throws Exception {
        super.testDeploy();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.CREATED, false, null);
    }

    @Override
    public void testDeployWithParameters() throws Exception {
        super.testDeployWithParameters();
        assertModule(new ModuleURN("metc:strategy:system:strat2"),
                ModuleState.CREATED, true, ImmutableMap.of("abc", "xyz", "123",
                        "abc"));
    }

    @Test
    public void testDeployInvalidLanguage() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setLanguage("ABC");
        new ExpectedFailure<I18NException>(
                "The embedded engine supports Ruby strategies only.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployNonExistentFile() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setScriptPath("1234dslsdaksdf/strategy.java");
        new ExpectedFailure<I18NException>(
                "The file specified as the strategy source, \""
                        + new File("1234dslsdaksdf/strategy.java")
                                .getAbsolutePath()
                        + "\", must exist and must be readable.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testDeployBogusURL() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        final Strategy strategy = createStrategyToDeploy();
        strategy.setScriptPath("http://www.google.com");
        new ExpectedFailure<I18NException>(
                "The file specified as the strategy source, \""
                        + new File("http://www.google.com").getAbsolutePath()
                        + "\", must exist and must be readable.") {
            @Override
            protected void run() throws Exception {
                fixture.deploy(strategy);
            }
        };
    }

    @Override
    public void testUndeploy() throws Exception {
        super.testUndeploy();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
    }

    @Test
    public void testUndeployNonExistantModule() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        final DeployedStrategy deployed = fixture.deploy(strategy);
        mModuleManager.deleteModule(deployed.getUrn());
        new ExpectedFailure<ModuleNotFoundException>(
                "Unable to find a module with URN 'metc:strategy:system:strat1'. Ensure that the module URN is correct and retry operation") {
            @Override
            protected void run() throws Exception {
                fixture.undeploy(deployed);
            }
        };
    }

    @Override
    public void testStart() throws Exception {
        super.testStart();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.STARTED, false, null);
    }

    @Test
    public void testStartingStartedModule() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        final DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        new ExpectedFailure<ModuleStateException>(
                "Unable to start module 'metc:strategy:system:strat1' as it is in state 'STARTED'. A module can be started when it is in one of '[CREATED, START_FAILED, STOPPED]' states. Ensure that module 'metc:strategy:system:strat1' is in one of '[CREATED, START_FAILED, STOPPED]' states and retry operation.") {
            @Override
            protected void run() throws Exception {
                fixture.start(deployed);
            }
        };
    }

    @Override
    public void testStop() throws Exception {
        super.testStop();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.STOPPED, false, null);
    }

    @Test
    public void testStoppingStoppedModule() throws Exception {
        final AbstractStrategyEngineConnection fixture = createFixture();
        Strategy strategy = createStrategyToDeploy();
        final DeployedStrategy deployed = fixture.deploy(strategy);
        fixture.start(deployed);
        fixture.stop(deployed);
        new ExpectedFailure<ModuleStateException>(
                "Unable to stop Module 'metc:strategy:system:strat1' as it is in state 'STOPPED'. A module can be stopped if it is in one of '[STARTED, STOP_FAILED]' states. Ensure that the module is in one of '[STARTED, STOP_FAILED]' states and retry operation. ") {
            @Override
            protected void run() throws Exception {
                fixture.stop(deployed);
            }
        };
    }

    @Override
    public void testRestart() throws Exception {
        super.testRestart();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.STARTED, false, null);
    }

    @Override
    public void testUndeployRunningStrategy() throws Exception {
        super.testUndeployRunningStrategy();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
    }

    @Override
    public void testUpdate() throws Exception {
        super.testUpdate();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.CREATED, true, ImmutableMap.of("xyz", "123"));
    }

    @Override
    public void testRefreshDeployedStrategy() throws Exception {
        super.testRefreshDeployedStrategy();
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.STARTED, true, ImmutableMap.of("xyz", "123"));
    }

    @Override
    public void testRefreshDeployedStrategyThatNoLongerExists()
            throws Exception {
        super.testRefreshDeployedStrategyThatNoLongerExists();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
    }

    @Override
    public void testRefresh() throws Exception {
        super.testRefresh();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(2));
        assertModule(new ModuleURN("metc:strategy:system:strat1"),
                ModuleState.STARTED, true, ImmutableMap.of("xyz", "123"));
        assertModule(new ModuleURN("metc:strategy:system:strat3"),
                ModuleState.CREATED, false, null);
    }

    @Override
    protected AbstractStrategyEngineConnection createFixture() {
        EmbeddedConnection fixture = new EmbeddedConnection(mEngine,
                new ImmediateExecutorService(), null);
        fixture.initialize();
        return fixture;
    }

    @Override
    protected void externalUpdateAndStartStrategy(ModuleURN urn,
            Strategy newConfiguration) throws Exception {
        StrategyMXBean proxy = getProxy(urn);
        proxy.setRoutingOrdersToORS(newConfiguration.isRouteOrdersToServer());
        Properties properties = new Properties();
        properties.putAll(newConfiguration.getParameters().map());
        proxy.setParameters(Util.propertiesToString(properties));
        mModuleManager.start(urn);
    }

    @Override
    protected void externalUndeployStrategy(ModuleURN urn) throws Exception {
        mModuleManager.deleteModule(urn);
    }

    @Override
    protected void externalDeployStrategy(Strategy strategy) throws Exception {
        Properties properties = new Properties();
        properties.putAll(strategy.getParameters().map());
        mModuleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                strategy.getInstanceName(), strategy.getClassName(), strategy
                        .getLanguage(), new File(strategy.getScriptPath()),
                properties, strategy.isRouteOrdersToServer(),
                SinkModuleFactory.INSTANCE_URN);
    }

    private void assertModule(ModuleURN urn, ModuleState state, boolean route,
            Map<String, String> parameters) throws Exception {
        assertThat(mModuleManager.getModuleInfo(urn).getState(), is(state));
        StrategyMXBean proxy = getProxy(urn);
        String parametersString = null;
        if (parameters != null) {
            Properties p = new Properties();
            p.putAll(parameters);
            parametersString = Util.propertiesToString(p);
        }
        assertThat(proxy.getParameters(), is(parametersString));
        assertThat(proxy.getOutputDestination(),
                is(SinkModuleFactory.INSTANCE_URN.toString()));
        assertThat(proxy.isRoutingOrdersToORS(), is(route));
    }

    private StrategyMXBean getProxy(final ModuleURN urn)
            throws MXBeanOperationException {
        ObjectName objectName = urn.toObjectName();
        StrategyMXBean proxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
                StrategyMXBean.class);
        return proxy;
    }
}
