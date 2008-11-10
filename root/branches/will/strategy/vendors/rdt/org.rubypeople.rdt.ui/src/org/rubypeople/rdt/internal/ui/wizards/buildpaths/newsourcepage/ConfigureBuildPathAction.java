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
package org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.packageview.LoadPathContainer;
import org.rubypeople.rdt.internal.ui.preferences.BuildPathsPropertyPage;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;

/**
 * 
 */
public class ConfigureBuildPathAction extends Action implements ISelectionChangedListener {

	private final IWorkbenchSite fSite;
	private IProject fProject;

	public ConfigureBuildPathAction(IWorkbenchSite site) {
		super(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ConfigureBP_label, RubyPluginImages.DESC_ELCL_CONFIGURE_BUILDPATH);
		setToolTipText(NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_ConfigureBP_tooltip);
		setDisabledImageDescriptor(RubyPluginImages.DESC_DLCL_CONFIGURE_BUILDPATH);		
		fSite= site;
	}
	
	private Shell getShell() {
		return fSite.getShell();
	}
	
	public void run() {
		if (fProject != null) {
			PreferencesUtil.createPropertyDialogOn(getShell(), fProject, BuildPathsPropertyPage.PROP_ID, null, null).open();
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(final SelectionChangedEvent event) {
		final ISelection selection = event.getSelection();
		if (selection instanceof IStructuredSelection) {
			setEnabled(canHandle((IStructuredSelection) selection));
		} else {
			setEnabled(canHandle(StructuredSelection.EMPTY));
		}
	}

	private boolean canHandle(IStructuredSelection elements) {
		if (elements.size() != 1)
			return false;
	
		Object firstElement= elements.getFirstElement();
		fProject= getProjectFromSelectedElement(firstElement);
		return fProject != null;
	}


	private IProject getProjectFromSelectedElement(Object firstElement) {
		if (firstElement instanceof IRubyElement) {
			IRubyElement element= (IRubyElement) firstElement;
			ISourceFolderRoot root= RubyModelUtil.getSourceFolderRoot(element);
			if (root != null && root != element && root.isArchive()) {
				return null;
			}
			IRubyProject project= element.getRubyProject();
			if (project != null) {
				return project.getProject();
			}
			return null;
		} else if (firstElement instanceof LoadPathContainer) {
			return ((LoadPathContainer) firstElement).getRubyProject().getProject();
		} else if (firstElement instanceof IAdaptable) {
			IResource res= (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
			if (res != null) {
				return res.getProject();
			}
		}
		return null;
	}

}
