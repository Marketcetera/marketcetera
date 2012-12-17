package org.marketcetera.photon.notification;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Random;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.photon.notification.preferences.NotificationPreferences;

/* $License$ */

/**
 * Test {@link NotificationPlugin}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class NotificationPluginTest {

	private NotificationPlugin fixture;
	private IPreferenceStore mockPreferences;

	@Before
	public void setUp() {
		mockPreferences = mock(IPreferenceStore.class);
		fixture = new NotificationPlugin() {
			@Override
			public IPreferenceStore getPreferenceStore() {
				return mockPreferences;
			}
		};
	}
	
	@After
	public void tearDown() {
		NotificationPlugin.setOverride(null);
	}
	
	@Test
	public void testSetOverride() {
		NotificationPlugin original = NotificationPlugin.getDefault();
		NotificationPlugin mock = mock(NotificationPlugin.class);
		NotificationPlugin.setOverride(mock);
		assertEquals(mock, NotificationPlugin.getDefault());
		NotificationPlugin.setOverride(null);
		assertEquals(original, NotificationPlugin.getDefault());
	}

	@Test
	public void testShouldDisplayPopup() {
		when(mockPreferences.getString(NotificationPreferences.PRIORITY)).thenReturn(Severity.HIGH.name());		
		assertFalse(fixture.shouldDisplayPopup(Severity.LOW));
		assertFalse(fixture.shouldDisplayPopup(Severity.MEDIUM));
		assertTrue(fixture.shouldDisplayPopup(Severity.HIGH));
		when(mockPreferences.getString(NotificationPreferences.PRIORITY)).thenReturn(Severity.MEDIUM.name());		
		assertFalse(fixture.shouldDisplayPopup(Severity.LOW));
		assertTrue(fixture.shouldDisplayPopup(Severity.MEDIUM));
		assertTrue(fixture.shouldDisplayPopup(Severity.HIGH));
		when(mockPreferences.getString(NotificationPreferences.PRIORITY)).thenReturn(Severity.LOW.name());		
		assertTrue(fixture.shouldDisplayPopup(Severity.LOW));
		assertTrue(fixture.shouldDisplayPopup(Severity.MEDIUM));
		assertTrue(fixture.shouldDisplayPopup(Severity.HIGH));
		when(mockPreferences.getString(NotificationPreferences.PRIORITY)).thenReturn("");
		assertFalse(fixture.shouldDisplayPopup(Severity.LOW));
		assertFalse(fixture.shouldDisplayPopup(Severity.MEDIUM));
		assertFalse(fixture.shouldDisplayPopup(Severity.HIGH));
	}

	@Test
	public void testShouldPlaySound() {
		for (Severity severity : Severity.values()) {
			when(mockPreferences.getBoolean(NotificationPreferences.SOUND_ENABLED_PREFIX + severity.name())).thenReturn(true);		
			assertTrue(fixture.shouldPlaySound(severity));
			when(mockPreferences.getBoolean(NotificationPreferences.SOUND_ENABLED_PREFIX + severity.name())).thenReturn(false);		
			assertFalse(fixture.shouldPlaySound(severity));
		}
	}
	
	@Test
	public void testGetSoundClip() {
		for (Severity severity : Severity.values()) {
			String string = Long.toString(new Random().nextLong());
			when(mockPreferences.getString(NotificationPreferences.SOUND_CLIP_PREFIX + severity.name())).thenReturn(string);		
			assertEquals(string, fixture.getSoundClip(severity));
		}
	}
}
