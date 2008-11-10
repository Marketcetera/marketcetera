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
package org.rubypeople.rdt.internal.ui.filters;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.util.StringMatcher;

/**
 * The NamePatternFilter selects the elements which
 * match the given string patterns.
 * <p>
 * The following characters have special meaning:
 *   ? => any character
 *   * => any string
 * </p>
 * 
 * @since 2.0
 */
public class NamePatternFilter extends ViewerFilter {
    private String[] fPatterns;
    private StringMatcher[] fMatchers;
    
    /**
     * Return the currently configured StringMatchers.
     */
    private StringMatcher[] getMatchers() {
        return fMatchers;
    }
    
    /**
     * Gets the patterns for the receiver.
     */
    public String[] getPatterns() {
        return fPatterns;
    }
    
    
    /* (non-Javadoc)
     * Method declared on ViewerFilter.
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        String matchName= null;
        if (element instanceof IRubyElement) {
            matchName= ((IRubyElement) element).getElementName();
        } else if (element instanceof IAdaptable) {
            IAdaptable adaptable= (IAdaptable) element;
            IRubyElement javaElement= (IRubyElement)adaptable.getAdapter(IRubyElement.class);
            if (javaElement != null)
                matchName= javaElement.getElementName();
            else {
                IResource resource= (IResource)adaptable.getAdapter(IResource.class);
                if (resource != null)
                    matchName= resource.getName();
            }
        }
        if (matchName != null) {
            StringMatcher[] testMatchers= getMatchers();
            for (int i = 0; i < testMatchers.length; i++) {
                if (testMatchers[i].match(matchName))
                    return false;
            }
            return true;
        }
        return true;
    }
    
    /**
     * Sets the patterns to filter out for the receiver.
     * <p>
     * The following characters have special meaning:
     *   ? => any character
     *   * => any string
     * </p>
     */
    public void setPatterns(String[] newPatterns) {
        fPatterns = newPatterns;
        fMatchers = new StringMatcher[newPatterns.length];
        for (int i = 0; i < newPatterns.length; i++) {
            //Reset the matchers to prevent constructor overhead
            fMatchers[i]= new StringMatcher(newPatterns[i], true, false);
        }
    }
}