package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.log.*;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.trade.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.client.users.UserInfo;
import org.marketcetera.client.config.SpringConfig;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.JmsUtils;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.client.jms.OrderEnvelope;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.apache.commons.lang.ObjectUtils;

import java.util.*;
import java.util.HashMap;
import java.math.BigDecimal;
import java.beans.ExceptionListener;

import javax.jms.JMSException;

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
    public ReportBase[] getReportsSince
        (Date inDate)
        throws ConnectionException
    {
        failIfClosed();
        try {
            ReportBaseImpl[] reports = mService.getReportsSince(getServiceContext(),inDate);
            return reports == null ? new ReportBase[0] : reports;
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public BigDecimal getPositionAsOf
        (Date inDate,
         MSymbol inSymbol)
        throws ConnectionException
    {
        failIfClosed();
        try {
            return mService.getPositionAsOf
                (getServiceContext(),inDate,inSymbol);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public Map<MSymbol, BigDecimal> getPositionsAsOf
        (Date inDate)
        throws ConnectionException
    {
        failIfClosed();
        try {
            return mService.getPositionsAsOf
                (getServiceContext(),inDate).getMap();
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
    }

    @Override
    public BrokersStatus getBrokersStatus()
        throws ConnectionException
    {
        failIfClosed();
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
        if (useCache) {
            synchronized (mUserInfoCache) {
                result=mUserInfoCache.get(id);
                if (result!=null) {
                    return result;
                }
            }
        }
        try {
            result=mService.getUserInfo(getServiceContext(),id);
        } catch (RemoteException ex) {
            throw new ConnectionException(ex,Messages.ERROR_REMOTE_EXECUTION);
        }
        synchronized (mUserInfoCache) {
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
            } else {
                notifyCancelReject((OrderCancelReject)inReport);
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
        return mService.getNextOrderID(getServiceContext());
    }

    private void internalClose() {
        if (mContext != null) {
            try {
                mTradeMessageListener.destroy();
            } catch (Exception ex) {
                SLF4JLoggerProxy.debug
                    (this,"Error when closing trade message listener",ex); //$NON-NLS-1$
                ExceptUtils.interrupt(ex);
            } finally {
                try {
                    mBrokerStatusListener.destroy();
                } catch (Exception ex) {
                    SLF4JLoggerProxy.debug
                        (this,"Error when closing broker status listener",ex); //$NON-NLS-1$
                    ExceptUtils.interrupt(ex);
                } finally {
                    try {
                        mContext.close();
                    } catch (Exception ex) {
                        SLF4JLoggerProxy.debug
                            (this,"Error when closing context",ex); //$NON-NLS-1$
                        ExceptUtils.interrupt(ex);
                    } finally {
                        try {
                            mServiceClient.logout();
                        } catch (Exception ex) {
                            SLF4JLoggerProxy.debug
                                (this,"Error when closing web service client",ex); //$NON-NLS-1$
                            ExceptUtils.interrupt(ex);
                        } finally {
                            mToServer = null;
                        }
                    }
                }
            }
        }
        setContext(null);
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

            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                    new String[]{
                            "client.xml"},  //$NON-NLS-1$
                    parentCtx);
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
                        new AppId("Client"));  //$NON-NLS-1$
            mServiceClient.login(mParameters.getUsername(),
                                 mParameters.getPassword());
            mService = mServiceClient.getService(Service.class);

            JmsManager jmsMgr=new JmsManager
                (cfg.getIncomingConnectionFactory(),
                 cfg.getOutgoingConnectionFactory(),this);
            mTradeMessageListener =
                jmsMgr.getIncomingJmsFactory().registerHandlerTMX
                (new TradeMessageReceiver(),
                 JmsUtils.getReplyTopicName(getSessionId()),true);
            mBrokerStatusListener =
                jmsMgr.getIncomingJmsFactory().registerHandlerBSX
                (new BrokerStatusReceiver(),Service.BROKER_STATUS_TOPIC,true);
            mToServer = jmsMgr.getOutgoingJmsFactory().createJmsTemplateX
                (Service.REQUEST_QUEUE,false);

            ClientIDFactory idFactory = new ClientIDFactory(
                    mParameters.getIDPrefix(), this);
            idFactory.init();
            Factory.getInstance().setOrderIDFactory(idFactory);
        } catch (Throwable t) {
            ExceptUtils.interrupt(t);
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
        failIfClosed();
        SLF4JLoggerProxy.debug(TRAFFIC, "Sending order:{}", inOrder);  //$NON-NLS-1$
        try {
            if (mToServer == null) {
                throw new ClientInitException(Messages.NOT_CONNECTED_TO_SERVER);
            }
            mToServer.convertAndSend
                (new OrderEnvelope(inOrder,getSessionId()));
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

    private volatile AbstractApplicationContext mContext;
    private volatile SimpleMessageListenerContainer mTradeMessageListener;
    private volatile SimpleMessageListenerContainer mBrokerStatusListener;
    private volatile JmsOperations mToServer;
    private volatile ClientParameters mParameters;
    private volatile boolean mClosed = false;
    private final Deque<ReportListener> mReportListeners =
            new LinkedList<ReportListener>();
    private final Deque<BrokerStatusListener> mBrokerStatusListeners=
        new LinkedList<BrokerStatusListener>();
    private final Deque<ExceptionListener> mExceptionListeners =
            new LinkedList<ExceptionListener>();
    private Date mLastConnectTime;
    private HashMap<UserID,UserInfo> mUserInfoCache=
        new HashMap<UserID,UserInfo>();

    private org.marketcetera.util.ws.stateful.Client mServiceClient;
    private Service mService;

    private static final String TRAFFIC = ClientImpl.class.getPackage().
            getName() + ".traffic";  //$NON-NLS-1$
}
