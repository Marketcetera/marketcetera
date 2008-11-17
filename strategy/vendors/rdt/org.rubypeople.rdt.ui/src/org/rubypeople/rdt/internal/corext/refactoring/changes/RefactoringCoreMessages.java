package org.rubypeople.rdt.internal.corext.refactoring.changes;

import org.eclipse.osgi.util.NLS;

public class RefactoringCoreMessages extends NLS {

	private static final String BUNDLE_NAME = RefactoringCoreMessages.class.getName();

	public static String UndoRubyScriptChange_no_resource;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, RefactoringCoreMessages.class);
	}
	
}
