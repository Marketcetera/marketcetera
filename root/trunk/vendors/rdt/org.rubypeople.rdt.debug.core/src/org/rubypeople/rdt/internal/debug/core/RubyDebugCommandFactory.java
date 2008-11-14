package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class RubyDebugCommandFactory implements ICommandFactory {

	public String createReadFrames(RubyThread thread) {
		return "w" ;
	}
	
	public String createReadLocalVariables(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; v l " ;
	}
	
	public String createReadInstanceVariable(RubyVariable variable) {
		return "frame " + variable.getStackFrame().getIndex() + " ; v i " + variable.getObjectId();
	}

	public String createStepOver(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; next" ;
	}

	public String createForcedStepOver(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; next+";
	}

	public String createStepReturn(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + "; finish";
	}

	public String createStepInto(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; step";
	}

	public String createForcedStepInto(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; step+";
	}

	public String createReadThreads() {
		return "th l";
	}

	public String createLoad(String filename) {
		// TODO Call "reload" to just reload all the code that the debugger is aware of (http://www.datanoise.com/articles/2006/12/20/post-mortem-debugging)! Does calling load work?
		return "load " + filename;
	}

	public String createInspect(RubyStackFrame frame, String expression) {
		return "frame " + frame.getIndex() + " ; v inspect " + expression.replaceAll(";", "\\;");
	}

	public String createResume(RubyThread thread) {
		return "cont";
	}

	public String createAddBreakpoint(String file, int line) {
		StringBuffer setBreakPointCommand = new StringBuffer();
		setBreakPointCommand.append("b ") ;
		setBreakPointCommand.append(file);
		setBreakPointCommand.append(":");
		setBreakPointCommand.append(line);
		return setBreakPointCommand.toString();
	}
	
	public String createRemoveBreakpoint(int index) {
		return "delete " + index ;
	}

	public String createCatchOff() {
		return "catch off";
	}

	public String createCatchOn(IBreakpoint breakpoint) throws CoreException {
		return "catch " + ((RubyExceptionBreakpoint) breakpoint).getException();
	}

	public String createThreadStop(RubyThread thread) {
		return "thread stop " + thread.getId();
	}
}
