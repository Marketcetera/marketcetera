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
package org.marketcetera.photon.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.marketcetera.photon.model.FIXMessageHistory;

/**
 * Tree content provider for objects that can be adapted to the interface
 * {@link org.eclipse.ui.model.IWorkbenchAdapter IWorkbenchAdapter}.
 * <p>
 * This class may be instantiated, or subclassed.
 * </p>
 * 
 * @see IWorkbenchAdapter
 * @since 3.0
 */
public class AveragePriceContentProvider implements IStructuredContentProvider{

    /**
     * Creates a new workbench content provider.
     *
     */
    public AveragePriceContentProvider() {
    }
    /* (non-Javadoc)
     * Method declared on IContentProvider.
     */
    public void dispose() {
        // do nothing
    }



    /* (non-Javadoc)
     * Method declared on ITreeContentProvider.
     */
    public Object[] getChildren(Object element) {
    	if (element instanceof FIXMessageHistory) {
			FIXMessageHistory history = (FIXMessageHistory) element;
			return history.getHistory();
		}
    	return new Object[0];
    }

    /* (non-Javadoc)
     * Method declared on IStructuredContentProvider.
     */
    public Object[] getElements(Object element) {
        return getChildren(element);
    }


    /* (non-Javadoc)
     * Method declared on ITreeContentProvider.
     */
    public boolean hasChildren(Object element) {
    	if (element instanceof FIXMessageHistory) {
			FIXMessageHistory history = (FIXMessageHistory) element;
			return history.size() > 0;
    	}
    	return false;
    }

    /* (non-Javadoc)
     * Method declared on IContentProvider.
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // do nothing
    }

    

}
