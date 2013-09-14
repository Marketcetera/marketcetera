package org.marketcetera.photon.actions;

import java.beans.ExceptionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;
import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.core.ICredentials;
import org.marketcetera.photon.core.ICredentialsService;
import org.marketcetera.photon.core.ICredentialsService.IAuthenticationHelper;
import org.marketcetera.photon.core.ILogoutService;
import org.marketcetera.photon.ui.ServerStatusIndicator;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Collects user name and password and reconnects Photon to ORS.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ReconnectServerJob extends UIJob {

    private static final AtomicBoolean sScheduled = new AtomicBoolean();

    /**
     * Constructor.
     */
    public ReconnectServerJob() {
        super(Messages.RECONNECT_SERVER_JOB_NAME.getText());
        setUser(true);
        // don't visualize progress for this job since it's modal
        setSystem(true);
    }

    @Override
    public boolean shouldSchedule() {
        // fails if already scheduled
        return sScheduled.compareAndSet(false, true);
    }

    private final static Runnable sClientCloser = new Runnable() {
        @Override
        public void run() {
            if (sScheduled.compareAndSet(false, true)) {
                Lock closeLock = clientLock.writeLock();
                try {
                    closeLock.lockInterruptibly();
                    if(sClient != null) {
                        sClient.close();
                        sClient = null;
                    }
                } catch (Exception e) {
                    // ignore
                } finally {
                    sScheduled.set(false);
                    closeLock.unlock();
                }
            }
        }
    };
    @Override
    public IStatus runInUIThread(IProgressMonitor monitor) {
        try {
            // load connection properties
            ScopedPreferenceStore prefs = PhotonPlugin.getDefault().getPreferenceStore();
            final String url = prefs.getString(PhotonPreferences.JMS_URL);
            final String hostname = prefs.getString(PhotonPreferences.WEB_SERVICE_HOST);
            final int port = prefs.getInt(PhotonPreferences.WEB_SERVICE_PORT);
            final String idPrefix = prefs.getString(PhotonPreferences.ORDER_ID_PREFIX);
            // try to login
            ICredentialsService credentialsService = PhotonPlugin.getDefault().getCredentialsService();
            ILogoutService logoutService = PhotonPlugin.getDefault().getLogoutService();
            logoutService.addLogoutRunnable(sClientCloser);
            boolean success = credentialsService.authenticateWithCredentials(new IAuthenticationHelper() {
                @Override
                public boolean authenticate(ICredentials credentials)
                {
                    final ClientParameters parameters = new ClientParameters(credentials.getUsername(),
                                                                             credentials.getPassword() == null ? null : credentials.getPassword().toCharArray(),
                                                                             url,
                                                                             hostname,
                                                                             port,
                                                                             idPrefix);
                    IRunnableWithProgress op = new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException,InterruptedException
                        {
                             // Invalidate position engine, it will be recreated if trading history is retrieved.
                            PhotonPlugin.getDefault().disposePositionEngine();
                            ServerStatusIndicator.setDisconnected();
                            PhotonPlugin.getDefault().setSessionStartTime(null);
                            // connect
                            Lock reconnectLock = clientLock.writeLock();
                            try {
                                reconnectLock.lockInterruptibly();
                                sClient = ClientManager.getManagerInstance().getInstance(parameters.getParametersSpec());
                                // if already initialized, reconnect
                                if(sClient != null) {
                                    sClient.reconnect();
                                } else {
                                    // first time initialization
                                    sClient = ClientManager.getManagerInstance().init(parameters);
                                    // add listeners
                                    sClient.addExceptionListener(new ExceptionListener() {
                                        @Override
                                        public void exceptionThrown(Exception e) {
                                            // When disconnected, client sends continual notifications, so we want to avoid cluttering the console.
                                            if(getMessage(e) != org.marketcetera.client.Messages.ERROR_HEARTBEAT_FAILED) {
                                                PhotonPlugin.getMainConsoleLogger().error(Messages.CLIENT_EXCEPTION.getText(),
                                                                                          e);
                                            }
                                        }
                                        private I18NMessage getMessage(Exception e) {
                                            if(e instanceof ConnectionException) {
                                                I18NBoundMessage bound = ((ConnectionException)e).getI18NBoundMessage();
                                                if(bound != null) {
                                                    return bound.getMessage();
                                                }
                                            }
                                            return null;
                                        }
                                    });
                                    ServerNotificationListener serverNotificationListener = new ServerNotificationListener();
                                    // Simulate initial connection notification that we missed because it was issued during initialization, above.
                                    serverNotificationListener.receiveServerStatus(true);
                                    sClient.addServerStatusListener(serverNotificationListener);
                                    sClient.addReportListener(PhotonPlugin.getDefault().getPhotonController());
                                    sClient.addBrokerStatusListener(new BrokerNotificationListener());
                                }
                                // Refresh Broker Status
                                try {
                                    asyncUpdateBrokers(sClient.getBrokersStatus());
                                } catch (ConnectionException e) {
                                    throw new InvocationTargetException(e);
                                }
                            } catch (ConnectionException e) {
                                throw new InvocationTargetException(e);
                            } catch (ClientInitException e) {
                                throw new InvocationTargetException(e);
                            } finally {
                                reconnectLock.unlock();
                            }
                        }
                    };
                    try {
                        new ProgressMonitorDialog(getDisplay().getActiveShell()).run(true, false, op);
                        // CD 20130820 commented out in lieu of replacing the call below in receiveServerStatus (one or the other, not both)
//                        new RetrieveTradingHistoryJob().schedule();
                        return true;
                    } catch (InterruptedException e) {
                        // Intentionally not restoring the interrupt status since this is the main UI thread where it will be ignored.
                        Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED.error(ReconnectServerJob.this, e);
                        return false;
                    } catch (InvocationTargetException e) {
                        Throwable realException = e.getTargetException();
                        String message = realException.getLocalizedMessage();
                        if (message == null) {
                            message = Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED.getText();
                        }
                        MessageDialog.openError(getDisplay().getActiveShell(),
                                                Messages.RECONNECT_SERVER_JOB_ERROR_DIALOG_TITLE.getText(),
                                                message);
                        Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED.error(ReconnectServerJob.this,
                                                                              realException);
                        return false;
                    }
                }
            });
            return success ? Status.OK_STATUS : Status.CANCEL_STATUS;
        } finally {
            sScheduled.set(false);
        }
    }

    private static void asyncUpdateBrokers(final BrokersStatus brokersStatus) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                BrokerManager.getCurrent().setBrokersStatus(brokersStatus);
            }
        });
    }
    /**
     * Handles broker status updates.
     */
    @ClassVersion("$Id$")
    static final class BrokerNotificationListener
            implements BrokerStatusListener
    {
        @Override
        public void receiveBrokerStatus(final BrokerStatus status)
        {
            try {
                I18NMessage0P subject;
                I18NMessage1P details;
                if (status.getLoggedOn()) {
                    subject = Messages.BROKER_NOTIFICATION_BROKER_AVAILABLE;
                    details = Messages.BROKER_NOTIFICATION_BROKER_AVAILABLE_DETAILS;
                } else {
                    subject = Messages.BROKER_NOTIFICATION_BROKER_UNAVAILABLE;
                    details = Messages.BROKER_NOTIFICATION_BROKER_UNAVAILABLE_DETAILS;
                }
                NotificationManager.getNotificationManager().publish(Notification.high(subject.getText(),
                                                                                       details.getText(Messages.BROKER_LABEL_PATTERN.getText(status.getName(),
                                                                                                                                             status.getId())),
                                                                                       getClass().getName()));
                Lock subscribeLock = clientLock.readLock();
                try {
                    subscribeLock.lockInterruptibly();
                    asyncUpdateBrokers(sClient.getBrokersStatus());
                } finally {
                    subscribeLock.unlock();
                }
            } catch (Exception e) {
                Messages.BROKER_NOTIFICATION_BROKER_ERROR_OCCURRED.error(this,
                                                                         e,
                                                                         status);
            }
        }
    }

    /**
     * Handles server status updates.
     */
    @ClassVersion("$Id$")
    static final class ServerNotificationListener
            implements ServerStatusListener
    {
        @Override
        public void receiveServerStatus(boolean status)
        {
            try {
                String text;
                if (status) {
                    ServerStatusIndicator.setConnected();
                    text = Messages.SERVER_NOTIFICATION_SERVER_ALIVE.getText();
                    // CD 20120915 Fix rolled back due to performance problems for high
                    //  volume installations
                    // CD 20130820 Replaced (still vulnerable to high-volume problems)
                    new RetrieveTradingHistoryJob().schedule();
                } else {
                    ServerStatusIndicator.setDisconnected();
                    text = Messages.SERVER_NOTIFICATION_SERVER_DEAD.getText();
                }
                // notifications are not necessary if the reconnect job is
                // running
                if (!sScheduled.get()) {
                    NotificationManager.getNotificationManager()
                            .publish(
                                    Notification.high(text, text, getClass()
                                            .getName()));
                }
            } catch (Exception e) {
                Messages.SERVER_NOTIFICATION_SERVER_ERROR_OCCURRED.error(this,
                        e);
            }
        }
    }
    /**
     * client connect common to all reconnect server job instances, may be <code>null</code>
     */
    @GuardedBy("clientLock")
    private static Client sClient;
    /**
     * guards access to the client object
     */
    private static final ReadWriteLock clientLock = new ReentrantReadWriteLock();
}
