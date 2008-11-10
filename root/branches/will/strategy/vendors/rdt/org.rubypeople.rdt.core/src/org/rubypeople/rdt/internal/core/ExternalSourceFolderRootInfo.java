package org.rubypeople.rdt.internal.core;

public class ExternalSourceFolderRootInfo extends SourceFolderRootInfo {
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	public Object[] getNonRubyResources() {
		fNonRubyResources = NO_NON_RUBY_RESOURCES;
		return fNonRubyResources;
	}
}
