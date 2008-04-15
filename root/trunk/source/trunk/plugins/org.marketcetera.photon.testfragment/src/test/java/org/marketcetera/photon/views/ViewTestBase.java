package org.marketcetera.photon.views;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.photon.PhotonPlugin;

public abstract class ViewTestBase extends TestCase {

	private IViewPart testView;

	public ViewTestBase(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		waitForJobs();
		if (PhotonPlugin.getDefault().getStockOrderTicketModel() == null){
			PhotonPlugin.getDefault().initOrderTickets();
		}
		testView = PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			showView(getViewID());
		waitForJobs();
	}

	protected abstract String getViewID();

	@Override
	protected void tearDown() throws Exception {
		waitForJobs();
		
		PlatformUI.
			getWorkbench().
			getActiveWorkbenchWindow().
			getActivePage().
			hideView(testView);
		
		super.tearDown();
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
