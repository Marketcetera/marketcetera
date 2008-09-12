package org.marketcetera.photon.test.util;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * Utility methods for Photon UI testing
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class SWTTestUtil {

	/**
	 * Process UI input but do not return for the specified time interval.
	 * 
	 * Must be called from UI thread.
	 * 
	 * @param waitTimeMillis
	 *            the number of milliseconds to wait
	 */
	public static void delay(long delay, TimeUnit delayUnit) {
		Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalArgumentException("Must be called from UI thread");

		long endTimeMillis = System.currentTimeMillis()
				+ delayUnit.toMillis(delay);

		while (System.currentTimeMillis() < endTimeMillis) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.update();
	}

	/**
	 * Process UI input until either condition.call returns true or the timeout
	 * expires. If a timeout occurs, an unchecked exception will be thrown.
	 * 
	 * Must be called from UI thread.
	 * 
	 * @param timeout
	 *            the number of milliseconds before timeout
	 * @param condition
	 *            condition controlling when method should return
	 * @throws Exception
	 *             if condition throws an exception
	 */
	public static void conditionalDelay(long timeout, TimeUnit timeoutUnit,
			Callable<Boolean> condition) throws Exception {
		Display display = Display.getCurrent();
		if (display == null)
			throw new IllegalArgumentException("Must be called from UI thread");

		long endTimeMillis = System.currentTimeMillis()
				+ timeoutUnit.toMillis(timeout);

		while (!condition.call()) {
			if (System.currentTimeMillis() > endTimeMillis)
				throw new RuntimeException("Timeout");
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.update();
	}

	/**
	 * A version of conditionalDelay that re-throws checked exceptions as
	 * unchecked ones. This can be used if the caller knows their condition will
	 * not throw an exception, or if they just want to let exceptions bubble up.
	 * 
	 * Must be called from UI thread.
	 * 
	 * @param maxMillis
	 *            the number of milliseconds before timeout
	 * @param condition
	 *            condition controlling when method should return
	 */
	public static void conditionalDelayUnchecked(long timeout,
			TimeUnit timeoutUnit, Callable<Boolean> condition) {
		try {
			conditionalDelay(timeout, timeoutUnit, condition);
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			throw new RuntimeException(e);
		}
	}

	/**
	 * Wait until all Eclipse jobs complete or the timeout expires. If a timeout
	 * occurs, an unchecked exception will be thrown.
	 * 
	 * Must be called from UI thread.
	 */
	public static void waitForJobs(long timeout, TimeUnit timeoutUnit) {
		conditionalDelayUnchecked(timeout, timeoutUnit,
				new Callable<Boolean>() {
					@Override
					public Boolean call() {
						return Job.getJobManager().currentJob() != null;
					}
				});
	}
}
