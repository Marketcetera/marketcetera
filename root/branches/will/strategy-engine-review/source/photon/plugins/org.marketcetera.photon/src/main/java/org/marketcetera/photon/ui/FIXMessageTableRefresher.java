package org.marketcetera.photon.ui;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.preferences.FIXMessageColumnPreferenceParser;

public class FIXMessageTableRefresher implements IPropertyChangeListener {

	private FIXMessageColumnPreferenceParser prefsParser = new FIXMessageColumnPreferenceParser();

	private TableViewer parentTableViewer;

	private FIXMessageTableFormat<?> tableFormat;

	public FIXMessageTableRefresher(TableViewer parentTableViewer,
			FIXMessageTableFormat<?> tableFormat) {
		this.parentTableViewer = parentTableViewer;
		this.tableFormat = tableFormat;
		addListeners();
	}

	protected void addListeners() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault()
				.getPreferenceStore();
		thePreferenceStore.addPropertyChangeListener(this);
	}

	protected void removeListeners() {
		ScopedPreferenceStore thePreferenceStore = PhotonPlugin.getDefault()
				.getPreferenceStore();
		thePreferenceStore.removePropertyChangeListener(this);
	}

	public void dispose() {
		removeListeners();
	}

	public void propertyChange(PropertyChangeEvent event) {
		String affectedProperty = event.getProperty();
		if (prefsParser.isPreferenceForView(affectedProperty, tableFormat
				.getAssignedViewID())) {
			tableFormat.updateColumnsFromPreferences();
			parentTableViewer.refresh();
		}
	}
}
