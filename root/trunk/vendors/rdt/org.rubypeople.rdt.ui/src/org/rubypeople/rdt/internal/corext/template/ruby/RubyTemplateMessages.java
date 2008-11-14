package org.rubypeople.rdt.internal.corext.template.ruby;

import org.eclipse.osgi.util.NLS;

public class RubyTemplateMessages extends NLS {

	private static final String BUNDLE_NAME = RubyTemplateMessages.class
			.getName();

	private RubyTemplateMessages() {
		// Do not instantiate
	}

	public static String ContextType_error_multiple_cursor_variables;

	public static String Context_error_cannot_evaluate;

	static {
		NLS.initializeMessages(BUNDLE_NAME, RubyTemplateMessages.class);
	}

}
