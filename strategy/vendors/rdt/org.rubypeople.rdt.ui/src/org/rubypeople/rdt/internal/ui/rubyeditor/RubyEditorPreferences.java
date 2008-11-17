package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class RubyEditorPreferences {
	private static final String RESOURCE_BUNDLE = "org.rubypeople.rdt.ui.rubyeditor.RubyEditorPreferences";
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private RubyEditorPreferences() {
	}

	public static String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
