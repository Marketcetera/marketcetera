package org.marketcetera.photon;

import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;
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
	 * Creates a new MainConsole and adds it to the ConsoleManager
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui.application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		
		registerIdeAdapters();
		declareIdeWorkbenchImages();
		
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { new MainConsole() });
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

		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

		declareIdeWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY,
				PATH_ETOOL + "problem_category.gif", true); //$NON-NLS-1$
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
