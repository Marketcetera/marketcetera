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

package org.rubypeople.rdt.internal.corext.buildpath;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierOperation;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.GenerateBuildPathActionGroup.EditFilterAction;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries.IInclusionExclusionQuery;

/**
 * Operation to edit the inclusion / exclusion filters of an
 * <code>IRubyElement</code>.
 * 
 * @see org.eclipse.jdt.internal.corext.buildpath.LoadpathModifier#editFilters(IRubyElement, IRubyProject, IInclusionExclusionQuery, IProgressMonitor)
 */
public class EditFiltersOperation extends LoadpathModifierOperation {
	
	private final ILoadpathInformationProvider fCPInformationProvider;
	private final ILoadpathModifierListener fListener;

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
	public EditFiltersOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider) {
		super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Edit_tooltip, ILoadpathInformationProvider.EDIT_FILTERS);
		fListener= listener;
		fCPInformationProvider= informationProvider; 
		
	}
	
	/**
	 * Method which runs the actions with a progress monitor.<br>
	 * 
	 * This operation requires the following query:
	 * <li>IInclusionExclusionQuery</li>
	 * 
	 * @param monitor a progress monitor, can be <code>null</code>
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException {
		EditFilterAction action= new EditFilterAction();
		IStructuredSelection selection= fCPInformationProvider.getSelection();
		Object firstElement= selection.getFirstElement();
		action.selectionChanged(selection);
		action.run();
		List l= new ArrayList();
		l.add(firstElement);
		if (fListener != null) {
			List entries= action.getCPListElements();
			fListener.classpathEntryChanged(entries);
		}
		fCPInformationProvider.handleResult(l, null, ILoadpathInformationProvider.EDIT_FILTERS);
	}
	
	/**
	 * Find out whether this operation can be executed on 
	 * the provided list of elements.
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
		if (elements.size() != 1)
			return false;
		IRubyProject project= fInformationProvider.getRubyProject();
		Object element= elements.get(0);
		
		if (element instanceof IRubyProject) {
			if (isSourceFolder(project))
				return true;
		} else if (element instanceof ISourceFolderRoot) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get a description for this operation. The description depends on 
	 * the provided type parameter, which must be a constant of 
	 * <code>DialogPackageExplorerActionGroup</code>. If the type is 
	 * <code>DialogPackageExplorerActionGroup.MULTI</code>, then the 
	 * description will be very general to describe the situation of 
	 * all the different selected objects as good as possible.
	 * 
	 * @param type the type of the selected object, must be a constant of 
	 * <code>DialogPackageExplorerActionGroup</code>.
	 * @return a string describing the operation
	 */
	public String getDescription(int type) {
		if (type == DialogPackageExplorerActionGroup.RUBY_PROJECT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		if (type == DialogPackageExplorerActionGroup.SOURCE_FOLDER_ROOT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		if (type == DialogPackageExplorerActionGroup.MODIFIED_FRAGMENT_ROOT)
			return NewWizardMessages.PackageExplorerActionGroup_FormText_Edit; 
		return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_Edit; 
	}
}
