package org.marketcetera.photon.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.PhotonPlugin;

public class FIXMessageDetailPreferenceParser {
	private static final String KeySuffixDelimiter = "_";

	private static final String FieldDelimiter = ",";

	public FIXMessageDetailPreferenceParser() {
	}

	private ScopedPreferenceStore getPreferenceStore() {
		return PhotonPlugin.getDefault().getPreferenceStore();
	}

	private String getKey(char orderStatus) {
		return FIXMessageDetailPreferencePage.ID + KeySuffixDelimiter
				+ orderStatus;
	}

	private String toValue(List<Integer> fixFields) {
		StringBuilder builder = new StringBuilder();
		for (int field : fixFields) {
			builder.append(field);
			builder.append(FieldDelimiter);
		}
		return builder.toString();
	}

	private List<Integer> fromValue(String strValue) {
		List<Integer> fields = new ArrayList<Integer>();
		if (strValue == null) {
			return fields;
		}
		String[] parts = strValue.split(FieldDelimiter);
		if (parts == null) {
			return fields;
		}
		for (String part : parts) {
			try {
				int field = Integer.parseInt(part);
				fields.add(field);
			} catch (Exception anyException) {
				PhotonPlugin.getMainConsoleLogger().debug(
						getClass() + " Failed to parse field value: " + part); // $NON-NLS-1$
			}
		}
		return fields;
	}

	public void setFieldsToShow(char orderStatus, List<Integer> fixFields) {
		String key = getKey(orderStatus);
		String value = toValue(fixFields);
		getPreferenceStore().setValue(key, value);
	}

	public List<Integer> getFieldsToShow(char orderStatus) {
		String key = getKey(orderStatus);
		String value = getPreferenceStore().getString(key);
		List<Integer> fields = fromValue(value);
		return fields;
	}
}
