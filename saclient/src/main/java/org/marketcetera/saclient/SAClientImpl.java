package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.MapWrapper;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.modules.remote.emitter.RemoteDataEmitter;
import org.marketcetera.modules.remote.emitter.EmitterAdapter;

import java.util.*;

/* $License$ */
/**
 * The client implementation that implements the details of communicating
 * with the remote strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
class SAClientImpl implements SAClient, EmitterAdapter {

    @Override
    public List<ModuleURN> getProviders() throws ConnectionException {
        failIfDisconnected();
        try {
            List<ModuleURN> list = mSAService.getProviders(getServiceContext());
            //translate nulls to empty lists for more usable API.
            return list != null? list: new ArrayList<ModuleURN>();
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public List<ModuleURN> getInstances(ModuleURN inProviderURN)
            throws ConnectionException {
        failIfDisconnected();
        try {
            List<ModuleURN> list = mSAService.getInstances(getServiceContext(),
                    inProviderURN);
            //translate nulls to empty lists for more usable API.
            return list != null? list: new ArrayList<ModuleURN>();
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public ModuleInfo getModuleInfo(ModuleURN inURN) throws ConnectionException {
        failIfDisconnected();
        try {
            return mSAService.getModuleInfo(getServiceContext(), inURN);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public void start(ModuleURN inURN) throws ConnectionException {
        failIfDisconnected();
        try {
            mSAService.start(getServiceContext(), inURN);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public void stop(ModuleURN inURN) throws ConnectionException {
        failIfDisconnected();
        try {
            mSAService.stop(getServiceContext(), inURN);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public void delete(ModuleURN inURN) throws ConnectionException {
        failIfDisconnected();
        try {
            mSAService.delete(getServiceContext(), inURN);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public Map<String, Object> getProperties(ModuleURN inURN)
            throws ConnectionException {
        failIfDisconnected();
        try {
            MapWrapper<String, Object> value = mSAService.getProperties(
                    getServiceContext(), inURN);
            return value == null? null: value.getMap();
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public Map<String, Object> setProperties(ModuleURN inURN, Map<String,
            Object> inProperties)
            throws ConnectionException {
        failIfDisconnected();
        try {
            MapWrapper<String, Object> map = mSAService.setProperties(
                    getServiceContext(), inURN,
                    new MapWrapper<String, Object>(inProperties));
            return map == null
                    ? null
                    : map.getMap();
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public ModuleURN createStrategy(CreateStrategyParameters inParameters)
            throws ConnectionException {
        failIfDisconnected();
        try {
            return mSAService.createStrategy(getServiceContext(), inParameters);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }

    @Override
    public CreateStrategyParameters getStrategyCreateParms(ModuleURN inURN)
            throws ConnectionException {
        failIfDisconnected();
        try {
            return mSAService.getStrategyCreateParms(getServiceContext(), inURN);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
            throws ConnectionException
    {
        failIfDisconnected();
        try {
            mSAService.sendData(getServiceContext(),
                                inData);
        } catch (Exception e) {
            throw wrapRemoteFailure(e);
        }
    }
    @Override
    public void addDataReceiver(DataReceiver inReceiver) {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (mReceivers) {
            mReceivers.addFirst(inReceiver);
        }
    }

    @Override
    public void removeDataReciever(DataReceiver inReceiver) {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (mReceivers) {
            mReceivers.removeFirstOccurrence(inReceiver);
        }
    }

    @Override
    public void addConnectionStatusListener(ConnectionStatusListener inListener) {
        if(inListener == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (mListeners) {
            mListeners.addFirst(inListener);
        }
    }

    @Override
    public void removeConnectionStatusListener(ConnectionStatusListener inListener) {
        if(inListener == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (mListeners) {
            mListeners.removeFirstOccurrence(inListener);
        }
    }

    @Override
    public SAClientParameters getParameters() {
        return new SAClientParameters(
                mParameters.getUsername(),
                "*****".toCharArray(),  //$NON-NLS-1$
                mParameters.getURL(),
                mParameters.getHostname(),
                mParameters.getPort());
    }

    @Override
    public synchronized void close() {
        //Don't do anything if already closed.
        if(mClosed) {
            return;
        }
        SLF4JLoggerProxy.debug(this, "Closing Strategy Agent Client");  //$NON-NLS-1$
        try {
            mServiceClient.logout();
        } catch (Exception e) {
            SLF4JLoggerProxy.debug(this,
                    "Ignoring error when closing the web service connection.",  //$NON-NLS-1$ 
                    e);
            ExceptUtils.interrupt(e);
        }
        mEmitter.close();
        mClosed = true;
        SLF4JLoggerProxy.debug(this, "Closed Strategy Agent Client");  //$NON-NLS-1$
    }

    @Override
    public void receiveData(Object inObject) {
        synchronized (mReceivers) {
            for(DataReceiver receiver: mReceivers) {
                try {
                    receiver.receiveData(inObject);
                } catch (Exception e) {
                    Messages.LOG_ERROR_RECEIVE_DATA.warn(this, e, inObject);
                    ExceptUtils.interrupt(e);
                }
            }
        }
    }

    @Override
    public void connectionStatusChanged(boolean inOldStatus, boolean inNewStatus) {
        mConnected = inNewStatus;
        synchronized (mListeners) {
            for(ConnectionStatusListener listener: mListeners) {
                try {
                    listener.receiveConnectionStatus(inNewStatus);
                } catch (Exception e) {
                    Messages.LOG_ERROR_RECEIVE_CONNECT_STATUS.warn(this, e, inNewStatus);
                    ExceptUtils.interrupt(e);
                }
            }
        }
    }
    /**
     * Creates an instance. Once created, the client is connected to the
     * remote strategy agent.
     *
     * @param inParameters the connection details. Cannot be null.
     *
     * @throws ConnectionException if there were errors connecting to
     * the remote strategy agent. 
     */
    SAClientImpl(SAClientParameters inParameters) throws ConnectionException {
        if(inParameters == null) {
            throw new NullPointerException();
        }
        mParameters = inParameters;
        try {
            mServiceClient = new Client(inParameters.getHostname(),
                                        inParameters.getPort(),
                                        SAClientVersion.APP_ID,
                                        inParameters.getContextClassProvider());
            mServiceClient.login(inParameters.getUsername(),
                                 inParameters.getPassword());
            mSAService = mServiceClient.getService(SAService.class);
        } catch (Exception e) {
            throw new ConnectionException(e, new I18NBoundMessage3P(
                    Messages.ERROR_WS_CONNECT,
                    inParameters.getHostname(),
                    String.valueOf(inParameters.getPort()),
                    inParameters.getUsername()));
        }
        boolean isJMSFailed = true;
        try {
            mEmitter = new RemoteDataEmitter(inParameters.getURL(),
                    inParameters.getUsername(),
                    String.valueOf(inParameters.getPassword()), this);
            isJMSFailed = false;
        } catch (Exception e) {
            throw new ConnectionException(e, new I18NBoundMessage2P(
                    Messages.ERROR_JMS_CONNECT,
                    inParameters.getURL(),
                    inParameters.getUsername()));
        } finally {
            //Disconnect the WS connection if the JMS connection failed.
            if(isJMSFailed) {
                try {
                    mServiceClient.logout();
                } catch (RemoteException e) {
                    SLF4JLoggerProxy.debug(this,
                            "Ignoring failure when logging out",  //$NON-NLS-1$ 
                            e);
                }
            }
        }
    }

    /**
     * Creates a connection exception wrapping the supplied exception.
     * <p>
     * If the supplied exception is a <code>RemoteException</code>,
     * the exception wrapped by it is extracted and wrapped into the
     * returned exception.
     *
     * @param inFailure the exception that needs to be wrapped.
     *
     * @return the connection exception wrapping the failure.
     */
    private ConnectionException wrapRemoteFailure(Exception inFailure) {
        Throwable cause;
        //if it's a remote server failure, extract the nested cause.
        if (inFailure instanceof RemoteException) {
            cause = inFailure.getCause() != null
                    ? inFailure.getCause()
                    : inFailure;
        } else {
            cause = inFailure;
        }
        return new ConnectionException(cause,
                new I18NBoundMessage1P(Messages.ERROR_WS_OPERATION,
                        cause.getLocalizedMessage()));
    }

    /**
     * Gets the client context to use when making WS calls.
     *
     * @return the client context.
     */
    private ClientContext getServiceContext() {
        return mServiceClient.getContext();
    }

    /**
     * Fails if the connection to the client is closed or disconnected.
     *
     * @throws ConnectionException if the connection to the client is
     * closed or disconnected.
     */
    private void failIfDisconnected() throws ConnectionException {
        failIfClosed();
        if(!mConnected) {
            throw new ConnectionException(Messages.CLIENT_DISCONNECTED);
        }
    }

    /**
     * Fails if the connection to the client has been closed.
     *
     * @throws IllegalStateException if the connection to the client
     * has been closed.
     */
    private void failIfClosed() throws IllegalStateException {
        if(mClosed) {
            throw new IllegalStateException(Messages.CLIENT_CLOSED.getText());
        }
    }

    private final Deque<DataReceiver> mReceivers =
            new LinkedList<DataReceiver>();
    private final Deque<ConnectionStatusListener> mListeners =
            new LinkedList<ConnectionStatusListener>();
    private final Client mServiceClient;
    private final RemoteDataEmitter mEmitter;
    private final SAService mSAService;
    private final SAClientParameters mParameters;
    private volatile boolean mClosed = false;
    private volatile boolean mConnected;
}
