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
package org.rubypeople.rdt.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.corext.util.BusyIndicatorRunnableContext;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.BuildPathsBlock;

/**
 * Standard wizard page for creating new Ruby projects. This page can be used in 
 * project creation wizards. The page shows UI to configure the project with a Ruby 
 * build path and output location. On finish the page will also configure the Ruby nature.
 * <p>
 * This is a replacement for <code>NewRubyProjectWizardPage</code> with a cleaner API.
 * </p>
 * <p>
 * Clients may instantiate or subclass.
 * </p>
 * 
 * @since 2.0
 */
public class RubyCapabilityConfigurationPage extends NewElementWizardPage {

	private static final String PAGE_NAME= "RubyCapabilityConfigurationPage"; //$NON-NLS-1$
	
	private IRubyProject fRubyProject;
	private BuildPathsBlock fBuildPathsBlock;
	
	/**
	 * Creates a wizard page that can be used in a Ruby project creation wizard.
	 * It contains UI to configure a the loadpath and the output folder.
	 * 
	 * <p>
	 * After constructing, a call to {@link #init(IRubyProject, IPath, IClasspathEntry[], boolean)} is required.
	 * </p>
	 */	
	public RubyCapabilityConfigurationPage() {
        super(PAGE_NAME);
        fRubyProject= null;
        
        setTitle(NewWizardMessages.RubyCapabilityConfigurationPage_title); 
        setDescription(NewWizardMessages.RubyCapabilityConfigurationPage_description); 
	}
    
    private BuildPathsBlock getBuildPathsBlock() {
        if (fBuildPathsBlock == null) {
            IStatusChangeListener listener= new IStatusChangeListener() {
                public void statusChanged(IStatus status) {
                    updateStatus(status);
                }
            };
            fBuildPathsBlock= new BuildPathsBlock(new BusyIndicatorRunnableContext(), listener, 0, useNewSourcePage(), null);
        }
        return fBuildPathsBlock;
    }
	
	/**
	 * Clients can override this method to choose if the new source page is used. The new source page
	 * requires that the project is already created as Ruby project. The page will directly manipulate the loadpath.
	 * By default <code>false</code> is returned.
	 * @return Returns <code>true</code> if the new source page should be used.
	 * @since 3.1
	 */
	protected boolean useNewSourcePage() {
		return false;
	}

	/**
	 * Initializes the page with the project and default loadpath.
	 * <p>
	 * The default loadpath entries must correspond the given project.
	 * </p>
	 * <p>
	 * The caller of this method is responsible for creating the underlying project. The page will create the output,
	 * source and library folders if required.
	 * </p>
	 * <p>
	 * The project does not have to exist at the time of initialization, but must exist when executing the runnable
	 * obtained by <code>getRunnable()</code>.
	 * </p>
	 * @param jproject The Ruby project.
	 * @param defaultOutputLocation The default loadpath entries or <code>null</code> to let the page choose the default
	 * @param defaultEntries The folder to be taken as the default output path or <code>null</code> to let the page choose the default
	 * @param defaultsOverrideExistingClasspath If set to <code>true</code>, an existing '.loadpath' file is ignored. If set to <code>false</code>
	 * the given default loadpath and output location is only used if no '.loadpath' exists.
	 */
	public void init(IRubyProject jproject, IPath defaultOutputLocation, ILoadpathEntry[] defaultEntries, boolean defaultsOverrideExistingClasspath) {
		if (!defaultsOverrideExistingClasspath && jproject.exists() && jproject.getProject().getFile(".loadpath").exists()) { //$NON-NLS-1$
			defaultOutputLocation= null;
			defaultEntries= null;
		}
		getBuildPathsBlock().init(jproject, defaultOutputLocation, defaultEntries);
		fRubyProject= jproject;
	}	

	/* (non-Javadoc)
	 * @see WizardPage#createControl
	 */	
	public void createControl(Composite parent) {
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1, false));
		Control control= getBuildPathsBlock().createControl(composite);
		control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Dialog.applyDialogFont(composite);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IRubyHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);
		setControl(composite);
	}
		
	/**
	 * Returns the currently configured loadpath. Note that the loadpath might 
	 * not be valid.
	 * 
	 * @return the currently configured loadpath
	 */	
	public ILoadpathEntry[] getRawClassPath() {
		return getBuildPathsBlock().getRawClassPath();
	}
	
	/**
	 * Returns the Ruby project that was passed in {@link #init(IRubyProject, IPath, IClasspathEntry[], boolean)} or <code>null</code> if the 
	 * page has not been initialized yet.
	 * 
	 * @return the managed Ruby project or <code>null</code>
	 */	
	public IRubyProject getRubyProject() {
		return fRubyProject;
	}	
	

	/**
	 * Returns the runnable that will create the Ruby project or <code>null</code> if the page has 
	 * not been initialized. The runnable sets the project's loadpath and output location to the values 
	 * configured in the page and adds the Ruby nature if not set yet. The method requires that the 
	 * project is created and opened.
	 *
	 * @return the runnable that creates the new Ruby project
	 */		
	public IRunnableWithProgress getRunnable() {
		if (getRubyProject() != null) {
			return new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						configureRubyProject(monitor);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			};
		}
		return null;	
	}

	/**
	 * Helper method to create and open a IProject. The project location
	 * is configured. No natures are added.
	 * 
	 * @param project The handle of the project to create.
	 * @param locationPath The location of the project <code>null</code> to create the project in the workspace
	 * @param monitor a progress monitor to report progress or <code>null</code> if
	 *  progress reporting is not desired
	 * @throws CoreException if the project couldn't be created
	 * @since 2.1
	 * @deprecated use {@link #createProject(IProject, URI, IProgressMonitor)} instead.
	 */
	public static void createProject(IProject project, IPath locationPath, IProgressMonitor monitor) throws CoreException {
		createProject(project, locationPath != null ? URIUtil.toURI(locationPath) : null, monitor);
	}
	
	/**
	 * Helper method to create and open a IProject. The project location
	 * is configured. No natures are added.
	 * 
	 * @param project The handle of the project to create.
	 * @param locationURI The location of the project or <code>null</code> to create the project in the workspace
	 * @param monitor a progress monitor to report progress or <code>null</code> if
	 *  progress reporting is not desired
	 * @throws CoreException if the project couldn't be created
	 * @see org.eclipse.core.resources.IProjectDescription#setLocationURI(java.net.URI)
	 * @since 3.2
	 */
	public static void createProject(IProject project, URI locationURI, IProgressMonitor monitor) throws CoreException {
		BuildPathsBlock.createProject(project, locationURI, monitor);
	}
	
	/**
	 * Adds the Ruby nature to the project (if not set yet) and configures the build loadpath.
	 * 
	 * @param monitor a progress monitor to report progress or <code>null</code> if
	 * progress reporting is not desired
	 * @throws CoreException Thrown when the configuring the Ruby project failed.
	 * @throws InterruptedException Thrown when the operation has been canceled.
	 */
	public void configureRubyProject(IProgressMonitor monitor) throws CoreException, InterruptedException {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		
		int nSteps= 6;			
		monitor.beginTask(NewWizardMessages.RubyCapabilityConfigurationPage_op_desc_ruby, nSteps); 
		
		try {
			IProject project= getRubyProject().getProject();
			BuildPathsBlock.addRubyNature(project, new SubProgressMonitor(monitor, 1));
			getBuildPathsBlock().configureRubyProject(new SubProgressMonitor(monitor, 5));
		} catch (OperationCanceledException e) {
			throw new InterruptedException();
		} finally {
			monitor.done();
		}			
	}
}
