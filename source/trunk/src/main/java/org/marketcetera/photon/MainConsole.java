package org.marketcetera.photon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


public class MainConsole extends MessageConsole {
	
	private MessageConsoleStream errorMessageStream;
	private MessageConsoleStream warnMessageStream;
	private MessageConsoleStream infoMessageStream;
	private MessageConsoleStream debugMessageStream;

	public MainConsole() {
		super(Messages.MainConsole_Name, null);
		Display display = Display.getCurrent();
		errorMessageStream = newMessageStream();
		errorMessageStream.setColor(display.getSystemColor(SWT.COLOR_RED));
		warnMessageStream = newMessageStream();
		warnMessageStream.setColor(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
		infoMessageStream = newMessageStream();
		infoMessageStream.setColor(display.getSystemColor(SWT.COLOR_BLACK));
		debugMessageStream = newMessageStream();
		debugMessageStream.setColor(display.getSystemColor(SWT.COLOR_DARK_GRAY));
	}

	protected void dispose()
	{
		
	}

	/**
	 * @return Returns the debugMessageStream.
	 */
	public MessageConsoleStream getDebugMessageStream() {
		return debugMessageStream;
	}

	/**
	 * @return Returns the errorMessageStream.
	 */
	public MessageConsoleStream getErrorMessageStream() {
		return errorMessageStream;
	}

	/**
	 * @return Returns the infoMessageStream.
	 */
	public MessageConsoleStream getInfoMessageStream() {
		return infoMessageStream;
	}

	public MessageConsoleStream getWarnMessageStream() {
		return warnMessageStream;
	}
	
	
	
}
