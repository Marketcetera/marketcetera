package org.marketcetera.photon.strategy.engine.model.core.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Utilities for testing with the strategy engine core model.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEngineCoreTestUtil {

    private StrategyEngineCoreTestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

    public static StrategyEngine createEngine(String name) {
        return createEngine(name, "A Strategy Engine");
    }

    public static StrategyEngine createConnectedEngine(String name) {
        return createEngine(name, "A Strategy Engine",
                ConnectionState.CONNECTED);
    }

    public static StrategyEngine createEngine(String name, String description) {
        return createEngine(name, description, ConnectionState.DISCONNECTED);
    }

    public static StrategyEngine createEngine(String name, String description,
            ConnectionState state) {
        StrategyEngine engine = StrategyEngineCoreFactory.eINSTANCE
                .createStrategyEngine();
        engine.setName(name);
        engine.setDescription(description);
        engine.setConnectionState(state);
        engine.setConnection(new MockConnection());
        return engine;
    }

    public static Strategy createStrategy(String instanceName) {
        Strategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createStrategy();
        strategy.setInstanceName(instanceName);
        return strategy;
    }

    public static DeployedStrategy createDeployedStrategy(String instanceName) {
        DeployedStrategy strategy = StrategyEngineCoreFactory.eINSTANCE
                .createDeployedStrategy();
        strategy.setInstanceName(instanceName);
        return strategy;
    }

    public static List<StrategyEngine> buildEngines(EObject... objects) {
        List<StrategyEngine> engines = new ArrayList<StrategyEngine>();
        StrategyEngine currentEngine = null;
        for (EObject eObject : objects) {
            if (eObject instanceof StrategyEngine) {
                if (currentEngine != null) {
                    engines.add(currentEngine);
                }
                currentEngine = (StrategyEngine) eObject;
                currentEngine.getDeployedStrategies().clear();
            } else if (eObject instanceof DeployedStrategy) {
                if (currentEngine != null) {
                    currentEngine.getDeployedStrategies().add(
                            (DeployedStrategy) eObject);
                }
            }
        }
        if (currentEngine != null) {
            engines.add(currentEngine);
        }
        return engines;
    }

    public static void assertStrategy(Strategy strategy, String instanceName,
            String className, String language, String scriptPath,
            boolean route, Map<String, String> parameters) {
        assertThat(strategy.getInstanceName(), is(instanceName));
        assertThat(strategy.getClassName(), is(className));
        assertThat(strategy.getLanguage(), is(language));
        assertThat(strategy.getScriptPath(), is(scriptPath));
        assertThat(strategy.isRouteOrdersToServer(), is(route));
        if (parameters != null) {
            assertThat(strategy.getParameters().map(), is(parameters));
        } else {
            assertThat(strategy.getParameters().size(), is(0));
        }
    }

    public static void assertDeployedStrategy(DeployedStrategy deployed,
            ModuleURN urn, StrategyEngine engine, StrategyState state,
            String instanceName, String className, String language,
            String scriptPath, boolean route, Map<String, String> parameters) {
        assertStrategy(deployed, instanceName, className, language, scriptPath, route, parameters);
        assertThat(deployed.getEngine(), sameInstance(engine));
        assertThat(deployed.getState(), is(state));
    }

    public static class MockConnection extends StrategyEngineConnectionImpl {

        private final List<Strategy> mDeployed = Lists.newArrayList();

        public List<Strategy> getDeployed() {
            return mDeployed;
        }

        @Override
        public DeployedStrategy deploy(Strategy strategy) throws Exception {
            mDeployed.add(strategy);
            DeployedStrategy result = createDeployedStrategy(strategy.getInstanceName());
            result.setScriptPath(strategy.getScriptPath());
            result.setClassName(strategy.getClassName());
            result.setLanguage(strategy.getLanguage());
            result.setRouteOrdersToServer(strategy.isRouteOrdersToServer());
            getEngine().getDeployedStrategies().add(result);
            return result;
        }

        @Override
        public void start(DeployedStrategy strategy) throws Exception {
        }

        @Override
        public void stop(DeployedStrategy strategy) throws Exception {
        }

        @Override
        public void undeploy(DeployedStrategy strategy) throws Exception {
            getEngine().getDeployedStrategies().remove(strategy);
        }

        @Override
        public void update(DeployedStrategy strategy, Strategy newConfiguration)
                throws Exception {
            
        }
        
        @Override
        public void refresh() throws Exception {
        }
        
        @Override
        public void refresh(DeployedStrategy strategy) throws Exception {
        }
    }
}
