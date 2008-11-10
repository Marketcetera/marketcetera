package org.rubypeople.rdt.internal.core;

public class ExternalSourceFolderInfo extends SourceFolderInfo {
	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	Object[] getNonRubyResources() {
		return this.nonRubyResources;
	}
}
