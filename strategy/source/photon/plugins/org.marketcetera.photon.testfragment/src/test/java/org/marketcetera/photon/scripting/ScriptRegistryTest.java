package org.marketcetera.photon.scripting;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.bsf.BSFException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MockMarketDataFeed;
import org.marketcetera.marketdata.MockMarketDataFeedCredentials;
import org.marketcetera.marketdata.IFeedComponent.FeedType;
import org.marketcetera.photon.EclipseUtils;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.Bundle;

import quickfix.Message;
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

	private Classpath getClasspath() throws IOException {
		Classpath classpath = new Classpath();

		Bundle bundle = PhotonPlugin.getDefault().getBundle();
		Enumeration<URL> entries = bundle.findEntries("/lib", "requiring.rb", true);
		URL entryURL = entries.nextElement();
			
		IPath path = Path.fromOSString(FileLocator.toFileURL(entryURL).getFile()).removeLastSegments(1);
		classpath.add(path);

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
    
    /**
     * A test implementation of <code>ScriptRegistry</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.43-SNAPSHOT
     */
    public static class TestScriptRegistry
        extends ScriptRegistry
    {
        /**
         * tracks all market data messages received from data feeds
         */
        private final List<Message> mMessagesReceived = new ArrayList<Message>();
        /**
         * Create a new TestScriptRegistry instance.
         *
         */
        public TestScriptRegistry()
        {
            super();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.photon.scripting.ScriptRegistry#onMarketDataEvent(quickfix.Message)
         */
        @Override
        public void onMarketDataEvent(Message inMessage)
        {
            synchronized(mMessagesReceived) {
                mMessagesReceived.add(inMessage);
            }
            super.onMarketDataEvent(inMessage);
        }
        /**
         * Returns a list of the messages received from all data feeds connected to the registry. 
         *
         * @return a <code>List&lt;Message&gt;</code> value
         */
        public List<Message> getMessagesReceived()
        {
            synchronized(mMessagesReceived) {
                return new ArrayList<Message>(mMessagesReceived);
            }
        }
        /**
         * Resets the state of the registry.
         */
        public void reset()
        {
            synchronized(mMessagesReceived) {
                mMessagesReceived.clear();
            }
        }
    }
}
