package org.marketcetera.photon.actions;

import java.beans.ExceptionListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.ui.LoginDialog;
import org.marketcetera.photon.ui.ServerStatusIndicator;
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
	public IStatus runInUIThread(IProgressMonitor monitor) {
		// load connection properties
		ScopedPreferenceStore prefs = PhotonPlugin.getDefault()
				.getPreferenceStore();
		String url = prefs.getString(PhotonPreferences.JMS_URL);
		String hostname = prefs.getString(PhotonPreferences.WEB_SERVICE_HOST);
		int port = prefs.getInt(PhotonPreferences.WEB_SERVICE_PORT);
		String idPrefix = prefs.getString(PhotonPreferences.ORDER_ID_PREFIX);

		// try to login
		LoginDialog loginDialog = new LoginDialog(getDisplay().getActiveShell());
		while (loginDialog.open() == Window.OK) {
			ConnectionDetails details = loginDialog.getConnectionDetails();
			final ClientParameters parameters = new ClientParameters(details
					.getUserId(), details.getPassword() == null ? null
					: details.getPassword().toCharArray(), url, hostname, port,
					idPrefix);
			IRunnableWithProgress op = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					// close previous connection if it exists
					try {
						ClientManager.getInstance().close();
					} catch (ClientInitException e) {
						// already closed
					}
					ServerStatusIndicator.setDisconnected();
					PhotonPlugin.getDefault().setSessionStartTime(null);

					// connect
					try {
						ClientManager.init(parameters);
					} catch (ConnectionException e) {
						throw new InvocationTargetException(e);
					} catch (ClientInitException e) {
						throw new InvocationTargetException(e);
					}

					Client client;
					try {
						client = ClientManager.getInstance();
					} catch (ClientInitException e) {
						// should not happen since ClientManager.init returned
						// successfully
						assert false;
						throw new InvocationTargetException(e);
					}
					ServerStatusIndicator.setConnected();

					// add listeners
					client.addExceptionListener(new ExceptionListener() {
						@Override
						public void exceptionThrown(Exception e) {
							PhotonPlugin.getMainConsoleLogger().error(
									Messages.CLIENT_EXCEPTION.getText(), e);
							ServerStatusIndicator.setError();
						}
					});
					client.addReportListener(PhotonPlugin.getDefault()
							.getPhotonController());
					client
							.addBrokerStatusListener(new BrokerNotificationListener(
									client));
					try {
						asyncUpdateBrokers(client.getBrokersStatus());
					} catch (ConnectionException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
			try {
				new ProgressMonitorDialog(getDisplay().getActiveShell()).run(
						true, false, op);
				new RetrieveTradingHistoryJob().schedule();
				return Status.OK_STATUS;
			} catch (InterruptedException e) {
				// Intentionally not restoring the interrupt status since this
				// is the main UI thread where it will be ignored
				Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED.error(this, e);
				return Status.CANCEL_STATUS;
			} catch (InvocationTargetException e) {
				Throwable realException = e.getTargetException();
				String message = realException.getLocalizedMessage();
				if (message == null) {
					message = Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED
							.getText();
				}
				MessageDialog.openError(getDisplay().getActiveShell(),
						Messages.RECONNECT_SERVER_JOB_ERROR_DIALOG_TITLE
								.getText(), message);
				Messages.RECONNECT_SERVER_JOB_CONNECTION_FAILED.error(this,
						realException);
			}
		}
		return Status.CANCEL_STATUS;
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
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.5.0
	 */
	@ClassVersion("$Id$")
	static final class BrokerNotificationListener implements BrokerStatusListener {

		private final Client mClient;

		/**
		 * @param client Client instance to use to refresh brokers status
		 */
		public BrokerNotificationListener(Client client) {
			mClient = client;
		}

		@Override
		public void receiveBrokerStatus(final BrokerStatus status) {
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
				NotificationManager.getNotificationManager().publish(
						Notification.high(subject.getText(), details
								.getText(Messages.BROKER_LABEL_PATTERN.getText(
										status.getName(), status.getId())),
								getClass().getName()));
				asyncUpdateBrokers(mClient.getBrokersStatus());
			} catch (Exception e) {
				Messages.BROKER_NOTIFICATION_BROKER_ERROR_OCCURRED.error(this,
						e, status);
			}
		}
	}

}
