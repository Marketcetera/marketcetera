package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.internal.core.util.Util;

public class ERBBuildContext extends BuildContext {

	private char[] fContents;

	public ERBBuildContext(IFile resource) {
		super(resource);
	}
	
	/**
	 * Returns the contents of the ruby script.
	 * 
	 * @return the contents of the ruby script
	 */
	public char[] getContents() {
		if (fContents == null) {
			char[] contents = super.getContents();		
			fContents = Util.replaceNonRubyCodeWithWhitespace(new String(contents));
		}		
		return fContents;
	}	

}
