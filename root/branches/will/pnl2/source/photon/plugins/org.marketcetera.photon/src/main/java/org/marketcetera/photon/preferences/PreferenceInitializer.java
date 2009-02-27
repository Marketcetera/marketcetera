package org.marketcetera.photon.preferences;

import java.io.IOException;
import java.util.TimeZone;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;
import org.marketcetera.util.log.SLF4JLoggerProxy;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore prefs = PhotonPlugin.getDefault().getPreferenceStore();
		initializeTradingHistoryStartTime(prefs);
	}

	void initializeTradingHistoryStartTime(ScopedPreferenceStore prefs) {
		TimeOfDay time = TimeOfDay.create(0, 0, 0, TimeZone.getDefault());
		prefs.setDefault(PhotonPreferences.TRADING_HISTORY_START_TIME, time.toFormattedString());
		// Sanity check that the preference value can be parsed into a time.
		// Previous versions of Photon allowed this preference to be empty so
		// this also handles the migration case.
		if (TimeOfDay.create(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME)) == null) {
			SLF4JLoggerProxy
					.debug(this,
							"Invalid preference value for trading history start time.  It will be reset to the default value."); //$NON-NLS-1$
			prefs.setToDefault(PhotonPreferences.TRADING_HISTORY_START_TIME);
			try {
				prefs.save();
			} catch (IOException e) {
				SLF4JLoggerProxy.debug(this, e, "Could not save preferences."); //$NON-NLS-1$
			}
		}
	}

}
