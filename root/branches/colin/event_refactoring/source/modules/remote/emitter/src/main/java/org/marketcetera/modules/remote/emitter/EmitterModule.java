package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.module.*;

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
        implements DataEmitter, EmitterModuleMXBean, NotificationEmitter, EmitterAdapter {

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
        return getState().isStarted() && mDataEmitter != null && mDataEmitter.isConnected();
    }

    @Override
    public String getLastFailure() {
        if(mDataEmitter != null && mDataEmitter.getLastFailure() != null) {
             return mDataEmitter.getLastFailure().toString();
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
            mDataEmitter = new RemoteDataEmitter(url, getUsername(), mPassword, this);
        } catch(Exception e) {
            throw new ModuleException(e, Messages.ERROR_STARTING_MODULE);
        }
    }

    @Override
    protected void preStop() throws ModuleException {
        mDataEmitter.close();
        mDataEmitter = null;
    }

    /**
     * This method is invoked when an object is received from the remote
     * receiver.
     * <p>
     * The received object is delivered to all the data flows sequentially.
     *
     * @param inObject the received object.
     */
    @Override
    public void receiveData(Object inObject) {
        for(DataEmitterSupport support: mRequests.values()) {
            support.send(inObject);
        }
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
     * Sends an attribute change notification for change in the
     * {@link #isConnected()} value.
     *
     * @param inOldValue the old attribute value.
     * @param inNewValue the new attribute value.
     */
    @Override
    public void connectionStatusChanged(boolean inOldValue, boolean inNewValue) {
        if (getState().isStarted() ||
                getState() == ModuleState.STARTING ||
                getState() == ModuleState.STOPPING) {
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
    }

    private volatile String mURL;
    private volatile String mUsername;
    private volatile String mPassword;

    private volatile RemoteDataEmitter mDataEmitter;
    
    private final AtomicLong mSequence = new AtomicLong();
    private final NotificationBroadcasterSupport mNotifySupport =
            new NotificationBroadcasterSupport(new MBeanNotificationInfo(
                    new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE},
                    AttributeChangeNotification.class.getName(),
                    Messages.ATTRIB_CHANGE_NOTIFICATION.getText()));
    

    private final Map<RequestID, DataEmitterSupport> mRequests =
                    new ConcurrentHashMap<RequestID, DataEmitterSupport>();
}
