package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;

public class RubyEvaluationResult implements IEvaluationResult {

	private String fSnippet;
	private IThread fThread;
	private IValue fValue;
	private DebugException debugException;

	public RubyEvaluationResult(String expression, IThread thread) {
		this.fSnippet = expression;
		this.fThread = thread;
	}

	public String[] getErrorMessages() {
		// TODO Auto-generated method stub
		return new String[0];
	}

	public DebugException getException() {
		return debugException;
	}
	
	public void setException(DebugException e) {
		this.debugException = e;
	}

	public String getSnippet() {
		return fSnippet;
	}

	public IThread getThread() {
		return fThread;
	}

	public IValue getValue() {
		return fValue;
	}

	public void setValue(IValue value) {
		this.fValue = value;
	}
	
	public boolean hasErrors() {
		return getErrorMessages().length > 0 || getException() != null;
	}

}
