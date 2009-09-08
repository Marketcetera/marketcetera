package org.marketcetera.photon.internal.strategy.engine.strategyagent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.marketcetera.photon.commons.ExceptionUtils;
import org.marketcetera.photon.core.ICredentials;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.core.ICredentialsService.IAuthenticationHelper;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.strategyagent.impl.StrategyAgentEngineImpl;
import org.marketcetera.photon.strategy.engine.strategyagent.StrategyAgentEngines;
import org.marketcetera.saclient.ConnectionException;
import org.marketcetera.saclient.ConnectionStatusListener;
import org.marketcetera.saclient.DataReceiver;
import org.marketcetera.saclient.SAClient;
import org.marketcetera.saclient.SAClientFactory;
import org.marketcetera.saclient.SAClientParameters;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Internal engine implementation. See
 * {@link StrategyAgentEngines#createStrategyAgentEngine(ExecutorService, ICredentialsService, ILogoutService, StrategyAgentEngine)}
 * .
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class InternalStrategyAgentEngine extends StrategyAgentEngineImpl {

    private final ExecutorService mGUIExecutor;
    private final ICredentialsService mCredentialsService;
    private final SAClientFactory mClientFactory;
    private final ILogoutService mLogoutService;
    private final ISinkDataManager mSinkDataManager;
    private final Runnable mLogoutRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                disconnect();
            } catch (Exception e) {
                /*
                 * Note that getName() is not guaranteed to be visible here.
                 */
                Messages.INTERNAL_STRATEGY_AGENT_ENGINE_DISCONNECT_ON_LOGOUT_FAILED
                        .error(InternalStrategyAgentEngine.this, e, getName());
            }
        }
    };

    private volatile SAClient mClient;

    /**
     * @param engine
     *            the desired engine configuration
     * @param guiExecutor
     *            the executor to run tasks that change the model state
     * @param credentialsService
     *            the service to use to authenticate connections
     * @param logoutService
     *            the service used to disconnect remote connections on logout
     * @param factory
     *            the SAClient factory
     * @param sinkDataManager
     *            manager to send data received from remote agent
     */
    public InternalStrategyAgentEngine(StrategyAgentEngine engine,
            ExecutorService guiExecutor,
            ICredentialsService credentialsService,
            ILogoutService logoutService, SAClientFactory factory,
            ISinkDataManager sinkDataManager) {
        mGUIExecutor = guiExecutor;
        mCredentialsService = credentialsService;
        mLogoutService = logoutService;
        mClientFactory = factory;
        mSinkDataManager = sinkDataManager;
        setName(engine.getName());
        setDescription(engine.getDescription());
        setJmsUrl(engine.getJmsUrl());
        setWebServiceHostname(engine.getWebServiceHostname());
        setWebServicePort(engine.getWebServicePort());
    }

    @Override
    public void connect() throws Exception {
        final ImmutableStrategyAgentEngine immutable = safeGet();
        final AtomicReference<ConnectionException> exception = new AtomicReference<ConnectionException>();
        if (!mCredentialsService
                .authenticateWithCredentials(new IAuthenticationHelper() {
                    @Override
                    public boolean authenticate(ICredentials credentials) {
                        try {
                            mClient = mClientFactory
                                    .create(new SAClientParameters(credentials
                                            .getUsername(), credentials
                                            .getPassword().toCharArray(),
                                            immutable.getJmsUrl(), immutable
                                                    .getWebServiceHostname(),
                                            immutable.getWebServicePort()));
                            return true;
                        } catch (ConnectionException e) {
                            exception.set(e);
                            return false;
                        }
                    }
                })) {
            if (exception.get() != null) {
                throw exception.get();
            } else {
                return;
            }
        }

        mClient.addConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void receiveConnectionStatus(boolean inStatus) {
                if (!inStatus) {
                    mLogoutService.removeLogoutRunnable(mLogoutRunnable);
                    try {
                        ExceptionUtils.launderedGet(mGUIExecutor
                                .submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        setConnection(null);
                                        setConnectionState(ConnectionState.DISCONNECTED);
                                        getDeployedStrategies().clear();
                                    }
                                }));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });

        mClient.addDataReceiver(new DataReceiver() {
            @Override
            public void receiveData(Object inObject) {
                mSinkDataManager.sendData(immutable.getName(), inObject);
            }
        });

        mLogoutService.addLogoutRunnable(mLogoutRunnable);
        final StrategyAgentConnection newConnection = new StrategyAgentConnection(
                mClient, mGUIExecutor);
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Runnable() {
            @Override
            public void run() {
                setConnection(newConnection);
                setConnectionState(ConnectionState.CONNECTED);
            }
        }));
        // pull remote state
        newConnection.refresh();
    }

    @Override
    public void disconnect() throws Exception {
        mClient.close();
        mClient = null;
        mLogoutService.removeLogoutRunnable(mLogoutRunnable);
        ExceptionUtils.launderedGet(mGUIExecutor.submit(new Runnable() {
            @Override
            public void run() {
                setConnection(null);
                setConnectionState(ConnectionState.DISCONNECTED);
                getDeployedStrategies().clear();
            }
        }));
    }

    private ImmutableStrategyAgentEngine safeGet() throws InterruptedException {
        return ExceptionUtils.launderedGet(mGUIExecutor
                .submit(new Callable<ImmutableStrategyAgentEngine>() {
                    @Override
                    public ImmutableStrategyAgentEngine call() {
                        return new ImmutableStrategyAgentEngine(
                                InternalStrategyAgentEngine.this);
                    }
                }));
    }
}
