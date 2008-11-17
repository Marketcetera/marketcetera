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
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.ui.wizards.ILoadpathContainerPage;
import org.rubypeople.rdt.ui.wizards.ILoadpathContainerPageExtension;
import org.rubypeople.rdt.ui.wizards.ILoadpathContainerPageExtension2;


/**
  */
public class LoadpathContainerWizard extends Wizard {

	private LoadpathContainerDescriptor fPageDesc;
	private ILoadpathEntry fEntryToEdit;

	private ILoadpathEntry[] fNewEntries;
	private ILoadpathContainerPage fContainerPage;
	private IRubyProject fCurrProject;
	private ILoadpathEntry[] fCurrLoadpath;
	
	private LoadpathContainerSelectionPage fSelectionWizardPage;

	/**
	 * Constructor for LoadpathContainerWizard.
	 */
	public LoadpathContainerWizard(ILoadpathEntry entryToEdit, IRubyProject currProject, ILoadpathEntry[] currEntries) {
		this(entryToEdit, null, currProject, currEntries);
	}
	
	/**
	 * Constructor for LoadpathContainerWizard.
	 */
	public LoadpathContainerWizard(LoadpathContainerDescriptor pageDesc, IRubyProject currProject, ILoadpathEntry[] currEntries) {
		this(null, pageDesc, currProject, currEntries);	
	}

	private LoadpathContainerWizard(ILoadpathEntry entryToEdit, LoadpathContainerDescriptor pageDesc, IRubyProject currProject, ILoadpathEntry[] currEntries) {
		fEntryToEdit= entryToEdit;
		fPageDesc= pageDesc;
		fNewEntries= null;
		
		fCurrProject= currProject;
		fCurrLoadpath= currEntries;
		
		String title;
		if (entryToEdit == null) {
			title= NewWizardMessages.LoadpathContainerWizard_new_title; 
		} else {
			title= NewWizardMessages.LoadpathContainerWizard_edit_title; 
		}
		setWindowTitle(title);
	}
	
	/**
	 * @deprecated use getNewEntries()
	 */
	public ILoadpathEntry getNewEntry() {
		ILoadpathEntry[] entries= getNewEntries();
		if (entries != null) {
			return entries[0];
		}
		return null;
	}
	
	public ILoadpathEntry[] getNewEntries() {
		return fNewEntries;
	}

	/* (non-Javadoc)
	 * @see IWizard#performFinish()
	 */
	public boolean performFinish() {
		if (fContainerPage != null) {
			if (fContainerPage.finish()) {
				if (fEntryToEdit == null && fContainerPage instanceof ILoadpathContainerPageExtension2) {
					fNewEntries= ((ILoadpathContainerPageExtension2) fContainerPage).getNewContainers();
				} else {
					ILoadpathEntry entry= fContainerPage.getSelection();
					fNewEntries= (entry != null) ? new ILoadpathEntry[] { entry } : null;
				}
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see IWizard#addPages()
	 */
	public void addPages() {
		if (fPageDesc != null) {
			fContainerPage= getContainerPage(fPageDesc);
			addPage(fContainerPage);			
		} else if (fEntryToEdit == null) { // new entry: show selection page as first page
			LoadpathContainerDescriptor[] containers= LoadpathContainerDescriptor.getDescriptors();

			fSelectionWizardPage= new LoadpathContainerSelectionPage(containers);
			addPage(fSelectionWizardPage);

			// add as dummy, will not be shown
			fContainerPage= new LoadpathContainerDefaultPage();
			addPage(fContainerPage);
		} else { // fPageDesc == null && fEntryToEdit != null
			LoadpathContainerDescriptor[] containers= LoadpathContainerDescriptor.getDescriptors();
			LoadpathContainerDescriptor descriptor= findDescriptorPage(containers, fEntryToEdit);
			fContainerPage= getContainerPage(descriptor);
			addPage(fContainerPage);				
		}
		super.addPages();
	}
	
	private ILoadpathContainerPage getContainerPage(LoadpathContainerDescriptor pageDesc) {
		ILoadpathContainerPage containerPage= null;
		if (pageDesc != null) {
			ILoadpathContainerPage page= pageDesc.getPage();
			if (page != null) {
				return page; // if page is already created, avoid double initialization
			}
			try {
				containerPage= pageDesc.createPage();
			} catch (CoreException e) {
				handlePageCreationFailed(e);
				containerPage= null;
			}
		}

		if (containerPage == null)	{
			containerPage= new LoadpathContainerDefaultPage();
			if (pageDesc != null) {
				pageDesc.setPage(containerPage); // avoid creation next time
			}
		}

		if (containerPage instanceof ILoadpathContainerPageExtension) {
			((ILoadpathContainerPageExtension) containerPage).initialize(fCurrProject, fCurrLoadpath);
		}

		containerPage.setSelection(fEntryToEdit);
		containerPage.setWizard(this);
		return containerPage;
	}
	
	/* (non-Javadoc)
	 * @see IWizard#getNextPage(IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == fSelectionWizardPage) {

			LoadpathContainerDescriptor selected= fSelectionWizardPage.getSelected();
			fContainerPage= getContainerPage(selected);
			
			return fContainerPage;
		}
		return super.getNextPage(page);
	}
	
	private void handlePageCreationFailed(CoreException e) {
		String title= NewWizardMessages.LoadpathContainerWizard_pagecreationerror_title; 
		String message= NewWizardMessages.LoadpathContainerWizard_pagecreationerror_message; 
		ExceptionHandler.handle(e, getShell(), title, message);
	}
	
	
	private LoadpathContainerDescriptor findDescriptorPage(LoadpathContainerDescriptor[] containers, ILoadpathEntry entry) {
		for (int i = 0; i < containers.length; i++) {
			if (containers[i].canEdit(entry)) {
				return containers[i];
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#dispose()
	 */
	public void dispose() {
		if (fSelectionWizardPage != null) {
			LoadpathContainerDescriptor[] descriptors= fSelectionWizardPage.getContainers();
			for (int i= 0; i < descriptors.length; i++) {
				descriptors[i].dispose();
			}
		}
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see IWizard#canFinish()
	 */
	public boolean canFinish() {
		if (fSelectionWizardPage != null) {
			if (!fContainerPage.isPageComplete()) {
				return false;
			}
		}
		if (fContainerPage != null) {
			return fContainerPage.isPageComplete();
		}
		return false;
	}
	
	public static int openWizard(Shell shell, LoadpathContainerWizard wizard) {
		WizardDialog dialog= new WizardDialog(shell, wizard);
		PixelConverter converter= new PixelConverter(shell);
		dialog.setMinimumPageSize(converter.convertWidthInCharsToPixels(70), converter.convertHeightInCharsToPixels(20));
		dialog.create();
		return dialog.open();
	}
	
}
