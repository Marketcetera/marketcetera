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
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IRegion;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.core.hierarchy.RegionBasedTypeHierarchy;
import org.rubypeople.rdt.internal.core.hierarchy.TypeHierarchy;

/**
 * This operation creates an <code>ITypeHierarchy</code> for a specific type within
 * a specified region, or for all types within a region. The specified
 * region limits the number of resolved subtypes (to the subset of
 * types in the specified region). The resolved supertypes may go outside
 * of the specified region in order to reach the root(s) of the type
 * hierarchy. A Ruby Project is required to provide a context (classpath)
 * to use while resolving supertypes and subtypes.
 *
 * @see ITypeHierarchy
 */

public class CreateTypeHierarchyOperation extends RubyModelOperation {
	/**
	 * The generated type hierarchy
	 */
	protected TypeHierarchy typeHierarchy;
	
/**
 * Constructs an operation to create a type hierarchy for the
 * given type within the specified region, in the context of
 * the given project.
 */
public CreateTypeHierarchyOperation(IRegion region, IRubyScript[] workingCopies, IType element, boolean computeSubtypes) {
	super(element);
	this.typeHierarchy = new RegionBasedTypeHierarchy(region, workingCopies, element, computeSubtypes);
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, IRubyScript[] workingCopies, IRubySearchScope scope, boolean computeSubtypes) {
	super(element);
	IRubyScript[] copies;
	if (workingCopies != null) {
		int length = workingCopies.length;
		copies = new IRubyScript[length];
		System.arraycopy(workingCopies, 0, copies, 0, length);
	} else {
		copies = null;
	}
	this.typeHierarchy = new TypeHierarchy(element, copies, scope, computeSubtypes);
}
/**
 * Constructs an operation to create a type hierarchy for the
 * given type and working copies.
 */
public CreateTypeHierarchyOperation(IType element, IRubyScript[] workingCopies, IRubyProject project, boolean computeSubtypes) {
	super(element);
	IRubyScript[] copies;
	if (workingCopies != null) {
		int length = workingCopies.length;
		copies = new IRubyScript[length];
		System.arraycopy(workingCopies, 0, copies, 0, length);
	} else {
		copies = null;
	}
	this.typeHierarchy = new TypeHierarchy(element, copies, project, computeSubtypes);
}
/**
 * Performs the operation - creates the type hierarchy
 * @exception RubyModelException The operation has failed.
 */
protected void executeOperation() throws RubyModelException {	
	try {
		this.typeHierarchy.refresh(this);
	} catch (IllegalStateException e) {
		this.typeHierarchy = null;
	}
}
/**
 * Returns the generated type hierarchy.
 */
public ITypeHierarchy getResult() {
	return this.typeHierarchy;
}
/**
 * @see RubyModelOperation
 */
public boolean isReadOnly() {
	return true;
}
/**
 * Possible failures: <ul>
 *	<li>NO_ELEMENTS_TO_PROCESS - at least one of a type or region must
 *			be provided to generate a type hierarchy.
 *	<li>ELEMENT_NOT_PRESENT - the provided type or type's project does not exist
 * </ul>
 */
public IRubyModelStatus verify() {
	IRubyElement elementToProcess= getElementToProcess();
	if (elementToProcess == null && !(this.typeHierarchy instanceof RegionBasedTypeHierarchy)) {
		return new RubyModelStatus(IRubyModelStatusConstants.NO_ELEMENTS_TO_PROCESS);
	}
	if (elementToProcess != null && !elementToProcess.exists()) {
		return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, elementToProcess);
	}
	IRubyProject project = this.typeHierarchy.rubyProject();
	if (project != null && !project.exists()) {
		return new RubyModelStatus(IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, project);
	}
	return RubyModelStatus.VERIFIED_OK;
}
}
