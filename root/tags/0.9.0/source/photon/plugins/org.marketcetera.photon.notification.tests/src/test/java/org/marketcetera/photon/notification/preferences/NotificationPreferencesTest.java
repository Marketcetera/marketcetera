package org.marketcetera.photon.notification.preferences;

import static org.mockito.Mockito.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.NotificationPlugin;

/* $License$ */

/**
 * Test {@link NotificationPreferences}
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationPreferencesTest {

	private IPreferenceStore mockPreferenceStore;
	private NotificationPlugin mockPlugin;

	@Before
	public void setUp() {
		mockPreferenceStore = mock(IPreferenceStore.class);
		mockPlugin = mock(NotificationPlugin.class);
		stub(mockPlugin.getPreferenceStore()).toReturn(mockPreferenceStore);
		NotificationPlugin.setOverride(mockPlugin);
	}

	@After
	public void tearDown() {
		NotificationPlugin.setOverride(null);
	}

	/**
	 * Simple test to ensure defaults are set as expected
	 */
	@Test
	public void testInitializeDefaults() {
		new NotificationPreferences().initializeDefaultPreferences();
		verify(mockPreferenceStore).setDefault(
				NotificationPreferences.PRIORITY, Severity.HIGH.name());
		for (Severity severity : Severity.values()) {
			verify(mockPreferenceStore).setDefault(
					NotificationPreferences.SOUND_ENABLED_PREFIX
							+ severity.name(), false);
			verify(mockPreferenceStore).setDefault(
					NotificationPreferences.SOUND_ENABLED_PREFIX
							+ severity.name(), "");
		}
	}

}
