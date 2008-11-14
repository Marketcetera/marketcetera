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
 *******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.util.CoreUtility;
import org.rubypeople.rdt.internal.ui.viewsupport.ImageDisposer;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.NewSourceContainerWorkbookPage;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class BuildPathsBlock {

	public static interface IRemoveOldBinariesQuery {
		
		/**
		 * Do the callback. Returns <code>true</code> if .class files should be removed from the
		 * old output location.
		 * @param oldOutputLocation The old output location
		 * @return Returns true if .class files should be removed.
		 * @throws OperationCanceledException
		 */
		boolean doQuery(IPath oldOutputLocation) throws OperationCanceledException;
		
	}

	private CheckedListDialogField fLoadPathList;
	
	private StatusInfo fLoadPathStatus;
	private StatusInfo fBuildPathStatus;

	private IRubyProject fCurrJProject;
		
	private IPath fOutputLocationPath;
	
	private IStatusChangeListener fContext;
	private Control fSWTWidget;	
	private TabFolder fTabFolder;
	
	private int fPageIndex;
	
	private BuildPathBasePage fSourceContainerPage;
	private ProjectsWorkbookPage fProjectsPage;
	private LibrariesWorkbookPage fLibrariesPage;
	
	private BuildPathBasePage fCurrPage;
	
	private String fUserSettingsTimeStamp;
	private long fFileTimeStamp;
    
    private IRunnableContext fRunnableContext;
    private boolean fUseNewPage;

	private final IWorkbenchPreferenceContainer fPageContainer; // null when invoked from a non-property page context
		
	public BuildPathsBlock(IRunnableContext runnableContext, IStatusChangeListener context, int pageToShow, boolean useNewPage, IWorkbenchPreferenceContainer pageContainer) {
		fPageContainer= pageContainer;
		fContext= context;
		fUseNewPage= useNewPage;
		
		fPageIndex= pageToShow;
		
		fSourceContainerPage= null;
		fLibrariesPage= null;
		fProjectsPage= null;
		fCurrPage= null;
        fRunnableContext= runnableContext;
				
		BuildPathAdapter adapter= new BuildPathAdapter();			
	
		String[] buttonLabels= new String[] {
			NewWizardMessages.BuildPathsBlock_classpath_up_button, 
			NewWizardMessages.BuildPathsBlock_classpath_down_button, 
			/* 2 */ null,
			NewWizardMessages.BuildPathsBlock_classpath_checkall_button, 
			NewWizardMessages.BuildPathsBlock_classpath_uncheckall_button
		
		};
		
		fLoadPathList= new CheckedListDialogField(null, buttonLabels, new CPListLabelProvider());
		fLoadPathList.setDialogFieldListener(adapter);
		fLoadPathList.setLabelText(NewWizardMessages.BuildPathsBlock_classpath_label);  
		fLoadPathList.setUpButtonIndex(0);
		fLoadPathList.setDownButtonIndex(1);
		fLoadPathList.setCheckAllButtonIndex(3);
		fLoadPathList.setUncheckAllButtonIndex(4);		
			
		fBuildPathStatus= new StatusInfo();
		fLoadPathStatus= new StatusInfo();
		
		fCurrJProject= null;
	}
	
	// -------- UI creation ---------
	
	public Control createControl(Composite parent) {
		fSWTWidget= parent;
		
		Composite composite= new Composite(parent, SWT.NONE);	
		composite.setFont(parent.getFont());
		
		GridLayout layout= new GridLayout();
		layout.marginWidth= 0;
		layout.marginHeight= 0;
		layout.numColumns= 1;		
		composite.setLayout(layout);
		
		TabFolder folder= new TabFolder(composite, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		folder.setFont(composite.getFont());
		
		TabItem item;
        item= new TabItem(folder, SWT.NONE);
        item.setText(NewWizardMessages.BuildPathsBlock_tab_source); 
        item.setImage(RubyPluginImages.get(RubyPluginImages.IMG_OBJS_SOURCE_FOLDER_ROOT));
		
        if (fUseNewPage) {
			fSourceContainerPage= new NewSourceContainerWorkbookPage(fLoadPathList, fRunnableContext, this);
        } else {
			fSourceContainerPage= new SourceContainerWorkbookPage(fLoadPathList);
        }
        item.setData(fSourceContainerPage);     
        item.setControl(fSourceContainerPage.getControl(folder));
		
		IWorkbench workbench= RubyPlugin.getDefault().getWorkbench();	
		Image projectImage= workbench.getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
		
		fProjectsPage= new ProjectsWorkbookPage(fLoadPathList, fPageContainer);		
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlock_tab_projects); 
		item.setImage(projectImage);
		item.setData(fProjectsPage);
		item.setControl(fProjectsPage.getControl(folder));
		
		fLibrariesPage= new LibrariesWorkbookPage(fLoadPathList, fPageContainer);		
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlock_tab_libraries); 
		item.setImage(RubyPluginImages.get(RubyPluginImages.IMG_OBJS_LIBRARY));
		item.setData(fLibrariesPage);
		item.setControl(fLibrariesPage.getControl(folder));
		
		// a non shared image
		Image cpoImage= RubyPluginImages.DESC_TOOL_LOADPATH_ORDER.createImage();
		composite.addDisposeListener(new ImageDisposer(cpoImage));	
		
		LoadpathOrderingWorkbookPage ordpage= new LoadpathOrderingWorkbookPage(fLoadPathList);		
		item= new TabItem(folder, SWT.NONE);
		item.setText(NewWizardMessages.BuildPathsBlock_tab_order); 
		item.setImage(cpoImage);
		item.setData(ordpage);
		item.setControl(ordpage.getControl(folder));
				
		if (fCurrJProject != null) {
			fSourceContainerPage.init(fCurrJProject);
			fLibrariesPage.init(fCurrJProject);
			fProjectsPage.init(fCurrJProject);
		}
		
		folder.setSelection(fPageIndex);
		fCurrPage= (BuildPathBasePage) folder.getItem(fPageIndex).getData();
		folder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tabChanged(e.item);
			}	
		});
		fTabFolder= folder;

		Dialog.applyDialogFont(composite);
		return composite;
	}
	
	/**
	 * Initializes the classpath for the given project. Multiple calls to init are allowed,
	 * but all existing settings will be cleared and replace by the given or default paths.
	 * @param jproject The java project to configure. Does not have to exist.
	 * @param outputLocation The output location to be set in the page. If <code>null</code>
	 * is passed, jdt default settings are used, or - if the project is an existing Ruby project- the
	 * output location of the existing project 
	 * @param classpathEntries The classpath entries to be set in the page. If <code>null</code>
	 * is passed, jdt default settings are used, or - if the project is an existing Ruby project - the
	 * classpath entries of the existing project
	 */	
	public void init(IRubyProject jproject, IPath outputLocation, ILoadpathEntry[] classpathEntries) {
		fCurrJProject= jproject;
		boolean projectExists= false;
		List newClassPath= null;
		IProject project= fCurrJProject.getProject();
		projectExists= (project.exists() && project.getFile(".loadpath").exists()); //$NON-NLS-1$
		if  (projectExists) {
			if (classpathEntries == null) {
				classpathEntries=  fCurrJProject.readRawLoadpath();
			}
		}

		if (classpathEntries != null) {
			newClassPath= getExistingEntries(classpathEntries);
		}
		if (newClassPath == null) {
			newClassPath= getDefaultClassPath(jproject);
		}
		
		List exportedEntries = new ArrayList();
		for (int i= 0; i < newClassPath.size(); i++) {
			CPListElement curr= (CPListElement) newClassPath.get(i);
			if (curr.isExported() || curr.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
				exportedEntries.add(curr);
			}
		}
		
		// inits the dialog field
		fLoadPathList.setElements(newClassPath);
		fLoadPathList.setCheckedElements(exportedEntries);
		
		initializeTimeStamps();
		updateUI();
	}
	
	protected void updateUI() {
		if (fSWTWidget == null || fSWTWidget.isDisposed()) {
			return;
		}
		
		if (Display.getCurrent() != null) {
			doUpdateUI();
		} else {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (fSWTWidget == null || fSWTWidget.isDisposed()) {
						return;
					}
					doUpdateUI();
				}
			});
		}
	}

	protected void doUpdateUI() {
		fLoadPathList.refresh();
	
		if (fSourceContainerPage != null) {
			fSourceContainerPage.init(fCurrJProject);
			fProjectsPage.init(fCurrJProject);
			fLibrariesPage.init(fCurrJProject);
		}
		doStatusLineUpdate();
	}
	
	private String getEncodedSettings() {
		StringBuffer buf= new StringBuffer();	
		CPListElement.appendEncodePath(fOutputLocationPath, buf).append(';');

		int nElements= fLoadPathList.getSize();
		buf.append('[').append(nElements).append(']');
		for (int i= 0; i < nElements; i++) {
			CPListElement elem= (CPListElement) fLoadPathList.getElement(i);
			elem.appendEncodedSettings(buf);
		}
		return buf.toString();
	}
	
	public boolean hasChangesInDialog() {
		String currSettings= getEncodedSettings();
		return !currSettings.equals(fUserSettingsTimeStamp);
	}
	
	public boolean hasChangesInLoadpathFile() {
		IFile file= fCurrJProject.getProject().getFile(".loadpath"); //$NON-NLS-1$
		return fFileTimeStamp != file.getModificationStamp();
	}
	
	public void initializeTimeStamps() {
		IFile file= fCurrJProject.getProject().getFile(".loadpath"); //$NON-NLS-1$
		fFileTimeStamp= file.getModificationStamp();
		fUserSettingsTimeStamp= getEncodedSettings();
	}
	
	

	private ArrayList getExistingEntries(ILoadpathEntry[] classpathEntries) {
		ArrayList newClassPath= new ArrayList();
		for (int i= 0; i < classpathEntries.length; i++) {
			ILoadpathEntry curr= classpathEntries[i];
			newClassPath.add(CPListElement.createFromExisting(curr, fCurrJProject));
		}
		return newClassPath;
	}
	
	// -------- public api --------
	
	/**
	 * @return Returns the Ruby project. Can return <code>null<code> if the page has not
	 * been initialized.
	 */
	public IRubyProject getRubyProject() {
		return fCurrJProject;
	}
	
	/**
	 *  @return Returns the current class path (raw). Note that the entries returned must not be valid.
	 */	
	public ILoadpathEntry[] getRawClassPath() {
		List elements=  fLoadPathList.getElements();
		int nElements= elements.size();
		ILoadpathEntry[] entries= new ILoadpathEntry[elements.size()];

		for (int i= 0; i < nElements; i++) {
			CPListElement currElement= (CPListElement) elements.get(i);
			entries[i]= currElement.getLoadpathEntry();
		}
		return entries;
	}
	
	public int getPageIndex() {
		return fPageIndex;
	}
	
	
	// -------- evaluate default settings --------
	
	private List getDefaultClassPath(IRubyProject jproj) {
		List list= new ArrayList();
		IResource srcFolder= jproj.getProject();

		list.add(new CPListElement(jproj, ILoadpathEntry.CPE_SOURCE, srcFolder.getFullPath(), srcFolder));

		ILoadpathEntry[] jreEntries= PreferenceConstants.getDefaultRubyVMLibrary();
		list.addAll(getExistingEntries(jreEntries));
		return list;
	}
		
	private class BuildPathAdapter implements IStringButtonAdapter, IDialogFieldListener {

		// -------- IStringButtonAdapter --------
		public void changeControlPressed(DialogField field) {
			buildPathChangeControlPressed(field);
		}
		
		// ---------- IDialogFieldListener --------
		public void dialogFieldChanged(DialogField field) {
			buildPathDialogFieldChanged(field);
		}
	}
	
	private void buildPathChangeControlPressed(DialogField field) {
	}
	
	private void buildPathDialogFieldChanged(DialogField field) {
		if (field == fLoadPathList) {
			updateLoadPathStatus();
		}
		doStatusLineUpdate();
	}	
	

	
	// -------- verification -------------------------------
	
	private void doStatusLineUpdate() {
		if (Display.getCurrent() != null) {
			IStatus res= findMostSevereStatus();
			fContext.statusChanged(res);
		}
	}
	
	private IStatus findMostSevereStatus() {
		return StatusUtil.getMostSevere(new IStatus[] { fLoadPathStatus, fBuildPathStatus });
	}
	
	
	/**
	 * Validates the build path.
	 */
	public void updateLoadPathStatus() {
		fLoadPathStatus.setOK();
		
		List elements= fLoadPathList.getElements();
	
		CPListElement entryMissing= null;
		int nEntriesMissing= 0;
		ILoadpathEntry[] entries= new ILoadpathEntry[elements.size()];

		for (int i= elements.size()-1 ; i >= 0 ; i--) {
			CPListElement currElement= (CPListElement)elements.get(i);
			boolean isChecked= fLoadPathList.isChecked(currElement);
			if (currElement.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
				if (!isChecked) {
					fLoadPathList.setCheckedWithoutUpdate(currElement, true);
				}
				if (!fLoadPathList.isGrayed(currElement)) {
					fLoadPathList.setGrayedWithoutUpdate(currElement, true);
				}
			} else {
				currElement.setExported(isChecked);
			}

			entries[i]= currElement.getLoadpathEntry();
			if (currElement.isMissing()) {
				nEntriesMissing++;
				if (entryMissing == null) {
					entryMissing= currElement;
				}
			}
		}
				
		if (nEntriesMissing > 0) {
			if (nEntriesMissing == 1) {
				fLoadPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntryMissing, entryMissing.getPath().toString())); 
			} else {
				fLoadPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntriesMissing, String.valueOf(nEntriesMissing))); 
			}
		}
				
