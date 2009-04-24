package org.marketcetera.photon;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.photon.ui.EquityPerspectiveFactory;
import org.osgi.framework.Bundle;

/* $License$ */

/**
 * Initializes the workbench.
 * 
 * Note, we are using internal IDE classes because Eclipse has not provided a better way to
 * initialize things like standard workbench images.  See https://bugs.eclipse.org/186891.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
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
		// make sure we always save and restore workspace state
		configurer.setSaveAndRestore(true);

		// register workspace adapters
		WorkbenchAdapterBuilder.registerAdapters();

		// register shared images
		declareWorkbenchImages();
	}

	@Override
	public void postStartup() {
		// Hides Editor area when leaving Ruby perspective so editor-related toolbar
		// items will disappear
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(
				new PerspectiveAdapter() {
					@Override
					public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspective) {
						IWorkbenchPartReference reference =
								page.getReference(page.getActiveEditor());
						if (reference != null) {
							if (perspective.getId().equals(
									"org.marketcetera.photon.StrategyPerspective")) //$NON-NLS-1$
								page.setPartState(reference, IWorkbenchPage.STATE_RESTORED);
							else
								page.setPartState(reference, IWorkbenchPage.STATE_MINIMIZED);
						}
					}
				});
	}

	@Override
	public void postShutdown() {
		stopClient();
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
	
	private void stopClient() {
		try {
			ClientManager.getInstance().close();
		} catch (ClientInitException e) {
			// already closed
		}
	}

	/**
	 * Declares shared images IDE images that Strategy perspective relies on.
	 * <p>
	 * In the context of the IDE, this is done by the
	 * <code>IDEWorkbenchAdvisor.declareWorkbenchImages()</code>. In Photon, however, we have our
	 * own workbench advisor and we must make sure these images are still registered.
	 */
	private void declareWorkbenchImages() {

		final String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$

		// Model object icons
		final String PATH_OBJECT = ICONS_PATH + "obj16/"; //$NON-NLS-1$

		// Wizard icons
		final String PATH_WIZBAN = ICONS_PATH + "wizban/"; //$NON-NLS-1$

		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

		// Used by save as dialog in unititled text editor.
		declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG,
				PATH_WIZBAN + "saveas_wiz.png", false); //$NON-NLS-1$

		// Used by project explorer/common navigator
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT, PATH_OBJECT
				+ "prj_obj.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT
				+ "cprj_obj.gif", true); //$NON-NLS-1$

		// used by RDT in problems view (I think)
		declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH,
				PATH_OBJECT + "error_tsk.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH,
				PATH_OBJECT + "warn_tsk.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH, PATH_OBJECT
				+ "info_tsk.gif", true); //$NON-NLS-1$

	}

	/**
	 * Declares an IDE-specific workbench image. Copied from IDEWorkbenchAdvisor in the
	 * org.eclipse.ui.ide.application plugin.
	 * 
	 * @param symbolicName
	 *            the symbolic name of the image
	 * @param path
	 *            the path of the image file; this path is relative to the base of the IDE plug-in
	 * @param shared
	 *            <code>true</code> if this is a shared image, and <code>false</code> if this is not
	 *            a shared image
	 * @see IWorkbenchConfigurer#declareImage
	 */
	private void declareWorkbenchImage(Bundle ideBundle, String symbolicName, String path,
			boolean shared) {
		URL url = FileLocator.find(ideBundle, new Path(path), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}

	@Override
	public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}

}
