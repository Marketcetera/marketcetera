package org.rubypeople.rdt.internal.debug.core.commands;

import java.io.IOException;

import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;

public class ClassicDebuggerConnection extends AbstractDebuggerConnection {

	private boolean isStarted;

	public ClassicDebuggerConnection(int port) {
		super(port);
	}

	@Override
	public void connect() throws DebuggerNotFoundException, IOException {
		createCommandConnection();
	}

	@Override
	public SuspensionReader start() throws DebuggerNotFoundException, IOException {
		StepCommand stepCommand = new StepCommand("cont");
		stepCommand.execute(this) ;
		isStarted = true ;
		return stepCommand.getSuspensionReader() ;
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

}
