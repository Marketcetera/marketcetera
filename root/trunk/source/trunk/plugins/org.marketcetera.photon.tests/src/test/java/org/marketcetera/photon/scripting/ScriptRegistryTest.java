package org.marketcetera.photon.scripting;

import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.bsf.BSFManager;
import org.marketcetera.core.MMapEntry;
import org.marketcetera.core.MarketceteraTestSuite;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class ScriptRegistryTest extends TestCase {

	private final class MockScriptRegistry extends ScriptRegistry {
		public void unifyScriptLists(EventList<Entry<String, String>> newScripts, EventList<Entry<IScript, BSFManager>> existingScripts)
		{
			super.unifyScriptLists(newScripts, existingScripts);
		}

		@Override
		protected Script loadScript(String scriptWorkspacePath) {
			return new MockScript(scriptWorkspacePath, scriptWorkspacePath);
		}
	}

	public ScriptRegistryTest(){
		
	}
	
	public static Test suite()
    {
        MarketceteraTestSuite suite = new MarketceteraTestSuite(ScriptRegistryTest.class);
        return suite;
    }

	public void testUnifyLists() throws Exception {
		MockScriptRegistry registry = new MockScriptRegistry();
		
		EventList<Entry<String, String>> newScripts = new BasicEventList<Entry<String,String>>();
		EventList<Entry<IScript, BSFManager>> existingScripts = new BasicEventList<Entry<IScript, BSFManager>>();

		newScripts.add(new MMapEntry<String, String>("a/b/c", "qwer"));
		newScripts.add(new MMapEntry<String, String>("f/G/h", "poiu"));		
		registry.unifyScriptLists(newScripts, existingScripts);
		assertEquals(2, existingScripts.size());
		assertEquals("qwer", existingScripts.get(0).getKey().getID());
		assertEquals("poiu", existingScripts.get(1).getKey().getID());
		
		existingScripts.add(new MMapEntry<IScript, BSFManager>(new MockScript("lkjh", "kljh"), new BSFManager()));
		registry.unifyScriptLists(newScripts, existingScripts);
		assertEquals(2, existingScripts.size());
		assertEquals("qwer", existingScripts.get(0).getKey().getID());
		assertEquals("poiu", existingScripts.get(1).getKey().getID());

	}
}
