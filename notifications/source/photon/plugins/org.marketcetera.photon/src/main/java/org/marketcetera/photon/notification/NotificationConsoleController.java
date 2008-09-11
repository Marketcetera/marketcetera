package org.marketcetera.photon.notification;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.core.notifications.INotificationManager;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.publisher.ISubscriber;

/**
 * Manages the Notification Console.
 * 
 * @author will@marketcetera.com
 */
public class NotificationConsoleController implements IConsoleFactory,
		IConsoleListener, ISubscriber {

	/**
	 * Eclipse console manager
	 */
	private IConsoleManager consoleManager;

	/**
	 * Core notification manager
	 */
	private INotificationManager notificationManager;

	/**
	 * The notification console
	 */
	private MessageConsole console;

	/**
	 * The notification message stream
	 */
	private MessageConsoleStream defaultStream;

	/**
	 * Constructor.
	 */
	public NotificationConsoleController() {
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		consoleManager.addConsoleListener(this);
		notificationManager = NotificationManager.getNotificationManager();
	}

	@Override
	public void openConsole() {
		if (console == null) {
			Messages.NOTIFICATION_CONSOLE_INIT.info(this);
			console = new MessageConsole(Messages.NOTIFICATION_CONSOLE_NAME
					.getText(), null);
			defaultStream = console.newMessageStream();
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { console });
			notificationManager.subscribe(this);
		}
		consoleManager.showConsoleView(console);
	}

	@Override
	public boolean isInteresting(Object inData) {
		return true;
	}

	/**
	 * This implementation prints the data to the Notification Console.
	 * 
	 * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
	 */
	@Override
	public void publishTo(Object inData) {
		defaultStream.println(inData.toString());
	}

	@Override
	public void consolesAdded(IConsole[] consoles) {
		// Do nothing
	}

	/**
	 * This implementation disconnects the Notification Console from the
	 * {@link INotificationManager} if it was removed.
	 * 
	 * @see org.eclipse.ui.console.IConsoleListener#consolesRemoved(org.eclipse.ui.console.IConsole[])
	 */
	@Override
	public void consolesRemoved(IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i] == console) {
				notificationManager.unsubscribe(this);
				defaultStream = null;
				console = null;
				return;
			}
		}
	}

}
