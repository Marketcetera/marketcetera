package org.marketcetera.photon.internal.strategy.engine.embedded;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.NotThreadSafe;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.FileLocator;
import org.marketcetera.core.Util;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.commons.ExceptionUtils;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineCoreFactory;
import org.marketcetera.photon.strategy.engine.model.core.StrategyState;
import org.marketcetera.photon.strategy.engine.model.core.impl.StrategyEngineConnectionImpl;
import org.marketcetera.photon.strategy.engine.model.core.util.ImmutableDeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.util.ImmutableStrategy;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * The embedded strategy engine connection.
 * <p>
 * This class is not fully thread safe. It does manage visibility of the model
 * objects by ensuring all updates are done using the guiExecutor. However, it
 * does not support concurrent access.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class EmbeddedConnection extends StrategyEngineConnectionImpl {

    private volatile MBeanServerConnection mMBeanServer;
    private volatile ModuleManager mModuleManager;
    private final ExecutorService mGUIExecutor;
    private final IPersistenceService mPersistenceService;

    /**
     * Constructor.
     * 
     * @param engine
     *            the engine to attach to
     * @param guiExecutor
     *            the executor to run tasks that change the model state
     * @param service
     *            the persistence service, or null if persistence is not desired
     * @throws IllegalArgumentException
     *             if engine or guiExecutor is null
     */
    public EmbeddedConnection(StrategyEngine engine,
            ExecutorService guiExecutor, IPersistenceService service) {
        Validate.notNull(engine, "engine", //$NON-NLS-1$
                guiExecutor, "guiExecutor"); //$NON-NLS-1$
        mGUIExecutor = guiExecutor;
        mPersistenceService = service;
        setEngine(engine);
    }

    /**
     * Initializes the embedded connection. This may take a while if restoring
     * deployed strategies from the filesystem.
     */
    public void initialize() {
        mMBeanServer = ModuleSupport.getMBeanServerConnection();
        mModuleManager = ModuleSupport.getModuleManager();
        if (mPersistenceService != null) {
            mPersistenceService.restore(EmbeddedConnection.this);
        }
    }

    @Override
    public DeployedStrategy deploy(final Strategy strategy) throws Exception {
        Validate.notNull(strategy, "strategy"); //$NON-NLS-1$
        final ImmutableStrategy immutableStrategy = getImmutableStrategy(strategy);
        final String scriptPath = immutableStrategy.getScriptPath();
        if (scriptPath == null) {
            throw new I18NException(
                    Messages.EMBEDDED_CONNECTION_MISSING_SCRIPT_PATH);
        }
        if (immutableStrategy.getClassName() == null) {
            throw new I18NException(
                    Messages.EMBEDDED_CONNECTION_MISSING_CLASS_NAME);
        }
        if (immutableStrategy.getLanguage() == null) {
            throw new I18NException(
                    Messages.EMBEDDED_CONNECTION_MISSING_LANGUAGE);
        }
        if (immutableStrategy.getInstanceName() == null) {
            throw new I18NException(
                    Messages.EMBEDDED_CONNECTION_MISSING_INSTANCE_NAME);
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
        final ModuleURN urn = mModuleManager.createModule(
                StrategyModuleFactory.PROVIDER_URN, immutableStrategy
                        .getInstanceName(), immutableStrategy.getClassName(),
                immutableStrategy.getLanguage(), script, null,
                immutableStrategy.isRouteOrdersToServer(),
                SinkModuleFactory.INSTANCE_URN);
        final AtomicReference<ImmutableList<ImmutableStrategy>> toSave = new AtomicReference<ImmutableList<ImmutableStrategy>>();
        DeployedStrategy result = ExceptionUtils.launderedGet(mGUIExecutor
                .submit(new Callable<DeployedStrategy>() {
                    @Override
                    public DeployedStrategy call() throws Exception {
                        DeployedStrategy deployed = StrategyEngineCoreFactory.eINSTANCE
                                .createDeployedStrategy();
                        deployed.setUrn(urn);
                        internalRefresh(deployed);
                        deployed.setScriptPath(scriptPath);
                        getEngine().getDeployedStrategies().add(deployed);
                        toSave.set(captureSnapshot(getEngine()
                                .getDeployedStrategies()));
                        return deployed;
                    }
                }));
        saveState(toSave.get());
        return result;
    }

    @Override
    public void start(final DeployedStrategy deployedStrategy) throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "start"); //$NON-NLS-1$
        mModuleManager.start(urn);
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
        mModuleManager.stop(urn);
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
        final StrategyMXBean proxy = getProxy(urn);
        proxy.setRoutingOrdersToORS(immutableConfiguration
                .isRouteOrdersToServer());
        Properties properties = new Properties();
        properties.putAll(immutableConfiguration.getParameters());
        proxy.setParameters(Util.propertiesToString(properties));
        ImmutableList<ImmutableStrategy> strategies = ExceptionUtils
                .launderedGet(mGUIExecutor
                        .submit(new Callable<ImmutableList<ImmutableStrategy>>() {
                            @Override
                            public ImmutableList<ImmutableStrategy> call()
                                    throws Exception {
                                internalRefresh(deployedStrategy);
                                return captureSnapshot(getEngine()
                                        .getDeployedStrategies());
                            }
                        }));
        saveState(strategies);
    }

    @Override
    public void refresh() throws Exception {
        final Set<ModuleURN> deployedModules = Sets.newHashSet(mModuleManager
                .getModuleInstances(StrategyModuleFactory.PROVIDER_URN));
        /*
         * For convenience the whole operation is currently done in the UI
         * thread.
         */
        ImmutableList<ImmutableStrategy> strategies = ExceptionUtils
                .launderedGet(mGUIExecutor
                        .submit(new Callable<ImmutableList<ImmutableStrategy>>() {
                            @Override
                            public ImmutableList<ImmutableStrategy> call()
                                    throws Exception {
                                Set<ModuleURN> visibleModules = Sets
                                        .newHashSet();
                                Iterator<DeployedStrategy> iterator = getEngine()
                                        .getDeployedStrategies().iterator();
                                /*
                                 * Iterate visible strategies, refresh ones that
                                 * should be there, remove ones that should not.
                                 */
                                while (iterator.hasNext()) {
                                    DeployedStrategy deployedStrategy = (DeployedStrategy) iterator
                                            .next();
                                    if (deployedModules
                                            .contains(deployedStrategy.getUrn())) {
                                        internalRefresh(deployedStrategy);
                                        visibleModules.add(deployedStrategy
                                                .getUrn());
                                    } else {
                                        iterator.remove();
                                    }
                                }
                                /*
                                 * Now add ones we didn't see.
                                 */
                                for (ModuleURN missing : Sets.difference(
                                        deployedModules, visibleModules)) {
                                    DeployedStrategy newStrategy = StrategyEngineCoreFactory.eINSTANCE
                                            .createDeployedStrategy();
                                    newStrategy.setUrn(missing);
                                    internalRefresh(newStrategy);
                                    getEngine().getDeployedStrategies().add(
                                            newStrategy);
                                }
                                return captureSnapshot(getEngine()
                                        .getDeployedStrategies());
                            }
                        }));
        saveState(strategies);
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
            public Void call() throws Exception {
                internalRefresh(deployedStrategy);
                return null;
            }
        }));
    }

    @Override
    public void undeploy(final DeployedStrategy deployedStrategy)
            throws Exception {
        Validate.notNull(deployedStrategy, "deployedStrategy"); //$NON-NLS-1$
        ImmutableDeployedStrategy immutable = getImmutableDeployedStrategy(deployedStrategy);
        final ModuleURN urn = immutable.getUrn();
        validateURN(urn, "undeploy"); //$NON-NLS-1$
        if (mModuleManager.getModuleInfo(urn).getState().isStarted()) {
            mModuleManager.stop(urn);
        }
        mModuleManager.deleteModule(urn);
        final ImmutableList<ImmutableStrategy> toSave = ExceptionUtils
                .launderedGet(mGUIExecutor
                        .submit(new Callable<ImmutableList<ImmutableStrategy>>() {
                            @Override
                            public ImmutableList<ImmutableStrategy> call() {
                                deployedStrategy.setEngine(null);
                                deployedStrategy.setUrn(null);
                                return captureSnapshot(getEngine()
                                        .getDeployedStrategies());
                            }
                        }));
        saveState(toSave);
    }

    /*
     * Must be called from gui executor to ensure visibility of changes. Note
     * that this does makes calls to the underlying framework that may take some
     * time.
     */
    private void internalRefresh(DeployedStrategy deployed) throws Exception {
        StrategyMXBean proxy = getProxy(deployed.getUrn());
        /*
         * Should not be necessary to set instance name like this but it does
         * not hurt.
         */
        deployed.setInstanceName(deployed.getUrn().instanceName());
        deployed.setRouteOrdersToServer(proxy.isRoutingOrdersToORS());
        deployed.setClassName(proxy.getName());
        deployed.setLanguage(proxy.getLanguage().toString());
        String parameters = proxy.getParameters();
        if (mModuleManager.getModuleInfo(deployed.getUrn()).getState()
                .isStarted()) {
            deployed.setState(StrategyState.RUNNING);
        } else {
            deployed.setState(StrategyState.STOPPED);
        }
        deployed.getParameters().clear();
        if (parameters != null) {
            Properties properties = Util.propertiesFromString(parameters);
            for (String key : properties.stringPropertyNames()) {
                deployed.getParameters().put(key, properties.getProperty(key));
            }
        }
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

    private ImmutableList<ImmutableStrategy> captureSnapshot(
            List<DeployedStrategy> deployedStrategies) {
        /*
         * Take an immutable snapshot of the UI state. See #saveState.
         */
        ImmutableList.Builder<ImmutableStrategy> builder = ImmutableList
                .builder();
        for (DeployedStrategy deployedStrategy : deployedStrategies) {
            builder.add(new ImmutableStrategy(deployedStrategy));
        }
        return builder.build();
    }

    private void saveState(ImmutableList<ImmutableStrategy> strategies) {
        if (mPersistenceService != null) {
            /*
             * This is roundabout to convert to ImmutableStrategies then back to
             * Strategies, but it is the only thread safe way to get the unsafe
             * UI objects to the background thread for saving.
             */
            List<Strategy> toSave = Lists.newArrayList();
            for (ImmutableStrategy strategy : strategies) {
                toSave.add(strategy.fill(StrategyEngineCoreFactory.eINSTANCE
                        .createStrategy()));
            }
            try {
                mPersistenceService.save(toSave);
            } catch (IOException e) {
                Messages.EMBEDDED_CONNECTION_SAVE_FAILED.error(this, e);
            }
        }
    }

    private StrategyMXBean getProxy(final ModuleURN urn)
            throws MXBeanOperationException {
        ObjectName objectName = urn.toObjectName();
        StrategyMXBean proxy = JMX.newMXBeanProxy(mMBeanServer, objectName,
                StrategyMXBean.class);
        return proxy;
    }

}
