package org.marketcetera.photon.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.ComboFieldEditor;
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
import org.marketcetera.photon.marketdata.MarketDataFeed;
import org.marketcetera.photon.marketdata.MarketDataManager;

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

	private final MarketDataManager mdataManager = PhotonPlugin.getDefault()
			.getMarketDataManager();

	private UrlFieldEditor jmsUrlEditor;

	private ComboFieldEditor quoteFeedNameEditor;

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

		Collection<MarketDataFeed> providers = mdataManager.getProviders();
		List<String[]> namesValues = new ArrayList<String[]>();
		// blank one to represent no selection
		namesValues.add(new String[] { "", "" }); //$NON-NLS-1$ //$NON-NLS-2$
		for (MarketDataFeed provider : providers) {
			String name = provider.getName();
			if (name == null) {
				name = provider.getId();
			}
			namesValues.add(new String[] { name, provider.getId() });
		}
		Collections.sort(namesValues, new Comparator<String[]>() {

			@Override
			public int compare(String[] o1, String[] o2) {
				return o1[0].compareTo(o2[0]);
			}

		});
		quoteFeedNameEditor = new ComboFieldEditor(
				PhotonPreferences.DEFAULT_MARKETDATA_PROVIDER,
				MARKET_DATA_FEED_LABEL.getText(), namesValues
						.toArray(new String[namesValues.size()][]),
				getFieldEditorParent());
		addField(quoteFeedNameEditor);

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
