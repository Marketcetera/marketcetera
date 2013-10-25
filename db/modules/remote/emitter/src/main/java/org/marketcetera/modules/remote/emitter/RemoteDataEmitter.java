package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/* $License$ */
/**
 * A class that abstracts out receiving and publishing of data
 * from the remote receiver so that it can be reused without having
 * to go via the module framework.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class RemoteDataEmitter {
    /**
     * Creates an instance that connects to the specified remote source of data
     * and publishes the received data and notifications to the supplied
     * adapter instance.
     * 
     * @param inURL the URL of remote source of data. Cannot be null.
     * @param inUsername the user name to authenticate to the remote source. Cannot be null.
     * @param inPassword the password to authenticate to the remote source. Cannot be null.
     * @param inAdapter the adapter instance. Cannot be null.
     *
     * @throws RuntimeException if there were errors connecting.
     */
    public RemoteDataEmitter(String inURL, String inUsername, String inPassword,
                             EmitterAdapter inAdapter) {
        if(inURL == null) {
            throw new NullPointerException();
        }
        if(inAdapter == null) {
            throw new NullPointerException();
        }
        if(inUsername == null) {
            throw new NullPointerException();
        }
        if(inPassword == null) {
            throw new NullPointerException();
        }
        mAdapter = inAdapter;
        //Create spring contexts to initialize the broker and messaging topic
        StaticApplicationContext parent = new StaticApplicationContext();
        SpringUtils.addStringBean(parent, "brokerURI", inURL);  //$NON-NLS-1$
        SpringUtils.addStringBean(parent, "username",  //$NON-NLS-1$
                inUsername);
        SpringUtils.addStringBean(parent, "password",  //$NON-NLS-1$
                inPassword);
        parent.refresh();
        mContext  = new ClassPathXmlApplicationContext(new String[]{
                "remote-emitter-jms.xml"}, parent);  //$NON-NLS-1$
        mContext.start();
        MessagingDelegate delegate = (MessagingDelegate) mContext.getBean(
                "delegate", MessagingDelegate.class);  //$NON-NLS-1$
        delegate.setDataEmitter(this);
        //Reset last failure
        setLastFailure(null);
        //Send notification that the module is now connected.
        sendConnectedChanged(false, true);
    }

    /**
     * Closes the connection to the remote source of data.
     * This instance is unusable after this method is invoked.
     * <p>
     * If one needs to reconnect to the remote source, a new instance
     * of this class should be created.
     * <p>
     * This method does not fail if the attempt to close the connection fails.
     * In case of failures, the failures are logged and the method returns
     * silently.
     */
    public synchronized void close() {
        if(mContext == null) {
            return;
        }
        boolean isConnected = getLastFailure() == null;
        try {
            mContext.close();
        } catch (Exception e) {
            //Swallow the exception as it prevents the module from stopping.
            //If the receiver closed the connection from its end
            //this method always fails.
            Messages.LOG_ERROR_CLOSING_CONNECTION.warn(this, e);
        }
        mContext = null;
        if(isConnected) {
            sendConnectedChanged(isConnected, false);
        }
    }

    /**
     * The last failure encountered when receiving data.
     *
     * @return last failure encountered when receiving data.
     */
    public Exception getLastFailure() {
        return mLastFailure;
    }

    /**
     * Returns true if no failures have been encountered when
     * receiving data from the remote source.
     *
     * @return if no failures have been encountered when receiving data
     * from the remote source.
     */
    public boolean isConnected() {
        return mContext != null && mLastFailure == null;
    }

    /**
     * Receives the data received from the remote source.
     * The received data is handed off to the adapter.
     *
     * @param inObject the data received from the remote source.
     */
    void receive(Object inObject) {
        mAdapter.receiveData(inObject);
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
        if(oldConnected != newConnected) {
            sendConnectedChanged(oldConnected, newConnected);
        }
    }

    /**
     * Sends the connection change status to the adapter.
     *
     * @param inOldStatus old status.
     * @param inNewStatus new status.
     */
    private void sendConnectedChanged(boolean inOldStatus, boolean inNewStatus) {
        mAdapter.connectionStatusChanged(inOldStatus, inNewStatus);
    }

    private volatile ClassPathXmlApplicationContext mContext;
    private volatile Exception mLastFailure;
    private final EmitterAdapter mAdapter;
}
