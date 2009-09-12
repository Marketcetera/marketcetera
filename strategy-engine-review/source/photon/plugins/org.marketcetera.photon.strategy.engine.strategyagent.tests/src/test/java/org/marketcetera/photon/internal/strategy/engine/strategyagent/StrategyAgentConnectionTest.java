package org.marketcetera.photon.internal.strategy.engine.strategyagent;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.assertDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createStrategy;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.core.Util;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.test.PhotonTestBase;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Test {@link StrategyAgentConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentConnectionTest extends PhotonTestBase {

    private SAClient mMockClient;
    private StrategyAgentConnection mFixture;
    private StrategyEngine mEngine;
    private File mTempScript;

    @Before
    public void before() throws Exception {
        mMockClient = mock(SAClient.class);
        mFixture = new StrategyAgentConnection(mMockClient,
                new ImmediateExecutorService());
        mEngine = createEngine("SATestEngine");
        mFixture.setEngine(mEngine);
        mTempScript = File.createTempFile("my_strategy", "rb");
    }

    @After
    public void after() throws Exception {
        mTempScript.delete();
    }

    @Test
    public void testDeploy() throws Exception {
        new ExpectedNullArgumentFailure("strategy") {
            @Override
            protected void run() throws Exception {
                mFixture.deploy(null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        when(mMockClient.createStrategy((CreateStrategyParameters) anyObject()))
                .thenReturn(urn1);
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(true, "MySAStrategy", "JAVA", ImmutableMap.of(
                        "key", "value")));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        Strategy strategy = createTestStrategy();
        DeployedStrategy result = mFixture.deploy(strategy);
        assertDeployedStrategy(result, urn1, mEngine, StrategyState.STOPPED,
                "strat1", "MySAStrategy", "JAVA",
                mTempScript.getAbsolutePath(), true, ImmutableMap.of("key",
                        "value"));
    }

    private Strategy createTestStrategy() {
        Strategy strategy = createStrategy("strat1");
        strategy.setClassName("MySAStrategy");
        strategy.setLanguage("JAVA");
        strategy.setRouteOrdersToServer(true);
        strategy.setScriptPath(mTempScript.getAbsolutePath());
        strategy.getParameters().put("key", "value");
        return strategy;
    }

    static ModuleInfo createModuleInfo(ModuleState state) {
        return new ModuleInfo(null, state, null, null, null, null, null, false,
                false, false, false, false, null, null, -1, false, -1);
    }

    static Map<String, Object> createParameters(boolean routing, String name,
            String language, ImmutableMap<String, String> parameters) {
        String properties = null;
        if (parameters != null) {
            Properties props = new Properties();
            props.putAll(parameters);
            properties = Util.propertiesToString(props);
        }
        // can't use ImmutableMap since properties may be null
        Map<String, Object> map = Maps.newHashMap();
        map.put("RoutingOrdersToORS", routing);
        map.put("Name", name);
        map.put("Language", language);
        map.put("Parameters", properties);
        return map;
    }

    @Test
    public void testUndeploy() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.undeploy(null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        mEngine.getDeployedStrategies().add(strategy);
        strategy.setUrn(urn1);
        mFixture.undeploy(strategy);
        verify(mMockClient).delete(urn1);
        assertThat(mEngine.getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testUndeployRunningStrategy() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.undeploy(null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.STARTED));
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        mEngine.getDeployedStrategies().add(strategy);
        strategy.setUrn(urn1);
        mFixture.undeploy(strategy);
        verify(mMockClient).stop(urn1);
        verify(mMockClient).delete(urn1);
        assertThat(mEngine.getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testStart() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.start(null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        strategy.setState(StrategyState.STOPPED);
        strategy.setUrn(urn1);
        mFixture.start(strategy);
        verify(mMockClient).start(urn1);
        assertThat(strategy.getState(), is(StrategyState.RUNNING));
    }

    @Test
    public void testStop() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.stop(null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        strategy.setState(StrategyState.RUNNING);
        strategy.setUrn(urn1);
        mFixture.stop(strategy);
        verify(mMockClient).stop(urn1);
        assertThat(strategy.getState(), is(StrategyState.STOPPED));
    }

    @Test
    public void testUpdate() throws Exception {
        new ExpectedNullArgumentFailure("deployedStrategy") {
            @Override
            protected void run() throws Exception {
                mFixture.update(null, createStrategy("abc"));
            }
        };
        new ExpectedNullArgumentFailure("newConfiguration") {
            @Override
            protected void run() throws Exception {
                mFixture.update(createDeployedStrategy("abc"), null);
            }
        };
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        strategy.setUrn(urn1);
        strategy.setRouteOrdersToServer(true);
        // change routing and add parameter
        Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(false);
        newConfiguration.getParameters().put("xyz", "123");
        // need to mock client to return new values
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(false, "MySAStrategy", "JAVA", ImmutableMap
                        .of("xyz", "123")));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        mFixture.update(strategy, newConfiguration);
        Properties props = new Properties();
        props.setProperty("xyz", "123");
        // verify mock client was called
        verify(mMockClient).setProperties(
                urn1,
                ImmutableMap.of("RoutingOrdersToORS", (Object) false,
                        "Parameters", Util.propertiesToString(props)));
        // assert the strategy was updated
        assertDeployedStrategy(strategy, urn1, null, StrategyState.STOPPED,
                "strat1", "MySAStrategy", "JAVA", null, false, ImmutableMap.of(
                        "xyz", "123"));
    }

    @Test
    public void testUpdateError() throws Exception {
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        final DeployedStrategy strategy = createDeployedStrategy("strat1");
        strategy.setUrn(urn1);
        strategy.setRouteOrdersToServer(true);
        // change routing and add parameter
        final Strategy newConfiguration = createStrategy(null);
        newConfiguration.setRouteOrdersToServer(false);
        newConfiguration.getParameters().put("xyz", "123");
        // mock client to fail
        Properties props = new Properties();
        props.setProperty("xyz", "123");
        when(
                mMockClient.setProperties(urn1, ImmutableMap.of(
                        "RoutingOrdersToORS", (Object) false, "Parameters",
                        Util.propertiesToString(props)))).thenReturn(
                ImmutableMap.of("RoutingOrdersToORS",
                        (Object) new RemoteProperties(new Exception(
                                "invalid route")), "Parameters",
                        new RemoteProperties(
                                new Exception("invalid parameters"))));
        // need to mock client to return old values
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(true, "MySAStrategy", "JAVA", null));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        new ExpectedFailure<Exception>(
                "The remote strategy agent failed to set the order routing to 'false':\n  invalid route\n"
                        + "The remote strategy agent failed to set the strategy parameters to 'xyz=123':\n  invalid parameters") {
            @Override
            protected void run() throws Exception {
                mFixture.update(strategy, newConfiguration);
            }
        };
    }

    @Test
    public void testRefreshSingleStrategy() throws Exception {
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy strategy = createDeployedStrategy("strat1");
        strategy.setUrn(urn1);
        strategy.setClassName("MySAStrategy");
        strategy.setLanguage("JAVA");
        strategy.setRouteOrdersToServer(true);
        strategy.setScriptPath("C:\\MyStrat.java");
        strategy.getParameters().put("key", "value");
        // program mock
        when(mMockClient.getInstances(StrategyModuleFactory.PROVIDER_URN))
                .thenReturn(Arrays.asList(urn1));
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(false, "MySAStrategy2", "RUBY", ImmutableMap
                        .of("xyz", "123")));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.STARTED));
        // refresh and validate everything is pulled from the backend
        mFixture.refresh(strategy);
        assertDeployedStrategy(strategy, urn1, null, StrategyState.RUNNING,
                "strat1", "MySAStrategy2", "RUBY", "C:\\MyStrat.java", false,
                ImmutableMap.of("xyz", "123"));
    }

    @Test
    public void testRefreshSingleStrategyThatNoLongerExists() throws Exception {
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy deployed = createDeployedStrategy("DifferentName");
        deployed.setUrn(urn1);
        mFixture.getEngine().getDeployedStrategies().add(deployed);
        when(mMockClient.getInstances(StrategyModuleFactory.PROVIDER_URN))
                .thenReturn(Collections.<ModuleURN> emptyList());
        mFixture.refresh(deployed);
        // should be removed
        assertThat(mFixture.getEngine().getDeployedStrategies().size(), is(0));
    }

    @Test
    public void testRefreshEngine() throws Exception {
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        DeployedStrategy toUpdate = createDeployedStrategy("strat1");
        toUpdate.setUrn(urn1);
        toUpdate.setClassName("MySAStrategy");
        toUpdate.setLanguage("JAVA");
        toUpdate.setRouteOrdersToServer(true);
        toUpdate.setScriptPath("C:\\MyStrat.java");
        toUpdate.getParameters().put("key", "value");
        mEngine.getDeployedStrategies().add(toUpdate);
        ModuleURN urn2 = new ModuleURN("metc:strategy:system:strat2");
        DeployedStrategy toRemove = createDeployedStrategy("strat2");
        toRemove.setUrn(urn2);
        mEngine.getDeployedStrategies().add(toRemove);
        // program mock
        ModuleURN urn3 = new ModuleURN("metc:strategy:system:strat3");
        when(mMockClient.getInstances(StrategyModuleFactory.PROVIDER_URN))
                .thenReturn(Arrays.asList(urn1, urn3));
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(false, "MySAStrategy2", "RUBY", ImmutableMap
                        .of("xyz", "123")));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.STARTED));
        when(mMockClient.getModuleInfo(urn3)).thenReturn(
                createModuleInfo(ModuleState.STOPPED));
        when(mMockClient.getProperties(urn3)).thenReturn(
                createParameters(true, "MySAStrategy3", "JAVA", ImmutableMap
                        .of("abc", "123")));
        // refresh and validate everything is pulled from the backend
        mFixture.refresh();
        assertThat(mFixture.getEngine().getDeployedStrategies().size(), is(2));
        for (DeployedStrategy deployed : mFixture.getEngine()
                .getDeployedStrategies()) {
            if (deployed == toUpdate) {
                assertDeployedStrategy(deployed, urn1, mEngine,
                        StrategyState.RUNNING, "strat1", "MySAStrategy2",
                        "RUBY", "C:\\MyStrat.java", false, ImmutableMap.of(
                                "xyz", "123"));
            } else {
                assertDeployedStrategy(deployed, urn3, mEngine,
                        StrategyState.STOPPED, "strat3", "MySAStrategy3",
                        "JAVA", null, true, ImmutableMap.of("abc", "123"));
            }
        }
    }

    @Test
    public void testScriptPathURLResolves() throws Exception {
        ModuleURN urn1 = new ModuleURN("metc:strategy:system:strat1");
        when(mMockClient.createStrategy((CreateStrategyParameters) anyObject()))
                .thenReturn(urn1);
        when(mMockClient.getProperties(urn1)).thenReturn(
                createParameters(true, "MySAStrategy", "JAVA", ImmutableMap.of(
                        "key", "value")));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        Strategy strategy = createTestStrategy();
        String url = mTempScript.toURI().toString();
        String actualPath = mTempScript.getAbsolutePath();
        strategy.setScriptPath(url);
        setLevel(StrategyAgentConnection.class.getName(), Level.DEBUG);
        // should succeed if url is resolved to a local file path
        mFixture.deploy(strategy);
        // there will also be a debug message
        assertLastEvent(
                Level.DEBUG,
                StrategyAgentConnection.class.getName(),
                MessageFormat
                        .format(
                                "Resolved strategy scriptPath ''{0}'' as a URL to file ''{1}''.",
                                url, actualPath), null);
    }

}
