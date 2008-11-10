/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Jesper Kamstrup Linnet (eclipse@kamstrup-linnet.dk) - initial API and implementation 
 * 			(report 36180: Callers/Callees view)
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.callhierarchy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyImplementorFinder implements IImplementorFinder {
    /* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.IImplementorFinder#findImplementingTypes(org.eclipse.jdt.core.IType, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Collection findImplementingTypes(IType type, IProgressMonitor progressMonitor) {
        ITypeHierarchy typeHierarchy;

        try {
            typeHierarchy = type.newTypeHierarchy(progressMonitor);

            IType[] implementingTypes = typeHierarchy.getAllClasses();
            HashSet result = new HashSet(Arrays.asList(implementingTypes));

            return result;
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }

        return null;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.corext.callhierarchy.IImplementorFinder#findInterfaces(org.eclipse.jdt.core.IType, org.eclipse.core.runtime.IProgressMonitor)
     */
    public Collection findInterfaces(IType type, IProgressMonitor progressMonitor) {
        ITypeHierarchy typeHierarchy;

        try {
            typeHierarchy = type.newSupertypeHierarchy(progressMonitor);

            IType[] interfaces = typeHierarchy.getAllSuperModules(type);
            HashSet result = new HashSet(Arrays.asList(interfaces));

            return result;
        } catch (RubyModelException e) {
            RubyPlugin.log(e);
        }

        return null;
    }
}
