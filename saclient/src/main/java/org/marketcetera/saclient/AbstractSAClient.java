package org.marketcetera.saclient;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.modules.remote.emitter.EmitterAdapter;
import org.marketcetera.modules.remote.emitter.RemoteDataEmitter;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides common behavior for <code>SAClient</code> implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id: SAClientImpl.java 16853 2014-03-06 02:10:11Z colin $")
public abstract class AbstractSAClient
        implements SAClient,EmitterAdapter,Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized final void start()
    {
        doStart();
        if(parameters.getUseJms()) {
            try {
                emitter = new RemoteDataEmitter(parameters.getURL(),
                                                 parameters.getUsername(),
                                                 String.valueOf(parameters.getPassword()),
                                                 this);
            } catch (Exception e) {
                throw new ConnectionException(e,
                                              new I18NBoundMessage2P(Messages.ERROR_JMS_CONNECT,
                                                                     parameters.getURL(),
                                                                     parameters.getUsername()));
            }
        } else {
            emitter = null;
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized final void stop()
    {
        SLF4JLoggerProxy.debug(this,
                               "Closing Strategy Agent Client");  //$NON-NLS-1$
        try {
            try {
                doStop();
            } catch (Exception ignored) {}
            try {
                if(emitter != null) {
                    emitter.close();
                }
            } catch (Exception ignored) {}
        } finally {
            emitter = null;
            running.set(false);
            SLF4JLoggerProxy.debug(this,
                                   "Closed Strategy Agent Client");  //$NON-NLS-1$
        }
    }
    /**
     * 
     *
     *
     */
    protected void doStart()
    {
        
    }
    /**
     * 
     *
     *
     */
    protected void doStop()
    {
        
    }
    /**
     * Create a new AbstractSAClient instance.
     *
     * @param inParameters an <code>SAClientParameters</code> value
     * @throws ConnectionException if a connection could not be made
     */
    protected AbstractSAClient(SAClientParameters inParameters)
    {
        if(inParameters == null) {
            throw new NullPointerException();
        }
        parameters = inParameters;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#close()
     */
    @Override
    public synchronized final void close()
    {
        stop();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#addDataReceiver(org.marketcetera.saclient.DataReceiver)
     */
    @Override
    public void addDataReceiver(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (receivers) {
            receivers.addFirst(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#removeDataReciever(org.marketcetera.saclient.DataReceiver)
     */
    @Override
    public void removeDataReciever(DataReceiver inReceiver)
    {
        if(inReceiver == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (receivers) {
            receivers.removeFirstOccurrence(inReceiver);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#addConnectionStatusListener(org.marketcetera.saclient.ConnectionStatusListener)
     */
    @Override
    public void addConnectionStatusListener(ConnectionStatusListener inListener)
    {
        if(inListener == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (listeners) {
            listeners.addFirst(inListener);
        }
        inListener.receiveConnectionStatus(isRunning());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#removeConnectionStatusListener(org.marketcetera.saclient.ConnectionStatusListener)
     */
    @Override
    public void removeConnectionStatusListener(ConnectionStatusListener inListener)
    {
        if(inListener == null) {
            throw new NullPointerException();
        }
        failIfClosed();
        synchronized (listeners) {
            listeners.removeFirstOccurrence(inListener);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.SAClient#getParameters()
     */
    @Override
    public SAClientParameters getParameters()
    {
        return new SAClientParameters(parameters.getUsername(),
                                      "*****".toCharArray(),  //$NON-NLS-1$
                                      parameters.getURL(),
                                      parameters.getHostname(),
                                      parameters.getPort());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.remote.emitter.EmitterAdapter#receiveData(java.lang.Object)
     */
    @Override
    public void receiveData(Object inObject)
    {
        synchronized (receivers) {
            for(DataReceiver receiver: receivers) {
                try {
                    receiver.receiveData(inObject);
                } catch (Exception e) {
                    Messages.LOG_ERROR_RECEIVE_DATA.warn(this, e, inObject);
                    ExceptUtils.interrupt(e);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.remote.emitter.EmitterAdapter#connectionStatusChanged(boolean, boolean)
     */
    @Override
    public void connectionStatusChanged(boolean inOldStatus,
                                        boolean inNewStatus)
    {
        if(inOldStatus == inNewStatus) {
            return;
        }
        running.set(inNewStatus);
        synchronized(listeners) {
            for(ConnectionStatusListener listener: listeners) {
                try {
                    listener.receiveConnectionStatus(inNewStatus);
                } catch (Exception e) {
                    Messages.LOG_ERROR_RECEIVE_CONNECT_STATUS.warn(this,
                                                                   e,
                                                                   inNewStatus);
                    ExceptUtils.interrupt(e);
                }
            }
        }
    }
    /**
     * Fails if the connection to the client is closed or disconnected.
     *
     * @throws ConnectionException if the connection to the client is closed or disconnected.
     */
    protected void failIfDisconnected()
            throws ConnectionException
    {
        failIfClosed();
        if(!isRunning()) {
            throw new ConnectionException(Messages.CLIENT_DISCONNECTED);
        }
    }
    /**
     * Fails if the connection to the client has been closed.
     *
     * @throws IllegalStateException if the connection to the client has been closed.
     */
    protected void failIfClosed()
            throws IllegalStateException
    {
        if(!isRunning()) {
            throw new IllegalStateException(Messages.CLIENT_CLOSED.getText());
        }
    }
    /**
     * Creates a connection exception wrapping the supplied exception.
     * 
     * <p>If the supplied exception is a <code>RemoteException</code>,
     * the exception wrapped by it is extracted and wrapped into the
     * returned exception.
     *
     * @param inFailure the exception that needs to be wrapped.
     * @return the connection exception wrapping the failure.
     */
    protected ConnectionException wrapRemoteFailure(Exception inFailure)
    {
        Throwable cause;
        //if it's a remote server failure, extract the nested cause.
        if(inFailure instanceof RemoteException) {
            cause = inFailure.getCause() != null ? inFailure.getCause() : inFailure;
        } else {
            cause = inFailure;
        }
        return new ConnectionException(cause,
                                       new I18NBoundMessage1P(Messages.ERROR_WS_OPERATION,
                                                              cause.getLocalizedMessage()));
    }
    /**
     * remote emitter used to connect to external SA instances
     */
    private RemoteDataEmitter emitter;
    /**
     * receivers of remove data
     */
    private final Deque<DataReceiver> receivers = new LinkedList<DataReceiver>();
    /**
     * connection status listeners collection
     */
    private final Deque<ConnectionStatusListener> listeners = new LinkedList<ConnectionStatusListener>();
    /**
     * SA connection parameters
     */
    protected final SAClientParameters parameters;
    /**
     * indicates if the connection is active or not
     */
    protected final AtomicBoolean running = new AtomicBoolean(false);
}
