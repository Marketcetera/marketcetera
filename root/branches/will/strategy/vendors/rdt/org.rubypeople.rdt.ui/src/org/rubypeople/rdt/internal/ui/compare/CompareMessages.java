package org.rubypeople.rdt.internal.ui.compare;

import org.eclipse.osgi.util.NLS;

public class CompareMessages extends NLS {
	private static final String BUNDLE_NAME= "org.rubypeople.rdt.internal.ui.compare.CompareMessages";//$NON-NLS-1$

	private CompareMessages() {
		// Do not instantiate
	}

	public static String RubyNode_importDeclarations;
	public static String RubyNode_script;
	public static String RubyMergeViewer_title;
	public static String RubyStructureViewer_title;

	static {
		NLS.initializeMessages(BUNDLE_NAME, CompareMessages.class);
	}
}
