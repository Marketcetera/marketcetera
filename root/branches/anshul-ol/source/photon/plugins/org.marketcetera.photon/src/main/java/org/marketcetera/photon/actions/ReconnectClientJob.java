package org.marketcetera.photon.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWTException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientParameters;
import org.marketcetera.client.BrokerStatusListener;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.photon.BrokerManager;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;
import org.marketcetera.photon.messaging.ClientFeedService;
import org.marketcetera.photon.ui.LoginDialog;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/* $License$ */

/**
 * A job that reconnects the client to the server.
 * 
 * @author gmiller
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 * 
 */
@ClassVersion("$Id$")
public class ReconnectClientJob extends UIJob implements Messages {
	private static final AtomicBoolean sReconnectInProgress = new AtomicBoolean(
			false);
	private final boolean mDisconnectOnly;
	private static final BrokerNotificationListener sBrokerNotificationListener = new BrokerNotificationListener();

	private static class CreateApplicationContextRunnable implements
			IRunnableWithProgress {
		private ClientParameters mParameters;
		private Throwable failure = null;
		private ClientFeedService mService;

		public CreateApplicationContextRunnable(ClientParameters inParameters,
				ClientFeedService inFeedService) {
			mParameters = inParameters;
			mService = inFeedService;
		}

		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				mService.initClient(mParameters);
			} catch (Throwable ex) {
				setFailure(ex);
			}
		}

		public Throwable getFailure() {
			return failure;
		}

		public void setFailure(Throwable failure) {
			this.failure = failure;
		}
	}

	public ReconnectClientJob(String name) {
		this(name, false);
	}

	public ReconnectClientJob(String name, boolean disconnectOnly) {
		super(name);
		this.mDisconnectOnly = disconnectOnly;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (sReconnectInProgress.getAndSet(true))
			return Status.CANCEL_STATUS;

		ServiceTracker clientFeedTracker = null;
		BundleContext bundleContext;

		try {

			bundleContext = PhotonPlugin.getDefault().getBundleContext();
			clientFeedTracker = new ServiceTracker(bundleContext,
					ClientFeedService.class.getName(), null);
			clientFeedTracker.open();

			Logger logger = PhotonPlugin.getDefault().getMainLogger();

			try {
				monitor.beginTask(DISCONNECT_MESSAGE_SERVER.getText(), 2);
				disconnect(clientFeedTracker);
			} catch (Exception ex) {
				logger
						.error(CANNOT_DISCONNECT_FROM_MESSAGE_QUEUE.getText(),
								ex);
			}

			if (!mDisconnectOnly) {
				boolean succeeded = false;

				ClientFeedService feedService = new ClientFeedService();
				ServiceRegistration registration = bundleContext
						.registerService(ClientFeedService.class.getName(),
								feedService, null);
				feedService.setServiceRegistration(registration);

				try {
					monitor.beginTask(RECONNECT_MESSAGE_SERVER.getText(), 3);

					ScopedPreferenceStore prefs = PhotonPlugin.getDefault()
							.getPreferenceStore();
					String url = prefs
							.getString(PhotonPreferences.JMS_URL);
					String hostname = prefs
							.getString(PhotonPreferences.WEB_SERVICE_HOST);
					int port = prefs
							.getInt(PhotonPreferences.WEB_SERVICE_PORT);
					String idPrefix = prefs
							.getString(PhotonPreferences.ORDER_ID_PREFIX);
					Random random = new Random();
					LoginDialog loginDialog = new LoginDialog(null);
					while (true) {
						try {
							if (loginDialog.open() != Window.OK) {
								return Status.CANCEL_STATUS;
							}
						} catch (SWTException ex) {
							logger.error(CANNOT_SHOW_ORS_DIALOG.getText(), ex);
							return Status.CANCEL_STATUS;
						}
						ConnectionDetails details = loginDialog
								.getConnectionDetails();
						ClientParameters parameters = new ClientParameters(
								details.getUserId(),
								details.getPassword() == null ? null : details
										.getPassword().toCharArray(), url,
								hostname, port, idPrefix);
						ProgressMonitorDialog progress = new ProgressMonitorDialog(
								null);
						progress.setCancelable(false);
						CreateApplicationContextRunnable runnable = new CreateApplicationContextRunnable(
								parameters, feedService);
						try {
							progress.run(true, true, runnable);
						} catch (InvocationTargetException ex) {
							logger.error(CANNOT_SHOW_PROGRESS_DIALOG.getText(),
									ex);
							return Status.CANCEL_STATUS;
						} catch (InterruptedException ex) {
							logger.error(CANNOT_SHOW_PROGRESS_DIALOG.getText(),
									ex);
							return Status.CANCEL_STATUS;
						}
						Throwable failure = runnable.getFailure();
						if (failure == null) {
							break;
						}
						Thread.sleep(500 + random.nextInt(1000));
						logger.error(CLIENT_CONNECTION_FAILED.getText(),
								failure);
					}

					monitor.worked(1);
					feedService.afterPropertiesSet();
					monitor.worked(1);

					final BrokersStatus brokersStatus = feedService
							.getClient().getBrokersStatus();
					BrokerManager.getCurrent().setBrokersStatus(
							brokersStatus);
					sBrokerNotificationListener.setService(feedService);
					feedService.getClient().addBrokerStatusListener(
							sBrokerNotificationListener);

					String timeString = PhotonPlugin
							.getDefault()
							.getPreferenceStore()
							.getString(
									PhotonPreferences.TRADING_HISTORY_START_TIME);
					if (StringUtils.isNotEmpty(timeString)) {
						TimeOfDay time = TimeOfDay
								.create(timeString);
						if (time != null) {
							ReportBase[] reports = feedService.getClient()
									.getReportsSince(time.getLastOccurrence());
							for (ReportBase reportBase : reports) {
								PhotonPlugin.getDefault().getTradeReportsHistory()
										.addIncomingMessage(reportBase);
							}
						}
					}

					succeeded = true;
					logger.info(MESSAGE_QUEUE_CONNECTED.getText(url));
				} catch (Throwable t) {
					logger.error(CANNOT_CONNECT_TO_MESSAGE_QUEUE.getText(), t);
				} finally {
					sReconnectInProgress.set(false);
					feedService.setExceptionOccurred(!succeeded);
					registration.setProperties(null);
					monitor.done();
				}
			}
		} finally {
			if (clientFeedTracker != null) {
				clientFeedTracker.close();
			}
		}
		return Status.OK_STATUS;
	}

	public static void disconnect(ServiceTracker clientFeedTracker) {
		ClientFeedService feed = (ClientFeedService) clientFeedTracker
				.getService();

		if (feed != null) {
			try {
				feed.getClient().removeBrokerStatusListener(
						sBrokerNotificationListener);
			} catch (ClientInitException e) {
				// already disconnected
			}
			sBrokerNotificationListener.setService(null);
			ServiceRegistration serviceRegistration;
			if (((serviceRegistration = feed.getServiceRegistration()) != null)) {
				serviceRegistration.unregister();
			}
			feed.disconnect();
		}
	}

	@Override
	public boolean shouldRun() {
		return !sReconnectInProgress.get();
	}

	@Override
	public boolean shouldSchedule() {
		return !sReconnectInProgress.get();
	}

	/**
	 * Handles broker status updates.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since $Release$
	 */
	@ClassVersion("$Id$")
	static class BrokerNotificationListener implements
			BrokerStatusListener {

		private ClientFeedService mService;

		/**
		 * Set the service to use to receive broker statuses.
		 * 
		 * @param service
		 *            the service
		 */
		void setService(ClientFeedService service) {
			mService = service;
		}

		@Override
		public void receiveBrokerStatus(final BrokerStatus status) {
			if (mService == null) {
				SLF4JLoggerProxy.error(this, new IllegalStateException());
				return;
			}
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
								getClass()));
				final BrokersStatus brokersStatus = mService
						.getClient().getBrokersStatus();
				PlatformUI.getWorkbench().getDisplay().asyncExec(
						new Runnable() {
							@Override
							public void run() {
								BrokerManager.getCurrent().setBrokersStatus(
										brokersStatus);
							}
						});
			} catch (Throwable e) {
				Messages.BROKER_NOTIFICATION_BROKER_ERROR_OCCURRED
						.getText(status);
			}
		}
	}

}
