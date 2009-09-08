package org.marketcetera.photon.internal.strategy.engine.embedded;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createStrategy;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.core.Util;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.photon.test.ExpectedIllegalStateException;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.except.I18NException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/* $License$ */

/**
 * Tests {@link EmbeddedConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EmbeddedConnectionTest extends PhotonTestBase {

    private final ModuleURN mTestInstanceURN = new ModuleURN(
            "metc:strategy:system:strat1");
    private ModuleManager mModuleManager;
    private MBeanServerConnection mMBeanServer;
    private MockPersistenceService mPersistenceService;
    private EmbeddedConnection mFixture;
    private File mTempScript;
    private Strategy mTestConfiguration;

    @Before
    public void before() throws Exception {
        mModuleManager = ModuleSupport.getModuleManager();
        mMBeanServer = ModuleSupport.getMBeanServerConnection();
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
        mPersistenceService = new MockPersistenceService();
        StrategyEngine engine = createEngine("Embedded");
        mFixture = new EmbeddedConnection(engine,
                new ImmediateExecutorService(), mPersistenceService);
        mFixture.initialize();
        mTempScript = File.createTempFile("my_strategy", "rb");
        FileWriter writer = new FileWriter(mTempScript);
        try {
            writer
                    .write("include_class \"org.marketcetera.strategy.ruby.Strategy\"\nclass MyStrategy < Strategy\n\nend");
        } finally {
            writer.close();
        }
        mTestConfiguration = createTestStrategy();
    }

    private Strategy createTestStrategy() {
        final Strategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createStrategy();
        strategy.setScriptPath(mTempScript.getPath());
        strategy.setClassName("MyStrategy");
        strategy.setLanguage("RUBY");
        strategy.setInstanceName("strat1");
        strategy.setRouteOrdersToServer(true);
        return strategy;
    }

    @After
    public void after() throws Exception {
        mTempScript.delete();
        try {
            mModuleManager.deleteModule(mTestInstanceURN);
        } catch (Exception e) {
            // ignore
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

    @Test
    public void testDeployValidation() throws Exception {
        new ExpectedNullArgumentFailure("strategy") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(null);
            }
        };
        final Strategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createStrategy();
        new ExpectedFailure<I18NException>(
                "The strategy script path was not specified.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
        strategy.setScriptPath("C:\\1234dslsdaksdf\\strategy.java");
        new ExpectedFailure<I18NException>(
                "The strategy class name was not specified.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
        strategy.setClassName("Strategy");
        new ExpectedFailure<I18NException>(
                "The strategy language was not specified.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
        strategy.setLanguage("ABC");
        new ExpectedFailure<I18NException>(
                "The strategy instance name was not specified.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
        strategy.setInstanceName("Strat1");
        new ExpectedFailure<I18NException>(
                "The strategy module could not translate \"ABC\" to a valid language type.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
        strategy.setLanguage("JAVA");
        new ExpectedFailure<I18NException>(
                "The file specified as the strategy source, \"C:\\1234dslsdaksdf\\strategy.java\", must exist and must be readable.") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(strategy);
            }
        };
    }

    @Test
    public void testValidation() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.start(null);
            }
        };
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.stop(null);
            }
        };
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.update(null, null);
            }
        };
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.undeploy(null);
            }
        };
        final DeployedStrategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createDeployedStrategy();
        new ExpectedNullArgumentFailure("newConfiguration") {
            @Override
            protected void run() throws Exception {
                mFixture.update(strategy, null);
            }
        };
        new ExpectedIllegalStateException(
                "Cannot start a DeployedStrategy that does not have a ModuleURN") {
            @Override
            protected void run() throws Exception {
                mFixture.start(strategy);
            }
        };
        new ExpectedIllegalStateException(
                "Cannot stop a DeployedStrategy that does not have a ModuleURN") {
            @Override
            protected void run() throws Exception {
                mFixture.stop(strategy);
            }
        };
        new ExpectedIllegalStateException(
                "Cannot update a DeployedStrategy that does not have a ModuleURN") {
            @Override
            protected void run() throws Exception {
                mFixture.update(strategy, StrategyEngineCoreFactory.eINSTANCE
                        .createStrategy());
            }
        };
        new ExpectedIllegalStateException(
                "Cannot undeploy a DeployedStrategy that does not have a ModuleURN") {
            @Override
            protected void run() throws Exception {
                mFixture.undeploy(strategy);
            }
        };
        new ExpectedIllegalStateException(
                "Cannot refresh a DeployedStrategy that does not have a ModuleURN") {
            @Override
            protected void run() throws Exception {
                mFixture.refresh(strategy);
            }
        };
    }

    @Test
    public void testDeployStartStopUpdateUndeploy() throws Exception {
        DeployedStrategy deployed = deployAndStart(mTestConfiguration,
                mTestInstanceURN);
        mFixture.stop(deployed);
        assertModule(mTestInstanceURN, ModuleState.STOPPED, true, null);
        assertDeployedStrategy(deployed, mTestInstanceURN,
                mFixture.getEngine(), StrategyState.STOPPED, "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), true, null);
        Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(false);
        newConfiguration.getParameters().put("xyz", "123");
        mFixture.update(deployed, newConfiguration);
        assertModule(mTestInstanceURN, ModuleState.STOPPED, false, ImmutableMap
                .of("xyz", "123"));
        assertDeployedStrategy(deployed, mTestInstanceURN,
                mFixture.getEngine(), StrategyState.STOPPED, "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), false,
                ImmutableMap.of("xyz", "123"));
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), false,
                ImmutableMap.of("xyz", "123"));
        undeploy(deployed);
    }

    @Test
    public void testUndeployRunningStrategy() throws Exception {
        DeployedStrategy deployed = deployAndStart(mTestConfiguration,
                mTestInstanceURN);
        undeploy(deployed);
    }

    @Test
    public void testScriptPathURLResolves() throws Exception {
        Strategy strategy = createTestStrategy();
        String url = mTempScript.toURI().toString();
        String actualPath = mTempScript.getAbsolutePath();
        strategy.setScriptPath(url);
        setLevel(EmbeddedConnection.class.getName(), Level.DEBUG);
        // should succeed if url is resolved to a local file path
        mFixture.deploy(strategy);
        // there will also be a debug message
        assertLastEvent(
                Level.DEBUG,
                EmbeddedConnection.class.getName(),
                MessageFormat
                        .format(
                                "Resolved strategy scriptPath ''{0}'' as a URL to file ''{1}''.",
                                url, actualPath), EmbeddedConnection.class
                        .getName());
    }

    @Test
    public void testRefreshSingleStrategy() throws Exception {
        DeployedStrategy deployed = refreshHelper();
        // refresh and validate everything is pulled from the backend
        mFixture.refresh(deployed);
        assertDeployedStrategy(deployed, mTestInstanceURN,
                mFixture.getEngine(), StrategyState.STOPPED, "strat1",
                "TestStrat", "JAVA", "abc", true, ImmutableMap.of("xyz", "123"));
    }

    @Test
    public void testRefreshEngine() throws Exception {
        DeployedStrategy toRefresh = refreshHelper();
        // deploy another one
        ModuleURN frameworkNotUI = mModuleManager
                .createModule(StrategyModuleFactory.PROVIDER_URN, "strat2",
                        "TestStrat2", "JAVA", mTempScript, null, true,
                        SinkModuleFactory.INSTANCE_URN);
        DeployedStrategy inUINotFramework = createDeployedStrategy("ShouldRemove");
        inUINotFramework.setUrn(new ModuleURN("metc:strategy:abc"));
        mFixture.getEngine().getDeployedStrategies().add(inUINotFramework);
        // refresh and validate everything is pulled from the backend
        mFixture.refresh();
        assertThat(mFixture.getEngine().getDeployedStrategies().size(), is(2));
        for (DeployedStrategy deployed : mFixture.getEngine()
                .getDeployedStrategies()) {
            if (deployed == toRefresh) {
                assertDeployedStrategy(deployed, mTestInstanceURN, mFixture
                        .getEngine(), StrategyState.STOPPED, "strat1",
                        "TestStrat", "JAVA", "abc", true, ImmutableMap.of(
                                "xyz", "123"));
            } else {
                assertDeployedStrategy(deployed, frameworkNotUI, mFixture
                        .getEngine(), StrategyState.STOPPED, "strat2",
                        "TestStrat2", "JAVA", null, true, null);
            }
        }
        mModuleManager.deleteModule(frameworkNotUI);
    }

    private DeployedStrategy refreshHelper() throws ModuleException {
        // deploy directly
        Properties props = new Properties();
        props.put("xyz", "123");
        ModuleURN urn = mModuleManager.createModule(
                StrategyModuleFactory.PROVIDER_URN, mTestInstanceURN
                        .instanceName(), "TestStrat", "JAVA", mTempScript,
                props, true, SinkModuleFactory.INSTANCE_URN);
        // add directly to the engine with same URN but different values
        DeployedStrategy deployed = createDeployedStrategy("DifferentName");
        deployed.setUrn(urn);
        deployed.setLanguage("RUBY");
        deployed.setRouteOrdersToServer(false);
        deployed.setScriptPath("abc");
        deployed.setClassName("Bogus");
        deployed.setState(StrategyState.RUNNING);
        deployed.getParameters().put("abc", "def");
        mFixture.getEngine().getDeployedStrategies().add(deployed);
        return deployed;
    }

    private DeployedStrategy deployAndStart(Strategy strategy, ModuleURN urn)
            throws Exception {
        DeployedStrategy deployed = deploy(strategy, urn);
        mFixture.start(deployed);
        assertModule(urn, ModuleState.STARTED, true, null);
        assertDeployedStrategy(deployed, mTestInstanceURN,
                mFixture.getEngine(), StrategyState.RUNNING, "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), true, null);
        return deployed;
    }

    private DeployedStrategy deploy(Strategy strategy, ModuleURN urn)
            throws Exception {
        DeployedStrategy deployed = mFixture.deploy(strategy);
        assertModule(urn, ModuleState.CREATED, true, null);
        assertDeployedStrategy(deployed, mTestInstanceURN,
                mFixture.getEngine(), StrategyState.STOPPED, "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), true, null);
        assertThat(mPersistenceService.getPersisted().size(), is(1));
        assertStrategy(mPersistenceService.getPersisted().get(0), "strat1",
                "MyStrategy", "RUBY", mTempScript.getPath(), true, null);
        return deployed;
    }

    private void undeploy(DeployedStrategy deployed) throws Exception,
            InvalidURNException {
        mFixture.undeploy(deployed);
        assertThat(mModuleManager.getModuleInstances(
                StrategyModuleFactory.PROVIDER_URN).size(), is(0));
        assertThat(mPersistenceService.getPersisted().size(), is(0));
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

    private class MockPersistenceService implements IPersistenceService {

        private ImmutableList<Strategy> mPersisted;

        @Override
        public void restore(StrategyEngineConnection connection) {
        }

        @Override
        public void save(List<? extends Strategy> strategies)
                throws IOException {
            mPersisted = ImmutableList.copyOf(strategies);
        }

        public ImmutableList<Strategy> getPersisted() {
            return mPersisted;
        }

    }

}
