package org.marketcetera.photon.internal.strategy.engine.strategyagent;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
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
import org.marketcetera.photon.strategy.engine.model.core.util.ImmutableDeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.util.ImmutableStrategy;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Adapts {@link SAClient} to {@link StrategyEngineConnection}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentConnection extends StrategyEngineConnectionImpl {

    private static final String NAME_KEY = "Name"; //$NON-NLS-1$
    private static final String LANGUAGE_KEY = "Language"; //$NON-NLS-1$
    private static final String PARAMETERS_KEY = "Parameters"; //$NON-NLS-1$
    private static final String ROUTING_ORDERS_TO_ORS_KEY = "RoutingOrdersToORS"; //$NON-NLS-1$
    private final SAClient mClient;
    private final ExecutorService mGUIExecutor;

    public StrategyAgentConnection(SAClient saClient, ExecutorService service) {
        mClient = saClient;
        mGUIExecutor = service;
    }

    @Override
    public DeployedStrategy deploy(final Strategy strategy) throws Exception {
        Validate.notNull(strategy, "strategy"); //$NON-NLS-1$
        CreateStrategyParameters parameters = ExceptionUtils.launderedGet(
                mGUIExecutor.submit(new Callable<CreateStrategyParameters>() {
                    @Override
                    public CreateStrategyParameters call()
                            throws FileNotFoundException {
                        String scriptPath = strategy.getScriptPath();
                        File script = new File(scriptPath);
                        if (!script.isFile()) {
                            /*
                             * The script path can also be a URL, as long as the
                             * platform can resolve it to a File URL.
                             */
                            File resolved = null;
                            try {
                                URL fileURL = FileLocator.toFileURL(new URL(
                                        scriptPath));
                                if (fileURL.getProtocol().equals("file")) { //$NON-NLS-1$
                                    resolved = new File(fileURL.getPath());
                                    if (resolved.isFile()) {
                                        SLF4JLoggerProxy
                                                .debug(
                                                        StrategyAgentConnection.this,
                                                        "Resolved strategy scriptPath '{}' as a URL to file '{}'.", //$NON-NLS-1$
                                                        scriptPath, resolved);
                                        script = resolved;
                                    }
                                }
                            } catch (Exception e) {
                                // ignore, send strategy the original
                                // path
                            }
                        }
                        String parameters = null;
                        return new CreateStrategyParameters(strategy
                                .getInstanceName(), strategy.getClassName(),
                                strategy.getLanguage(), script, parameters,
                                strategy.isRouteOrdersToServer());
                    }
                }), FileNotFoundException.class);
        final ModuleURN urn = mClient.createStrategy(parameters);
        DeployedStrategy result = ExceptionUtils.launderedGet(mGUIExecutor
                .submit(new Callable<DeployedStrategy>() {
                    @Override
                    public DeployedStrategy call() throws ConnectionException {
                        DeployedStrategy deployed = StrategyEngineCoreFactory.eINSTANCE
                                .createDeployedStrategy();
                        deployed.setUrn(urn);
                        internalRefresh(deployed);
                        deployed.setScriptPath(strategy.getScriptPath());
                        getEngine().getDeployedStrategies().add(deployed);
                        return deployed;
                    }
                }), ConnectionException.class);
        return result;
    }

    @Override
    public void undeploy(final DeployedStrategy deployedStrategy)
            throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "undeploy"); //$NON-NLS-1$
        if (mClient.getModuleInfo(urn).getState().isStarted()) {
            mClient.stop(urn);
        }
        mClient.delete(urn);
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setEngine(null);
                deployedStrategy.setUrn(null);
            }
        }));
    }

    private ImmutableDeployedStrategy getImmutableDeployedStrategy(
            final DeployedStrategy strategy) throws InterruptedException {
        return ExceptionUtils.launderedGet(mGUIExecutor
                .submit(new Callable<ImmutableDeployedStrategy>() {
                    @Override
                    public ImmutableDeployedStrategy call() {
                        return new ImmutableDeployedStrategy(strategy);
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

    @Override
    public void start(final DeployedStrategy deployedStrategy) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "start"); //$NON-NLS-1$
        mClient.start(urn);
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setState(StrategyState.RUNNING);
            }
        }));
    }

    @Override
    public void stop(final DeployedStrategy deployedStrategy) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "stop"); //$NON-NLS-1$
        mClient.stop(urn);
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Runnable() {
            @Override
            public void run() {
                deployedStrategy.setState(StrategyState.STOPPED);
            }
        }));
    }

    @Override
    public void update(final DeployedStrategy deployedStrategy,
            Strategy newConfiguration) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy", //$NON-NLS-1$ 
                newConfiguration, "newConfiguration"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "update"); //$NON-NLS-1$
        ImmutableStrategy immutableConfiguration = getImmutableStrategy(newConfiguration);
        String properties = null;
        ImmutableMap<String, String> parameters = immutableConfiguration
                .getParameters();
        if (parameters != null) {
            Properties props = new Properties();
            props.putAll(parameters);
            properties = Util.propertiesToString(props);
        }
        // can't use ImmutableMap.of because properties may be null
        Map<String, Object> map = Maps.newHashMap();
        map.put(ROUTING_ORDERS_TO_ORS_KEY, immutableConfiguration
                .isRouteOrdersToServer());
        map.put(PARAMETERS_KEY, properties);
        Map<String, Object> result = mClient.setProperties(urn, map);
        StringBuilder errorMessage = new StringBuilder(); //$NON-NLS-1$
        boolean errorFound = appendError(result.get(ROUTING_ORDERS_TO_ORS_KEY),
                immutableConfiguration.isRouteOrdersToServer(), errorMessage,
                Messages.STRATEGY_AGENT_CONNECTION_UPDATE_ROUTING_ERROR, false);
        errorFound |= appendError(result.get(PARAMETERS_KEY), properties,
                errorMessage,
                Messages.STRATEGY_AGENT_CONNECTION_UPDATE_PARAMETERS_ERROR,
                errorFound);
        if (errorFound) {
            throw new Exception(errorMessage.toString());
        }
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws ConnectionException {
                internalRefresh(deployedStrategy);
                return null;
            }
        }), ConnectionException.class);
    }

    private boolean appendError(Object result, Object value,
            StringBuilder errorMessage, I18NMessage2P description,
            boolean errorFound) {
        if (result instanceof RemoteProperties) {
            if (errorFound) {
                errorMessage.append('\n');
            }
            Throwable throwable = ((RemoteProperties) result).getThrowable();
            String detail = null;
            if (throwable != null) {
                detail = throwable.getLocalizedMessage();
            }
            errorMessage.append(description.getText(value, detail));
            return true;
        }
        return false;
    }

    @Override
    public void refresh(final DeployedStrategy deployedStrategy)
            throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "refresh"); //$NON-NLS-1$
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws ConnectionException {
                internalRefresh(deployedStrategy);
                return null;
            }
        }), ConnectionException.class);
    }

    @Override
    public void refresh() throws Exception {
        final Set<ModuleURN> deployedModules = Sets.newHashSet(mClient
                .getInstances(StrategyModuleFactory.PROVIDER_URN));
        /*
         * For convenience the whole operation is currently done in the UI
         * thread.
         */
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws ConnectionException {
                Set<ModuleURN> visibleModules = Sets.newHashSet();
                Iterator<DeployedStrategy> iterator = getEngine()
                        .getDeployedStrategies().iterator();
                /*
                 * Iterate visible strategies, refresh ones that should be
                 * there, remove ones that should not.
                 */
                while (iterator.hasNext()) {
                    DeployedStrategy deployedStrategy = (DeployedStrategy) iterator
                            .next();
                    if (deployedModules.contains(deployedStrategy.getUrn())) {
                        internalRefresh(deployedStrategy);
                        visibleModules.add(deployedStrategy.getUrn());
                    } else {
                        iterator.remove();
                    }
                }
                /*
                 * Now add ones we didn't see.
                 */
                for (ModuleURN missing : Sets.difference(deployedModules,
                        visibleModules)) {
                    DeployedStrategy newStrategy = StrategyEngineCoreFactory.eINSTANCE
                            .createDeployedStrategy();
                    newStrategy.setUrn(missing);
                    internalRefresh(newStrategy);
                    getEngine().getDeployedStrategies().add(newStrategy);
                }
                return null;
            }
        }), ConnectionException.class);
    }

    private ImmutableStrategy getImmutableStrategy(final Strategy strategy)
            throws InterruptedException {
        return ExceptionUtils.launderedGet(mGUIExecutor
                .submit(new Callable<ImmutableStrategy>() {
                    @Override
                    public ImmutableStrategy call() {
                        return new ImmutableStrategy(strategy);
                    }
                }));
    }

    /*
     * Must be called from gui executor to ensure visibility of changes. Note
     * that this does makes calls to the underlying framework that may take some
     * time.
     */
    private void internalRefresh(DeployedStrategy deployed)
            throws ConnectionException {
        Map<String, Object> properties = mClient.getProperties(deployed
                .getUrn());
        deployed.setInstanceName(deployed.getUrn().instanceName());
        deployed.setRouteOrdersToServer((Boolean) properties
                .get(ROUTING_ORDERS_TO_ORS_KEY));
        deployed.setClassName((String) properties.get(NAME_KEY));
        deployed.setLanguage((String) properties.get(LANGUAGE_KEY));
        String parameters = (String) properties.get(PARAMETERS_KEY);
        deployed.getParameters().clear();
        if (parameters != null) {
            Properties props = Util.propertiesFromString(parameters);
            for (String key : props.stringPropertyNames()) {
                deployed.getParameters().put(key, props.getProperty(key));
            }
        }
        if (mClient.getModuleInfo(deployed.getUrn()).getState().isStarted()) {
            deployed.setState(StrategyState.RUNNING);
        } else {
            deployed.setState(StrategyState.STOPPED);
        }
    }
}
