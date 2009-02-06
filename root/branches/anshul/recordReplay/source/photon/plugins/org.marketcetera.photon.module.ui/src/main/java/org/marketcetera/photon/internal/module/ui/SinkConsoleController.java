package org.marketcetera.photon.internal.module.ui;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the Sink Console.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #format(Object)} - to customize formatting of data</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: SinkConsoleController.java 10229 2008-12-09 21:48:48Z klim $
 * @since 1.0.0
 */
@ClassVersion("$Id: SinkConsoleController.java 10229 2008-12-09 21:48:48Z klim $")//$NON-NLS-1$
public class SinkConsoleController implements IConsoleFactory, IConsoleListener {

	/**
	 * Eclipse console manager
	 */
	private final IConsoleManager mConsoleManager;

	/**
	 * Core notification manager
	 */
	private final ModuleManager mModuleManager;

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
	public SinkConsoleController() {
		mConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
		mConsoleManager.addConsoleListener(this);
		mModuleManager = ModuleSupport.getModuleManager();
	}

	/**
	 * This implementation of {@link IConsoleFactory#openConsole()} shows the
	 * Sink Console.
	 * 
	 * This method is called by the Eclipse framework when a user selects the
	 * action associated to this {@link IConsoleFactory}.
	 */
	@Override
	public final void openConsole() {
		if (mConsole == null) {
			mConsole = new MessageConsole(Messages.SINK_CONSOLE_NAME.getText(), null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { mConsole });
			mSubscriber = new InternalSubscriber(mConsole.newMessageStream());
		}
		mConsoleManager.showConsoleView(mConsole);
	}

	/**
	 * Formats the notification object into a String.
	 * 
	 * Subclasses may override to provide different formatting.
	 * 
	 * @param inFlowID
	 *            the data flow ID
	 * @param inData
	 *            the data flow data object 
	 * @return a string representation of <code>inData</code> to write to the
	 *         Sink Console, must not be null
	 */
	protected String format(final Object inFlowID, final Object inData) {
		return inFlowID.toString() + " " + inData.toString(); //$NON-NLS-1$
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
	 * Sink Console if it was removed.
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
	 * Internal helper class to subscribe to sink data flows and handle
	 * synchronization.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id: SinkConsoleController.java 10229 2008-12-09 21:48:48Z klim $
	 * @since 1.0.0
	 */
	@ClassVersion("$Id: SinkConsoleController.java 10229 2008-12-09 21:48:48Z klim $")//$NON-NLS-1$
	private final class InternalSubscriber implements SinkDataListener {

		/**
		 * The stream to publish notifications
		 */
		private MessageConsoleStream mStream;

		/**
		 * Constructor.
		 * 
		 * @param stream
		 *            stream to publish data
		 */
		private InternalSubscriber(MessageConsoleStream stream) {
			mStream = stream;
			mModuleManager.addSinkListener(this);
		}

		/**
		 * This implementation of
		 * {@link SinkDataListener#receivedData(DataFlowID, Object)} writes the
		 * object to the console stream. The object is formatted to a String
		 * using the parent {@link SinkConsoleController#format(Object)}.
		 */
		@Override
		public void receivedData(DataFlowID inFlowID, Object inData) {
			mStream.println(format(inFlowID, inData));
		}

		/**
		 * Stops subscriber from writing to console stream. After this method
		 * has been called, this object should no longer be used.
		 */
		private synchronized void dispose() {
			mModuleManager.removeSinkListener(this);
			mStream = null;
		}

	}

}
