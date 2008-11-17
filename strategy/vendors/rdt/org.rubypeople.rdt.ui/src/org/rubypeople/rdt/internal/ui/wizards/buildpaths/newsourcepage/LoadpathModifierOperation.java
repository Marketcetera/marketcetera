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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.buildpath.ILoadpathInformationProvider;
import org.rubypeople.rdt.internal.corext.buildpath.LoadpathModifier;


/**
 * Abstract class which represents classpath modifier operation, this is, 
 * Operation that call methods on <code>LoadpathModifier</code>.
 */
public abstract class LoadpathModifierOperation extends LoadpathModifier implements IRunnableWithProgress {
    protected ILoadpathInformationProvider fInformationProvider;
    protected CoreException fException;
    private int fType;
    /**
     * A human readable name for this operation
     */
    private String fName; 
    
    /**
     * Constructor
     * 
     * @param listener a <code>ILoadpathModifierListener</code> that is notified about 
     * changes on classpath entries or <code>null</code> if no such notification is 
     * necessary.
     * @param informationProvider a provider to offer information to the operation
     * @param name a human readable name for this operation
     * @param type the type of the operation, that is a constant of <code>
     * ILoadpathInformationProvider</code>
     * 
     * @see ILoadpathInformationProvider
     * @see LoadpathModifier
     */
    public LoadpathModifierOperation(ILoadpathModifierListener listener, ILoadpathInformationProvider informationProvider, String name, int type) {
        super(listener);
        fInformationProvider= informationProvider;
        fException= null;
        fName= name;
        fType= type;
    }
    
    protected void handleResult(List result, IProgressMonitor monitor) throws InvocationTargetException{
        /*
         * if (fMonitor != null && fException != null) then
         * the action was called with the run method of 
         * the IRunnableWithProgress which will throw an 
         * InvocationTargetException in the case that an 
         * exception ocurred. Then error handling is 
         * done by the client which called run(monitor).
         * 
         * Otherwise we pass the information back to the 
         * information provider.
         */
        if (monitor == null || fException == null)
            fInformationProvider.handleResult(result, fException, fType);
        else
            throw new InvocationTargetException(fException);
        fException= null;
    }
    
    /**
     * Method which runs the actions with a progress monitor.<br>
     * 
     * @param monitor a progress monitor, can be <code>null</code>
     */
    public abstract void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;
    
    /**
     * Get the type converted into a string.
     * 
     * @return the ID (that is the type) of this operation as string.
     */
    public String getId() {
        return Integer.toString(fType);
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
     * 
     * @throws RubyModelException
     */
    public abstract boolean isValid(List elements, int[] types) throws RubyModelException;
    
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
     * @return a string describing the operation.
     */
    public abstract String getDescription(int type);
    
    public String getName() {
        return fName;
    }
    
    public List getSelectedElements() {
        return fInformationProvider.getSelection().toList();
    }
    
    public int getTypeId() {
    	return fType;
    }

	public boolean isValid() throws RubyModelException {
        List selectedElements= getSelectedElements();
        int[] types= new int[selectedElements.size()];
        for(int i= 0; i < types.length; i++) {
            types[i]= DialogPackageExplorerActionGroup.getType(selectedElements.get(i), fInformationProvider.getRubyProject());
        }
		return isValid(selectedElements, types);
	}
}
