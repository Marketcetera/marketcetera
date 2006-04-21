package org.marketcetera.photon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.marketcetera.photon.parser.Parser;

public class MainConsole extends MessageConsole {
	
	Parser commandParser = new Parser();
	private MessageConsoleStream userMessageStream;
	private MessageConsoleStream appMessageStream;
	
	public MainConsole() {
		super(Messages.MainConsole_Name, null);
		Display display = Display.getCurrent();
		userMessageStream = newMessageStream();
		userMessageStream.setColor(display.getSystemColor(SWT.COLOR_BLACK));
		appMessageStream = newMessageStream();
		appMessageStream.setColor(display.getSystemColor(SWT.COLOR_BLUE));
	}

	public void userMessagePrintln(String userMessage)
	{
		userMessageStream.println(userMessage);
	}
	public void appMessagePrintln(String appMessage)
	{
		userMessageStream.println(appMessage);
	}
	public void userMessagePrint(String userMessage)
	{
		userMessageStream.print(userMessage);
	}
	public void appMessagePrint(String appMessage)
	{
		userMessageStream.print(appMessage);
	}
}