/*		if (fCurrJProject.hasLoadpathCycle(entries)) {
			fLoadPathStatus.setWarning(NewWizardMessages.getString("BuildPathsBlock.warning.CycleInClassPath")); //$NON-NLS-1$
		}
*/		
		updateBuildPathStatus();
	}
		
	private void updateBuildPathStatus() {
		List elements= fLoadPathList.getElements();
		ILoadpathEntry[] entries= new ILoadpathEntry[elements.size()];
	
		for (int i= elements.size()-1 ; i >= 0 ; i--) {
			CPListElement currElement= (CPListElement)elements.get(i);
			entries[i]= currElement.getLoadpathEntry();
		}
		
		IRubyModelStatus status= RubyConventions.validateLoadpath(fCurrJProject, entries, fOutputLocationPath);
		if (!status.isOK()) {
			fBuildPathStatus.setError(status.getMessage());
			return;
		}
		fBuildPathStatus.setOK();
	}
	
	// -------- creation -------------------------------
	
	public static void createProject(IProject project, URI locationURI, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}				
		monitor.beginTask(NewWizardMessages.BuildPathsBlock_operationdesc_project, 10); 

		// create the project
		try {
			if (!project.exists()) {
				IProjectDescription desc= project.getWorkspace().newProjectDescription(project.getName());
				if (locationURI != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(locationURI)) {
					locationURI= null;
				}
				desc.setLocationURI(locationURI);
				project.create(desc, monitor);
				monitor= null;
			}
			if (!project.isOpen()) {
				project.open(monitor);
				monitor= null;
			}
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	public static void addRubyNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!project.hasNature(RubyCore.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length]= RubyCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		} else {
			if (monitor != null) {
				monitor.worked(1);
			}
		}
	}
	
	public void configureRubyProject(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
		
		flush(fLoadPathList.getElements(), getRubyProject(), monitor);
		initializeTimeStamps();
		
		updateUI();
	}
    	
	/*
	 * Creates the Ruby project and sets the configured build path and output location.
	 * If the project already exists only build paths are updated.
	 */
	public static void flush(List classPathEntries, IRubyProject javaProject, IProgressMonitor monitor) throws CoreException, OperationCanceledException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		monitor.setTaskName(NewWizardMessages.BuildPathsBlock_operationdesc_java); 
		monitor.beginTask("", classPathEntries.size() * 4 + 4); //$NON-NLS-1$
		try {
			
			IProject project= javaProject.getProject();
			IPath projPath= project.getFullPath();
					
			monitor.worked(1);
			
			IWorkspaceRoot fWorkspaceRoot= RubyPlugin.getWorkspace().getRoot();
			
			monitor.worked(1);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			int nEntries= classPathEntries.size();
			ILoadpathEntry[] classpath= new ILoadpathEntry[nEntries];
			int i= 0;
			
			for (Iterator iter= classPathEntries.iterator(); iter.hasNext();) {
				CPListElement entry= (CPListElement)iter.next();
				classpath[i]= entry.getLoadpathEntry();
				i++;
				
				IResource res= entry.getResource();
				//1 tick
				if (res instanceof IFolder && entry.getLinkTarget() == null && !res.exists()) {
					CoreUtility.createFolder((IFolder)res, true, true, new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				
				//3 ticks
				if (entry.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
					monitor.worked(1);
					
					IPath path= entry.getPath();
					if (projPath.equals(path)) {
						monitor.worked(2);
						continue;	
					}
					
					if (projPath.isPrefixOf(path)) {
						path= path.removeFirstSegments(projPath.segmentCount());
					}
					IFolder folder= project.getFolder(path);
					IPath orginalPath= entry.getOrginalPath();
					if (orginalPath == null) {
						if (!folder.exists()) {
							//New source folder needs to be created
							if (entry.getLinkTarget() == null) {
								CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 2));
							} else {
								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 2));
							}
						}
					} else {
						if (projPath.isPrefixOf(orginalPath)) {
							orginalPath= orginalPath.removeFirstSegments(projPath.segmentCount());
						}
						IFolder orginalFolder= project.getFolder(orginalPath);
						if (entry.getLinkTarget() == null) {
							if (!folder.exists()) {
								//Source folder was edited, move to new location
								IPath parentPath= entry.getPath().removeLastSegments(1);
								if (projPath.isPrefixOf(parentPath)) {
									parentPath= parentPath.removeFirstSegments(projPath.segmentCount());
								}
								if (parentPath.segmentCount() > 0) {
									IFolder parentFolder= project.getFolder(parentPath);
									if (!parentFolder.exists()) {
										CoreUtility.createFolder(parentFolder, true, true, new SubProgressMonitor(monitor, 1));
									} else {
										monitor.worked(1);
									}
								} else {
									monitor.worked(1);
								}
								orginalFolder.move(entry.getPath(), true, true, new SubProgressMonitor(monitor, 1));
							}
						} else {
							if (!folder.exists() || !entry.getLinkTarget().equals(entry.getOrginalLinkTarget())) {
								orginalFolder.delete(true, new SubProgressMonitor(monitor, 1));
								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 1));
							}
						}
					}
				} else {
					monitor.worked(3);
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}

			javaProject.setRawLoadpath(classpath, new SubProgressMonitor(monitor, 2));
		} finally {
			monitor.done();
		}
	}
	
	public static boolean hasClassfiles(IResource resource) throws CoreException {
		if (resource.isDerived()) { 
			return true;
		}		
		if (resource instanceof IContainer) {
			IResource[] members= ((IContainer) resource).members();
			for (int i= 0; i < members.length; i++) {
				if (hasClassfiles(members[i])) {
					return true;
				}
			}
		}
		return false;
	}
	

	public static void removeOldClassfiles(IResource resource) throws CoreException {
		if (resource.isDerived()) {
			resource.delete(false, null);
		} else if (resource instanceof IContainer) {
			IResource[] members= ((IContainer) resource).members();
			for (int i= 0; i < members.length; i++) {
				removeOldClassfiles(members[i]);
			}
		}
	}
	
	public static IRemoveOldBinariesQuery getRemoveOldBinariesQuery(final Shell shell) {
		return new IRemoveOldBinariesQuery() {
			public boolean doQuery(final IPath oldOutputLocation) throws OperationCanceledException {
				final int[] res= new int[] { 1 };
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						Shell sh= shell != null ? shell : RubyPlugin.getActiveWorkbenchShell();
						String title= NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_title; 
						String message= Messages.format(NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_description, oldOutputLocation.toString()); 
						MessageDialog dialog= new MessageDialog(sh, title, null, message, MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
						res[0]= dialog.open();
					}
				});
				if (res[0] == 0) {
					return true;
				} else if (res[0] == 1) {
					return false;
				}
				throw new OperationCanceledException();
			}
		};
	}	
	
	// -------- tab switching ----------
	
	private void tabChanged(Widget widget) {
		if (widget instanceof TabItem) {
			TabItem tabItem= (TabItem) widget;
			BuildPathBasePage newPage= (BuildPathBasePage) tabItem.getData();
			if (fCurrPage != null) {
				List selection= fCurrPage.getSelection();
				if (!selection.isEmpty()) {
					newPage.setSelection(selection, false);
				}
			}
			fCurrPage= newPage;
			fPageIndex= tabItem.getParent().getSelectionIndex();
		}
	}
	
	private int getPageIndex(int entryKind) {
		switch (entryKind) {
			case ILoadpathEntry.CPE_CONTAINER:
			case ILoadpathEntry.CPE_LIBRARY:
			case ILoadpathEntry.CPE_VARIABLE:
				return 2;
			case ILoadpathEntry.CPE_PROJECT:
				return 1;
			case ILoadpathEntry.CPE_SOURCE:
				return 0;
		}
		return 0;
	}
	
	private CPListElement findElement(ILoadpathEntry entry) {
		for (int i= 0, len= fLoadPathList.getSize(); i < len; i++) {
			CPListElement curr= (CPListElement) fLoadPathList.getElement(i);
			if (curr.getEntryKind() == entry.getEntryKind() && curr.getPath().equals(entry.getPath())) {
				return curr;
			}
		}
		return null;
	}
	
	public void setElementToReveal(ILoadpathEntry entry, String attributeKey) {
		int pageIndex= getPageIndex(entry.getEntryKind());
		if (fTabFolder == null) {
			fPageIndex= pageIndex;
		} else {
			fTabFolder.setSelection(pageIndex);
			CPListElement element= findElement(entry);
			if (element != null) {
				Object elementToSelect= element;
				
				if (attributeKey != null) {
					Object attrib= element.findAttributeElement(attributeKey);
					if (attrib != null) {
						elementToSelect= attrib;
					}
				}
				BuildPathBasePage page= (BuildPathBasePage) fTabFolder.getItem(pageIndex).getData();
				List selection= new ArrayList(1);
				selection.add(elementToSelect);
				page.setSelection(selection, true);
			}	
		}
	}
	
	public void addElement(ILoadpathEntry entry) {
		int pageIndex= getPageIndex(entry.getEntryKind());
		if (fTabFolder == null) {
			fPageIndex= pageIndex;
		} else {
			fTabFolder.setSelection(pageIndex);

			Object page=  fTabFolder.getItem(pageIndex).getData();
			if (page instanceof LibrariesWorkbookPage) {
				CPListElement element= CPListElement.createFromExisting(entry, fCurrJProject);
				((LibrariesWorkbookPage) page).addElement(element);
			}
		}
	}
	
	public boolean isOKStatus() {
	    return findMostSevereStatus().isOK();
    }
}
