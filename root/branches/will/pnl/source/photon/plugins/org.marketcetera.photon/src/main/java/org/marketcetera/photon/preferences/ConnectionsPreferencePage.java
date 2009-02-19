package org.marketcetera.photon.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;

/**
 * Connection Preferences.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ConnectionsPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, Messages {

	public static final String ID = "org.marketcetera.photon.preferences.connections"; //$NON-NLS-1$

	private UrlFieldEditor jmsUrlEditor;

	private StringFieldEditor orderIDPrefixEditor;

	private UrlFieldEditor webServiceHostEditor;

	private IntegerFieldEditor webServicePortEditor;

	public ConnectionsPreferencePage() {
		super(GRID);
		setPreferenceStore(PhotonPlugin.getDefault().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		Group group = new Group(getFieldEditorParent(), SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(
				group);
		GridLayoutFactory.swtDefaults().applyTo(group);
		group.setText(CONNECTION_PREFERENCES_SERVER_LABEL.getText());
		Composite composite = new Composite(group, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);
		jmsUrlEditor = new UrlFieldEditor(PhotonPreferences.JMS_URL,
				CONNECTION_PREFERENCES_JMS_URL_LABEL.getText(), composite);
		addField(jmsUrlEditor);
		webServiceHostEditor = new UrlFieldEditor(
				PhotonPreferences.WEB_SERVICE_HOST,
				CONNECTION_PREFERENCES_WEB_SERVICE_HOST_LABEL.getText(),
				composite);
		addField(webServiceHostEditor);

		webServicePortEditor = new IntegerFieldEditor(
				PhotonPreferences.WEB_SERVICE_PORT,
				CONNECTION_PREFERENCES_WEB_SERVICE_PORT_LABEL.getText(),
				composite);
		addField(webServicePortEditor);

		orderIDPrefixEditor = new StringFieldEditor(
				PhotonPreferences.ORDER_ID_PREFIX, ORDER_ID_PREFIX_LABEL
						.getText(), getFieldEditorParent());
		addField(orderIDPrefixEditor);
	}

	@Override
	public boolean performOk() {
		jmsUrlEditor.setStringValue(jmsUrlEditor.getStringValue().trim());
		webServiceHostEditor.setStringValue(webServiceHostEditor
				.getStringValue().trim());
		return super.performOk();
	}

}
