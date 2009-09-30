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

/* $License$ */

/**
 * Utilities for testing with the strategy engine core model.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: StrategyEngineCoreTestUtil.java 10744 2009-09-12 00:12:39Z will
 *          $
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
            StrategyEngine engine, StrategyState state, String instanceName,
            String className, String language, String scriptPath,
            boolean route, Map<String, String> parameters) {
        assertStrategy(deployed, instanceName, className, language, scriptPath,
                route, parameters);
        assertThat(deployed.getEngine(), sameInstance(engine));
        assertThat(deployed.getState(), is(state));
        assertThat(deployed.getUrn(), is(new ModuleURN("metc:strategy:system:"
                + instanceName)));
    }

    public static class MockConnection extends StrategyEngineConnectionImpl {

        @Override
        public DeployedStrategy deploy(Strategy strategy) throws Exception {
            DeployedStrategy result = createDeployedStrategy(strategy
                    .getInstanceName());
            update(result, strategy);
            getEngine().getDeployedStrategies().add(result);
            return result;
        }

        @Override
        public void start(DeployedStrategy strategy) throws Exception {
            strategy.setState(StrategyState.RUNNING);
        }

        @Override
        public void stop(DeployedStrategy strategy) throws Exception {
            strategy.setState(StrategyState.STOPPED);
        }

        @Override
        public void undeploy(DeployedStrategy strategy) throws Exception {
            getEngine().getDeployedStrategies().remove(strategy);
        }

        @Override
        public void update(DeployedStrategy strategy, Strategy newConfiguration)
                throws Exception {
            strategy.setScriptPath(newConfiguration.getScriptPath());
            strategy.setClassName(newConfiguration.getClassName());
            strategy.setLanguage(newConfiguration.getLanguage());
            strategy.setRouteOrdersToServer(newConfiguration
                    .isRouteOrdersToServer());
            strategy.getParameters().clear();
            strategy.getParameters().addAll(newConfiguration.getParameters());
        }

        @Override
        public void refresh() throws Exception {
        }

        @Override
        public void refresh(DeployedStrategy strategy) throws Exception {
        }
    }
}
