package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceFolderRootInfo extends OpenableElementInfo {
	/**
	 * A array with all the non-ruby resources contained by this PackageFragment
	 */
	protected Object[] fNonRubyResources;

	/**
	 * Create and initialize a new instance of the receiver
	 */
	public SourceFolderRootInfo() {
		this.fNonRubyResources = null;
	}
	
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	Object[] getNonRubyResources(IResource underlyingResource) {
		if (this.fNonRubyResources == null) {
			try {
				this.fNonRubyResources = 
					computeFolderNonRubyResources((IContainer)underlyingResource);
			} catch (RubyModelException e) {
				// root doesn't exist: consider package has no nonRubyResources
				this.fNonRubyResources = NO_NON_RUBY_RESOURCES;
			}
		}
		return this.fNonRubyResources;
	}
	/**
	 * Set the nonRubyResources to res value
	 */
	void setNonRubyResources(Object[] resources) {
		this.fNonRubyResources = resources;
	}

	/**
	 * Starting at this folder, create non-ruby resources for this package fragment root 
	 * and add them to the non-ruby resources collection.
	 * 
	 * @exception RubyModelException  The resource associated with this package fragment does not exist
	 */
	static Object[] computeFolderNonRubyResources(IContainer folder) throws RubyModelException {
		Object[] nonRubyResources = new IResource[5];
		int nonRubyResourcesCounter = 0;
		try {
			IResource[] members = folder.members();
			nextResource: for (int i = 0, max = members.length; i < max; i++) {
				IResource member = members[i];
				switch (member.getType()) {
					case IResource.FILE :
						String fileName = member.getName();
						
						// ignore .rb files that are not excluded
						if (Util.isValidRubyScriptName(fileName)) 
							continue nextResource;
						break;

					case IResource.FOLDER :
							continue nextResource;
				}
				if (nonRubyResources.length == nonRubyResourcesCounter) {
					// resize
					System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter * 2]), 0, nonRubyResourcesCounter);
				}
				nonRubyResources[nonRubyResourcesCounter++] = member;

			}
			if (nonRubyResources.length != nonRubyResourcesCounter) {
				System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter]), 0, nonRubyResourcesCounter);
			}
			return nonRubyResources;
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}

	/**
	 * Starting at this folder, create non-ruby resources for this package fragment root 
	 * and add them to the non-ruby resources collection.
	 * 
	 * @exception RubyModelException  The resource associated with this package fragment does not exist
	 */
	static Object[] computeFolderNonRubyResources(RubyProject project, IContainer folder, char[][] inclusionPatterns, char[][] exclusionPatterns) throws RubyModelException {
		Object[] nonRubyResources = new IResource[5];
		int nonRubyResourcesCounter = 0;
		try {
			ILoadpathEntry[] classpath = project.getResolvedLoadpath(true/*ignoreUnresolvedEntry*/, false/*don't generateMarkerOnError*/, false/*don't returnResolutionInProgress*/);
			IResource[] members = new IResource[0];
			if (folder != null) members = folder.members();
			nextResource: for (int i = 0, max = members.length; i < max; i++) {
				IResource member = members[i];
				switch (member.getType()) {
					case IResource.FILE :
						String fileName = member.getName();
						
						// ignore .rb files that are not excluded
						if (Util.isValidRubyScriptName(fileName) && !Util.isExcluded(member, inclusionPatterns, exclusionPatterns)) 
							continue nextResource;
						// ignore .zip or .jar file on classpath
//						if (org.eclipse.jdt.internal.compiler.util.Util.isArchiveFileName(fileName) && isClasspathEntry(member.getFullPath(), classpath)) 
//							continue nextResource;
						break;

					case IResource.FOLDER :
						// ignore valid packages or excluded folders that correspond to a nested pkg fragment root
						if (Util.isValidSourceFolderName(member.getName())
								&& (!Util.isExcluded(member, inclusionPatterns, exclusionPatterns) 
									|| isLoadpathEntry(member.getFullPath(), classpath)))
							continue nextResource;
						break;
				}
				if (nonRubyResources.length == nonRubyResourcesCounter) {
					// resize
					System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter * 2]), 0, nonRubyResourcesCounter);
				}
				nonRubyResources[nonRubyResourcesCounter++] = member;

			}
			if (nonRubyResources.length != nonRubyResourcesCounter) {
				System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter]), 0, nonRubyResourcesCounter);
			}
			return nonRubyResources;
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}
	
	private static boolean isLoadpathEntry(IPath path, ILoadpathEntry[] resolvedLoadpath) {
		for (int i = 0, length = resolvedLoadpath.length; i < length; i++) {
			ILoadpathEntry entry = resolvedLoadpath[i];
			if (entry.getPath().equals(path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	synchronized Object[] getNonRubyResources(IRubyProject project, IResource underlyingResource, SourceFolderRoot handle) {
		Object[] nonRubyResources = this.fNonRubyResources;
		if (nonRubyResources == null) {
			nonRubyResources = this.computeNonRubyResources(project, underlyingResource, handle);
			this.fNonRubyResources = nonRubyResources;
		}
		return nonRubyResources;
	}

/**
 * Compute the non-ruby resources of this source folder root.
 */
private Object[] computeNonRubyResources(IRubyProject project, IResource underlyingResource, SourceFolderRoot handle) {
	Object[] nonRubyResources = NO_NON_RUBY_RESOURCES;
	try {
		// the underlying resource may be a folder or a project (in the case that the project folder
		// is actually the source folder root)
		if (underlyingResource.getType() == IResource.FOLDER || underlyingResource.getType() == IResource.PROJECT) {
			nonRubyResources = 
				computeFolderNonRubyResources(
					(RubyProject)project, 
					(IContainer) underlyingResource,  
					handle.fullInclusionPatternChars(),
					handle.fullExclusionPatternChars());
		}
	} catch (RubyModelException e) {
		// ignore
	}
	return nonRubyResources;
}
}
