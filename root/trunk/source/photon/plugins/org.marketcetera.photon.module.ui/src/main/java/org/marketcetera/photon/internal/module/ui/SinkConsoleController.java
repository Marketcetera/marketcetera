package org.marketcetera.photon.internal.module.ui;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.event.LogEvent;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.photon.module.ISinkDataHandler;
import org.marketcetera.photon.module.ISinkDataManager;
import org.marketcetera.photon.module.ModuleSupport;
import org.marketcetera.photon.module.SinkDataHandler;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Manages the Sink Console.
 * 
 * This class may be subclassed to override
 * <ul>
 * <li>{@link #format(Object, Object)} - to customize formatting of data</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class SinkConsoleController implements IConsoleFactory, IConsoleListener {

	/**
	 * Eclipse console manager
	 */
	private final IConsoleManager mConsoleManager;

	/**
	 * Sink data manager
	 */
	private final ISinkDataManager mSinkDataManager;

	/**
	 * The sink console
	 */
	private MessageConsole mConsole;

	/**
	 * The current handler
	 */
	private InternalHandler mHandler;

	/**
	 * Constructor.
	 */
	public SinkConsoleController() {
		mConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
		mConsoleManager.addConsoleListener(this);
		mSinkDataManager = ModuleSupport.getSinkDataManager();
	}

	/**
	 * This implementation of {@link IConsoleFactory#openConsole()} shows the Sink Console.
	 * 
	 * This method is called by the Eclipse framework when a user selects the action associated to
	 * this {@link IConsoleFactory}.
	 */
	@Override
	public final void openConsole() {
		if (mConsole == null) {
			mConsole = new MessageConsole(Messages.SINK_CONSOLE_NAME.getText(), null);
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { mConsole });
			mHandler = new InternalHandler(mConsole.newMessageStream());
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
	 * @return a string representation of <code>inData</code> to write to the Sink Console, must not
	 *         be null
	 */
	protected String format(final Object inFlowID, final Object inData) {
		return inFlowID.toString() + " " + inData.toString(); //$NON-NLS-1$
	}

	/**
	 * This implementation of {@link IConsoleListener#consolesAdded(IConsole[])} does nothing.
	 */
	@Override
	public final void consolesAdded(final IConsole[] consoles) {
		// Do nothing
	}

	/**
	 * This implementation of {@link IConsoleListener#consolesRemoved(IConsole[])} cleans up the
	 * Sink Console if it was removed.
	 */
	@Override
	public final void consolesRemoved(final IConsole[] consoles) {
		for (int i = 0; i < consoles.length; i++) {
			if (consoles[i] == mConsole) {
				mHandler.dispose();
				mConsole = null;
				return;
			}
		}
	}

	/**
	 * Internal helper class to register default handling of sink data and manage synchronization.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")//$NON-NLS-1$
	private final class InternalHandler extends SinkDataHandler {

		/**
		 * The console output stream
		 */
		private MessageConsoleStream mStream;

		/**
		 * Constructor.
		 * 
		 * @param stream
		 *            stream to publish data
		 */
		private InternalHandler(MessageConsoleStream stream) {
			mStream = stream;
			mSinkDataManager.registerDefault(this);
		}

		/**
		 * This implementation of {@link ISinkDataHandler#receivedData(DataFlowID, Object)} writes
		 * the object to the console stream. The object is formatted to a String using the parent
		 * {@link SinkConsoleController#format(Object)}.
		 */
		@Override
		public void receivedData(DataFlowID inFlowID, Object inData) {
			// only log log events if the category is higher than USER_MSG_CATEGORY
			if (!(inData instanceof LogEvent)
					|| LogEvent.shouldLog((LogEvent) inData,
							org.marketcetera.core.Messages.USER_MSG_CATEGORY)) {
				mStream.println(format(inFlowID, inData));
			}
		}

		/**
		 * Stops handler from writing to console stream. After this method has been called, this
		 * object should no longer be used.
		 */
		private synchronized void dispose() {
			mSinkDataManager.unregisterDefault(this);
			mStream = null;
		}

	}

}
