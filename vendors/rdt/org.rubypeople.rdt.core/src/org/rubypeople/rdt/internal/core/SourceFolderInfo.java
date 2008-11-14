package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.rubypeople.rdt.core.RubyModelException;

public class SourceFolderInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-ruby resources contained by this PackageFragment
	 */
	protected Object[] nonRubyResources;

/**
 * Create and initialize a new instance of the receiver
 */
public SourceFolderInfo() {
	this.nonRubyResources = null;
}
/**
 */
boolean containsRubyResources() {
	return this.children.length != 0;
}
/**
 * Returns an array of non-ruby resources contained in the receiver.
 */
Object[] getNonRubyResources(IResource underlyingResource, SourceFolderRoot rootHandle) {
	if (this.nonRubyResources == null) {
		try {
			this.nonRubyResources = 
				SourceFolderRootInfo.computeFolderNonRubyResources(
					(RubyProject)rootHandle.getRubyProject(), 
					(IContainer)underlyingResource, 
					rootHandle.fullInclusionPatternChars(),
					rootHandle.fullExclusionPatternChars());
		} catch (RubyModelException e) {
			// root doesn't exist: consider package has no nonRubyResources
			this.nonRubyResources = NO_NON_RUBY_RESOURCES;
		}
	}
	return this.nonRubyResources;
}
/**
 * Set the nonRubyResources to res value
 */
void setNonRubyResources(Object[] resources) {
	this.nonRubyResources = resources;
}
}
