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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.Messages;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.DialogPackageExplorerActionGroup;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierOperation;


/**
 * Operation to exclude objects of type <code>IRubyElement</code>.
 * 
 * @see org.eclipse.jdt.internal.corext.buildpath.LoadpathModifier#exclude(List, IRubyProject, IProgressMonitor)
 * @see org.eclipse.jdt.internal.corext.buildpath.UnexcludeOperation
 */
public class ExcludeOperation extends LoadpathModifierOperation {
    
    /**
     * Constructor
     * 
     * @param listener a <code>ILoadpathModifierListener</code> that is notified about 
     * changes on classpath entries or <code>null</code> if no such notification is 
     * necessary.
     * @param informationProvider a provider to offer information to the operation
     * 
     * @see ILoadpathInformationProvider
     * @see LoadpathModifier
     */
    public ExcludeOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider) {
        super(listener, informationProvider, NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_Exclude_tooltip, ILoadpathInformationProvider.EXCLUDE); 
    }
    
    /**
     * Method which runs the actions with a progress monitor.<br>
     * 
     * This operation does not require any queries from the provider.
     * 
     * @param monitor a progress monitor, can be <code>null</code>
     */
    public void run(IProgressMonitor monitor) throws InvocationTargetException {
        List result= null;
        fException= null;
        try {
            List javaElements= getSelectedElements();
            IRubyProject project= fInformationProvider.getRubyProject();
            result= exclude(javaElements, project, monitor);
        } catch (CoreException e) {
            fException= e;
            result= null;
        }
        super.handleResult(result, monitor);
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
        if (elements.size() == 0)
            return false;
        for(int i= 0; i < elements.size(); i++) {
            Object element= elements.get(i);
            int type= types[i];
			if (!(type == DialogPackageExplorerActionGroup.SOURCE_FOLDER || 
                    type == DialogPackageExplorerActionGroup.INCLUDED_FOLDER || 
                    element instanceof IRubyScript))
                return false;
        }
        return true;
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
        IRubyElement elem= (IRubyElement) getSelectedElements().get(0);
        String name= escapeSpecialChars(elem.getElementName());
        if (type == DialogPackageExplorerActionGroup.SOURCE_FOLDER)
            return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_ExcludePackage, name); 
        if (type == DialogPackageExplorerActionGroup.INCLUDED_FOLDER)
            return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_ExcludePackage, name); 
        if (type == DialogPackageExplorerActionGroup.RUBY_SCRIPT)
            return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_ExcludeFile, name); 
        if (type == DialogPackageExplorerActionGroup.INCLUDED_FILE)
            return Messages.format(NewWizardMessages.PackageExplorerActionGroup_FormText_ExcludeFile, name); 
        return NewWizardMessages.PackageExplorerActionGroup_FormText_Default_Exclude;
    }
}
