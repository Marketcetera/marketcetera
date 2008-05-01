package org.marketcetera.photon.views;

import java.util.concurrent.Callable;

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

    /**
     * Waits until the passed block evaluates to <code>true</code> while
     * allowing the system display queue to execute events.
     * 
     * <p>This call will evaluate the block until it it returns <code>true</code>
     * for a maximum of 60 seconds.  After 60 seconds, it will throw an exception.
     * 
     * @param inCondition a <code>Callable&lt;Boolean&gt;</code> value which must evaluate to true or false
     * @throws Exception if an error occurs
     */
	public static void doDelay(Callable<Boolean> inCondition)
        throws Exception
    {
        int counter = 0;
        try {
            Boolean result = inCondition.call();
            while(!result &&
                    counter < 600) {
                delay(100);
                result = inCondition.call();
                counter += 1;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Unexpected exception");
        } finally {
            if(counter >= 600) {
                fail("Timeout waiting for async condition");
            }
        }
    }   

	protected static void delay(long millis) {
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

	public static void waitForJobs() {
		while (Platform.getJobManager().currentJob() != null){
			delay(1000);
		}
	}

	public IViewPart getTestView() {
		return testView;
	}

}
