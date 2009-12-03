package org.marketcetera.photon.test;

import static org.junit.Assert.*;
import org.junit.Assert;

/**
 * Base class for multi-threaded tests. Provides subclasses with the following
 * features:
 * <ul>
 * <li>A mechanism for reporting exceptions - JUnit only reports uncaught
 * exceptions thrown in its thread. Other threads can catch exceptions and call
 * {@link #setFailure} to record an uncaught exception, which can be later
 * checked from the JUnit thread using {@link #checkFailure}.</li>
 * </ul>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
public class MultiThreadedTestBase {

	/**
	 * Caches the first reported exception.
	 */
	private Throwable mFailure;

	/**
	 * Records an exception to report to JUnit. Only the first reported
	 * exception is stored.
	 * 
	 * @param failure
	 *            the exception to record
	 */
	protected synchronized void setFailure(Throwable failure) {
		if (this.mFailure == null)
			this.mFailure = failure;
	}

	/**
	 * If an exception was recorded, it will be wrapped and thrown as a
	 * {@link RuntimeException}. This should be called from the main JUnit
	 * thread so the exception will be reported.
	 * 
	 * @throws RuntimeException
	 *             if an exception was recorded by this class
	 */
	protected synchronized void checkFailure() {
		if (mFailure != null)
			throw new RuntimeException(mFailure);
	}

	/**
	 * Convenience method to check for reported failures before calling
	 * {@link Assert#assertEquals(Object, Object)}.
	 * 
	 * @param expected
	 *            expected value
	 * @param actual
	 *            value to check against <code>expected</code>
	 */
	protected void checkFailureAndAssertEquals(Object expected, Object actual) {
		checkFailure();
		assertEquals(expected, actual);
	}

	/**
	 * Convenience method to check for reported failures before calling
	 * {@link Assert#assertTrue(boolean)}.
	 * 
	 * @param condition
	 *            condition to be checked
	 */
	protected void checkFailureAndAssertTrue(boolean condition) {
		checkFailure();
		assertTrue(condition);
	}
	
	/**
	 * Convenience thread that reports any exceptions thrown by a runnable.
	 */
	protected abstract class ReportingThread extends Thread {

		public ReportingThread() {
			super();
		}

		public ReportingThread(String name) {
			super(name);
		}

		@Override
		public final void run() {
			try {
				runWithReporting();
			} catch (Exception e) {
				setFailure(e);
			}
		}

		/**
		 * Hook for subclass code to run. Any thrown exception will be reported.
		 */
		protected abstract void runWithReporting() throws Exception;
	}

}
