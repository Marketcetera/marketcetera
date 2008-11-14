package org.rubypeople.rdt.internal.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyFileMatcher;
/**
 * @deprecated Please do not use anymore
 * @author Chris Williams
 *
 */
public class RubyElementVisitor implements IResourceVisitor {

	protected List rubyFiles ;
	protected RubyFileMatcher rubyFileMatcher ;
	
	public RubyElementVisitor() {
		rubyFiles = new ArrayList() ;
		rubyFileMatcher = RubyPlugin.getDefault().getRubyFileMatcher() ;
	}

	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
		case IResource.PROJECT:
			return true;

		case IResource.FOLDER:
			return true;

		case IResource.FILE:
			IFile fileResource = (IFile) resource;
			if (rubyFileMatcher.hasRubyEditorAssociation(fileResource)) {
				this.rubyFiles.add(resource) ;
				return true ;
			}
			return false ;

		default:
			return false;
		}
	}

	public Object[] getCollectedRubyFiles() {
		return rubyFiles.toArray();
	}
}