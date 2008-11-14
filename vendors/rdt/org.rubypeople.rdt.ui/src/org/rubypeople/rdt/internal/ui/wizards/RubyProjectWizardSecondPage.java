/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 ********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.util.CoreUtility;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.wizards.RubyCapabilityConfigurationPage;

/**
 * As addition to the RubyCapabilityConfigurationPage, the wizard does an
 * early project creation (so that linked folders can be defined) and, if an
 * existing external location was specified, offers to do a loadpath detection
 */
public class RubyProjectWizardSecondPage extends RubyCapabilityConfigurationPage {

	private static final String FILENAME_PROJECT= ".project"; //$NON-NLS-1$
	private static final String FILENAME_LOADPATH= ".loadpath"; //$NON-NLS-1$

	private final RubyProjectWizardFirstPage fFirstPage;

	private URI fCurrProjectLocation; // null if location is platform location
	private IProject fCurrProject;
	
	private boolean fKeepContent;

	private File fDotProjectBackup;
	private File fDotClasspathBackup;
	private Boolean fIsAutobuild;

	/**
	 * Constructor for RubyProjectWizardSecondPage.
	 */
	public RubyProjectWizardSecondPage(RubyProjectWizardFirstPage mainPage) {
		fFirstPage= mainPage;
		fCurrProjectLocation= null;
		fCurrProject= null;
		fKeepContent= false;
		
		fDotProjectBackup= null;
		fDotClasspathBackup= null;
		fIsAutobuild= null;
	}
	
	protected boolean useNewSourcePage() {
		return true;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		if (visible) {
			changeToNewProject();
		} else {
			removeProject();
		}
		super.setVisible(visible);
	}
	
