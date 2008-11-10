package org.rubypeople.rdt.internal.debug.core.commands;

import java.io.IOException;

import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.BreakpointModificationReader;
import org.rubypeople.rdt.internal.debug.core.parsing.XmlStreamReader;

public class BreakpointCommand  extends AbstractCommand {
		
	public BreakpointCommand(String command) {
		super(command, true) ;
	}

	@Override
	protected XmlStreamReader createResultReader(AbstractReadStrategy readStrategy) {
		return new BreakpointModificationReader(readStrategy) ;
	}
	
	public BreakpointModificationReader getBreakpointAddedReader() {
		return (BreakpointModificationReader) getResultReader() ;
	}
	
	public int executeWithResult(AbstractDebuggerConnection connection) throws DebuggerNotFoundException, IOException {
		execute(connection) ;
		return getBreakpointAddedReader().readBreakpointNo() ;
	}
}
