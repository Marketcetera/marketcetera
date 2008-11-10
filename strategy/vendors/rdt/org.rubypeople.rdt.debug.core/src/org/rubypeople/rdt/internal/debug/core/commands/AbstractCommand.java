package org.rubypeople.rdt.internal.debug.core.commands;

import java.io.IOException;

import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.XmlStreamReader;

public abstract class AbstractCommand {
	private String command ;
	private boolean isControl ;
	private XmlStreamReader resultReader;
	
	protected AbstractCommand(String command, boolean isControl) {
		this.command = command;
		this.isControl = isControl;
	}
	
	public void execute(AbstractDebuggerConnection debuggerConnection) throws DebuggerNotFoundException, IOException {
		AbstractReadStrategy readStrategy = debuggerConnection.sendCommand(this) ;
		resultReader = createResultReader(readStrategy) ;
	}
	
	protected abstract XmlStreamReader createResultReader(AbstractReadStrategy readStrategy) ;

	public XmlStreamReader getResultReader() {
		if (!isExecuted()) {
			throw new IllegalStateException("getResultReader must only be called after the command was executed.") ;
		}
		return resultReader;
	}

	public String getCommand() {
		return command;
	}

	public boolean isControl() {
		return isControl;
	}
	
	public boolean isExecuted() {
		return resultReader != null ;
	}
}
