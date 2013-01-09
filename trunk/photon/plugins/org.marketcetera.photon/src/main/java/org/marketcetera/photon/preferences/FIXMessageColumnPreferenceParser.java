package org.marketcetera.photon.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.PhotonPlugin;

public class FIXMessageColumnPreferenceParser {
	private static final String KeyPrefix = "org.marketcetera.photon.preferences.fixcolumnfields"; //$NON-NLS-1$
	private static final String KeyDelimiter = "_"; //$NON-NLS-1$

	private static final String FieldDelimiter = ","; //$NON-NLS-1$

	public FIXMessageColumnPreferenceParser() {
	}

	private ScopedPreferenceStore getPreferenceStore() {
		return PhotonPlugin.getDefault().getPreferenceStore();
	}

	private String getFullPrefix() {
		return KeyPrefix + KeyDelimiter ;
	}
	private String getKey(String viewID) {
		return getFullPrefix() + viewID;
	}
	
	public boolean isPreferenceForView(String preferenceName, String viewID) {
		String key = getKey(viewID);
		if(preferenceName != null && preferenceName.startsWith(key)) {
			return true;
		}
		return false;
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
				PhotonPlugin.getMainConsoleLogger().debug(String.format("%s Failed to parse field value: %s", //$NON-NLS-1$
				                                                        getClass(),
				                                                        part));
			}
		}
		return fields;
	}

	public void setFieldsToShow(String viewID, List<Integer> fixFields) {
		String key = getKey(viewID);
		String value = toValue(fixFields);
		getPreferenceStore().setValue(key, value);
	}

	public List<Integer> getFieldsToShow(String viewID, ScopedPreferenceStore prefStore) {
		String key = getKey(viewID);
		String value = prefStore.getString(key);
		if( value == null) {
			return null;
		}
		List<Integer> fields = fromValue(value);
		return fields;
	}
	
	public List<Integer> getFieldsToShow(String viewID) {
		return getFieldsToShow(viewID, getPreferenceStore());
	}
}
