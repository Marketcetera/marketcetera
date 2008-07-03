package org.marketcetera.photon.scripting;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
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
     * Tests the ability of the <code>ScriptRegistry</code> to receive market data
     * from connected <code>IMarketDataFeed</code> objects.
     *
     * @throws Exception
     */
    public void testRegisterReceivesMarketData()
        throws Exception
    {
        /* 
         * 1) create a feed
         * 2) send messages to the feed, making sure registry doesn't receive the messages
         * 3) connect the feed
         * 4) send another message, making sure the registry does receive the message
         * 5) disconnect the feed
         * 6) send another message, making sure the registry doesn't receive the message
         */
        // step #1, the setup
        // this is the feed we'll use to connect to the registry        
        MockMarketDataFeedCredentials credentials = new MockMarketDataFeedCredentials();
        MockMarketDataFeed feed = new MockMarketDataFeed(FeedType.UNKNOWN,
                                                         credentials);
        feed.start();
        // add a subscriber so we know when the message gets through
        final MockSubscriber subscriber = new MockSubscriber();
        // this is the registry we'll use to check the messages
        final TestScriptRegistry registry = new TestScriptRegistry();
        // create three distinct messages to use, making sure they are distinguishable from each other
        Message message1 = AbstractMarketDataFeed.levelOneMarketDataRequest(Arrays.asList(new MSymbol[] { new MSymbol("GOOG") } ), 
                                                                            false);
        Message message2 = AbstractMarketDataFeed.levelOneMarketDataRequest(Arrays.asList(new MSymbol[] { new MSymbol("YHOO") } ), 
                                                                            false);
        Message message3 = AbstractMarketDataFeed.levelOneMarketDataRequest(Arrays.asList(new MSymbol[] { new MSymbol("MSFT") } ), 
                                                                            false);
        assertFalse(message1.equals(message2));
        assertFalse(message1.equals(message3));
        assertFalse(message2.equals(message3));
        // registry starts empty
        assertTrue(registry.getMessagesReceived().isEmpty());
        // subscriber has not been notified yet
        assertNull(subscriber.getData());
        // step #2 pre-test
        // send a message through the feed
        feed.execute(message1,
                     subscriber);
        // wait until the subscriber is notified
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return subscriber.getData() != null;
            }            
        });
        // make sure the subscriber got a single message
        assertEquals(1,
                     subscriber.getPublishCount());
        // make sure it was the message we expected
        assertEquals(message1,
                     ((EventBase)subscriber.getData()).getFIXMessage());
        // make sure the registry was not notified
        assertTrue(registry.getMessagesReceived().isEmpty());
        // reset the subscriber
        subscriber.reset();
        // step #3, connect the feed to the registry
        registry.connectToMarketDataFeed(feed);
        // step #4, test
        feed.execute(message2,
                     subscriber);
        // wait until the subscriber is notified
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return subscriber.getData() != null;
            }            
        });
        // wait until the registry is notified this time
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return !registry.getMessagesReceived().isEmpty();
            }            
        });
        // make sure the subscriber got a single message
        assertEquals(1,
                     subscriber.getPublishCount());
        // make sure it was the message we expected
        assertEquals(message2,
                     ((EventBase)subscriber.getData()).getFIXMessage());
        // make sure the registry *did* get this one
        assertEquals(1,
                     registry.getMessagesReceived().size());
        // make sure it was the message we expected
        assertEquals(message2,
                     registry.getMessagesReceived().get(0));
        // reset
        subscriber.reset();
        registry.reset();
        // step #5, disconnect
        registry.disconnectFromMarketDataFeed(feed);
        // step #6, re-test
        feed.execute(message3,
                     subscriber);
        // wait until the subscriber is notified
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return subscriber.getData() != null;
            }            
        });
        // make sure the subscriber got a single message
        assertEquals(1,
                     subscriber.getPublishCount());
        // make sure it was the message we expected
        assertEquals(message3,
                     ((EventBase)subscriber.getData()).getFIXMessage());
        // make sure the registry was not notified
        assertTrue(registry.getMessagesReceived().isEmpty());
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
