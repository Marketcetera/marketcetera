package org.marketcetera.photon.internal.strategy.engine.embedded;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.NotThreadSafe;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.MXBeanOperationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngine;
import org.marketcetera.strategy.StrategyMXBean;
import org.marketcetera.strategy.StrategyModuleFactory;
import org.marketcetera.util.misc.ClassVersion;

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
public class EmbeddedConnection extends AbstractStrategyEngineConnection {

    private volatile MBeanServerConnection mMBeanServer;
    private volatile ModuleManager mModuleManager;
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
        super(guiExecutor);
        Validate.notNull(engine, "engine"); //$NON-NLS-1$
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
        DeployedStrategy result;
        try {
            result = super.deploy(strategy);
        } finally {
            saveState();
        }
        return result;
    }

    @Override
    protected ModuleURN doDeploy(Strategy strategy, File script)
            throws Exception {
        return mModuleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                strategy.getInstanceName(), strategy.getClassName(), strategy
                        .getLanguage(), script, getProperties(strategy),
                strategy.isRouteOrdersToServer(),
                SinkModuleFactory.INSTANCE_URN);
    }

    @Override
    public void update(final DeployedStrategy deployedStrategy,
            Strategy newConfiguration) throws Exception {
        try {
            super.update(deployedStrategy, newConfiguration);
        } finally {
            saveState();
        }
    }

    @Override
    protected void doUpdate(ModuleURN urn, Strategy newConfiguration)
            throws Exception {
        StrategyMXBean proxy = getProxy(urn);
        proxy.setRoutingOrdersToORS(newConfiguration.isRouteOrdersToServer());
        proxy.setParameters(getPropertiesString(newConfiguration));
    }

    @Override
    public void refresh() throws Exception {
        try {
            super.refresh();
        } finally {
            saveState();
        }
    }

    @Override
    public void refresh(final DeployedStrategy deployedStrategy)
            throws Exception {
        try {
            super.refresh(deployedStrategy);
        } finally {
            saveState();
        }
    }

    @Override
    public void undeploy(final DeployedStrategy deployedStrategy)
            throws Exception {
        try {
            super.undeploy(deployedStrategy);
        } finally {
            saveState();
        }
    }

    @Override
    protected void doRefresh(ModuleURN urn, DeployedStrategy strategy)
            throws Exception {
        /*
         * Pull all properties, even immutable ones like language and name since
         * we may be refreshing strategies deployed by other means.
         */
        StrategyMXBean proxy = getProxy(urn);
        strategy.setLanguage(proxy.getLanguage().name());
        strategy.setClassName(proxy.getName());
        strategy.setInstanceName(urn.instanceName());
        strategy.setRouteOrdersToServer(proxy.isRoutingOrdersToORS());
        strategy.getParameters().clear();
        strategy.getParameters()
                .putAll(getPropertiesMap(proxy.getParameters()));
    }

    private void saveState() {
        if (mPersistenceService != null) {
            try {
                mPersistenceService.save(getEngine().getDeployedStrategies());
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

    @Override
    protected boolean isRunning(ModuleURN urn) throws ModuleNotFoundException,
            InvalidURNException {
        return mModuleManager.getModuleInfo(urn).getState().isStarted();
    }

    @Override
    protected void doStart(ModuleURN urn) throws ModuleException {
        mModuleManager.start(urn);
    }

    @Override
    protected void doStop(ModuleURN urn) throws ModuleException {
        mModuleManager.stop(urn);
    }

    @Override
    protected void doUndeploy(ModuleURN urn) throws ModuleException {
        mModuleManager.deleteModule(urn);
    }

    @Override
    protected List<ModuleURN> getDeployed() throws InvalidURNException {
        return mModuleManager
                .getModuleInstances(StrategyModuleFactory.PROVIDER_URN);
    }

}
