package org.marketcetera.photon.scripting;

import static org.marketcetera.photon.Messages.*;

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
                return subscriber.getPublishCount() == 6;
            }
        });
        // check the publications we got to make sure we got what we expected
        // count the number of notifications of each severity
        int highCount = 0;
        int mediumCount = 0;
        int lowCount = 0;
        // check for the occurrence of one each of the permutations of severity and default vs. custom subject lines 
        boolean defaultHighFound = false;
        boolean customHighFound = false;
        boolean defaultMediumFound = false;
        boolean customMediumFound = false;
        boolean defaultLowFound = false;
        boolean customLowFound = false;
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
            // check high
            if(notification.getSeverity().equals(Severity.HIGH)) {
                highCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(HIGH_PRIORITY_SUBJECT.getText())) {
                    defaultHighFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customHighFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
            // check medium
            if(notification.getSeverity().equals(Severity.MEDIUM)) {
                mediumCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(MEDIUM_PRIORITY_SUBJECT.getText())) {
                    defaultMediumFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customMediumFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
            // check low
            if(notification.getSeverity().equals(Severity.LOW)) {
                lowCount += 1;
                String subject = notification.getSubject();
                if(subject.equals(LOW_PRIORITY_SUBJECT.getText())) {
                    defaultLowFound = true;
                } else if(subject.equals(tuple.getSubject())) {
                    customLowFound = true;
                } else {
                    fail("Unexpected subject: " + subject);
                }
            }
        }
        // now make sure the markers are correct
        assertEquals(2,
                     highCount);
        assertEquals(2,
                     mediumCount);
        assertEquals(2,
                     lowCount);
        assertTrue(defaultHighFound);
        assertTrue(customHighFound);
        assertTrue(defaultMediumFound);
        assertTrue(customMediumFound);
        assertTrue(defaultLowFound);
        assertTrue(customLowFound);
        // if all this passed, then we got the notifications we were supposed to get and no more
	}
	/**
	 * Combines the textual components of an {@link INotification}.
	 *
	 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
	 * @version $Id$
	 * @since 0.8.0
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
	    inStrategy.notify_high(body);
        inStrategy.notify_high(subject,
                               body);
        inStrategy.notify_medium(body);
        inStrategy.notify_medium(subject,
                                 body);
        inStrategy.notify_low(body);
        inStrategy.notify_low(subject,
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
