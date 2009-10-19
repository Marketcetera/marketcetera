package org.marketcetera.photon.internal.strategy.engine.sa;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createDeployedStrategy;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createStrategy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.core.Util;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleState;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.photon.internal.strategy.engine.sa.StrategyAgentConnection;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnectionTestBase;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.test.ExpectedFailure;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.ConnectionStatusListener;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.DataReceiver;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Test {@link StrategyAgentConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentConnectionTest extends
        AbstractStrategyEngineConnectionTestBase {

    private SAClient mMockClient;
    private StrategyEngine mEngine;

    @Before
    public void before() throws Exception {
        super.before();
        mMockClient = new MockClient();
        mEngine = createEngine("SATestEngine");
    }

    @Test
    public void testConstructorValidation() throws Exception {
        new ExpectedNullArgumentFailure("client") {
            @Override
            protected void run() throws Exception {
                new StrategyAgentConnection(null, mock(ExecutorService.class));
            }
        };
        new ExpectedNullArgumentFailure("guiExecutor") {
            @Override
            protected void run() throws Exception {
                new StrategyAgentConnection(mMockClient, null);
            }
        };
    }

    @Test
    public void testUpdateError() throws Exception {
        /*
         * Use a Mockito mock to program failures.
         */
        mMockClient = mock(SAClient.class);
        final AbstractStrategyEngineConnection fixture = createFixture();
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
                createParameters(true, "MySAStrategy", "JAVA", (String) null));
        when(mMockClient.getModuleInfo(urn1)).thenReturn(
                createModuleInfo(ModuleState.CREATED));
        new ExpectedFailure<Exception>(
                "The remote strategy agent failed to set the order routing to 'false':\n  invalid route\n"
                        + "The remote strategy agent failed to set the strategy parameters to 'xyz=123':\n  invalid parameters") {
            @Override
            protected void run() throws Exception {
                fixture.update(strategy, newConfiguration);
            }
        };
    }

    static ModuleInfo createModuleInfo(ModuleState state) {
        return new ModuleInfo(null, state, null, null, null, null, null, false,
                false, false, false, false, null, null, -1, false, -1);
    }

    static String getParametersString(Map<String, String> parameters) {
        String properties = null;
        if (parameters != null) {
            Properties props = new Properties();
            props.putAll(parameters);
            properties = Util.propertiesToString(props);
        }
        return properties;
    }

    static Map<String, Object> createParameters(boolean routing, String name,
            String language, String parameters) {
        // can't use ImmutableMap since properties may be null
        Map<String, Object> map = Maps.newHashMap();
        map.put("Name", name);
        map.put("Language", language);
        map.putAll(createWritableParameters(routing, parameters));
        return map;
    }

    static Map<String, Object> createWritableParameters(boolean routing,
            String parameters) {
        // can't use ImmutableMap since properties may be null
        Map<String, Object> map = Maps.newHashMap();
        map.put("RoutingOrdersToORS", routing);
        map.put("Parameters", parameters);
        return map;
    }

    @Override
    protected AbstractStrategyEngineConnection createFixture() {
        StrategyAgentConnection fixture = new StrategyAgentConnection(
                mMockClient, new ImmediateExecutorService());
        fixture.setEngine(mEngine);
        return fixture;
    }

    @Override
    protected void externalDeployStrategy(Strategy strategy) throws Exception {
        Properties parameters = new Properties();
        parameters.putAll(strategy.getParameters().map());
        mMockClient.createStrategy(new CreateStrategyParameters(strategy
                .getInstanceName(), strategy.getClassName(), strategy
                .getLanguage(), getTempScript(), Util
                .propertiesToString(parameters), strategy
                .isRouteOrdersToServer()));
    }

    @Override
    protected void externalUndeployStrategy(ModuleURN urn) throws Exception {
        mMockClient.delete(urn);
    }

    @Override
    protected void externalUpdateAndStartStrategy(ModuleURN urn,
            Strategy newConfiguration) throws Exception {
        mMockClient.setProperties(urn, createWritableParameters(
                newConfiguration.isRouteOrdersToServer(),
                getParametersString(newConfiguration.getParameters().map())));
        mMockClient.start(urn);

    }

    private class MockClient implements SAClient {

        private Map<ModuleURN, CreateStrategyParameters> mDeployedStrategies = Maps
                .newHashMap();
        private Set<ModuleURN> mRunning = Sets.newHashSet();

        @Override
        public ModuleURN createStrategy(CreateStrategyParameters inParameters)
                throws ConnectionException {

            ModuleURN urn = new ModuleURN("metc:strategy:system:"
                    + inParameters.getInstanceName());
            assertNull(mDeployedStrategies.put(urn, inParameters));
            return urn;
        }

        @Override
        public void delete(ModuleURN inURN) throws ConnectionException {
            assertThat(mRunning, not(hasItem(inURN)));
            assertNotNull(mDeployedStrategies.remove(inURN));
        }

        @Override
        public List<ModuleURN> getInstances(ModuleURN inProviderURN)
                throws ConnectionException {
            return ImmutableList.copyOf(mDeployedStrategies.keySet());
        }

        @Override
        public Map<String, Object> setProperties(ModuleURN inURN,
                Map<String, Object> inProperties) throws ConnectionException {
            CreateStrategyParameters deployed = mDeployedStrategies.get(inURN);
            try {
                mDeployedStrategies.put(inURN, new CreateStrategyParameters(
                        deployed.getInstanceName(), deployed.getStrategyName(),
                        deployed.getLanguage(), getTempScript(),
                        (String) inProperties.get("Parameters"),
                        (Boolean) inProperties.get("RoutingOrdersToORS")));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            return inProperties;
        }

        @Override
        public void start(ModuleURN inURN) throws ConnectionException {
            assertTrue(mRunning.add(inURN));
        }

        @Override
        public void stop(ModuleURN inURN) throws ConnectionException {
            assertTrue(mRunning.remove(inURN));
        }

        @Override
        public ModuleInfo getModuleInfo(ModuleURN inURN)
                throws ConnectionException {
            return createModuleInfo(mRunning.contains(inURN) ? ModuleState.STARTED
                    : ModuleState.STOPPED);
        }

        @Override
        public Map<String, Object> getProperties(ModuleURN inURN)
                throws ConnectionException {
            CreateStrategyParameters deployed = mDeployedStrategies.get(inURN);
            return createParameters(deployed.isRouteOrdersToServer(), deployed
                    .getStrategyName(), deployed.getLanguage(), deployed
                    .getParameters());
        }

        @Override
        public SAClientParameters getParameters() {
            return null;
        }

        @Override
        public List<ModuleURN> getProviders() throws ConnectionException {
            return null;
        }

        @Override
        public CreateStrategyParameters getStrategyCreateParms(ModuleURN inURN)
                throws ConnectionException {
            return null;
        }

        @Override
        public void addConnectionStatusListener(
                ConnectionStatusListener inListener) {
        }

        @Override
        public void addDataReceiver(DataReceiver inReceiver) {
        }

        @Override
        public void close() {
        }

        @Override
        public void removeConnectionStatusListener(
                ConnectionStatusListener inListener) {
        }

        @Override
        public void removeDataReciever(DataReceiver inReceiver) {
        }

    }

}
