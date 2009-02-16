package org.marketcetera.photon.preferences;

import java.util.TimeZone;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		TimeOfDay time = TimeOfDay.create(0, 0, 0, TimeZone.getDefault());
		PhotonPlugin.getDefault().getPreferenceStore().setDefault(
				PhotonPreferences.TRADING_HISTORY_START_TIME,
				time.toFormattedString());
	}

}
