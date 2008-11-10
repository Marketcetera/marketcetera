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
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierOperation;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.GenerateBuildPathActionGroup.CreateLinkedSourceFolderAction;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries.ILinkToQuery;

/**
 * Operation create a link to a source folder.
 * 
 * @see org.eclipse.jdt.internal.corext.buildpath.LoadpathModifier#createLinkedSourceFolder(ILinkToQuery, IRubyProject, IProgressMonitor)
 */
public class LinkedSourceFolderOperation extends LoadpathModifierOperation {

    private ILoadpathModifierListener fListener;
	private ILoadpathInformationProvider fCPInformationProvider;

	/**
     * Constructor
     * 
     * @param listener a <code>ILoadpathModifierListener</code> that is notified about 
     * changes on classpath entries or <code>null</code> if no such notification is 
     * necessary.
     * @param informationProvider a provider to offer information to the action
     * 
     * @see ILoadpathInformationProvider
     * @see LoadpathModifier
     */
    public LinkedSourceFolderOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider) {
        super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Link_tooltip, ILoadpathInformationProvider.CREATE_LINK); 
		fListener= listener;
		fCPInformationProvider= informationProvider;
    }
    
    /**
     * Method which runs the actions with a progress monitor.<br>
     * 
     * This operation requires the following query from the provider:
     * <li>ILinkToQuery</li>
     * 
     * @param monitor a progress monitor, can be <code>null</code>
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    	CreateLinkedSourceFolderAction action= new CreateLinkedSourceFolderAction();
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
	        fCPInformationProvider.handleResult(result, null, ILoadpathInformationProvider.CREATE_LINK);   
		} catch (RubyModelException e) {
			if (monitor == null) {
				fCPInformationProvider.handleResult(Collections.EMPTY_LIST, e, ILoadpathInformationProvider.CREATE_LINK);
			} else {
				throw new InvocationTargetException(e);
			}
		}
    }

    /**
     * This particular operation is always valid.
     * 
     * @param elements a list of elements
     * @param types an array of types for each element, that is, 
     * the type at position 'i' belongs to the selected element 
     * at position 'i' 
     * 
     * @return <code>true</code> if the operation can be 
     * executed on the provided list of elements, <code>
     * false</code> otherwise.
     * @throws RubyModelException 
     */
    public boolean isValid(List elements, int[] types) throws RubyModelException {
        return types.length == 1 && types[0] == DialogPackageExplorerActionGroup.RUBY_PROJECT;
    }

    /**
     * Get a description for this operation.
     * 
     * @param type the type of the selected object, must be a constant of 
     * <code>DialogPackageExplorerActionGroup</code>.
     * @return a string describing the operation
     */
    public String getDescription(int type) {
        return NewWizardMessages.PackageExplorerActionGroup_FormText_createLinkedFolder; 
    }

}
