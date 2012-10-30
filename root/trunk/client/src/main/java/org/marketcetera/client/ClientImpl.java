package org.marketcetera.client;

import java.beans.ExceptionListener;
import java.math.BigDecimal;
import java.util.*;

import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.config.SpringConfig;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.JmsUtils;
import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.core.Util;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.metrics.ThreadedMetric;
import org.marketcetera.trade.*;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage4P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.DateWrapper;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.RemoteProxyException;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.SimpleMessageListenerContainer;

/* $License$ */
/**
 * The implementation of Client that connects to the server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class ClientImpl implements Client, javax.jms.ExceptionListener {

    @Override
    public void sendOrder(OrderSingle inOrderSingle)
            throws ConnectionException, OrderValidationException {
        Validations.validate(inOrderSingle);

        convertAndSend(inOrderSingle);
    }

    @Override
    public void sendOrder(OrderReplace inOrderReplace)
            throws ConnectionException, OrderValidationException {
        Validations.validate(inOrderReplace);

        convertAndSend(inOrderReplace);
    }

    @Override
    public void sendOrder(OrderCancel inOrderCancel)
            throws ConnectionException, OrderValidationException {
        Validations.validate(inOrderCancel);

        convertAndSend(inOrderCancel);
    }

    @Override
    public void sendOrderRaw(FIXOrder inFIXOrder)
            throws ConnectionException, OrderValidationException {
        Validations.validate(inFIXOrder);
        convertAndSend(inFIXOrder);
    }

    @Override
    public void addReportListener(ReportListener inListener) {
        failIfClosed();
        synchronized (mReportListeners) {
            mReportListeners.addFirst(inListener);
        }
    }

    @Override
    public void removeReportListener(ReportListener inListener) {
        failIfClosed();
        synchronized (mReportListeners) {
            mReportListeners.removeFirstOccurrence(inListener);
        }
    }

    @Override
    public void addBrokerStatusListener
        (BrokerStatusListener listener)
    {
        failIfClosed();
        synchronized (mBrokerStatusListeners) {
            mBrokerStatusListeners.addFirst(listener);
        }
    }

    @Override
    public void removeBrokerStatusListener
        (BrokerStatusListener listener)
    {
        failIfClosed();
        synchronized (mBrokerStatusListeners) {
            mBrokerStatusListeners.removeFirstOccurrence(listener);
        }
    }

    @Override
    public void addServerStatusListener
        (ServerStatusListener listener)
    {
        failIfClosed();
        synchronized (mServerStatusListeners) {
            mServerStatusListeners.addFirst(listener);
        }
    }

    @Override
    public void removeServerStatusListener
        (ServerStatusListener listener)
    {
        failIfClosed();
        synchronized (mServerStatusListeners) {
            mServerStatusListeners.removeFirstOccurrence(listener);
        }
    }

    @Override
    public ReportBase[] getReportsSince
        (Date inDate)
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            ReportBaseImpl[] reports = mService.getReportsSince(getServiceContext(),new DateWrapper(inDate));
            return reports == null ? new ReportBase[0] : reports;
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public BigDecimal getEquityPositionAsOf
        (Date inDate,
         Equity inEquity)
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getEquityPositionAsOf
                (getServiceContext(),new DateWrapper(inDate),inEquity);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllEquityPositionsAsOf
        (Date inDate)
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getAllEquityPositionsAsOf
                (getServiceContext(),new DateWrapper(inDate)).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    
    @Override
    public BigDecimal getCurrencyPositionAsOf
        (Date inDate,
         Currency inCurrency)
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getCurrencyPositionAsOf
                (getServiceContext(),new DateWrapper(inDate),inCurrency);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Map<PositionKey<Currency>, BigDecimal> getAllCurrencyPositionsAsOf
        (Date inDate)
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getAllCurrencyPositionsAsOf
                (getServiceContext(),new DateWrapper(inDate)).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getAllFuturePositionsAsOf(getServiceContext(),
                                                      new DateWrapper(inDate)).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getFuturePositionAsOf(getServiceContext(),
                                                  new DateWrapper(inDate),
                                                  inFuture);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate, Option inOption)
            throws ConnectionException {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getOptionPositionAsOf(
                    getServiceContext(), new DateWrapper(inDate), inOption);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex, Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getAllOptionPositionsAsOf
                    (getServiceContext(), new DateWrapper(inDate)).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex, Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                       String... inSymbols)
            throws ConnectionException {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getOptionPositionsAsOf
                    (getServiceContext(), new DateWrapper(inDate), inSymbols).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex, Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public String getUnderlying(String inOptionRoot) throws ConnectionException {
        failIfClosed();
        failIfDisconnected();
        try {
            String value;
            synchronized (mUnderlyingToRootCache) {
                if(mUnderlyingToRootCache.containsKey(inOptionRoot)) {
                    value = mUnderlyingToRootCache.get(inOptionRoot);
                } else {
                    //cache null return values too
                    value = mService.getUnderlying(getServiceContext(),
                            inOptionRoot);
                    mUnderlyingToRootCache.put(inOptionRoot, value);
                }
            }
            return value;
        } catch (RemoteException ex) {
            throw new ConnectionException(ex, Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException {
        failIfClosed();
        failIfDisconnected();
        try {
            Collection<String> value;
            synchronized (mRootToUnderlyingCache) {
                if(mRootToUnderlyingCache.containsKey(inUnderlying)) {
                    value = mRootToUnderlyingCache.get(inUnderlying);
                } else {
                    value = mService.getOptionRoots(getServiceContext(),
                            inUnderlying);
                    mRootToUnderlyingCache.put(inUnderlying, value);
                }
            }
            return value;
        } catch (RemoteException ex) {
            throw new ConnectionException(ex, Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public BrokersStatus getBrokersStatus()
        throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return mService.getBrokersStatus(getServiceContext());
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public UserInfo getUserInfo(UserID id,
                                boolean useCache)
        throws ConnectionException
    {
        failIfClosed();
        UserInfo result;
        synchronized (mUserInfoCache) {
            if (useCache) {
                result=mUserInfoCache.get(id);
                if (result!=null) {
                    return result;
                }
            }
            failIfDisconnected();
            try {
                result=mService.getUserInfo(getServiceContext(),id);
            } catch (RemoteException ex) {
                throw new ConnectionException
                    (ex,Messages.ERROR_REMOTE_EXECUTION);
            }
            mUserInfoCache.put(id,result);
        }
        return result;
    }

    @Override
    public synchronized void close() {
        internalClose();
        ClientManager.reset();
        mClosed = true;
    }

    @Override
    public void reconnect() throws ConnectionException {
        reconnect(null);
    }

    @Override
    public synchronized void reconnect(ClientParameters inParameters)
            throws ConnectionException {
        failIfClosed();
        if(mContext != null) {
            internalClose();
        }
        if(inParameters != null) {
            setParameters(inParameters);
        }
        connect();
    }

    @Override
    public void addExceptionListener(ExceptionListener inListener) {
        failIfClosed();
        synchronized (mExceptionListeners) {
            mExceptionListeners.addFirst(inListener);
        }
    }

    @Override
    public void removeExceptionListener(ExceptionListener inListener) {
        failIfClosed();
        synchronized (mExceptionListeners) {
            mExceptionListeners.removeFirstOccurrence(inListener);
        }
    }

    @Override
    public ClientParameters getParameters() {
        failIfClosed();
        return new ClientParameters(
                mParameters.getUsername(),
                //hide the password value.
                "*****".toCharArray(),   //$NON-NLS-1$
                mParameters.getURL(), mParameters.getHostname(),
                mParameters.getPort(), mParameters.getIDPrefix());
    }

    @Override
    public Date getLastConnectTime() {
        failIfClosed();
        return mLastConnectTime;
    }

    @Override
    public boolean isCredentialsMatch(String inUsername, char[] inPassword) {
        return (!mClosed) &&
                ObjectUtils.equals(mParameters.getUsername(), inUsername) &&
                Arrays.equals(mParameters.getPassword(), inPassword);
    }

    @Override
    public boolean isServerAlive()
    {
        return ((!mClosed) && mServerAlive);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            return Util.propertiesFromString(mService.getUserData(getServiceContext()));
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.Client#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inProperties)
            throws ConnectionException
    {
        failIfClosed();
        failIfDisconnected();
        try {
            mService.setUserData(getServiceContext(),
                                 Util.propertiesToString(inProperties));
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,
                                          Messages.ERROR_REMOTE_EXECUTION);
        }
    }
    /**
     * Creates an instance given the parameters and connects to the server.
     *
     * @param inParameters the parameters to connect to the server, cannot
     * be null.
     *
     * @throws ConnectionException if there were errors connecting
     * to the server.
     */
    ClientImpl(ClientParameters inParameters) throws ConnectionException {
        setParameters(inParameters);
        connect();
    }

    // TradeMessage reception; public scope required by Spring.

    public class TradeMessageReceiver
        implements ReceiveOnlyHandler<TradeMessage>
    {
        @Override
        public void receiveMessage
            (TradeMessage inReport)
        {
            if (inReport instanceof ExecutionReport) {
                notifyExecutionReport((ExecutionReport)inReport);
            } else if (inReport instanceof OrderCancelReject) {
                notifyCancelReject((OrderCancelReject)inReport);
            } else {
                Messages.LOG_RECEIVED_FIX_REPORT.warn
                    (this,ObjectUtils.toString(inReport));
            }
        }
    }

    void notifyExecutionReport(ExecutionReport inReport) {
        SLF4JLoggerProxy.debug(TRAFFIC, "Received Exec Report:{}", inReport);  //$NON-NLS-1$
        synchronized (mReportListeners) {
            for(ReportListener listener: mReportListeners) {
                try {
                    listener.receiveExecutionReport(inReport);
                } catch (Throwable t) {
                    Messages.LOG_ERROR_RECEIVE_EXEC_REPORT.warn(this, t,
                            ObjectUtils.toString(inReport));
                    ExceptUtils.interrupt(t);
                }
            }
        }
    }

    void notifyCancelReject(OrderCancelReject inReport) {
        SLF4JLoggerProxy.debug(TRAFFIC, "Received Cancel Reject:{}", inReport);  //$NON-NLS-1$
        synchronized (mReportListeners) {
            for(ReportListener listener: mReportListeners) {
                try {
                    listener.receiveCancelReject(inReport);
                } catch (Throwable t) {
                    Messages.LOG_ERROR_RECEIVE_CANCEL_REJECT.warn(this, t,
                            ObjectUtils.toString(inReport));
                    ExceptUtils.interrupt(t);
                }
            }
        }
    }

    // ReceiveOnlyHandler<BrokerStatus>; public scope required by Spring.

    public class BrokerStatusReceiver
        implements ReceiveOnlyHandler<BrokerStatus>
    {
        @Override
        public void receiveMessage
            (BrokerStatus status)
        {
            notifyBrokerStatus(status);
        }
    }
    
    void notifyBrokerStatus(BrokerStatus status) {
        SLF4JLoggerProxy.debug
            (TRAFFIC,"Received Broker Status:{}",status); //$NON-NLS-1$
        synchronized (mBrokerStatusListeners) {
            for (BrokerStatusListener listener:
                     mBrokerStatusListeners) {
                try {
                    listener.receiveBrokerStatus(status);
                } catch (Throwable t) {
                    Messages.LOG_ERROR_RECEIVE_BROKER_STATUS.warn(this, t,
                            ObjectUtils.toString(status));
                    ExceptUtils.interrupt(t);
                }
            }
        }
    }

    void notifyServerStatus(boolean status) {
        SLF4JLoggerProxy.debug
            (TRAFFIC,"Received Server Status:{}",status); //$NON-NLS-1$
        synchronized (mServerStatusListeners) {
            for (ServerStatusListener listener:
                     mServerStatusListeners) {
                try {
                    listener.receiveServerStatus(status);
                } catch (Throwable t) {
                    Messages.LOG_ERROR_RECEIVE_SERVER_STATUS.warn(this, t,
                            status);
                    ExceptUtils.interrupt(t);
                }
            }
        }
    }

    // javax.jms.ExceptionListener.

    @Override
    public void onException(JMSException e) {
        exceptionThrown(new ConnectionException
                        (e,Messages.ERROR_RECEIVING_JMS_MESSAGE));
    }

    void exceptionThrown(ConnectionException inException) {
        synchronized (mExceptionListeners) {
            for(ExceptionListener l: mExceptionListeners) {
                try {
                    l.exceptionThrown(inException);
                } catch (Exception e) {
                    Messages.LOG_ERROR_NOTIFY_EXCEPTION.warn(this, e,
                            ObjectUtils.toString(inException));
                    ExceptUtils.interrupt(e);
                }
            }
        }
    }

    /**
     * Fetches the next orderID base from server.
     *
     * @return the next orderID base from server.
     *
     * @throws RemoteException if there were communication errors.
     */
    String getNextServerID() throws RemoteException {
        failIfDisconnected();
        return mService.getNextOrderID(getServiceContext());
    }

    /**
     * The 'heart' that produces heartbeats, keeping the connection to
     * the ORS server alive.
     */

    private class Heart
        extends Thread
    {
        private volatile boolean mMarked;

        Heart()
        {
            super(Thread.currentThread().getThreadGroup(),
                  Messages.HEARTBEAT_THREAD_NAME.getText());
            setDaemon(true);
        }

        void markExit()
        {
            mMarked=true;
        }

        private boolean isMarked()
        {
            return mMarked;
        }

        @Override
        public void run()
        {
            while (true) {
                try {
                    Thread.sleep(mParameters.getHeartbeatInterval());
                } catch (InterruptedException ex) {
                    SLF4JLoggerProxy.debug
                        (HEARTBEATS,"Stopped (interrupted)"); //$NON-NLS-1$
                    markExit();
                    setServerAlive(false);
                    return;
                }
                if (isMarked()) {
                    SLF4JLoggerProxy.debug
                        (HEARTBEATS,"Stopped (marked)"); //$NON-NLS-1$
                    setServerAlive(false);
                    return;
                }
                try {
                    mService.heartbeat(getServiceContext());
                    setServerAlive(true);
                } catch (Exception ex) {
                    setServerAlive(false);
                    if (ExceptUtils.isInterruptException(ex)) {
                        SLF4JLoggerProxy.debug
                            (HEARTBEATS,"Stopped (interrupted)"); //$NON-NLS-1$
                        markExit();
                        return;
                    }
                    SLF4JLoggerProxy.debug
                        (HEARTBEATS,"Failed",ex); //$NON-NLS-1$
                    exceptionThrown(new ConnectionException
                                    (ex,Messages.ERROR_HEARTBEAT_FAILED));
                    if (ex instanceof RemoteException) {
                        // We connected to the server, but the session
                        // may have expired: attempt to auto-reconnect
                        // after a short delay to let the server
                        // settle (if it has just restarted). The
                        // delay is random so that not all clients
                        // will try and contact the ORS at the same
                        // time.
                        long delay=(long)(RECONNECT_WAIT_INTERVAL*
                                          (0.75+1.25*Math.random()));
                        SLF4JLoggerProxy.debug
                            (HEARTBEATS,"Reconnecting in "+ //$NON-NLS-1$
                             delay+"ms..."); //$NON-NLS-1$
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException ex2) {
                            SLF4JLoggerProxy.debug
                                (HEARTBEATS,
                                 "Stopped (interrupted)"); //$NON-NLS-1$
                            markExit();
                            return;
                        }
                        try {
                            mServiceClient.logout();
                            mServiceClient.login(mParameters.getUsername(),
                                                 mParameters.getPassword());
                            setServerAlive(true);
                            SLF4JLoggerProxy.debug
                                (HEARTBEATS,
                                 "...reconnect succeeded."); //$NON-NLS-1$
                        } catch (Exception ex2) {
                            setServerAlive(false);
                            if (ExceptUtils.isInterruptException(ex2)) {
                                SLF4JLoggerProxy.debug
                                    (HEARTBEATS,
                                     "Stopped (interrupted)"); //$NON-NLS-1$
                                markExit();
                                return;
                            }
                            SLF4JLoggerProxy.debug
                                (HEARTBEATS,
                                 "...reconnect failed.",ex2); //$NON-NLS-1$
                            exceptionThrown
                                (new ConnectionException
                                 (ex2,Messages.ERROR_HEARTBEAT_FAILED));
                        }
                    }
                }
                if (isMarked()) {
                    SLF4JLoggerProxy.debug
                        (HEARTBEATS,"Stopped (marked)"); //$NON-NLS-1$
                    setServerAlive(false);
                    return;
                }
            }
        }
    }


    private void internalClose() {
        if (mContext == null) {
            return;
        }
        //Clear all the caches
        synchronized (mUnderlyingToRootCache) {
            mUnderlyingToRootCache.clear();
        }
        synchronized (mRootToUnderlyingCache) {
            mRootToUnderlyingCache.clear();
        }
        synchronized (mUserInfoCache) {
            mUserInfoCache.clear();
        }
        // Close the heartbeat generator first so that it won't
        // re-create a JMS connection during subsequent shutdown. In
        // fact, the generator will normally shut down the JMS
        // connection before it terminates.
        if (mHeart!=null) {
            mHeart.markExit();
            mHeart.interrupt();
            try {
                mHeart.join();
            } catch (InterruptedException ex) {
                SLF4JLoggerProxy.debug
                    (this,"Error when joining with heartbeat thread",ex); //$NON-NLS-1$
                ExceptUtils.interrupt(ex);
            }
        }
        setServerAlive(false);
        try {
            if (mServiceClient!=null) {
                mServiceClient.logout();
            }
        } catch (Exception ex) {
            SLF4JLoggerProxy.debug
                (this,"Error when closing web service client",ex); //$NON-NLS-1$
            ExceptUtils.interrupt(ex);
        } finally {
            try {
                if (mContext!=null) {
                    mContext.close();
                }
            } catch (Exception ex) {
                SLF4JLoggerProxy.debug
                    (this,"Error when closing context",ex); //$NON-NLS-1$
                ExceptUtils.interrupt(ex);
            } finally {
                setContext(null);
            }
        }
    }

    private void connect() throws ConnectionException {
        if(mParameters.getURL() == null || mParameters.getURL().
                trim().isEmpty()) {
            throw new ConnectionException(Messages.CONNECT_ERROR_NO_URL);
        }
        if(mParameters.getUsername() == null || mParameters.getUsername().
                trim().isEmpty()) {
            throw new ConnectionException(Messages.CONNECT_ERROR_NO_USERNAME);
        }
        if(mParameters.getHostname() == null || mParameters.getHostname().trim().isEmpty()) {
            throw new ConnectionException(Messages.CONNECT_ERROR_NO_HOSTNAME);
        }
        if(mParameters.getPort() < 1 || mParameters.getPort() > 0xFFFF) {
            throw new ConnectionException(new I18NBoundMessage1P(
                    Messages.CONNECT_ERROR_INVALID_PORT, mParameters.getPort()));
        }
        try {
            StaticApplicationContext parentCtx = new StaticApplicationContext();
            SpringUtils.addStringBean(parentCtx, "brokerURL",  //$NON-NLS-1$
                    mParameters.getURL());
            SpringUtils.addStringBean(parentCtx,
                    "runtimeUsername", mParameters.getUsername());  //$NON-NLS-1$
            SpringUtils.addStringBean(parentCtx,
                    "runtimePassword", mParameters == null    //$NON-NLS-1$
                    ? null
                    : String.valueOf(mParameters.getPassword()));
            parentCtx.refresh();
            AbstractApplicationContext ctx;
            try {
                ctx = new FileSystemXmlApplicationContext(new String[] {"file:"+ApplicationBase.CONF_DIR+"client.xml"}, //$NON-NLS-1$
                                                          parentCtx);
            } catch (BeansException e) {
                ctx = new ClassPathXmlApplicationContext(new String[] {"client.xml"},  //$NON-NLS-1$
                                                         parentCtx);
            }
            ctx.registerShutdownHook();
            ctx.start();
            setContext(ctx);
            SpringConfig cfg=SpringConfig.getSingleton();
            if (cfg==null) {
                throw new ConnectionException
                    (Messages.CONNECT_ERROR_NO_CONFIGURATION);
            }

            mServiceClient = new org.marketcetera.util.ws.stateful.Client
                (mParameters.getHostname(), mParameters.getPort(),
                 ClientVersion.APP_ID);
            mServiceClient.login(mParameters.getUsername(),
                                 mParameters.getPassword());
            mService = mServiceClient.getService(Service.class);

            mJmsMgr=new JmsManager
                (cfg.getIncomingConnectionFactory(),
                 cfg.getOutgoingConnectionFactory(),this);
            startJms();
            mServerAlive=true;
            notifyServerStatus(true);

            mHeart = new Heart();
            mHeart.start();

            ClientIDFactory idFactory = new ClientIDFactory(
                    mParameters.getIDPrefix(), this);
            idFactory.init();
            Factory.getInstance().setOrderIDFactory(idFactory);
        } catch (Throwable t) {
            internalClose();
            ExceptUtils.interrupt(t);
            if (t.getCause() instanceof RemoteProxyException) {
                RemoteProxyException ex=(RemoteProxyException)t.getCause();
                if (IncompatibleComponentsException.class.getName().equals
                    (ex.getServerName())) {
                    throw new ConnectionException
                        (t,new I18NBoundMessage1P
                         (Messages.ERROR_CONNECT_INCOMPATIBLE_DEDUCED,
                          ex.getMessage()));
                }
            } else if (t.getCause() instanceof
                       IncompatibleComponentsException) {
                IncompatibleComponentsException ex=
                    (IncompatibleComponentsException)t.getCause();
                throw new ConnectionException
                    (t,new I18NBoundMessage2P
                     (Messages.ERROR_CONNECT_INCOMPATIBLE_DIRECT,
                      ClientVersion.APP_ID,
                      ex.getServerVersion()));
            }
            throw new ConnectionException(t, new I18NBoundMessage4P(
                    Messages.ERROR_CONNECT_TO_SERVER, mParameters.getURL(),
                    mParameters.getUsername(), mParameters.getHostname(),
                    mParameters.getPort()));
        }
        mLastConnectTime = new Date();
    }

    private void setContext(AbstractApplicationContext inContext) {
        mContext = inContext;
    }

    private void convertAndSend(Order inOrder) throws ConnectionException {
        ThreadedMetric.event("client-OUT",  //$NON-NLS-1$ 
                inOrder instanceof OrderBase
                        ? ((OrderBase) inOrder).getOrderID()
                        : null);
        failIfClosed();
        SLF4JLoggerProxy.debug(TRAFFIC, "Sending order:{}", inOrder);  //$NON-NLS-1$
        try {
            if (mToServer == null) {
                throw new ClientInitException(Messages.NOT_CONNECTED_TO_SERVER);
            }
            failIfDisconnected();
            SpringConfig cfg = SpringConfig.getSingleton();
            Collection<OrderModifier> orderModifiers = cfg.getOrderModifiers();
            for(OrderModifier modifier : orderModifiers) {
                modifier.modify(inOrder);
            }
            mToServer.convertAndSend(new OrderEnvelope(inOrder,
                                                       getSessionId()));
        } catch (Exception e) {
            ConnectionException exception;
            exception = new ConnectionException(e, new I18NBoundMessage1P(
                    Messages.ERROR_SEND_MESSAGE, ObjectUtils.toString(inOrder)));
            Messages.LOG_ERROR_SEND_EXCEPTION.warn(this, exception,
                    ObjectUtils.toString(inOrder));
            ExceptUtils.interrupt(e);
            exceptionThrown(exception);
            throw exception;
        }
    }

    /**
     * Checks to see if the client is closed and fails if the client
     * is closed.
     *
     * @throws IllegalStateException if the client is closed.
     */
    private void failIfClosed() throws IllegalStateException {
        if(mClosed) {
            throw new IllegalStateException(Messages.CLIENT_CLOSED.getText());
        }
    }

    /**
     * Asserts that the client's connection to the server is alive;
     * fails otherwise.
     *
     * @throws IllegalStateException if the server connection is dead.
     */
    private void failIfDisconnected() throws IllegalStateException {
        if(!isServerAlive()) {
            throw new IllegalStateException
                (Messages.SERVER_CONNECTION_DEAD.getText());
        }
    }

    private ClientContext getServiceContext()
    {
        return mServiceClient.getContext();
    }

    SessionId getSessionId()
    {
        return getServiceContext().getSessionId();
    }

    /**
     * Sets the client parameters value.
     *
     * @param inParameters the client parameters, cannot be null.
     */
    private void setParameters(ClientParameters inParameters) {
        if(inParameters == null) {
            throw new NullPointerException();
        }
        mParameters = inParameters;
    }

    private void stopJms()
    {
        if (mToServer==null) {
            return;
        }
        try {
            if (mTradeMessageListener!=null) {
                mTradeMessageListener.shutdown();
            }
        } catch (Exception ex) {
            SLF4JLoggerProxy.debug
                (this,"Error when closing trade message listener",ex); //$NON-NLS-1$
            ExceptUtils.interrupt(ex);
        } finally {
            try {
                if (mBrokerStatusListener!=null) {
                    mBrokerStatusListener.shutdown();
                }
            } catch (Exception ex) {
                SLF4JLoggerProxy.debug
                    (this,"Error when closing broker status listener",ex); //$NON-NLS-1$
                ExceptUtils.interrupt(ex);
            } finally {
                mToServer = null;
            }
        }
    }

    private void startJms()
        throws JAXBException
    {
        if (mToServer!=null) {
            return;
        } 
        mTradeMessageListener =
            mJmsMgr.getIncomingJmsFactory().registerHandlerTMX
            (new TradeMessageReceiver(),
             JmsUtils.getReplyTopicName(getSessionId()),true);
        mBrokerStatusListener =
            mJmsMgr.getIncomingJmsFactory().registerHandlerBSX
            (new BrokerStatusReceiver(),Service.BROKER_STATUS_TOPIC,true);
        mToServer=mJmsMgr.getOutgoingJmsFactory().createJmsTemplateX
            (Service.REQUEST_QUEUE,false);
   }

    /**
     * Sets the server connection status. If the status changed, the
     * registered callbacks are invoked.
     *
     * @param serverAlive True means the server connection is alive.
     */
    private void setServerAlive(boolean serverAlive)
    {
        if (mServerAlive==serverAlive) {
            return;
        }
        if (serverAlive) {
            try {
                startJms();
            } catch (JAXBException ex) {
                exceptionThrown(new ConnectionException
                                (ex,Messages.ERROR_CREATING_JMS_CONNECTION));
                return;
            }
        } else {
            stopJms();
        }
        mServerAlive=serverAlive;
        notifyServerStatus(isServerAlive());
    }

    private volatile AbstractApplicationContext mContext;
    private volatile JmsManager mJmsMgr;
    private volatile SimpleMessageListenerContainer mTradeMessageListener;
    private volatile SimpleMessageListenerContainer mBrokerStatusListener;
    private volatile JmsOperations mToServer;
    private volatile ClientParameters mParameters;
    private volatile boolean mClosed = false;
    private volatile boolean mServerAlive = false;
    private final Deque<ReportListener> mReportListeners =
            new LinkedList<ReportListener>();
    private final Deque<BrokerStatusListener> mBrokerStatusListeners=
        new LinkedList<BrokerStatusListener>();
    private final Deque<ServerStatusListener> mServerStatusListeners=
        new LinkedList<ServerStatusListener>();
    private final Deque<ExceptionListener> mExceptionListeners =
            new LinkedList<ExceptionListener>();
    private Date mLastConnectTime;
    private final Map<UserID,UserInfo> mUserInfoCache=
        new HashMap<UserID,UserInfo>();
    private final Map<String,String> mUnderlyingToRootCache= new HashMap<String, String>();
    private final Map<String,Collection<String>> mRootToUnderlyingCache=
            new HashMap<String, Collection<String>>();

    private static final long RECONNECT_WAIT_INTERVAL = 30000;

    private volatile org.marketcetera.util.ws.stateful.Client mServiceClient;
    private Service mService;
    private Heart mHeart;

    private static final String TRAFFIC = ClientImpl.class.getPackage().
            getName() + ".traffic";  //$NON-NLS-1$
    private static final String HEARTBEATS = ClientImpl.class.getPackage().
            getName() + ".heartbeats";  //$NON-NLS-1$
}
