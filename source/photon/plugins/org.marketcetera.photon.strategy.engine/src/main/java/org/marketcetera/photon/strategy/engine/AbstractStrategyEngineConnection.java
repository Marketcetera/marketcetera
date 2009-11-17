package org.marketcetera.photon.strategy.engine;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang.StringUtils;
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
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * A base class with common {@link StrategyEngineConnection} functionality. This
 * class handles the model updates and delegates to abstract methods to
 * implement the interactions with the underlying module framework
 * implmentation.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
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
        if (StringUtils.isBlank(scriptPath)) {
            throw new I18NException(
                    Messages.ABSTRACT_STRATEGY_ENGINE_CONNECTION_MISSING_SCRIPT_PATH);
        }
        if (StringUtils.isBlank(strategy.getClassName())) {
            throw new I18NException(
                    Messages.ABSTRACT_STRATEGY_ENGINE_CONNECTION_MISSING_CLASS_NAME);
        }
        if (StringUtils.isBlank(strategy.getLanguage())) {
            throw new I18NException(
                    Messages.ABSTRACT_STRATEGY_ENGINE_CONNECTION_MISSING_LANGUAGE);
        }
        if (StringUtils.isBlank(strategy.getInstanceName())) {
            throw new I18NException(
                    Messages.ABSTRACT_STRATEGY_ENGINE_CONNECTION_MISSING_INSTANCE_NAME);
        }
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

    /**
     * Return strategy properties as a {@link Properties} object.
     * 
     * @param strategy
     *            the strategy
     * @return the properties
     */
    protected static Properties getProperties(Strategy strategy) {
        Properties properties = new Properties();
        properties.putAll(strategy.getParameters().map());
        return properties;
    }

    /**
     * Return strategy properties as a String.
     * 
     * @param strategy
     *            the strategy
     * @return the properties
     */
    protected static String getPropertiesString(Strategy strategy) {
        return Util.propertiesToString(getProperties(strategy));
    }

    /**
     * Return string based properties as a map.
     * 
     * @param properties
     *            the string-encoded properties
     * @return a map of properties
     */
    protected static Map<String, String> getPropertiesMap(String properties) {
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
        ModuleURN urn = deployedStrategy.getUrn();
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
        ModuleURN urn = deployedStrategy.getUrn();
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
        Set<ModuleURN> deployedModules = Sets.newHashSet(getDeployed());
        final Set<DeployedStrategy> removedStrategies = Collections
                .synchronizedSet(Sets.<DeployedStrategy> newHashSet());

        /*
         * Iterate visible strategies, refresh ones that should be there and
         * mark for removal those that should not.
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
                deployedModules.remove(deployedStrategy.getUrn());
            } else {
                removedStrategies.add(deployedStrategy);
            }
        }

        /*
         * Remove the ones that should not be there.
         */
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                getEngine().getDeployedStrategies()
                        .removeAll(removedStrategies);
            }
        }));

        /*
         * Now add new ones.
         */
        for (ModuleURN missing : deployedModules) {
            internalDeploy(null, missing);
        }
    }

    @Override
    public void update(final DeployedStrategy deployedStrategy,
            Strategy newConfiguration) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy", //$NON-NLS-1$ 
                newConfiguration, "newConfiguration"); //$NON-NLS-1$
        ModuleURN urn = deployedStrategy.getUrn();
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
        ModuleURN urn = deployedStrategy.getUrn();
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

    private DeployedStrategy internalDeploy(final String scriptPath,
            final ModuleURN urn) throws Exception {
        final DeployedStrategy deployed = ExceptionUtils
                .launderedGet(getGUIExecutor().submit(
                        new Callable<DeployedStrategy>() {
                            @Override
                            public DeployedStrategy call() {
                                DeployedStrategy deployed = StrategyEngineCoreFactory.eINSTANCE
                                        .createDeployedStrategy();
                                deployed.setUrn(urn);
                                deployed.setScriptPath(scriptPath);
                                return deployed;
                            }
                        }));
        internalRefresh(deployed);
        ExceptionUtils.launderedGet(getGUIExecutor().submit(new Runnable() {
            @Override
            public void run() {
                getEngine().getDeployedStrategies().add(
                        deployed);
            }
        }));
        return deployed;
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

    /**
     * Deploys the strategy and provide the URN.
     * 
     * @param strategy
     *            the strategy
     * @param script
     *            the script file (computed from the script path)
     * @return the URN of the new strategy
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract ModuleURN doDeploy(Strategy strategy, File script)
            throws Exception;

    /**
     * Refreshes the state of the strategy with the given urn. The state must be
     * stored in the given strategy and can be written directly (i.e. without
     * using the GUI executor).
     * 
     * @param urn
     *            the strategy urn
     * @param strategy
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract void doRefresh(ModuleURN urn, DeployedStrategy strategy)
            throws Exception;

    /**
     * Returns whether the strategy with the given urn is running.
     * 
     * @param urn
     *            the strategy urn
     * @return true if the strategy is running
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract boolean isRunning(ModuleURN urn) throws Exception;

    /**
     * Starts the strategy with the given urn.
     * 
     * @param urn
     *            the strategy urn
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract void doStart(ModuleURN urn) throws Exception;

    /**
     * Stops the strategy with the given urn.
     * 
     * @param urn
     *            the strategy urn
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract void doStop(ModuleURN urn) throws Exception;

    /**
     * Updates the strategy with the given urn.
     * 
     * @param urn
     *            the strategy urn
     * @param newConfiguration
     *            the new configuration to apply to the strategy
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract void doUpdate(ModuleURN urn, Strategy newConfiguration)
            throws Exception;

    /**
     * Undeploys the strategy with the given urn.
     * 
     * @param urn
     *            the strategy urn
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract void doUndeploy(ModuleURN urn) throws Exception;

    /**
     * Returns a list of deployed strategy urns.
     * 
     * @return the strategy urn
     * @throws Exception
     *             if something goes wrong
     */
    protected abstract List<ModuleURN> getDeployed() throws Exception;

}