	private void changeToNewProject() {
		fKeepContent= fFirstPage.getDetect();
		
		final IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (fIsAutobuild == null) {
						fIsAutobuild= Boolean.valueOf(CoreUtility.enableAutoBuild(false));
					}
                    updateProject(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} catch (OperationCanceledException e) {
					throw new InterruptedException();
				} finally {
                    monitor.done();
                }
			}
		};
	
		try {
			getContainer().run(true, false, new WorkspaceModifyDelegatingOperation(op));
		} catch (InvocationTargetException e) {
			final String title= NewWizardMessages.RubyProjectWizardSecondPage_error_title; 
			final String message= NewWizardMessages.RubyProjectWizardSecondPage_error_message; 
			ExceptionHandler.handle(e, getShell(), title, message);
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	final void updateProject(IProgressMonitor monitor) throws CoreException, InterruptedException {
		
		fCurrProject= fFirstPage.getProjectHandle();
		fCurrProjectLocation= getProjectLocationURI(); 
		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		try {
			monitor.beginTask(NewWizardMessages.RubyProjectWizardSecondPage_operation_initialize, 6); 
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			URI realLocation= fCurrProjectLocation;
			if (fCurrProjectLocation == null) {  // inside workspace
				try {
					URI rootLocation= ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
					realLocation= new URI(rootLocation.getScheme(), null,
						Path.fromPortableString(rootLocation.getPath()).append(fCurrProject.getName()).toString(),
						null);
				} catch (URISyntaxException e) {
					Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
				}
			}

			rememberExistingFiles(realLocation);
            
			createProject(fCurrProject, fCurrProjectLocation, new SubProgressMonitor(monitor, 2));
				
			ILoadpathEntry[] entries= null;
	
			if (fFirstPage.getDetect()) {
				if (!fCurrProject.getFile(FILENAME_LOADPATH).exists()) { 
					final LoadPathDetector detector= new LoadPathDetector(fCurrProject, new SubProgressMonitor(monitor, 2));
					entries= detector.getLoadpath();
				} else {
					monitor.worked(2);
				}
			} else if (fFirstPage.isSrcBin()) {
				IPreferenceStore store= PreferenceConstants.getPreferenceStore();
//				IPath srcPath= new Path(store.getString(PreferenceConstants.SRCBIN_SRCNAME)); FIXME Make a preference for the default src folder name
				IPath srcPath= new Path("src");
				
				if (srcPath.segmentCount() > 0) {
					IFolder folder= fCurrProject.getFolder(srcPath);
					CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
								
				final IPath projectPath= fCurrProject.getFullPath();

				// configure the loadpath entries, including the default jre library.
				List cpEntries= new ArrayList();
				cpEntries.add(RubyCore.newSourceEntry(projectPath.append(srcPath)));
				cpEntries.addAll(Arrays.asList(getDefaultLoadpathEntry()));
				entries= (ILoadpathEntry[]) cpEntries.toArray(new ILoadpathEntry[cpEntries.size()]);
			} else {
				IPath projectPath= fCurrProject.getFullPath();
				List cpEntries= new ArrayList();
				cpEntries.add(RubyCore.newSourceEntry(projectPath));
				cpEntries.addAll(Arrays.asList(getDefaultLoadpathEntry()));
				entries= (ILoadpathEntry[]) cpEntries.toArray(new ILoadpathEntry[cpEntries.size()]);

				monitor.worked(2);
			}
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
            init(RubyCore.create(fCurrProject), null, entries, false);
			configureRubyProject(new SubProgressMonitor(monitor, 3)); // create the Ruby project to allow the use of the new source folder page
		} finally {
			monitor.done();
		}
	}
	
	private URI getProjectLocationURI() throws CoreException {
		if (fFirstPage.isInWorkspace()) {
			return null;
		}
		return URIUtil.toURI(fFirstPage.getLocationPath());
	}
	
	private ILoadpathEntry[] getDefaultLoadpathEntry() {
		ILoadpathEntry[] defaultJRELibrary= PreferenceConstants.getDefaultRubyVMLibrary();
		String compliance= fFirstPage.getCompilerCompliance();
		IPath jreContainerPath= new Path(RubyRuntime.RUBY_CONTAINER);
		if (compliance == null || defaultJRELibrary.length > 1 || !jreContainerPath.isPrefixOf(defaultJRELibrary[0].getPath())) {
			// use default
			return defaultJRELibrary;
		}
		IVMInstall inst= fFirstPage.getJVM();
		if (inst != null) {
			IPath newPath= jreContainerPath.append(inst.getVMInstallType().getId()).append(inst.getName());
			return new ILoadpathEntry[] { RubyCore.newContainerEntry(newPath) };
		}
		return defaultJRELibrary;
	}
	
	private void rememberExistingFiles(URI projectLocation) throws CoreException {
		fDotProjectBackup= null;
		fDotClasspathBackup= null;
		
		IFileStore file= EFS.getStore(projectLocation);
		if (file.fetchInfo().exists()) {
			IFileStore projectFile= file.getChild(FILENAME_PROJECT);
			if (projectFile.fetchInfo().exists()) {
				fDotProjectBackup= createBackup(projectFile, "project-desc"); //$NON-NLS-1$ 
			}
			IFileStore loadpathFile= file.getChild(FILENAME_LOADPATH);
			if (loadpathFile.fetchInfo().exists()) {
				fDotClasspathBackup= createBackup(loadpathFile, "loadpath-desc"); //$NON-NLS-1$ 
			}
		}
	}
	
	private void restoreExistingFiles(URI projectLocation, IProgressMonitor monitor) throws CoreException {
		int ticks= ((fDotProjectBackup != null ? 1 : 0) + (fDotClasspathBackup != null ? 1 : 0)) * 2;
		monitor.beginTask("", ticks); //$NON-NLS-1$
		try {
			if (fDotProjectBackup != null) {
				IFileStore projectFile= EFS.getStore(projectLocation).getChild(FILENAME_PROJECT);
				projectFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotProjectBackup, projectFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.RubyProjectWizardSecondPage_problem_restore_project, e); 
			throw new CoreException(status);
		}
		try {
			if (fDotClasspathBackup != null) {
				IFileStore loadpathFile= EFS.getStore(projectLocation).getChild(FILENAME_LOADPATH);
				loadpathFile.delete(EFS.NONE, new SubProgressMonitor(monitor, 1));
				copyFile(fDotClasspathBackup, loadpathFile, new SubProgressMonitor(monitor, 1));
			}
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, NewWizardMessages.RubyProjectWizardSecondPage_problem_restore_loadpath, e); 
			throw new CoreException(status);
		}
	}
	
	private File createBackup(IFileStore source, String name) throws CoreException {
		try {
			File bak= File.createTempFile("eclipse-" + name, ".bak");  //$NON-NLS-1$//$NON-NLS-2$
			copyFile(source, bak);
			return bak;
		} catch (IOException e) {
			IStatus status= new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, Messages.format(NewWizardMessages.RubyProjectWizardSecondPage_problem_backup, name), e); 
			throw new CoreException(status);
		} 
	}
	
	private void copyFile(IFileStore source, File target) throws IOException, CoreException {
		InputStream is= source.openInputStream(EFS.NONE, null);
		FileOutputStream os= new FileOutputStream(target);
		copyFile(is, os);
	}
	
	private void copyFile(File source, IFileStore target, IProgressMonitor monitor) throws IOException, CoreException {
		FileInputStream is= new FileInputStream(source);
		OutputStream os= target.openOutputStream(EFS.NONE, monitor);
		copyFile(is, os);
	}
	
	private void copyFile(InputStream is, OutputStream os) throws IOException {		
		try {
			byte[] buffer = new byte[8192];
			while (true) {
				int bytesRead= is.read(buffer);
				if (bytesRead == -1)
					break;
				
				os.write(buffer, 0, bytesRead);
			}
		} finally {
			try {
				is.close();
			} finally {
				os.close();
			}
		}
	}
	
	/**
	 * Called from the wizard on finish.
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException, InterruptedException {
		try {
			monitor.beginTask(NewWizardMessages.RubyProjectWizardSecondPage_operation_create, 3); 
			if (fCurrProject == null) {
				updateProject(new SubProgressMonitor(monitor, 1));
			}
			configureRubyProject(new SubProgressMonitor(monitor, 2));
			
			if (!fKeepContent) {
//				String compliance= fFirstPage.getCompilerCompliance();
//				if (compliance != null) {
//					IRubyProject project= RubyCore.create(fCurrProject);
//					Map options= project.getOptions(false);
//					RubyModelUtil.setCompilanceOptions(options, compliance);
//					project.setOptions(options);
//				} FIXME Remove all the "compliance" stuff in here for now...
			}
		} finally {
			monitor.done();
			fCurrProject= null;
			if (fIsAutobuild != null) {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue());
				fIsAutobuild= null;
			}
		}
	}

	private void removeProject() { 
		if (fCurrProject == null || !fCurrProject.exists()) {
			return;
		}
		
		IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				doRemoveProject(monitor);
			}
		};
	
		try {
			getContainer().run(true, true, new WorkspaceModifyDelegatingOperation(op));
		} catch (InvocationTargetException e) {
			final String title= NewWizardMessages.RubyProjectWizardSecondPage_error_remove_title; 
			final String message= NewWizardMessages.RubyProjectWizardSecondPage_error_remove_message; 
			ExceptionHandler.handle(e, getShell(), title, message);		
		} catch  (InterruptedException e) {
			// cancel pressed
		}
	}
	
	final void doRemoveProject(IProgressMonitor monitor) throws InvocationTargetException {
		final boolean noProgressMonitor= (fCurrProjectLocation == null); // inside workspace
		if (monitor == null || noProgressMonitor) {
			monitor= new NullProgressMonitor();
		}
		monitor.beginTask(NewWizardMessages.RubyProjectWizardSecondPage_operation_remove, 3); 
		try {
			try {
				URI projLoc= fCurrProject.getLocationURI();
				
			    boolean removeContent= !fKeepContent && fCurrProject.isSynchronized(IResource.DEPTH_INFINITE);
			    fCurrProject.delete(removeContent, false, new SubProgressMonitor(monitor, 2));
				
				restoreExistingFiles(projLoc, new SubProgressMonitor(monitor, 1));
			} finally {
				CoreUtility.enableAutoBuild(fIsAutobuild.booleanValue()); // fIsAutobuild must be set
				fIsAutobuild= null;
			}
		} catch (CoreException e) {
			throw new InvocationTargetException(e);
		} finally {
			monitor.done();
			fCurrProject= null;
			fKeepContent= false;
		}
	}

	/**
	 * Called from the wizard on cancel.
	 */
	public void performCancel() {
		removeProject();
	}      
 }
