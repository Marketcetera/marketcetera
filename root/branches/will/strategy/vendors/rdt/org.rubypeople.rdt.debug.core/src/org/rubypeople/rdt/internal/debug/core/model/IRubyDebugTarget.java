package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.model.IDebugTarget;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;

public interface IRubyDebugTarget extends IDebugTarget {
	public final static String MODEL_IDENTIFIER = "org.rubypeople.rdt.debug";
	public abstract void suspensionOccurred(SuspensionPoint suspensionPoint) ;
	public void updateThreads() ;
	public void terminate() ;
	public void setRubyDebuggerProxy(RubyDebuggerProxy rubyDebuggerProxy) ;
	public int getPort() ;


}
