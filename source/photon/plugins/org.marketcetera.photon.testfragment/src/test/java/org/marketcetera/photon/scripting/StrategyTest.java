package org.marketcetera.photon.scripting;

import static org.marketcetera.photon.Messages.DEBUG_SUBJECT;
import static org.marketcetera.photon.Messages.ERROR_SUBJECT;
import static org.marketcetera.photon.Messages.INFO_SUBJECT;
import static org.marketcetera.photon.Messages.WARN_SUBJECT;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

import junit.framework.TestCase;

import org.marketcetera.core.notifications.INotification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.core.publisher.MockSubscriber;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.util.misc.RandomStrings;

import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.OrderID;
import quickfix.fix40.NewOrderSingle;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class StrategyTest extends TestCase {
	
	public void testCallbackTimeout() throws Exception
	{
		long start = System.currentTimeMillis();
		final Integer origClientData = 37;
		final Semaphore sema = new Semaphore(0);
        TestStrategy strategy = new TestStrategy() {
			public void timeout_callback(Object clientData) {
				assertEquals(origClientData, clientData);
				sema.release();
			}
		};
		// fake the registration to always show up as registered for this test
		strategy.setName(getName());
		strategy.setRegistry(new ScriptRegistry() {
			protected Boolean doIsRegistered(String requireString) {
                return true;
            }
        });
		strategy.registerTimedCallback(1000,37);
		sema.acquire();
		long now = System.currentTimeMillis();
		assertTrue("Didn't wait long enough: "+(now - start)+" milliseconds", now - start > 999);
		assertTrue("Waited too long: "+(now - start)+" milliseconds", now - start < 1500);
	}

    /** Register the script, then unregister it and make sure the callback doesn't get called again
     * Verify that we only have 5 times in the call-back function
     */
    public void testUnregisteredScriptCallback() throws Exception {
        ArrayList<String> callbackCounter = new ArrayList<String>();
        TestStrategy strategy = new TestStrategy() {
            public void timeout_callback(Object clientData) {
				((ArrayList<String>)clientData).add("in-call-back");
                registerTimedCallback(100, clientData);
            }
		};
        assertEquals(0, callbackCounter.size());
        strategy.setRegistry(new ScriptRegistry() {
            private int counter = 0;
            /** make it 'unregistered' after 5 runs */
            protected Boolean doIsRegistered(String requireString) {
                return (counter++ < 5);
            }
        });
        strategy.registerTimedCallback(100, callbackCounter);
        Thread.sleep(1000);
        assertEquals("strategy called wrong number of times", 5, callbackCounter.size());
    }

    /** Verify that only execreports with LastPx != 0 go through */
	public void testExecutionReports()
	{
		TestStrategy strategy = new TestStrategy();
		strategy.on_fix_message(new MarketDataRequest());
		assertEquals("non-execReport went through", 0, strategy.execReports.size());
		assertEquals("execReport went through", 0, strategy.mdSnapshots.size());
		
		strategy.on_fix_message(new ExecutionReport());
		assertEquals("empty execReport went through", 0, strategy.execReports.size());
		assertEquals("execReport went through", 0, strategy.mdSnapshots.size());
		
		Message execReport = new ExecutionReport();
		execReport.setField(new OrderID("abcd"));
		execReport.setField(new LastPx(0));
		strategy.on_fix_message(execReport);
		assertEquals("0 execReport went through", 0, strategy.execReports.size());
		assertEquals("execReport went through", 0, strategy.mdSnapshots.size());
		
		execReport.setField(new LastPx(37));
		strategy.on_fix_message(execReport);
		assertEquals("non-zero execReport didn't go through", 1, strategy.execReports.size());
		assertEquals("snapshot went through", 0, strategy.mdSnapshots.size());
		
		execReport.removeField(OrderID.FIELD);
		strategy.clear();
		strategy.on_fix_message(execReport);
		assertEquals("execReport without OrderID went through", 0, strategy.execReports.size());
		assertEquals("snapshot went through", 0, strategy.mdSnapshots.size());
	}
	
	public void testMDSnapshotRefresh()
	{
		TestStrategy strategy = new TestStrategy();
		strategy.on_market_data_message(new NewOrderSingle());
		assertEquals("non-execReport went through", 0, strategy.execReports.size());
		assertEquals("non-mdSnapshotRefresh went through", 0, strategy.mdSnapshots.size());
		
		strategy.on_market_data_message(new MarketDataSnapshotFullRefresh());
		assertEquals("non-execReport went through", 0, strategy.execReports.size());
		assertEquals("mdSnapshotRefresh didn't go through", 1, strategy.mdSnapshots.size());
	}
	/**
	 * Tests the ability of a strategy to produce notifications.
	 *
	 * @throws Exception
	 */
	public void testNotifications()
	    throws Exception
	{
        TestStrategy strategy = new TestStrategy();
        final MockSubscriber subscriber = new MockSubscriber();
        assertNull(subscriber.getData());
        assertEquals(0,
                     subscriber.getPublishCount());
        // notifications going nowhere still succeed
        doNotification(strategy);
        // not deterministic, but far longer than it would take to execute the publication
        Thread.sleep(1000);
        // make sure the publication didn't go through
        assertNull(subscriber.getData());
        assertEquals(0,
                     subscriber.getPublishCount());
        // now, subscribe to notifications
        NotificationManager.getNotificationManager().subscribe(subscriber);
        // reset the subscriber, even though it shouldn't be necessary since the subscriber hasn't received anything yet
        subscriber.reset();
        assertNull(subscriber.getData());
        assertEquals(0,
                     subscriber.getPublishCount());
        // notify again
        NotificationTuple tuple = doNotification(strategy);
        // wait until the subscriber gets notified (have to use a special incantation to get the right number of notifications)
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return subscriber.getPublishCount() == 8;
            }
        });
        // check the publications we got to make sure we got what we expected
        // count the number of notifications of each severity
        int errorCount = 0;
        int warnCount = 0;
        int infoCount = 0;
        int debugCount = 0;
        // check for the occurrence of one each of the permutations of severity and default vs. custom subject lines 
        boolean defaultErrorFound = false;
        boolean customErrorFound = false;
        boolean defaultWarningFound = false;
        boolean customWarningFound = false;
        boolean defaultInfoFound = false;
        boolean customInfoFound = false;
        boolean defaultDebugFound = false;
        boolean customDebugFound = false;
        // examine each of the publications captured by our subscriber
        for(Object publication : subscriber.getPublications()) {
            // publication must be an INotification
            assertTrue(publication instanceof INotification);
            INotification notification = (INotification)publication;
            // the body must be the one indicated by the returned tuple
            assertEquals(notification.getBody(),
                         tuple.getBody());
            assertEquals(StrategyNotification.class,
                         notification.getOriginator());
            // check the severity and increment the markers accordingly
            // check error
            if(notification.getSeverity().equals(Severity.ERROR)) {
                errorCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(ERROR_SUBJECT.getText())) {
                    defaultErrorFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customErrorFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
            // check warning
            if(notification.getSeverity().equals(Severity.WARNING)) {
                warnCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(WARN_SUBJECT.getText())) {
                    defaultWarningFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customWarningFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
            // check info
            if(notification.getSeverity().equals(Severity.INFO)) {
                infoCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(INFO_SUBJECT.getText())) {
                    defaultInfoFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customInfoFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
            // check debug
            if(notification.getSeverity().equals(Severity.DEBUG)) {
                debugCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(DEBUG_SUBJECT.getText())) {
                    defaultDebugFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customDebugFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
        }
        // now make sure the markers are correct
        assertEquals(2,
                     errorCount);
        assertEquals(2,
                     warnCount);
        assertEquals(2,
                     infoCount);
        assertEquals(2,
                     debugCount);
        assertTrue(defaultErrorFound);
        assertTrue(customErrorFound);
        assertTrue(defaultWarningFound);
        assertTrue(customWarningFound);
        assertTrue(defaultInfoFound);
        assertTrue(customInfoFound);
        assertTrue(defaultDebugFound);
        assertTrue(customDebugFound);
        // if all this passed, then we got the notifications we were supposed to get and no more
	}
	/**
	 * Combines the textual components of an {@link INotification}.
	 *
	 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
	 * @version $Id$
	 * @since $Release$
	 */
	private static class NotificationTuple
	{
	    private final String mSubject;
        private final String mBody;
	    private NotificationTuple(String inSubject,
	                              String inBody)
	    {
	        mSubject = inSubject;
	        mBody = inBody;
	    }
	    private String getSubject()
	    {
	        return mSubject;
	    }
	    private String getBody()
	    {
	        return mBody;
	    }
	}
	/**
	 * Executes a round of notifications at various severity levels.
	 *
	 * @param inStrategy a <code>Strategy</code> value containing the script to issue the notifications
	 */
	private NotificationTuple doNotification(Strategy inStrategy)
	{
	    String body = RandomStrings.genStrDefCharset();
	    String subject = RandomStrings.genStrDefCharset();
	    inStrategy.error(body);
        inStrategy.error(subject,
                         body);
        inStrategy.warn(body);
        inStrategy.warn(subject,
                        body);
        inStrategy.info(body);
        inStrategy.info(subject,
                        body);
        inStrategy.debug(body);
        inStrategy.debug(subject,
                         body);
        return new NotificationTuple(subject,
                                     body);
	}
	
	public static class TestStrategy extends Strategy {
		private Vector<Message> mdSnapshots = new Vector<Message>();
		private Vector<Message> execReports = new Vector<Message>();
		private ScriptRegistry registry = new ScriptRegistry();

        @Override
		public void on_execution_report(Message message) {
			execReports.add(message);
		}

		@Override
		public void on_market_data_snapshot(Message message) {
			mdSnapshots.add(message);
		}

        protected ScriptRegistry getScriptRegistry() {
            return registry;
        }

        public void setRegistry(ScriptRegistry registry)
        {
            this.registry = registry;
        }
        public void clear()
		{
			mdSnapshots.clear();
			execReports.clear();
		}
	}
}
