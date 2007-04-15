package org.marketcetera.photon;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.preferences.PhotonPage;

/**
 * This class provides methods for some basic application services,
 * as well as singleton member variables for Photon components, along
 * with static getters.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class Application implements IPlatformRunnable, IPropertyChangeListener {


	
	/**
	 * This method is called by the Eclipse RCP, and therefore is the main 
	 * entry point to the Application.  This method initializes the FIX version
	 * for the application (to FIX 4.2), the FIXMessageHistory, the JMSConnector
	 * the OrderManager.  Finally it creates the display, and creates and runs
	 * the workbench.
	 * 
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 * @see PlatformUI#createDisplay()
	 * @see PlatformUI#createAndRunWorkbench(Display, org.eclipse.ui.application.WorkbenchAdvisor)
	 */
	public Object run(Object args) throws Exception {
		PhotonPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		final Display display = PlatformUI.createDisplay();
		try {
			final int [] returnCode = new int[1];
			Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
				public void run() {
					returnCode[0] = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
				}
			});

			if (returnCode[0] == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}

	}

	

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPage.LOG_LEVEL_KEY)){
			PhotonPlugin.getDefault().changeLogLevel(""+event.getNewValue());
		}
	}

}
