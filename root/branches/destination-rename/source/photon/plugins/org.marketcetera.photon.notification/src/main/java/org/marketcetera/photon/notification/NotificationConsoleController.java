package org.marketcetera.photon.notification;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the Notification Console.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #isInteresting(Object)} - to filter notifications</li>
 * <li>{@link #format(Object)} - to customize formatting of notifications</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class NotificationConsoleController implements IConsoleFactory,
		IConsoleListener {

	/**
	 * Eclipse console manager
	 */
	private final IConsoleManager mConsoleManager;

	/**
	 * Core notification manager
	 */
	private final INotificationManager mNotificationManager;

	/**
	 * The notification console
	 */
	private MessageConsole mConsole;

	/**
	 * The current notification subscriber
	 */
	private InternalSubscriber mSubscriber;

	/**
	 * Constructor.
	 */
	public NotificationConsoleController() {
		mConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
		mConsoleManager.addConsoleListener(this);
		mNotificationManager = NotificationPlugin.getDefault()
				.getNotificationManager();
	}

	/**
	 * This implementation of {@link IConsoleFactory#openConsole()} shows the
	 * Notification Console.
	 * 
	 * This method is called by the Eclipse framework when a user selects the
	 * action associated to this {@link IConsoleFactory}.
	 */
	@Override
	public final void openConsole() {
		if (mConsole == null) {
			Messages.NOTIFICATION_CONSOLE_INIT.info(this);
			mConsole = new MessageConsole(Messages.NOTIFICATION_CONSOLE_NAME
					.getText(), null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { mConsole });
			mSubscriber = new InternalSubscriber(mConsole.newMessageStream());
		}
		mConsoleManager.showConsoleView(mConsole);
	}

	/**
	 * Determines if the data should be published to the Notification Console.
	 * This default implementation returns <code>true</code> for all
	 * {@link INotification} <code>inData</code>.
	 * 
	 * Subclasses may override to provide custom filtering.
	 */
	protected boolean isInteresting(final Object inData) {
		return inData instanceof INotification;
	}

	/**
	 * Formats the notification object into a String. Default implementation
	 * returns {@link Object#toString()}.
	 * 
	 * Subclasses may override to provide different formatting.
	 * 
	 * Note this will only be called for <code>inData</code> that are considered
	 * "interesting". See {@link #isInteresting(Object)}.
	 * 
	 * @param inData
	 *            the object to format
	 * @return a string representation of <code>inData</code> to write to the
	 *         Notification Console, must not be null
	 */
	protected String format(final Object inData) {
		return inData.toString();
	}

	/**
	 * This implementation of {@link IConsoleListener#consolesAdded(IConsole[])}
	 * does nothing.
	 */
	@Override
	public final void consolesAdded(final IConsole[] consoles) {
		// Do nothing
	}

	/**
	 * This implementation of
	 * {@link IConsoleListener#consolesRemoved(IConsole[])} cleans up the
	 * Notification Console if it was removed.
	 */
	@Override
	public final void consolesRemoved(final IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i] == mConsole) {
				mSubscriber.dispose();
				mConsole = null;
				return;
			}
		}
	}

	/**
	 * Internal helper class to subscribe to notifications and handle
	 * synchronization.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 0.8.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private final class InternalSubscriber implements ISubscriber {

		/**
		 * The stream to publish notifications
		 */
		private MessageConsoleStream mStream;

		/**
		 * Constructor.
		 * 
		 * @param stream
		 *            stream to publish notifications
		 */
		private InternalSubscriber(MessageConsoleStream stream) {
			mStream = stream;
			mNotificationManager.subscribe(this);
		}

		/**
		 * This implementation of {@link ISubscriber#isInteresting(Object)}
		 * returns true if the Notification Console is available and the parent
		 * {@link NotificationConsoleController#isInteresting(Object)} returns
		 * true.
		 */
		@Override
		public synchronized boolean isInteresting(Object inData) {
			return mStream != null
					&& NotificationConsoleController.this.isInteresting(inData);
		}

		/**
		 * This implementation of {@link ISubscriber#publishTo(Object)} writes
		 * the object to the Notification Console if available. The object is
		 * formatted to a String using the parent
		 * {@link NotificationConsoleController#format(Object)}.
		 */
		@Override
		public synchronized void publishTo(Object inData) {
			if (mStream != null)
				mStream.println(format(inData));
		}

		/**
		 * Stops subscriber from writing to notification stream. After this
		 * method has been called, this object should no longer be used.
		 */
		private synchronized void dispose() {
			mNotificationManager.unsubscribe(this);
			mStream = null;
		}

	}

}
