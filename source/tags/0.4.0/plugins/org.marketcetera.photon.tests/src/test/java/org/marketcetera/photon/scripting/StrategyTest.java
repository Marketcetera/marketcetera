package org.marketcetera.photon.scripting;

import junit.framework.TestCase;
import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.field.OrderID;
import quickfix.fix40.NewOrderSingle;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Semaphore;

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
                try {
                    registerTimedCallback(100, clientData);
                } catch (InterruptedException e) {
                    fail("strategy interrupted in callback");
                }
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
