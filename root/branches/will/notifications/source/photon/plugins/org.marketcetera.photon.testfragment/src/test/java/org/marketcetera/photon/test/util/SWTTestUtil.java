package org.marketcetera.photon.test.util;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * Utility methods for Photon testing
 *
 * @author will@marketcetera.com
 */
public class SWTTestUtil {
	
	/**
	 * Process UI input but do not return for the specified
	 * time interval.
	 * 
	 * @param waitTimeMillis the number of milliseconds
	 * @throws InterruptedException
	 * 
	 * @author from Building Commercial-Quality Plug-ins 2nd Edition
	 */
	public static void delay(long waitTimeMillis) {
		Display display = Display.getCurrent();
		
		// If this is the UI thread,
		// then process input.
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			display.update();
		}
		// Otherwise perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				fail("Unexpected interrupt");
			}
		}
	}
	
	/**
	 * Wait until all background tasks complete.
	 * 
	 * @author from Building Commercial-Quality Plug-ins 2nd Edition
	 */
	public static void waitForJobs() {
		while (Job.getJobManager().currentJob() != null)
			delay(1000);
	}
}
