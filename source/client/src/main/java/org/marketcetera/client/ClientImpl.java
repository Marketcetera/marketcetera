package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.log.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.trade.*;
import org.marketcetera.core.MSymbol;
import org.marketcetera.client.dest.DestinationsStatus;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.lang.ObjectUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.Deque;
import java.math.BigInteger;
import java.beans.ExceptionListener;

/* $License$ */
/**
 * The implementation of Client that connects to the server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    public ReportBase[] getReportsSince(Date inDate)
            throws ConnectionException {
        failIfClosed();
        throw new UnsupportedOperationException();
    }

    @Override
    public BigInteger getPositionAsOf(Date inDate, MSymbol inSymbol)
            throws ConnectionException {
        failIfClosed();
        throw new UnsupportedOperationException();
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
            mParameters = inParameters;
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
        return mParameters;
    }

    @Override
    public Date getLastConnectTime() {
        failIfClosed();
        return mLastConnectTime;
    }

    @Override
    public DestinationsStatus getDestinationsStatus() throws ConnectionException {
        failIfClosed();
        throw new UnsupportedOperationException();
    }

    ClientImpl(ClientParameters inParameters) throws ConnectionException {
        mParameters = inParameters;
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
                            "jms.xml"},  //$NON-NLS-1$
                    parentCtx);
            ctx.registerShutdownHook();
            MessagingDelegate delegate = (MessagingDelegate) ctx.getBean("delegate",  //$NON-NLS-1$
                    MessagingDelegate.class);
            setDelegate(delegate);
            setContext(ctx);
            ctx.start();

            // TODO: Server host and port must come from parameters.
            // AppId must also come from params (or maybe auto-set to
            // module name).
            org.marketcetera.util.ws.stateful.Client client=new
                org.marketcetera.util.ws.stateful.Client
                (new AppId("ORSClient"));
            client.login(mParameters.getUsername(),
                         mParameters.getPassword());
            Service service=client.getService(Service.class);
        } catch (Throwable t) {
            ExceptUtils.interrupt(t);
            throw new ConnectionException(t, new I18NBoundMessage2P(
                    Messages.ERROR_CONNECT_TO_SERVER, mParameters.getURL(),
                    mParameters.getUsername()));
        }
        mLastConnectTime = new Date();
    }

    private void setContext(AbstractApplicationContext inContext) {
        mContext = inContext;
    }

    private void convertAndSend(Order inOrder) throws ConnectionException {
        failIfClosed();
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

    private volatile AbstractApplicationContext mContext;
    private volatile MessagingDelegate mDelegate;
    private volatile ClientParameters mParameters;
    private volatile boolean mClosed = false;
    private final Deque<ReportListener> mReportListeners =
            new LinkedList<ReportListener>();
    private final Deque<ExceptionListener> mExceptionListeners =
            new LinkedList<ExceptionListener>();
    private Date mLastConnectTime;
    private static final String TRAFFIC = ClientImpl.class.getPackage().getName() + ".traffic";  //$NON-NLS-1$
}
