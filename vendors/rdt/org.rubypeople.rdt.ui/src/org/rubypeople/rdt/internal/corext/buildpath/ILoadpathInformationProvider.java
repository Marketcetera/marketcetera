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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.buildpaths.newsourcepage.LoadpathModifierQueries.IRemoveLinkedFolderQuery;


/**
 * Interface representing a information provider for
 * operations. The interface allows the operation to get 
 * information about the current state and to callback on 
 * the provider if the result of an operation needs to be handled.
 * 
 * @see org.eclipse.jdt.internal.corext.buildpath.LoadpathModifierOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.CreateFolderOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.AddSelectedSourceFolderOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.RemoveFromLoadpathOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.IncludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.UnincludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.ExcludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.UnexcludeOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.EditFiltersOperation
 * @see org.eclipse.jdt.internal.corext.buildpath.ResetOperation
 */
public interface ILoadpathInformationProvider {
    public static final int ADD_SEL_SF_TO_BP= 0x00;
    public static final int REMOVE_FROM_BP= 0x01;
    public static final int EXCLUDE= 0x02;
    public static final int UNEXCLUDE= 0x03;
    public static final int EDIT_FILTERS= 0x04;
    public static final int CREATE_LINK= 0x05;
    public static final int RESET_ALL= 0x06;
//    public static final int EDIT_OUTPUT= 0x07;
    public static final int CREATE_OUTPUT= 0x08;
    public static final int RESET= 0x09;
    public static final int INCLUDE= 0xA;
    public static final int UNINCLUDE= 0xB;
    public static final int CREATE_FOLDER= 0xC;
    public static final int ADD_JAR_TO_BP= 0xD;
    public static final int ADD_LIB_TO_BP= 0xE;
    public static final int ADD_SEL_LIB_TO_BP= 0xF;
    
    /**
     * Method to invoce the <code>ILoadpathInformationProvider</code> to 
     * process the result of the corresponding operation. Normally, operations 
     * call this method at the end of their computation an pass the result 
     * back to the provider.
     * 
     * @param resultElements the result list of an operation, can be empty
     * @param exception an exception object in case that an exception occurred, 
     * <code>null</code> otherwise. Note: clients should check the exception
     * object before processing the result because otherwise, the result might be
     * incorrect
     * @param operationType constant to specify which kind of operation was executed;
     * corresponds to one of the following constants of <code>ILoadpathInformationProvider</code>:
     * <li>CREATE_FOLDER</li>
     * <li>ADD_TO_BP</li>
     * <li>REMOVE_FROM_BP</li>
     * <li>INCLUDE</li>
     * <li>UNINCLUDE</li>
     * <li>EXCLUDE</li>
     * <li>UNEXCLUDE</li>
     * <li>EDIT</li>
     * <li>RESET</li>
     * <li>CREATE_OUTPUT</li>
     */
    public void handleResult(List resultElements, CoreException exception, int operationType);
    
    /**
     * Method to retrieve the current list of selected elements of the provider, this is 
     * the objects on which the operation should be executed on.
     * 
     * For example: if a tree item is selected and an operation should be 
     * executed on behalf of this item, then <code>getSelection()</code> 
     * should return this item. 
     * 
     * @return the current list of selected elements from the provider, must not be 
     * <code>null</code>
     */
    public IStructuredSelection getSelection();
    
    /**
     * Method to retrieve the Ruby project from the provider.
     * 
     * @return the current Ruby project, must not be <code>null</code>
     */
    public IRubyProject getRubyProject();

	public void deleteCreatedResources();

	public IRemoveLinkedFolderQuery getRemoveLinkedFolderQuery() throws RubyModelException;
}
