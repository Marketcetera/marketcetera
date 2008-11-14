package org.rubypeople.rdt.internal.debug.core;


public class DebuggerNotFoundException extends RuntimeException {

	public DebuggerNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = -7048656572730937337L;

	public DebuggerNotFoundException() {
		super("Could not connect to debugger.") ;
	}
}
