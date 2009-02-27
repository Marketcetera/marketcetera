package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.log.*;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.trade.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.lang.ObjectUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Deque;
import java.util.Arrays;
import java.math.BigDecimal;
import java.beans.ExceptionListener;

/* $License$ */
/**
 * The implementation of Client that connects to the server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
class ClientImpl implements Client {

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
                mContext.close();
            } catch (Exception e) {
                SLF4JLoggerProxy.debug(this,
                        "Error when closing connection to server", e);  //$NON-NLS-1$
                ExceptUtils.interrupt(e);
            }
        mContext = null;
        mDelegate = null;
        }
        setContext(null);
        setDelegate(null);
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
        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        try {
            //Set the thread context classloader so that spring classes,
            //that are loaded by the system classloader can find
            //jms.xml file, when the client module is loaded by a classloader
            //that is not the system classloader.
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
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
                            "jms.xml"},  //$NON-NLS-1$
                    parentCtx);
            ctx.registerShutdownHook();
            MessagingDelegate delegate = (MessagingDelegate) ctx.getBean("delegate",  //$NON-NLS-1$
                    MessagingDelegate.class);
            setDelegate(delegate);
            setContext(ctx);
            ctx.start();

            mServiceClient = new org.marketcetera.util.ws.stateful.Client
                (mParameters.getHostname(), mParameters.getPort(),
                        new AppId("Client"));  //$NON-NLS-1$
            mServiceClient.login(mParameters.getUsername(),
                                 mParameters.getPassword());
            mService = mServiceClient.getService(Service.class);
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
        } finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
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
            MessagingDelegate delegate = getDelegate();
            delegate.convertAndSend(inOrder);
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

    private MessagingDelegate getDelegate() throws ClientInitException {
        if (mDelegate == null) {
            throw new ClientInitException(Messages.NOT_CONNECTED_TO_SERVER);
        }
        return mDelegate;
    }

    private void setDelegate(MessagingDelegate inDelegate) {
        mDelegate = inDelegate;
        if (inDelegate !=null) {
            mDelegate.setClientImpl(this);
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
    private volatile MessagingDelegate mDelegate;
    private volatile ClientParameters mParameters;
    private volatile boolean mClosed = false;
    private final Deque<ReportListener> mReportListeners =
            new LinkedList<ReportListener>();
    private final Deque<BrokerStatusListener> mBrokerStatusListeners=
        new LinkedList<BrokerStatusListener>();
    private final Deque<ExceptionListener> mExceptionListeners =
            new LinkedList<ExceptionListener>();
    private Date mLastConnectTime;

    private org.marketcetera.util.ws.stateful.Client mServiceClient;
    private Service mService;

    private static final String TRAFFIC = ClientImpl.class.getPackage().
            getName() + ".traffic";  //$NON-NLS-1$
}
