package org.rubypeople.rdt.debug.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;

public class RubyLineBreakpoint extends LineBreakpoint {
	public static final String RUBY_BREAKPOINT_MARKER = "org.rubypeople.rdt.debug.core.rubyLineBreakpointMarker"; //$NON-NLS-1$

	private static final String EXTERNAL_FILENAME = "externalFileName";
	private int index = -1 ; // index of breakpoint on ruby debugger side
	
	public RubyLineBreakpoint() {		
	}
	
	public RubyLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		this(resource, lineNumber, resource.getName());
	}
	
	public RubyLineBreakpoint(final IResource resource, final int lineNumber, final String fileName) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				setMarker(resource.createMarker(RUBY_BREAKPOINT_MARKER));
				getMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber + 1);
				getMarker().setAttribute(IBreakpoint.ID, getModelIdentifier());
				getMarker().setAttribute(REGISTERED, false);
				getMarker().setAttribute(EXTERNAL_FILENAME, fileName);
				// setEnabled must be set before calling setRegistered
				setEnabled(true);
				setRegistered(true);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null);
		} catch (CoreException e) {
			throw new DebugException(e.getStatus());
		}

	}
	
	public String getFileName() throws CoreException {
		IResource resource = ensureMarker().getResource();
		if (resource.equals(ResourcesPlugin.getWorkspace().getRoot())) {
			return ensureMarker().getAttribute(EXTERNAL_FILENAME, "");
		}
		return resource.getName();
	}

	public int getLineNumber() throws CoreException {
		return ensureMarker().getAttribute(IMarker.LINE_NUMBER, -1);
	}

	public int getCharStart() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_START, -1);
	}

	public int getCharEnd() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_END, -1);
	}

	/**
	 * Returns the type of marker associated with this type of breakpoints
	 */
	public static String getMarkerType() {
		return RUBY_BREAKPOINT_MARKER;
	}

	public String getModelIdentifier() {
		return RdtDebugCorePlugin.MODEL_IDENTIFIER;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
