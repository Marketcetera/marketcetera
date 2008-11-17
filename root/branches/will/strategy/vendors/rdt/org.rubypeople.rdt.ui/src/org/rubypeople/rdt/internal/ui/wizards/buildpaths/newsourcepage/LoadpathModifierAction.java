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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Action which is used when operations on the classpath 
 * are executed.
 */
public class LoadpathModifierAction extends Action {
    private LoadpathModifierOperation fOperation;
    
    /**
     * Constructor to create a classpath modifier action.
     * 
     * @param operation the operation to execute inside the action
     * @param imageDescriptor the image descriptor for the icon
     * @param disabledImageDescriptor the image descriptor for the disabled icon
     * @param text the text to be set as label for the action
     * @param tooltip the text to be set as tool tip
     * 
     * @see LoadpathModifierOperation
     */
    public LoadpathModifierAction(LoadpathModifierOperation operation, ImageDescriptor imageDescriptor, ImageDescriptor disabledImageDescriptor, String text, String tooltip, int style) {
        super(text, style);
        setImageDescriptor(imageDescriptor);
        setDisabledImageDescriptor(disabledImageDescriptor);
        setText(text);
        setToolTipText(tooltip);
        fOperation= operation;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        try {
            fOperation.run(null);
            setEnabled(fOperation.isValid());
        } catch (InvocationTargetException e) {
            // nothing to do
        } catch (InterruptedException e) {
            // nothing to do
        } catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
        // Remark: there is nothing to do because the operation that is executed 
        // ensures that the object receiving the result should do the exception handling
        // because it needs to implement interface ILoadpathInformationProvider 
    }
    
    /**
     * Find out whether this operation can be executed on 
     * the provided list of elements.
     * 
     * @param selectedElements a list of elements
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
    public boolean isValid(List selectedElements, int[] types) throws RubyModelException {
        return fOperation.isValid(selectedElements, types);
    }
    
    /**
     * Getter for the operation.
     * 
     * @return the operation that is executed within this action
     * 
     * @see LoadpathModifierOperation
     */
    public LoadpathModifierOperation getOperation() {
        return fOperation;
    }
        
    /**
     * Get the description suitable to the provided type
     * 
     * @param type the type of the selected element(s), must be a constant of 
     * <code>DialogPackageActionGroup</code>.
     * @return a short description of the operation.
     * 
     * @see LoadpathModifierOperation#getDescription(int)
     * @see DialogPackageExplorerActionGroup
     */
    public String getDescription(int type) {
        return fOperation.getDescription(type);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#getId()
     */
    public String getId() {
        return fOperation.getId();
    }
    
    /**
     * Get the action's name.
     * 
     * @return a human readable name for the operation/action executed
     */
    public String getName() {
        return fOperation.getName();
    }
}
