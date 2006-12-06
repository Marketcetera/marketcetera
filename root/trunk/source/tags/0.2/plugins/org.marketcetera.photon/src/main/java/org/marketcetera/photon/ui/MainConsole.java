package org.marketcetera.photon.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;

/**
 * Specialization of the RCP's {@link MessageConsole} that integrates the
 * platform's console system and the Log4J-based logging system in the
 * Marketcetera Core, by providing {@link MessageConsoleStream} objects for
 * error, warn, info and debug messages.
 * 
 * 
 * @author gmiller
 * 
 */
@ClassVersion("$Id$")
public class MainConsole extends MessageConsole {

	private MessageConsoleStream errorMessageStream;

	private MessageConsoleStream warnMessageStream;

	private MessageConsoleStream infoMessageStream;

	private MessageConsoleStream debugMessageStream;

	/**
	 * Creates a new MainConsole and initializes the
	 * {@link MessageConsoleStream} obejcts for error, warn, info, and debug
	 * messages, settin each of them to output text in a different color.
	 */
	public MainConsole() {
		super(Messages.MainConsole_Name, null);
		Display display = Display.getDefault();
		errorMessageStream = newMessageStream();
		errorMessageStream.setColor(display.getSystemColor(SWT.COLOR_RED));
		warnMessageStream = newMessageStream();
		warnMessageStream.setColor(display
				.getSystemColor(SWT.COLOR_DARK_YELLOW));
		infoMessageStream = newMessageStream();
		infoMessageStream.setColor(display.getSystemColor(SWT.COLOR_BLACK));
		debugMessageStream = newMessageStream();
		debugMessageStream
				.setColor(display.getSystemColor(SWT.COLOR_DARK_GRAY));
	}

	protected void dispose() {

	}

	/**
	 * Returns the {@link MessageConsoleStream} associated with debug messages.
	 * 
	 * @return returns the debug message stream.
	 */
	public MessageConsoleStream getDebugMessageStream() {
		return debugMessageStream;
	}

	/**
	 * Returns the {@link MessageConsoleStream} associated with error messages.
	 * 
	 * @return returns the error message stream.
	 */
	public MessageConsoleStream getErrorMessageStream() {
		return errorMessageStream;
	}

	/**
	 * Returns the {@link MessageConsoleStream} associated with info messages.
	 * 
	 * @return returns the info message stream.
	 */
	public MessageConsoleStream getInfoMessageStream() {
		return infoMessageStream;
	}

	/**
	 * Returns the {@link MessageConsoleStream} associated with warning
	 * messages.
	 * 
	 * @return returns the warning message stream.
	 */
	public MessageConsoleStream getWarnMessageStream() {
		return warnMessageStream;
	}

}
