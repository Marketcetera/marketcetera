package org.marketcetera.photon.strategy.engine;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.eclipse.core.runtime.FileLocator;
import org.marketcetera.core.Util;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.ExceptionUtils;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * A base class with common {@link StrategyEngineConnection} functionality.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractStrategyEngineConnection extends
        StrategyEngineConnectionImpl {

    private final ExecutorService mGUIExecutor;

    /**
     * Constructor.
     * 
     * @param guiExecutor
     *            the GUI executor service
     * @throws IllegalArgumentException
     *             if guiExecutor is null
     */
    protected AbstractStrategyEngineConnection(ExecutorService guiExecutor) {
        Validate.notNull(guiExecutor, "guiExecutor");
        mGUIExecutor = guiExecutor;
    }

    /**
     * Returns the executor service to be used for model updates.
     * 
     * @return the GUI executor service
     */
    protected ExecutorService getGUIExecutor() {
        return mGUIExecutor;
    }

    @Override
    public DeployedStrategy deploy(Strategy strategy) throws Exception {
        Validate.notNull(strategy, "strategy"); //$NON-NLS-1$
        String scriptPath = strategy.getScriptPath();
        File script = new File(scriptPath);
        if (!script.isFile()) {
            /*
             * The script path can also be a URL, as long as the platform can
             * resolve it to a File URL.
             */
            File resolved = null;
            try {
                URL fileURL = FileLocator.toFileURL(new URL(scriptPath));
                if (fileURL.getProtocol().equals("file")) { //$NON-NLS-1$
                    resolved = new File(fileURL.getPath());
                    if (resolved.isFile()) {
                        SLF4JLoggerProxy
                                .debug(
                                        this,
                                        "Resolved strategy scriptPath '{}' as a URL to file '{}'.", //$NON-NLS-1$
                                        scriptPath, resolved);
                        script = resolved;
                    }
                }
            } catch (Exception e) {
                // ignore, send strategy the original path
            }
        }
        ModuleURN urn = doDeploy(strategy, script);
        return internalDeploy(scriptPath, urn);
    }

    protected Properties getProperties(Strategy strategy) {
        Properties properties = new Properties();
        properties.putAll(strategy.getParameters().map());
        return properties;
    }

    protected String getPropertiesString(Strategy strategy) {
        return Util.propertiesToString(getProperties(strategy));
    }

    protected Map<String, String> getPropertiesMap(String properties) {
        Map<String, String> map = Maps.newHashMap();
        Properties props = Util.propertiesFromString(properties);
        if (props != null) {
            for (String key : props.stringPropertyNames()) {
                map.put(key, props.getProperty(key));
            }
        }
        return map;
    }

    @Override
    public void start(final DeployedStrategy deployedStrategy) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        final ModuleURN urn = deployedStrategy.getUrn();
        validateURN(urn, "start"); //$NON-NLS-1$
        doStart(urn);
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setState(StrategyState.RUNNING);
            }
        }));
    }

    @Override
    public void stop(final DeployedStrategy deployedStrategy) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        final ModuleURN urn = deployedStrategy.getUrn();
        validateURN(urn, "stop"); //$NON-NLS-1$
        doStop(urn);
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setState(StrategyState.STOPPED);
            }
        }));
    }

    @Override
    public void refresh(final DeployedStrategy deployedStrategy)
            throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ModuleURN urn = deployedStrategy.getUrn();
        validateURN(urn, "refresh"); //$NON-NLS-1$
        if (getDeployed().contains(urn)) {
            internalRefresh(deployedStrategy);
        } else {
            ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
                @Override
                public void run() {
                    deployedStrategy.setEngine(null);
                }
            }));
        }
    }

    @Override
    public void refresh() throws Exception {
        final Set<ModuleURN> deployedModules = Sets.newHashSet(getDeployed());
        Set<ModuleURN> visibleModules = Sets.newHashSet();
        Set<DeployedStrategy> removedStrategies = Sets.newHashSet();

        /*
         * Iterate visible strategies, refresh ones that should be there.
         */
        for (DeployedStrategy deployedStrategy : getEngine()
                .getDeployedStrategies()) {
            if (deployedModules.contains(deployedStrategy.getUrn())) {
                /*
                 * This may theoretically fail if the strategy was deleted since
                 * the call to getDeployed() above. The user would just have to
                 * refresh again to recover.
                 */
                internalRefresh(deployedStrategy);
                visibleModules.add(deployedStrategy.getUrn());
            } else {
                removedStrategies.add(deployedStrategy);
            }
        }

        /*
         * Remove the ones that should not be there.
         */
        final ImmutableSet<DeployedStrategy> toRemove = ImmutableSet
                .copyOf(removedStrategies);
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                getEngine().getDeployedStrategies().removeAll(toRemove);
            }
        }));

        /*
         * Now add new ones.
         */
        for (ModuleURN missing : Sets.difference(deployedModules,
                visibleModules)) {
            internalDeploy(null, missing);
        }
    }

    @Override
    public void update(final DeployedStrategy deployedStrategy,
            Strategy newConfiguration) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy", //$NON-NLS-1$ 
                newConfiguration, "newConfiguration"); //$NON-NLS-1$
        final ModuleURN urn = deployedStrategy.getUrn();
        validateURN(urn, "update"); //$NON-NLS-1$
        try {
            doUpdate(urn, newConfiguration);
        } finally {
            internalRefresh(deployedStrategy);
        }
    }

    @Override
    public void undeploy(final DeployedStrategy deployedStrategy)
            throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        final ModuleURN urn = deployedStrategy.getUrn();
        validateURN(urn, "undeploy"); //$NON-NLS-1$
        if (isRunning(urn)) {
            doStop(urn);
        }
        doUndeploy(urn);
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setEngine(null);
            }
        }));
    }

    private void validateURN(ModuleURN urn, String operation) {
        if (urn == null) {
            throw new IllegalStateException(
                    MessageFormat
                            .format(
                                    "Cannot {0} a DeployedStrategy that does not have a ModuleURN", //$NON-NLS-1$
                                    operation));
        }
    }

    private DeployedStrategy internalDeploy(final String scriptPath,
            final ModuleURN urn) throws Exception {
        final DeployedStrategy strategy = ExceptionUtils
                .launderedGet(getGUIExecutor().submit(
                        new Callable<DeployedStrategy>() {
                            @Override
                            public DeployedStrategy call() {
                                DeployedStrategy deployed = StrategyEngineCoreFactory.eINSTANCE
                                        .createDeployedStrategy();
                                deployed.setUrn(urn);
                                deployed.setScriptPath(scriptPath);
                                getEngine().getDeployedStrategies().add(
                                        deployed);
                                return deployed;
                            }
                        }));
        internalRefresh(strategy);
        return strategy;
    }

    private void internalRefresh(final DeployedStrategy deployed)
            throws Exception {
        final DeployedStrategy refreshed = StrategyEngineCoreFactory.eINSTANCE
                .createDeployedStrategy();
        doRefresh(deployed.getUrn(), refreshed);
        final boolean isRunning = isRunning(deployed.getUrn());
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                deployed.setInstanceName(refreshed.getInstanceName());
                deployed.setLanguage(refreshed.getLanguage());
                deployed.setClassName(refreshed.getClassName());
                deployed.setRouteOrdersToServer(refreshed
                        .isRouteOrdersToServer());
                deployed.getParameters().clear();
                deployed.getParameters().addAll(refreshed.getParameters());
                if (isRunning) {
                    deployed.setState(StrategyState.RUNNING);
                } else {
                    deployed.setState(StrategyState.STOPPED);
                }
            }
        }));
    }

    protected abstract ModuleURN doDeploy(Strategy strategy, File script)
            throws Exception;

    protected abstract void doRefresh(ModuleURN urn, DeployedStrategy strategy)
            throws Exception;

    protected abstract boolean isRunning(ModuleURN urn) throws Exception;

    protected abstract void doStart(ModuleURN urn) throws Exception;

    protected abstract void doStop(ModuleURN urn) throws Exception;

    protected abstract void doUpdate(ModuleURN urn, Strategy newConfiguration)
            throws Exception;

    protected abstract void doUndeploy(ModuleURN urn) throws Exception;

    protected abstract List<ModuleURN> getDeployed() throws Exception;

}
