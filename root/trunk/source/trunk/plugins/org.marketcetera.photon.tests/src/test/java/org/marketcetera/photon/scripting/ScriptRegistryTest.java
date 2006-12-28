package org.marketcetera.photon.scripting;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.eclipse.core.runtime.IPath;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonTestPlugin;

import quickfix.fix42.ExecutionReport;

public class ScriptRegistryTest extends TestCase {

	class MockScriptRegistry extends ScriptRegistry {
		public Object evalScript(String script) throws BSFException{
			return bsfManager.eval(RUBY_LANG_STRING, "<java>", 1, 1, script);
		}
	}
	
	public ScriptRegistryTest(){
		
	}
	
	public static Test suite()
    {
        MarketceteraTestSuite suite = new MarketceteraTestSuite(ScriptRegistryTest.class);
        return suite;
    }

	public void testBasicScript() throws Throwable {
		try {
			MockScriptRegistry registry = new MockScriptRegistry();
	
			Classpath classpath = getClasspath();
			registry.setAdditionalClasspath(classpath);
			registry.afterPropertiesSet();
	
			registry.evalScript("$quote_count = 0");
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));
			registry.unregister("test_script");
			assertTrue(!registry.isRegistered("test_script"));
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));
			registry.onEvent(new ExecutionReport());
			Long quoteCountObj = (Long)registry.evalScript("$quote_count");
			assertNotNull(quoteCountObj);
			long quoteCount = (long)quoteCountObj;
			assertEquals((long)1, quoteCount);
		} catch (BSFException ex) {
			throw ex.getTargetException();
		}
		
	}

	private Classpath getClasspath() {
		Classpath classpath = new Classpath();

		IPath pluginPath = EclipseUtils.getPluginPath(PhotonTestPlugin.getDefault());
		pluginPath = pluginPath.append("lib");
		classpath.add(pluginPath);

		IPath photonPluginPath = EclipseUtils.getPluginPath(PhotonPlugin.getDefault());
		photonPluginPath = photonPluginPath.append("src").append("main").append("resources");
		classpath.add(photonPluginPath);
		return classpath;
	}

	public void testSubdirScript() throws Throwable {
		try {
			MockScriptRegistry registry = new MockScriptRegistry();
	
			Classpath classpath = getClasspath();
			registry.setAdditionalClasspath(classpath);
			registry.afterPropertiesSet();
	
			registry.evalScript("$quote_count = 0");
			registry.register("subdir/test_script");
			assertTrue(registry.isRegistered("subdir/test_script"));
			registry.unregister("subdir/test_script");
			assertTrue(!registry.isRegistered("subdir/test_script"));
			registry.register("subdir/test_script");
			assertTrue(registry.isRegistered("subdir/test_script"));
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));

			registry.onEvent(new ExecutionReport());
			Long quoteCountObj = (Long)registry.evalScript("$quote_count");
			assertNotNull(quoteCountObj);
			long quoteCount = (long)quoteCountObj;
			assertEquals((long)82, quoteCount);
		} catch (BSFException ex) {
			throw ex.getTargetException();
		}
		
	}

	public void testRequiringScript() throws Throwable {
		try {
			MockScriptRegistry registry = new MockScriptRegistry();
	
			Classpath classpath = getClasspath();
			registry.setAdditionalClasspath(classpath);
			registry.afterPropertiesSet();
	
			registry.evalScript("require 'requiring'");
			Object result = registry.evalScript("Requiring.new.compute(0)");
			assertEquals(2, (long)(Long)result);
		} catch (BSFException ex) {
			throw ex.getTargetException();
		}
	}
		
}
