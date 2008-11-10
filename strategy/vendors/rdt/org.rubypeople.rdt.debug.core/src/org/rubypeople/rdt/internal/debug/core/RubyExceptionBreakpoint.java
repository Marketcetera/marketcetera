/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2005 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 */
 
package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.debug.core.RubyLineBreakpoint;

public class RubyExceptionBreakpoint extends Breakpoint {

	private static String RUBY_EXCEPTION_ATTR = "RubyException";
	
	private static final String RUBY_EXCEPTION_BREAKPOINT= "org.rubypeople.rdt.debug.core.rubyExceptionBreakpointMarker"; //$NON-NLS-1$
	
	public RubyExceptionBreakpoint() {		
	}
	
	public RubyExceptionBreakpoint(final String exception) throws CoreException {
		// we need to have a resource, because the marker needs it (BTW: why the hell do 
		// we need a marker for?) Possible Answer: so that changes can be detected and 
		// propagated to the DebugTarget in the same manner as for Line Breakpoints?
		// The workspace root is chosen because JavaExceptionsBreakpoints do so as well
		final IResource resource = ResourcesPlugin.getWorkspace().getRoot() ;
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				setMarker(resource.createMarker(RUBY_EXCEPTION_BREAKPOINT));
				getMarker().setAttribute(RUBY_EXCEPTION_ATTR, exception);
				getMarker().setAttribute(IBreakpoint.ID, getModelIdentifier());
				setEnabled(true);
				// not yet registered with the BreakpointManager ..
				getMarker().setAttribute(REGISTERED, false);
				// .. but now please do so and add the breakpoint to the breakpoint manager
				setRegistered(true);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null);
		} catch (CoreException e) {
			throw new DebugException(e.getStatus());
		}
	}

	/**
	 * Returns the type of marker associated with this type of breakpoints
	 */
	public static String getMarkerType() {
		return RubyLineBreakpoint.RUBY_BREAKPOINT_MARKER;
	}

	public String getModelIdentifier() {
		return RdtDebugCorePlugin.MODEL_IDENTIFIER;
	}

	public String getException() throws CoreException {
		return (String) this.ensureMarker().getAttribute(RUBY_EXCEPTION_ATTR);
	}
	
	public void setException(String newValue) throws CoreException {
		this.ensureMarker().setAttribute(RUBY_EXCEPTION_ATTR, newValue);
	}
}
