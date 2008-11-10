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
package org.rubypeople.rdt.internal.ui.viewsupport;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Filter for the methods viewer. Changing a filter property does not trigger a
 * refiltering of the viewer
 */
public class MemberFilter extends ViewerFilter {

    public static final int FILTER_NONPUBLIC = 1;
    public static final int FILTER_STATIC = 2;
    public static final int FILTER_FIELDS = 4;
    public static final int FILTER_LOCALTYPES = 8;

    private int fFilterProperties;

    /**
     * Modifies filter and add a property to filter for
     */
    public final void addFilter(int filter) {
        fFilterProperties |= filter;
    }

    /**
     * Modifies filter and remove a property to filter for
     */
    public final void removeFilter(int filter) {
        fFilterProperties &= (-1 ^ filter);
    }

    /**
     * Tests if a property is filtered
     */
    public final boolean hasFilter(int filter) {
        return (fFilterProperties & filter) != 0;
    }

    /*
     * @see ViewerFilter#isFilterProperty(java.lang.Object, java.lang.String)
     */
    public boolean isFilterProperty(Object element, Object property) {
        return false;
    }

    /*
     * @see ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IMember) {
            IMember member = (IMember) element;
            int memberType = member.getElementType();

            if (hasFilter(FILTER_FIELDS)) {
                if ((memberType == IRubyElement.CLASS_VAR) || (memberType == IRubyElement.CONSTANT)
                        || (memberType == IRubyElement.INSTANCE_VAR)
                        || (memberType == IRubyElement.LOCAL_VARIABLE)) { return false; }
            }

            if (member.isType(IRubyElement.METHOD)) {
                IMethod method = (IMethod) member;
                try {
                    if (hasFilter(FILTER_NONPUBLIC) && !Flags.isPublic(method.getVisibility())) { return false; }
                } catch (RubyModelException e) {
                   return true;
                }
                if (hasFilter(FILTER_STATIC) && method.isSingleton()) { return false; }
            }
            if (hasFilter(FILTER_LOCALTYPES) && 
            		(memberType == IRubyElement.LOCAL_VARIABLE || memberType == IRubyElement.DYNAMIC_VAR)) { return false; }
        }
        return true;
    }
}
