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
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the Notification Console.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public final class NotificationConsoleController implements IConsoleFactory,
		IConsoleListener, ISubscriber {

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
	 * The notification message stream
	 */
	private MessageConsoleStream mDefaultStream;

	/**
	 * Constructor.
	 */
	public NotificationConsoleController() {
		mConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
		mConsoleManager.addConsoleListener(this);
		mNotificationManager = NotificationManager.getNotificationManager();
	}

	@Override
	public void openConsole() {
		if (mConsole == null) {
			Messages.NOTIFICATION_CONSOLE_INIT.info(this);
			mConsole = new MessageConsole(Messages.NOTIFICATION_CONSOLE_NAME
					.getText(), null);
			mDefaultStream = mConsole.newMessageStream();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { mConsole });
			mNotificationManager.subscribe(this);
		}
		mConsoleManager.showConsoleView(mConsole);
	}

	@Override
	public boolean isInteresting(final Object inData) {
		return inData instanceof INotification;
	}

	/**
	 * This implementation prints the data to the Notification Console.
	 * 
	 * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
	 */
	@Override
	public void publishTo(final Object inData) {
		mDefaultStream.println(inData.toString());
	}

	@Override
	public void consolesAdded(final IConsole[] consoles) {
		// Do nothing
	}

	/**
	 * This implementation disconnects the Notification Console from the
	 * {@link INotificationManager} if it was removed.
	 * 
	 * @see org.eclipse.ui.console.IConsoleListener#consolesRemoved(org.eclipse.ui.console.IConsole[])
	 */
	@Override
	public void consolesRemoved(final IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i] == mConsole) {
				mNotificationManager.unsubscribe(this);
				mDefaultStream = null;
				mConsole = null;
				return;
			}
		}
	}

}
