package org.marketcetera.photon.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Preference page for controlling Trading History.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TradingHistoryPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(composite);
		Label description = new Label(composite, SWT.WRAP);
		description
				.setText(Messages.TRADING_HISTORY_PREFERENCE_PAGE_DESCRIPTION
						.getText());
		GridDataFactory.defaultsFor(description).applyTo(description);
		GridDataFactory.fillDefaults().applyTo(super.createContents(composite));
		return composite;
	}

	@Override
	protected void createFieldEditors() {
		addField(new TimeOfDayFieldEditor(
				PhotonPreferences.TRADING_HISTORY_START_TIME,
				Messages.TRADING_HISTORY_PREFERENCE_PAGE_SESSION_START_TIME_LABEL
						.getText(), getFieldEditorParent(), true));
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return PhotonPlugin.getDefault().getPreferenceStore();
	}

}
