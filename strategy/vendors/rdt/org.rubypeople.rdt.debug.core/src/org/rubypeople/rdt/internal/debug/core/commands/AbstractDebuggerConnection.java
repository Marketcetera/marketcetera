package org.rubypeople.rdt.internal.debug.core.commands;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public abstract class AbstractDebuggerConnection {

	private int commandPort;
	private Socket commandSocket;
	private PrintWriter writer;
	private AbstractReadStrategy commandReadStrategy ;

	public AbstractDebuggerConnection(int port) {
		super();
		this.commandPort = port;
	}

	/*
	 * connect to the debugger. After the connection is established the debugger
	 * listens for control commands
	 */
	public abstract void connect() throws DebuggerNotFoundException, IOException;

	/*
	 * start the debugger. Must be connected to start.
	 */
	public abstract SuspensionReader start() throws  DebuggerNotFoundException, IOException;

	public abstract boolean isStarted() ;
	
	/*
	 * always call via Command.execute
	 */
	protected AbstractReadStrategy sendCommand(AbstractCommand command) throws DebuggerNotFoundException, IOException {
		if (!isCommandPortConnected()) {
			throw new IllegalStateException(command  + " could not be sent since command socket is not open") ;
		}
		RdtDebugCorePlugin.debug("Sending command: " + command.getCommand()) ;
		getWriter().println(command.getCommand()) ;
		return getCommandReadStrategy() ;
	}
	
	public AbstractReadStrategy getCommandReadStrategy() {
		return commandReadStrategy ;
	}
	
	protected void createCommandConnection() throws DebuggerNotFoundException, IOException {
		getSocket() ;
		XmlPullParser xpp = createXpp(commandSocket) ;
		commandReadStrategy = new MultiReaderStrategy(xpp) ;
	}
	
	public boolean isCommandPortConnected() {
		return commandSocket != null ;
	}

	protected Socket getSocket() throws IOException, DebuggerNotFoundException {

		if (commandSocket == null) {
			commandSocket = acquireSocket(commandPort);
			if (commandSocket == null) {
				throw new DebuggerNotFoundException("Could not connect to debugger on port " + commandPort);
			}
		}
		return commandSocket;
	}
	

	protected static Socket acquireSocket(int port) throws IOException {
		Socket socket = null ;
		int tryCount = 10;
		for (int i = 0; i < tryCount; i++) {
			try {
				socket = new Socket("localhost", port);
				break ;
			} catch (IOException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {}
			}
		}
		return socket;

	}

	private PrintWriter getWriter() throws IOException, DebuggerNotFoundException {
		if (writer == null) {
			writer = new PrintWriter(commandSocket.getOutputStream(), true);
		}
		return writer;
	}

	protected static XmlPullParser createXpp(Socket socket) {
		XmlPullParser xpp = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance("org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer", null);
			xpp = factory.newPullParser();
			xpp.setInput(socket.getInputStream(), "UTF-8");
		} catch (XmlPullParserException e) {
			// TODO: log
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xpp;
	}

	public int getCommandPort() {
		return commandPort;
	}

	public void exit() throws IOException {
		if (commandSocket != null) {
			commandSocket.close() ;
		}
	}

}
