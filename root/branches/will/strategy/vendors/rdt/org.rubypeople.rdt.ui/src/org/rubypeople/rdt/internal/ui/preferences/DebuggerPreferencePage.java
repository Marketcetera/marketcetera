package org.rubypeople.rdt.internal.ui.preferences;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;

public class DebuggerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DebuggerPreferencePage() {
		super(GRID);
		Preferences launchingPreferences = LaunchingPlugin.getDefault().getPluginPreferences();
		setPreferenceStore(new PreferencesAdapter(launchingPreferences));
		setDescription(PreferencesMessages.DebuggerPreferencePage_description_label);
	}

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(org.rubypeople.rdt.internal.launching.PreferenceConstants.USE_RUBY_DEBUG, PreferencesMessages.DebuggerPreferencePage_useRubyDebug_label, getFieldEditorParent()));
		addField(new BooleanFieldEditor(org.rubypeople.rdt.internal.launching.PreferenceConstants.VERBOSE_DEBUGGER, PreferencesMessages.DebuggerPreferencePage_verboseDebugger_label, getFieldEditorParent()));
	}

	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		Label label = new Label(parent, SWT.WRAP);
		URL entry = LaunchingPlugin.getDefault().getBundle().getEntry("/");
		String installLocation;
		try {
			installLocation = FileLocator.resolve(entry).toString();
		} catch (IOException e) {
			installLocation = "<eclipseInstallation>/plugins/org.rubypeople.rdt.launching_<version>";
		}
		String message = MessageFormat.format(PreferencesMessages.DebuggerPreferencePage_useRubyDebug_comment, new Object[] { installLocation });
		label.setText(message);

		FontData[] fontData = getFont().getFontData();
		if (fontData.length > 0) {
			FontData italicFont = new FontData(fontData[0].getName(), fontData[0].getHeight(), SWT.ITALIC);
			label.setFont(new Font(null, italicFont));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {}

}