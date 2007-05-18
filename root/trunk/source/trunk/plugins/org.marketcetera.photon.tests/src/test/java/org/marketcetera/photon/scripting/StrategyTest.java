package org.marketcetera.photon.scripting;

import java.util.Vector;
import java.util.concurrent.Semaphore;

import junit.framework.TestCase;
import quickfix.Message;
import quickfix.field.LastPx;
import quickfix.fix40.NewOrderSingle;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class StrategyTest extends TestCase {
	
	public void testCallbackTimeout() throws Exception
	{
		long start = System.currentTimeMillis();
		final Integer origClientData = new Integer(37);
		final Semaphore sema = new Semaphore(0);
		TestStrategy strategy = new TestStrategy() {
			@Override
			public void timeout_callback(Object clientData) {
				assertEquals(origClientData, clientData);
				sema.release();
			}
		};
		strategy.registerTimedCallback(1000, new Integer(37));
		sema.acquire();
		long now = System.currentTimeMillis();
		assertTrue("Didn't wait long enough: "+(now - start)+" milliseconds", now - start > 999);
		assertTrue("Waited too long: "+(now - start)+" milliseconds", now - start < 1500);
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
		execReport.setField(new LastPx(0));
		strategy.on_fix_message(execReport);
		assertEquals("0 execReport went through", 0, strategy.execReports.size());
		assertEquals("execReport went through", 0, strategy.mdSnapshots.size());
		
		execReport.setField(new LastPx(37));
		strategy.on_fix_message(execReport);
		assertEquals("non-zero execReport didn't go through", 1, strategy.execReports.size());
		assertEquals("execReport went through", 0, strategy.mdSnapshots.size());
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

	
	
	private class TestStrategy extends Strategy {
		private Vector<Message> mdSnapshots = new Vector<Message>();
		private Vector<Message> execReports = new Vector<Message>();
		
		@Override
		public void on_execution_report(Message message) {
			execReports.add(message);
		}

		@Override
		public void on_market_data_snapshot(Message message) {
			mdSnapshots.add(message);
		}
		
		public void clear()
		{
			mdSnapshots.clear();
			execReports.clear();
		}
	}
}
