package org.marketcetera.photon.views;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;

import junit.framework.TestCase;

public abstract class ViewTestBase extends TestCase {

	private IViewPart testView;

	public ViewTestBase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		waitForJobs();
		testView = PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(getViewID());
		waitForJobs();
		delay(3000);
	}

	protected abstract String getViewID();

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		waitForJobs();
		
		PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			hideView(testView);
	}

	protected void delay(long millis) {
		Display display = Display.getCurrent();
		
		if (display != null){
			long endTimeMillis = 
				System.currentTimeMillis() + millis;
			while (System.currentTimeMillis() < endTimeMillis)
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
				display.update();
			}
		} else {
			try 
			{
				Thread.sleep(millis);
			} catch (InterruptedException ex){
			}
		}
	}

	public void waitForJobs() {
		while (Platform.getJobManager().currentJob() != null){
			delay(1000);
		}
	}

	public IViewPart getTestView() {
		return testView;
	}

}
