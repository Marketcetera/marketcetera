/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.ui.wizards.NewFileWizardPage;

public class NewFileCreationWizard extends NewElementWizard {

	private NewFileWizardPage fPage;
	
	public NewFileCreationWizard(NewFileWizardPage page) {
		setDefaultPageImageDescriptor(RubyPluginImages.DESC_WIZBAN_NEWFILE);
		setDialogSettings(RubyPlugin.getDefault().getDialogSettings());
		setWindowTitle(NewWizardMessages.NewFileCreationWizard_title);
		
		fPage= page;
	}
	
	public NewFileCreationWizard() {
		this(null);
	}
	
	/*
	 * @see Wizard#createPages
	 */	
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage= new NewFileWizardPage();
			fPage.init(getSelection());
		}
		addPage(fPage);
	}
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createScript(monitor); // use the full progress monitor
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		boolean res= super.performFinish();
		if (res) {
			IResource resource= fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}	
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IRubyElement getCreatedElement() {
		return fPage.getCreatedScript();
	}

}
