package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class ClassicDebuggerCommandFactory implements ICommandFactory {

	public String createReadFrames(RubyThread thread) {
		return "th " + thread.getId() + " ; w" ;
	}
	
	public String createReadLocalVariables(RubyStackFrame frame) {
		return "th " + ((RubyThread) frame.getThread()).getId() + " ; frame " + frame.getIndex() + " ; v l " ;
	}
	
	public String createReadInstanceVariable(RubyVariable variable) {
		return "th " + ((RubyThread) variable.getStackFrame().getThread()).getId() + " ; v i " + variable.getStackFrame().getIndex() + " " + variable.getObjectId();
	}

	public String createStepOver(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next";
	}

	public String createForcedStepOver(RubyStackFrame stackFrame) {
		// not supported by Classic Debugger
		return createStepOver(stackFrame);
	}

	public String createStepReturn(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next " + (stackFrame.getLineNumber() + 1);
	}

	public String createStepInto(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; step";
	}

	public String createForcedStepInto(RubyStackFrame stackFrame) {
		// not supported by Classic Debugger
		return createStepInto(stackFrame);
	}

	public String createReadThreads() {
		return "th l";
	}

	public String createLoad(String filename) {
		return "load " + filename;
	}

	public String createInspect(RubyStackFrame frame, String expression) {
		return "th " + ((RubyThread) frame.getThread()).getId() + " ; v inspect " + frame.getIndex() + " " + expression;
	}

	public String createResume(RubyThread thread) {
		return "th " + thread.getId() + ";cont";
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
		return "th stop " +thread.getId() ;
	}
}
