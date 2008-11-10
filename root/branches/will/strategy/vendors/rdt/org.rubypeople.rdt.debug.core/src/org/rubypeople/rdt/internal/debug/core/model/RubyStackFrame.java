package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;

//see RubyDebugTarget for the reason why PlatformObject is being extended
public class RubyStackFrame extends RubyDebugElement implements IStackFrame {

	private RubyThread thread;
	private String file;
	private int lineNumber;
	private int index;
	private RubyVariable[] variables;
	public RubyStackFrame(RubyThread thread, String file, int line, int index) {
		super(thread.getDebugTarget());
		this.lineNumber = line;
		this.index = index;
		this.file = file;
		this.thread = thread;
	} 
	
	public IThread getThread() {
		return thread;
	}
	
	public void setThread(RubyThread thread) {
		this.thread = thread;
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (variables == null) {
			variables = this.getRubyDebuggerProxy().readVariables(this);
		}
		return variables;
	}
	
	public boolean hasVariables() throws DebugException {
		return getVariables().length > 0;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public int getCharStart() throws DebugException {
		// charStart = -1  and charEnd = -1 is just the way these variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker
		return -1;
	}
	
	public int getCharEnd() throws DebugException {
		// charStart = -1  and charEnd = -1 is just the way thes variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker		
		return -1;
	}
	
	public String getName() {
		return file + ":" + this.getLineNumber(); //$NON-NLS-1$
	}

	public String getFileName() {
		return file;
	}
	
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}
	
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}
	
	public boolean canStepInto() {
		return canResume();
	}
	
	public boolean canStepOver() {
		return canResume();
	}
	
	public boolean canStepReturn() {
		return canResume();
	}
	
	public boolean isStepping() {
		return false;
	}
	
	public void stepInto() throws DebugException {
		thread.resume(true /*isstep*/) ;
		this.getRubyDebuggerProxy().sendStepIntoEnd(RubyStackFrame.this) ;	
		// TODO: resume event should be sent from ruby debugger
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_INTO);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}
	
	public void stepOver() throws DebugException {
		thread.resume(true /*isstep*/) ;
		this.getRubyDebuggerProxy().sendStepOverEnd(RubyStackFrame.this) ;
		// TODO: resume event should be sent from ruby debugger
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_OVER);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}

	public void stepReturn() throws DebugException {
		thread.resume(true /*isstep*/) ;
		this.getRubyDebuggerProxy().sendStepReturnEnd(RubyStackFrame.this) ;
		// TODO: resume event should be sent from ruby debugger
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_RETURN);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });		
	}

	
	public boolean canResume() {
		return this.getThread().canResume();
	}
	
	public boolean canSuspend() {
		return this.getThread().canSuspend();
	}
	
	public boolean isSuspended() {
		return this.getThread().isSuspended();
	} 
	
	public void resume() throws DebugException {
		this.getThread().resume();
	}
	
	public void suspend() throws DebugException {
	}
	
	public boolean canTerminate() {
		return this.getThread().canTerminate();
	}
	
	public boolean isTerminated() {
		return this.getThread().isTerminated();
	} 
	
	public void terminate() throws DebugException {
		this.getThread().terminate() ;		
	} 

	public int getIndex() {
		return index;
	}

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return thread.getRubyDebuggerProxy();
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
