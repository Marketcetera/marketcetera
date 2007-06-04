package org.marketcetera.photon;

import java.io.PrintStream;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;
import org.marketcetera.photon.ui.EquityPerspectiveFactory;
import org.marketcetera.photon.ui.PhotonConsole;
import org.osgi.framework.Bundle;

/**
 * Class required by the RCP to initialize the workbench.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/**
	 * Does nothing more than return a new {@link ApplicationWorkbenchWindowAdvisor}
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor(org.eclipse.ui.application.IWorkbenchWindowConfigurer)
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	public String getInitialWindowPerspectiveId() {
		return EquityPerspectiveFactory.ID;
	}

	/**
	 * Creates a new PhotonConsole and adds it to the ConsoleManager
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		
		registerIdeAdapters();
		declareIdeWorkbenchImages();
		
		PhotonConsole photonConsole = new PhotonConsole(Messages.MainConsole_Name, PhotonPlugin.MAIN_CONSOLE_LOGGER_NAME);
		PhotonConsole dataFeedConsole = new PhotonConsole(Messages.MarketDataConsole_Name, PhotonPlugin.MARKETDATA_CONSOLE_LOGGER_NAME);

		// I think the last one in the array is the one that is shown
		// by default at application startup.
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { dataFeedConsole, photonConsole });
		
		System.setOut(new PrintStream(photonConsole.getInfoMessageStream(), true));
		System.setErr(new PrintStream(photonConsole.getErrorMessageStream(), true));
	}

	
	@Override
	public void postShutdown() {
		try {
			// todo: Eclipse's IDEWorkbenchAdvisor.disconnectFromWorkspace uses
			// a progress monitor in a IRunnableWithProgress during the final save. 
			// Is such a thing useful here during shutdown?
			ResourcesPlugin.getWorkspace().save(true, null);
		} catch (Exception anyException) {
			org.marketcetera.photon.PhotonPlugin.getMainConsoleLogger().warn(
					"Failed to save workspace during workbench shutdown. Cause: " //$NON-NLS-1$
							+ anyException.getMessage(), anyException);
		}
	}

	/**
	 * Explicitly registers IDE- and resource-related adapters that RDT relies on.
	 */
	private void registerIdeAdapters() {
		WorkbenchAdapterBuilder.registerAdapters();
	}

	/**
	 * Declares shared images that RDT relies on. 
	 * <p>
	 * In the context of the IDE, this is done by the <code>IDEWorkbenchAdvisor.declareWorkbenchImages()</code>. 
	 * In the context of an RCP app, however, <code>IDEWorkbenchAdvisor</code> is not used. In addition, we cannot 
	 * directly invoke the method since it has private access. We thus have to explicitly declare the few shared 
	 * IDE images that RDT needs here.   
	 */
	private void declareIdeWorkbenchImages() {

		final String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$
		final String PATH_ETOOL = ICONS_PATH + "etool16/"; // Enabled toolbar icons.//$NON-NLS-1$
		final String PATH_OBJECT = ICONS_PATH + "obj16/"; // Model object icons//$NON-NLS-1$

		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

		declareIdeWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY,
				PATH_ETOOL + "problem_category.gif", true); //$NON-NLS-1$
		declareIdeWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH, PATH_OBJECT
						+ "error_tsk.gif", true); //$NON-NLS-1$
		declareIdeWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH, PATH_OBJECT
						+ "warn_tsk.gif", true); //$NON-NLS-1$
		declareIdeWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH, PATH_OBJECT
						+ "info_tsk.gif", true); //$NON-NLS-1$
		declareIdeWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT,
				PATH_OBJECT + "prj_obj.gif", true); //$NON-NLS-1$
		declareIdeWorkbenchImage(ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT
						+ "cprj_obj.gif", true); //$NON-NLS-1$
	}
	
	/**
	 * Declares an IDE-specific workbench image.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the image
	 * @param path
	 *            the path of the image file; this path is relative to the base
	 *            of the IDE plug-in
	 * @param shared
	 *            <code>true</code> if this is a shared image, and
	 *            <code>false</code> if this is not a shared image
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareIdeWorkbenchImage(Bundle ideBundle, String symbolicName,
			String path, boolean shared) {
		URL url = Platform.find(ideBundle, new Path(path));
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}

}
