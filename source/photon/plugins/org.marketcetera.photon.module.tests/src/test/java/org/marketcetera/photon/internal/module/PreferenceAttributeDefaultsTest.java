package org.marketcetera.photon.internal.module;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.module.IModuleAttributeDefaults;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/* $License$ */

/**
 * Test {@link PreferenceAttributeDefaults}
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
public class PreferenceAttributeDefaultsTest {

	private PreferenceAttributeDefaults mFixture;

	@Before
	public void setUp() throws Exception {
		clearPreferences();
		mFixture = new PreferenceAttributeDefaults();
	}

	private void clearPreferences() throws BackingStoreException {
		new InstanceScope().getNode(Activator.PLUGIN_ID).node(
				"ModuleAttributeDefaults").removeNode();
		new DefaultScope().getNode(Activator.PLUGIN_ID).node(
				"ModuleAttributeDefaults").removeNode();
	}

	@After
	public void tearDown() throws Exception {
		clearPreferences();
	}

	@Test
	public void singleInstance() {
		final ModuleURN instance = new ModuleURN("metc:test:test:test");
		mFixture.setDefaultFor(instance, "testA", "A");
		assertEquals("A", mFixture.getDefaultFor(instance, "testA"));
		assertEquals(null, mFixture.getDefaultFor(instance, "testB"));
		mFixture.removeDefaultFor(instance, "testA");
		assertEquals(null, mFixture.getDefaultFor(instance, "testA"));
	}

	@Test
	public void singleProvider() {
		final ModuleURN provider = new ModuleURN("metc:test:test");
		mFixture.setDefaultFor(provider, "testA", "A");
		assertEquals("A", mFixture.getDefaultFor(provider, "testA"));
		assertEquals(null, mFixture.getDefaultFor(provider, "testB"));
		mFixture.removeDefaultFor(provider, "testA");
		assertEquals(null, mFixture.getDefaultFor(provider, "testA"));
	}

	@Test
	public void instanceDefaults() {
		final ModuleURN provider = new ModuleURN("metc:test:test");
		final ModuleURN instance1 = new ModuleURN(provider, "test");
		final ModuleURN instance2 = new ModuleURN(provider, "test2");
		mFixture.setDefaultFor(instance1, "testA", "A");
		mFixture.setInstanceDefaultFor(provider, "testA", "B");
		assertEquals("A", mFixture.getDefaultFor(instance1, "testA"));
		assertEquals("B", mFixture.getDefaultFor(instance2, "testA"));
		mFixture.removeInstanceDefaultFor(provider, "testA");
		assertEquals(null, mFixture.getDefaultFor(instance2, "testA"));
	}

	@Test
	public void multipleAttributes() {
		final ModuleURN provider1 = new ModuleURN("metc:test:test");
		final ModuleURN provider2 = new ModuleURN("metc:abc:test");
		final ModuleURN provider3 = new ModuleURN("metc:abc:abc");
		final ModuleURN instance1a = new ModuleURN(provider1, "1a");
		final ModuleURN instance1b = new ModuleURN(provider1, "1b");
		final ModuleURN instance2 = new ModuleURN(provider2, "2");
		final ModuleURN instance3a = new ModuleURN(provider3, "3a");
		final ModuleURN instance3b = new ModuleURN(provider3, "3b");
		mFixture.setDefaultFor(provider1, "testA", "p1A");
		mFixture.setDefaultFor(provider1, "testB", "p1B");
		mFixture.setDefaultFor(provider2, "testA", "p2A");
		mFixture.setDefaultFor(provider2, "testC", "p2C");
		mFixture.setDefaultFor(provider3, "testA", "p3A");
		mFixture.setDefaultFor(instance1a, "testA", "i1aA");
		mFixture.setDefaultFor(instance1b, "testB", "i1bB");
		mFixture.setDefaultFor(instance2, "testA", "i2A");
		mFixture.setDefaultFor(instance3a, "testC", "i3aC");
		mFixture.setDefaultFor(instance3b, "testC", "i3bC");
		assertEquals("p1A", mFixture.getDefaultFor(provider1, "testA"));
		assertEquals("p1B", mFixture.getDefaultFor(provider1, "testB"));
		assertEquals("p2A", mFixture.getDefaultFor(provider2, "testA"));
		assertEquals("p2C", mFixture.getDefaultFor(provider2, "testC"));
		assertEquals("p3A", mFixture.getDefaultFor(provider3, "testA"));
		assertEquals("i1aA", mFixture.getDefaultFor(instance1a, "testA"));
		assertEquals("i1bB", mFixture.getDefaultFor(instance1b, "testB"));
		assertEquals("i2A", mFixture.getDefaultFor(instance2, "testA"));
		assertEquals("i3aC", mFixture.getDefaultFor(instance3a, "testC"));
		assertEquals("i3bC", mFixture.getDefaultFor(instance3b, "testC"));
	}

	@Test
	public void defaultScope() {
		final ModuleURN provider = new ModuleURN("metc:test:test");
		final ModuleURN instance = new ModuleURN(provider, "test");
		Preferences rootNode = new DefaultScope().getNode(Activator.PLUGIN_ID)
				.node("ModuleAttributeDefaults").node("test").node("test");
		rootNode.put("testA", "dpA");
		rootNode.put("testB", "dpB");
		mFixture.setDefaultFor(provider, "testB", "pB");
		rootNode.node("test").put("testA", "dA");
		rootNode.node("test").put("testB", "dB");
		mFixture.setDefaultFor(instance, "testB", "B");
		rootNode.node(IModuleAttributeDefaults.INSTANCE_DEFAULTS_IDENTIFIER)
				.put("itestA", "diA");
		rootNode.node(IModuleAttributeDefaults.INSTANCE_DEFAULTS_IDENTIFIER)
				.put("itestB", "diB");
		mFixture.setInstanceDefaultFor(provider, "itestB", "iB");
		assertEquals("dpA", mFixture.getDefaultFor(provider, "testA"));
		assertEquals("pB", mFixture.getDefaultFor(provider, "testB"));
		assertEquals("dA", mFixture.getDefaultFor(instance, "testA"));
		assertEquals("B", mFixture.getDefaultFor(instance, "testB"));
		assertEquals("diA", mFixture.getDefaultFor(instance, "itestA"));
		assertEquals("iB", mFixture.getDefaultFor(instance, "itestB"));
	}
	
	@Test
	public void invalidURN() throws Exception {
		new ExpectedFailure<IllegalArgumentException>("invalid") {
			@Override
			protected void run() throws Exception {
				mFixture.setInstanceDefaultFor(new ModuleURN("invalid"), "abc", "xyz");
			}	
		};
		new ExpectedFailure<IllegalArgumentException>("metc:test:test:test") {
			@Override
			protected void run() throws Exception {
				mFixture.removeInstanceDefaultFor(new ModuleURN("metc:test:test:test"), "abc");
			}	
		};
	}
}
