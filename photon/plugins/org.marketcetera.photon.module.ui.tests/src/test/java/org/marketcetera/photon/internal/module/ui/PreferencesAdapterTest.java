package org.marketcetera.photon.internal.module.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.photon.module.IModuleAttributeSupport;
import org.osgi.service.prefs.Preferences;


/* $License$ */

/**
 * Tests {@link PreferencesAdapter}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
public class PreferencesAdapterTest {

	private static final String PLUGIN_ID = "org.marketcetera.photon.module";
	PreferencesAdapter mFixture;
	IModuleAttributeSupport mAttributeSupport;
	Preferences mPreferences;
	Preferences mDefaultPreferences;
	
	@Before
	public void setUp() throws Exception {
		clearPreferences();
		mPreferences = new InstanceScope().getNode(PLUGIN_ID).node("ModuleAttributeDefaults");
		mDefaultPreferences = new DefaultScope().getNode(PLUGIN_ID).node("ModuleAttributeDefaults");
		mAttributeSupport = mock(IModuleAttributeSupport.class);
		mFixture = new PreferencesAdapter(mAttributeSupport);
	}
	
	@After
	public void tearDown() throws Exception {
		clearPreferences();
	}
	
	private void clearPreferences() throws Exception {
		new InstanceScope().getNode(PLUGIN_ID).removeNode();
		new DefaultScope().getNode(PLUGIN_ID).removeNode();
	}
	
	@Test
	public void testToTree() {
		mPreferences.node("test").node("test").put("pA", "A");
		mPreferences.node("test").node("test").put("pB", "B");
		mPreferences.node("test").node("test2").put("p2A", "A");
		mPreferences.node("test").node("test").node("%InstanceDefaults%").put("dC", "C");
		mPreferences.node("test").node("test").node("abc").put("iB", "B");
		PropertiesTree tree = mFixture.toTree();
		assertEquals(5, tree.size());
		assertEquals("A", tree.get("test.test.pA"));
		assertEquals("B", tree.get("test.test.pB"));
		assertEquals("A", tree.get("test.test2.p2A"));
		assertEquals("C", tree.get("test.test.%InstanceDefaults%.dC"));
		assertEquals("B", tree.get("test.test.abc.iB"));
	}
	
	@Test
	public void testToTreeWithDefaults() {
		mPreferences.node("test").node("test").put("pA", "A");
		mPreferences.node("test").node("test").put("pB", "B");
		mDefaultPreferences.node("test").node("test2").put("p2A", "A");
		mDefaultPreferences.node("test").node("test").node("%InstanceDefaults%").put("dC", "C");
		mDefaultPreferences.node("test").node("test").node("abc").put("iB", "B");
		PropertiesTree tree = mFixture.toTree();
		assertEquals(5, tree.size());
		assertEquals("A", tree.get("test.test.pA"));
		assertEquals("B", tree.get("test.test.pB"));
		assertEquals("A", tree.get("test.test2.p2A"));
		assertEquals("C", tree.get("test.test.%InstanceDefaults%.dC"));
		assertEquals("B", tree.get("test.test.abc.iB"));
	}
	
	@Test
	public void testFromTreeSimple() {
		PropertiesTree tree = new PropertiesTree();
		tree.put("test.test.pA", "A");
		tree.put("test.test.pB", "B");
		tree.put("test.test2.p2A", "A");
		tree.put("test.test.%InstanceDefaults%.dC", "C");
		tree.put("test.test.abc.iB", "B");
		mFixture.fromTree(tree);
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test"), "pA", "A");
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test"), "pB", "B");
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test2"), "p2A", "A");
		verify(mAttributeSupport).setInstanceDefaultFor(new ModuleURN("metc:test:test"), "dC", "C");
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test:abc"), "iB", "B");
		verifyNoMoreInteractions(mAttributeSupport);
	}
	
	@Test
	public void testFromTreeAdvanced() {
		mPreferences.node("test").node("test").put("pA", "A");
		mPreferences.node("test").node("test").put("pX", "X");
		mPreferences.node("test").node("test2").put("p2X", "X");
		mPreferences.node("test").node("test").node("%InstanceDefaults%").put("dC", "C");
		mPreferences.node("test").node("test").node("abc").put("iB", "B");
		mPreferences.node("test").node("test").node("abc").put("iX", "X");
		PropertiesTree tree = new PropertiesTree();
		tree.put("test.test.pA", "new");
		tree.put("test.test.pB", "B");
		tree.put("test.test.%InstanceDefaults%.dD", "D");
		tree.put("test.test.abc.iB", "new");
		mFixture.fromTree(tree);
		// addition/modification is done through IModuleAttributeSupport
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test"), "pA", "new");
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test"), "pB", "B");
		verify(mAttributeSupport).setInstanceDefaultFor(new ModuleURN("metc:test:test"), "dD", "D");
		verify(mAttributeSupport).setDefaultFor(new ModuleURN("metc:test:test:abc"), "iB", "new");
		verifyNoMoreInteractions(mAttributeSupport);
		// removal is done directly on the Preference object (other attributes are unchanged)
		assertEquals("A", mPreferences.node("test").node("test").get("pA", null));
		assertNull(mPreferences.node("test").node("test").get("pX", null));
		assertNull(mPreferences.node("test").node("test2").get("p2X", null));
		assertNull(mPreferences.node("test").node("test").node("%InstanceDefaults%").get("dC", null));
		assertEquals("B", mPreferences.node("test").node("test").node("abc").get("iB", null));
		assertNull(mPreferences.node("test").node("test").node("abc").get("iX", null));
	}
}
