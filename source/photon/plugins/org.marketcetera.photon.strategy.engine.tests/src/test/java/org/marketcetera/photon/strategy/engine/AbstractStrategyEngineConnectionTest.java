package org.marketcetera.photon.strategy.engine;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.photon.strategy.engine.model.core.test.StrategyEngineCoreTestUtil.createEngine;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.marketcetera.core.ImmediateExecutorService;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Test {@link AbstractStrategyEngineConnection}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class AbstractStrategyEngineConnectionTest extends AbstractStrategyEngineConnectionTestBase {
    
    private AbstractStrategyEngineConnection mMockFixture;

    @Override
    protected AbstractStrategyEngineConnection createFixture() {
        mMockFixture = new MockFixture(new ImmediateExecutorService());
        mMockFixture.setEngine(createEngine("TestEngine"));
        return mMockFixture;
    }

    @Override
    protected void externalDeployStrategy(Strategy strategy) throws Exception {
        mMockFixture.doDeploy(strategy, null);        
    }

    @Override
    protected void externalUndeployStrategy(ModuleURN urn) throws Exception {
        mMockFixture.doUndeploy(urn);
    }

    @Override
    protected void externalUpdateAndStartStrategy(ModuleURN urn, Strategy newConfiguration)
            throws Exception {
        mMockFixture.doUpdate(urn, newConfiguration);
        mMockFixture.doStart(urn);
    }
    
    private static class MockFixture extends AbstractStrategyEngineConnection {

        private Map<ModuleURN, Strategy> mDeployedStrategies = Maps.newHashMap();
        private Set<ModuleURN> mRunning = Sets.newHashSet();
        
        public MockFixture(ExecutorService guiExecutor) {
            super(guiExecutor);
        }

        @Override
        protected ModuleURN doDeploy(Strategy strategy, File script)
                throws Exception {
            ModuleURN urn = new ModuleURN("metc:strategy:system:" + strategy.getInstanceName());
            assertNull(mDeployedStrategies.put(urn, strategy));
            return urn;
        }

        @Override
        protected void doRefresh(ModuleURN urn, DeployedStrategy strategy)
                throws Exception {
            Strategy deployed = mDeployedStrategies.get(urn);
            strategy.setInstanceName(urn.instanceName());
            strategy.setScriptPath(deployed.getScriptPath());
            strategy.setClassName(deployed.getClassName());
            strategy.setLanguage(deployed.getLanguage());
            strategy.setRouteOrdersToServer(deployed.isRouteOrdersToServer());
            strategy.getParameters().addAll(deployed.getParameters());
        }

        @Override
        protected void doStart(ModuleURN urn) throws Exception {
            assertTrue(mRunning.add(urn));
        }

        @Override
        protected void doStop(ModuleURN urn) throws Exception {
            assertTrue(mRunning.remove(urn));
        }

        @Override
        protected void doUndeploy(ModuleURN urn) throws Exception {
            assertThat(mRunning, not(hasItem(urn)));
            assertNotNull(mDeployedStrategies.remove(urn));
        }

        @Override
        protected void doUpdate(ModuleURN urn, Strategy newConfiguration)
                throws Exception {
            Strategy deployed = mDeployedStrategies.get(urn);
            deployed.setRouteOrdersToServer(newConfiguration.isRouteOrdersToServer());
            deployed.getParameters().clear();
            deployed.getParameters().addAll(newConfiguration.getParameters());
        }

        @Override
        protected List<ModuleURN> getDeployed() throws Exception {
            return ImmutableList.copyOf(mDeployedStrategies.keySet());
        }

        @Override
        protected boolean isRunning(ModuleURN urn) throws Exception {
            return mRunning.contains(urn);
        }
    }
}
