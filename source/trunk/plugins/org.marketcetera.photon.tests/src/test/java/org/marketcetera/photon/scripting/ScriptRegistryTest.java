package org.marketcetera.photon.scripting;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.PhotonTestPlugin;

import quickfix.fix42.ExecutionReport;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class ScriptRegistryTest extends TestCase {
	public ScriptRegistryTest(){
		PhotonPlugin.getDefault().ensureDefaultProject(ProgressManager.getInstance().getDefaultMonitor());
	}
	
	public static Test suite()
    {
        MarketceteraTestSuite suite = new MarketceteraTestSuite(ScriptRegistryTest.class);
        return suite;
    }

	public void testBasicScript() throws Throwable {
		try {
			ScriptRegistry registry = new ScriptRegistry();
	
			Classpath classpath = getClasspath();
			registry.setAdditionalClasspath(classpath);
			registry.afterPropertiesSet();
	
			registry.evalScript("$quote_count = 0");
			registry.evalScript("$fix_count = 0");
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));
			registry.unregister("test_script");
			assertTrue(!registry.isRegistered("test_script"));
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));
			registry.onMarketDataEvent(new MarketDataSnapshotFullRefresh());
			Long quoteCountObj = (Long)registry.evalScript("$quote_count");
			assertNotNull(quoteCountObj);
			long quoteCount = (long)quoteCountObj;
			assertEquals((long)1, quoteCount);

			registry.onFIXEvent(new ExecutionReport());
			Long fixCountObj = (Long)registry.evalScript("$fix_count");
			assertNotNull(fixCountObj);
			long fixCount = (long)fixCountObj;
			assertEquals((long)1, fixCount);
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
			ScriptRegistry registry = new ScriptRegistry();
	
			Classpath classpath = getClasspath();
			registry.setAdditionalClasspath(classpath);
			registry.afterPropertiesSet();
	
			registry.evalScript("$quote_count = 0");
			registry.evalScript("$fix_count = 0");
			registry.register("subdir/test_script");
			assertTrue(registry.isRegistered("subdir/test_script"));
			registry.unregister("subdir/test_script");
			assertTrue(!registry.isRegistered("subdir/test_script"));
			registry.register("subdir/test_script");
			assertTrue(registry.isRegistered("subdir/test_script"));
			registry.register("test_script");
			assertTrue(registry.isRegistered("test_script"));

			registry.onMarketDataEvent(new MarketDataSnapshotFullRefresh());
			Long quoteCountObj = (Long)registry.evalScript("$quote_count");
			assertNotNull(quoteCountObj);
			long quoteCount = (long)quoteCountObj;
			assertEquals((long)82, quoteCount);

			registry.onFIXEvent(new ExecutionReport());
			Long fixCountObj = (Long)registry.evalScript("$fix_count");
			assertNotNull(fixCountObj);
			long fixCount = (long)fixCountObj;
			assertEquals((long) 82, fixCount);

		} catch (BSFException ex) {
			throw ex.getTargetException();
		}
		
	}

	public void testRequiringScript() throws Throwable {
		try {
			ScriptRegistry registry = new ScriptRegistry();
	
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

    public void testRegisterTimeoutCallback() throws Exception {
        final Vector<String> counter = new Vector<String>();
        ScriptRegistry registry = new ScriptRegistry() {
            public Boolean doIsRegistered(String requireString) {
                return counter.size() == 0;
            }
        };
        Strategy strategy = new StrategyTest.TestStrategy() {
            public void timeout_callback(Object clientData) {
                ((Vector<String>)clientData).add("inside");
            }
        };
        strategy.setName("bob");
        registry.registerTimedCallback(strategy, 50, TimeUnit.MILLISECONDS, counter);
        Thread.sleep(1000);
        assertEquals(1, counter.size());

        /** Now reset and run with unregistered script */
        registry.registerTimedCallback(strategy, 50, TimeUnit.MILLISECONDS, counter);
        Thread.sleep(1000);
        assertEquals("Shoudln't have called into callback again", 1, counter.size());
    }
}
