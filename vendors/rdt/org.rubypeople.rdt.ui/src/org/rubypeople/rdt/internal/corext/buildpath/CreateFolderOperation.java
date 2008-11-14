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
package org.rubypeople.rdt.internal.corext.buildpath;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierOperation;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.GenerateBuildPathActionGroup.CreateLocalSourceFolderAction;

public class CreateFolderOperation extends LoadpathModifierOperation {
	
	private final ILoadpathModifierListener fListener;
	private final ILoadpathInformationProvider fCPInformationProvider;

    /**
     * Creates a new <code>AddFolderOperation</code>.
     * 
     * @param listener a <code>ILoadpathModifierListener</code> that is notified about 
     * changes on classpath entries or <code>null</code> if no such notification is 
     * necessary.
     * @param informationProvider a provider to offer information to the action
     * 
     * @see ILoadpathInformationProvider
     * @see LoadpathModifier
     */
	public CreateFolderOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider) {
		super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_AddLibCP_tooltip, ILoadpathInformationProvider.CREATE_FOLDER);
		fListener= listener;
		fCPInformationProvider= informationProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		CreateLocalSourceFolderAction action= new CreateLocalSourceFolderAction();
		action.selectionChanged(new StructuredSelection(fCPInformationProvider.getRubyProject()));
		action.run();
		ISourceFolderRoot createdElement= (ISourceFolderRoot)action.getCreatedElement();
		if (createdElement == null) {
			//Wizard was cancled.
			return;
		}
		try {
			IResource correspondingResource= createdElement.getCorrespondingResource();
			List result= new ArrayList();
			result.add(correspondingResource);
			if (fListener != null) {
				List entries= action.getCPListElements();
				fListener.classpathEntryChanged(entries);
			}
	        fCPInformationProvider.handleResult(result, null, ILoadpathInformationProvider.CREATE_FOLDER);   
		} catch (RubyModelException e) {
			if (monitor == null) {
				fCPInformationProvider.handleResult(Collections.EMPTY_LIST, e, ILoadpathInformationProvider.CREATE_FOLDER);
			} else {
				throw new InvocationTargetException(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValid(List elements, int[] types) throws RubyModelException {
		return types.length == 1 && types[0] == DialogPackageExplorerActionGroup.RUBY_PROJECT;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription(int type) {
		return NewWizardMessages.PackageExplorerActionGroup_FormText_createNewSourceFolder; 
	}
}
