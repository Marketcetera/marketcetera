package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.module.*;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.management.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

/* $License$ */
/**
 * The remote emitter module that emits data received from the
 * remote receiver module.
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>None</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Start Operation</th><td>Connects to the remote receiver</td></tr>
 * <tr><th>Stop Operation</th><td>Disconnects from remote receiver</td></tr>
 * <tr><th>Management Interface</th><td>{@link EmitterModuleMXBean}</td></tr>
 * <tr><th>MX Notification</th><td>{@link AttributeChangeNotification}
 * whenever {@link #isConnected()} changes. </td></tr>
 * </table>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
class EmitterModule extends Module
        implements DataEmitter, EmitterModuleMXBean, NotificationEmitter {

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException {
        mRequests.put(inSupport.getRequestID(), inSupport);
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        mRequests.remove(inRequestID);
    }

    @Override
    public String getURL() {
        return mURL;
    }

    @Override
    public void setURL(String inURL) {
        failIfStarted(Messages.ILLEGAL_STATE_CHANGE_URL);
        mURL = inURL;
    }

    @Override
    public String getUsername() {
        return mUsername;
    }

    @Override
    public void setUsername(String inUsername) {
        failIfStarted(Messages.ILLEGAL_STATE_CHANGE_USERNAME);
        mUsername = inUsername;
    }

    @Override
    public void setPassword(String inPassword) {
        failIfStarted(Messages.ILLEGAL_STATE_CHANGE_PASSWORD);
        mPassword = inPassword;
    }

    @Override
    public boolean isConnected() {
        return getState().isStarted() && mLastFailure == null;
    }

    @Override
    public String getLastFailure() {
        if(mLastFailure != null) {
            return mLastFailure.toString();
        }
        return null;
    }

    @Override
    public void removeNotificationListener(NotificationListener listener,
                                           NotificationFilter filter,
                                           Object handback)
            throws ListenerNotFoundException {
        mNotifySupport.removeNotificationListener(listener, filter, handback);
    }

    @Override
    public void addNotificationListener(NotificationListener listener,
                                        NotificationFilter filter,
                                        Object handback)
            throws IllegalArgumentException {
        mNotifySupport.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener)
            throws ListenerNotFoundException {
        mNotifySupport.removeNotificationListener(listener);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return mNotifySupport.getNotificationInfo();
    }
    /**
     * Creates an instance.
     *
     * @param inURN the module instance's URN.
     */
    protected EmitterModule(ModuleURN inURN) {
        super(inURN, true);
    }

    @Override
    protected void preStart() throws ModuleException {
        //Check if the broker URL is supplied
        String url = getURL();
        if(url == null) {
            throw new ModuleException(Messages.START_FAIL_NO_URL);
        }
        try {
            //Create spring contexts to initialize the broker and messaging topic
            StaticApplicationContext parent = new StaticApplicationContext();
            SpringUtils.addStringBean(parent, "brokerURI", url);  //$NON-NLS-1$
            SpringUtils.addStringBean(parent, "username",  //$NON-NLS-1$
                    getUsername());
            SpringUtils.addStringBean(parent, "password",  //$NON-NLS-1$
                    mPassword);
            parent.refresh();
            mContext  =
                    new ClassPathXmlApplicationContext(new String[]{
                            "remote-emitter-jms.xml"}, parent);  //$NON-NLS-1$
            mContext.start();
            MessagingDelegate delegate = (MessagingDelegate) mContext.getBean(
                    "delegate", MessagingDelegate.class);  //$NON-NLS-1$
            delegate.setModule(this);
            //Reset last failure
            setLastFailure(null);
            //Send notification that the module is now connected.
            sendConnectedChanged(false, true);
        } catch(Exception e) {
            throw new ModuleException(e, Messages.ERROR_STARTING_MODULE);
        }
    }

    @Override
    protected void preStop() throws ModuleException {
        boolean isConnected = getLastFailure() == null;
        //Stop & destroy the broker.
        try {
            mContext.close();
            setLastFailure(null);
        } catch (Exception e) {
            //Swallow the exception as it prevents the module from stopping.
            //If the receiver closed the connection from its end
            //this method always fails.
            Messages.LOG_ERROR_STOPPING_MODULE.warn(this, e, getURN());
        }
        if(isConnected) {
            sendConnectedChanged(isConnected, false);
        }
    }

    /**
     * This method is invoked when an object is received from the remote
     * receiver.
     * <p>
     * The received object is delivered to all the data flows sequentially.
     *
     * @param inObject the received object.
     */
    void receive(Object inObject) {
        for(DataEmitterSupport support: mRequests.values()) {
            support.send(inObject);
        }
    }

    /**
     * This method is invoked when any failures are encountered receiving
     * messages from the remote receiver.
     * <p>
     * The text of the failure is available via {@link #getLastFailure()}.
     * <p>
     * The a non-null parameter to this method causes {@link #isConnected()}
     * to return false.
     *
     * @param inException the failure encountered.
     */
    void onException(Exception inException) {
        Messages.LOG_ERROR_RECEIVER_CONNECTION.warn(this, inException);
        setLastFailure(inException);
    }

    /**
     * Verifies if the module is not started.
     *
     * @param inMessage the message to use when the module is started.
     *
     * @throws IllegalStateException if the module is started.
     */
    private void failIfStarted(I18NMessage0P inMessage) {
        if(getState().isStarted()) {
            throw new IllegalStateException(inMessage.getText());
        }

    }

    /**
     * Sets the value of the last failure exception and sends out an
     * attribute change notification for {@link #isConnected()} if necessary.
     * <p>
     * It's assumed that this method can only be invoked when the module
     * is started.
     *
     * @param inLastFailure the failure exception, can be null.
     */
    private void setLastFailure(Exception inLastFailure) {
        boolean oldConnected = mLastFailure == null;
        mLastFailure = inLastFailure;
        boolean newConnected = mLastFailure == null;
        if(oldConnected != newConnected && getState().isStarted()) {
            sendConnectedChanged(oldConnected, newConnected);
        }
    }

    /**
     * Sends an attribute change notification for change in the
     * {@link #isConnected()} value.
     *
     * @param inOldValue the old attribute value.
     * @param inNewValue the new attribute value.
     */
    private void sendConnectedChanged(boolean inOldValue, boolean inNewValue) {
        SLF4JLoggerProxy.debug(this, "Sending attrib changed from {} to {}",  //$NON-NLS-1$
                inOldValue, inNewValue);
        mNotifySupport.sendNotification(new AttributeChangeNotification(
                getURN().toString(),
                mSequence.getAndIncrement(),
                System.currentTimeMillis(),
                Messages.ATTRIB_CHANGE_NOTIFICATION.getText(),
                "Connected",  //$NON-NLS-1$
                "boolean",  //$NON-NLS-1$
                inOldValue,
                inNewValue));
    }

    private volatile String mURL;
    private volatile String mUsername;
    private volatile String mPassword;
    private volatile Exception mLastFailure;

    private volatile ClassPathXmlApplicationContext mContext;
    
    private final AtomicLong mSequence = new AtomicLong();
    private final NotificationBroadcasterSupport mNotifySupport =
            new NotificationBroadcasterSupport(new MBeanNotificationInfo(
                    new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                    AttributeChangeNotification.class.getName(),
                    Messages.ATTRIB_CHANGE_NOTIFICATION.getText()));
    

    private final Map<RequestID, DataEmitterSupport> mRequests =
                    new ConcurrentHashMap<RequestID, DataEmitterSupport>();
}
