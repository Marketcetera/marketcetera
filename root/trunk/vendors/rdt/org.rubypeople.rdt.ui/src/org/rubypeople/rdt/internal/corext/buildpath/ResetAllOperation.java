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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierOperation;

/**
 * Operation to reset all changes and go back to the initial project state.
 */
public class ResetAllOperation extends LoadpathModifierOperation {
    private ILoadpathEntry[] fEntries;
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
    public ResetAllOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider) {
        super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Reset_tooltip, ILoadpathInformationProvider.RESET_ALL); 
    }

    /**
     * Method which runs the actions with a progress monitor.<br>
     * 
     * This operation requires the following query from the provider:
     * <li>IOutputFolderQuery</li>
     * 
     * @param monitor a progress monitor, can be <code>null</code>
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        fException= null;
        try {
            fInformationProvider.getRubyProject().setRawLoadpath(fEntries, null, monitor);
            fInformationProvider.deleteCreatedResources();
            fEntries= null;
        } catch (CoreException e) {
            fException= e;
        }
        
        super.handleResult(null, monitor);
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
        IRubyProject project= fInformationProvider.getRubyProject();
        if (project == null)
            return false;
        if (fEntries == null) {
            fEntries= project.getRawLoadpath();
           
        }
        ILoadpathEntry[] currentEntries= project.getRawLoadpath();
        if (currentEntries.length != fEntries.length)
            return true;
        for(int i= 0; i < fEntries.length; i++) {
            if (!fEntries[i].equals(currentEntries[i]))
                return true;
        }
        return false;
    }
    
    /**
     * Get a description for this operation. In this particual case 
     * the description is independent of the selection and it's 
     * provided type.
     * 
     * @param type the type of the selected object, must be a constant of 
     * <code>DialogPackageExplorerActionGroup</code>.
     * @return a string describing the operation
     */
    public String getDescription(int type) {
        return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_ResetAll; 
    }
}
