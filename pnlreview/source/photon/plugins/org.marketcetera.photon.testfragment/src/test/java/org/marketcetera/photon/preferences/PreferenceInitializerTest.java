package org.marketcetera.photon.preferences;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.TimeZone;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.photon.PhotonPreferences;
import org.marketcetera.photon.TimeOfDay;

/* $License$ */

/**
 * Test {@link PreferenceInitializer}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class PreferenceInitializerTest {

	private static final String DEFAULT_TIME = TimeOfDay.create(0, 0, 0, TimeZone.getDefault()).toFormattedString(); 
	
	private ScopedPreferenceStore prefs;
	private PreferenceInitializer fixture;

	@Before
	public void setup() {
		fixture = new PreferenceInitializer();
		prefs = new ScopedPreferenceStore(new InstanceScope(), "PreferenceInitializerTest");
		assertThat(prefs.getDefaultString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(""));
		assertThat(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(""));
	}
	
	@After
	public void cleanup() throws Exception {
		for (IEclipsePreferences node : prefs.getPreferenceNodes(true)) {
			node.removeNode();
		}
	}
	
	@Test
	public void testDefault() {
		fixture.initializeTradingHistoryStartTime(prefs);
		assertThat(prefs.getDefaultString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
		assertThat(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
	}
	
	@Test
	public void testResetEmpty() {
		prefs.setValue(PhotonPreferences.TRADING_HISTORY_START_TIME, "");
		fixture.initializeTradingHistoryStartTime(prefs);
		assertThat(prefs.getDefaultString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
		assertThat(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
	}
	
	@Test
	public void testResetInvalid() {
		prefs.setValue(PhotonPreferences.TRADING_HISTORY_START_TIME, "asdf");
		fixture.initializeTradingHistoryStartTime(prefs);
		assertThat(prefs.getDefaultString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
		assertThat(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
	}
	
	@Test
	public void testPreserveLegitimate() {
		String legitimate = TimeOfDay.create(4, 0, 0, TimeZone.getDefault()).toFormattedString();
		prefs.setValue(PhotonPreferences.TRADING_HISTORY_START_TIME,  legitimate);
		fixture.initializeTradingHistoryStartTime(prefs);
		assertThat(prefs.getDefaultString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(DEFAULT_TIME));
		assertThat(prefs.getString(PhotonPreferences.TRADING_HISTORY_START_TIME), is(legitimate));
	}

}
