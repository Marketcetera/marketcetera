package org.marketcetera.photon.internal.strategy.engine.sa;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.commons.Validate;
import org.marketcetera.photon.strategy.engine.AbstractStrategyEngineConnection;
import org.marketcetera.photon.strategy.engine.model.core.DeployedStrategy;
import org.marketcetera.photon.strategy.engine.model.core.Strategy;
import org.marketcetera.photon.strategy.engine.model.core.StrategyEngineConnection;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.CreateStrategyParameters;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import com.google.common.collect.Maps;

/* $License$ */

/**
 * Adapts {@link SAClient} to {@link StrategyEngineConnection}.
 * <p>
 * This class is not fully thread safe. It does manage visibility of the model
 * objects by ensuring all updates are done using the guiExecutor. However, it
 * does not support concurrent access.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@NotThreadSafe
@ClassVersion("$Id$")
public class StrategyAgentConnection extends AbstractStrategyEngineConnection {

    private static final String NAME_KEY = "Name"; //$NON-NLS-1$
    private static final String LANGUAGE_KEY = "Language"; //$NON-NLS-1$
    private static final String PARAMETERS_KEY = "Parameters"; //$NON-NLS-1$
    private static final String ROUTING_ORDERS_TO_ORS_KEY = "RoutingOrdersToORS"; //$NON-NLS-1$
    private static final ModuleURN STRATEGY_PROVIDER_URN = new ModuleURN(
            "metc:strategy:system"); //$NON-NLS-1$
    private final SAClient mClient;

    /**
     * Constructor.
     * 
     * @param client
     *            the client
     * @param guiExecutor
     *            the gui executor
     * @throws IllegalArgumentException
     *             if client or guiExecutor is null
     */
    public StrategyAgentConnection(SAClient client, ExecutorService guiExecutor) {
        super(guiExecutor);
        Validate.notNull(client, "client"); //$NON-NLS-1$
        mClient = client;
    }

    @Override
    protected ModuleURN doDeploy(Strategy strategy, File script)
            throws Exception {
        CreateStrategyParameters parameters = new CreateStrategyParameters(
                strategy.getInstanceName(), strategy.getClassName(), strategy
                        .getLanguage(), script, getPropertiesString(strategy),
                strategy.isRouteOrdersToServer());
        return mClient.createStrategy(parameters);
    }

    @Override
    protected void doUpdate(ModuleURN urn, Strategy newConfiguration)
            throws Exception {
        // can't use ImmutableMap.of because properties may be null
        Map<String, Object> map = Maps.newHashMap();
        map.put(ROUTING_ORDERS_TO_ORS_KEY, newConfiguration
                .isRouteOrdersToServer());
        String properties = getPropertiesString(newConfiguration);
        map.put(PARAMETERS_KEY, properties);
        Map<String, Object> result = mClient.setProperties(urn, map);
        StringBuilder errorMessage = new StringBuilder(); //$NON-NLS-1$
        boolean errorFound = appendError(result.get(ROUTING_ORDERS_TO_ORS_KEY),
                newConfiguration.isRouteOrdersToServer(), errorMessage,
                Messages.STRATEGY_AGENT_CONNECTION_UPDATE_ROUTING_ERROR, false);
        errorFound |= appendError(result.get(PARAMETERS_KEY), properties,
                errorMessage,
                Messages.STRATEGY_AGENT_CONNECTION_UPDATE_PARAMETERS_ERROR,
                errorFound);
        if (errorFound) {
            throw new Exception(errorMessage.toString());
        }
    }

    private boolean appendError(Object result, Object value,
            StringBuilder errorMessage, I18NMessage2P description,
            boolean errorFound) {
        if (result instanceof RemoteProperties) {
            if (errorFound) {
                errorMessage.append('\n');
            }
            errorMessage.append(description.getText(value,
                    ((RemoteProperties) result).getServerMessage()));
            return true;
        }
        return false;
    }

    @Override
    protected void doRefresh(ModuleURN urn, DeployedStrategy strategy)
            throws ConnectionException {
        /*
         * Pull all properties, even immutable ones like language and name since
         * we may be refreshing strategies deployed by other means.
         */
        Map<String, Object> properties = mClient.getProperties(urn);
        strategy.setLanguage((String) properties.get(LANGUAGE_KEY));
        strategy.setClassName((String) properties.get(NAME_KEY));
        strategy.setInstanceName(urn.instanceName());
        strategy.setRouteOrdersToServer((Boolean) properties
                .get(ROUTING_ORDERS_TO_ORS_KEY));
        strategy.getParameters().clear();
        strategy.getParameters().putAll(
                getPropertiesMap((String) properties.get(PARAMETERS_KEY)));
    }

    @Override
    protected boolean isRunning(ModuleURN urn) throws ConnectionException {
        return mClient.getModuleInfo(urn).getState().isStarted();
    }

    @Override
    protected void doStart(ModuleURN urn) throws ConnectionException {
        mClient.start(urn);
    }

    @Override
    protected void doStop(ModuleURN urn) throws ConnectionException {
        mClient.stop(urn);
    }

    @Override
    protected void doUndeploy(ModuleURN urn) throws ConnectionException {
        mClient.delete(urn);
    }

    @Override
    protected List<ModuleURN> getDeployed() throws ConnectionException {
        return mClient.getInstances(STRATEGY_PROVIDER_URN);
    }

}
