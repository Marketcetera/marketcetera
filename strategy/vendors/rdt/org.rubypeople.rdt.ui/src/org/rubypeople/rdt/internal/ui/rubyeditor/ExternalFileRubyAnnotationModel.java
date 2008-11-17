package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.core.ExternalRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class ExternalFileRubyAnnotationModel extends AbstractMarkerAnnotationModel {

	private IWorkspace fWorkspace;
	private IRubyScript fScript;

	public ExternalFileRubyAnnotationModel(IRubyScript script) {
		fScript = script;
		fWorkspace= ResourcesPlugin.getWorkspace();
		// TODO Grab all the markers from workspace root which match this file, and add annotaions for them?
	}
	
	protected void deleteMarkers(final IMarker[] markers) throws CoreException {
		fWorkspace.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i= 0; i < markers.length; ++i) {
					markers[i].delete();
				}
			}
		}, null, IWorkspace.AVOID_UPDATE, null);
	}

	protected boolean isAcceptable(IMarker marker) {
		return marker != null && marker.getResource().equals(getResource()) && getFileName(marker).equals(getFileName());
	}

	private String getFileName(IMarker marker) {
		return marker.getAttribute("externalFileName", ""); // FIXME This is duplicated in RubyLineBreakpoint. Move this to an interface as a constant!
	}

	protected void listenToMarkerChanges(boolean listen) {
		// TODO Auto-generated method stub

	}

	protected IMarker[] retrieveMarkers() throws CoreException {
		IMarker[] markers = getResource().findMarkers(IMarker.MARKER, true, IResource.DEPTH_INFINITE);
		List<IMarker> filtered = new ArrayList<IMarker>();
		for (int i = 0; i < markers.length; i++) {
			if (getFileName(markers[i]).equals(getFileName())) {
				filtered.add(markers[i]);
			}
		}
		return filtered.toArray(new IMarker[filtered.size()]);
	}

	private String getFileName() {
		if (fScript == null) return null;
		if (fScript instanceof ExternalRubyScript) {
			ExternalRubyScript script = (ExternalRubyScript) fScript;
			return script.getFile().getAbsolutePath();
		}
		return fScript.getPath().toPortableString();
	}

	private IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
