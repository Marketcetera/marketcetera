/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.hierarchy;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyProject;



public abstract class HierarchyBuilder {
	/**
	 * The hierarchy being built.
	 */
	protected TypeHierarchy hierarchy;

	/**
	 * A temporary cache of infos to handles to speed info
	 * to handle translation - it only contains the entries
	 * for the types in the region (in other words, it contains
	 * no supertypes outside the region).
	 */
	protected Map infoToHandle;
	/*
	 * The dot-separated fully qualified name of the focus type, or null of none.
	 */
	protected String focusQualifiedName;

	protected HierarchyResolver hierarchyResolver;
	
	public HierarchyBuilder(TypeHierarchy hierarchy) throws RubyModelException {
		
		this.hierarchy = hierarchy;
		RubyProject project = (RubyProject) hierarchy.rubyProject();
		
		IType focusType = hierarchy.getType();
		org.rubypeople.rdt.core.IRubyScript unitToLookInside = focusType == null ? null : focusType.getRubyScript();
		org.rubypeople.rdt.core.IRubyScript[] workingCopies = this.hierarchy.workingCopies;
		org.rubypeople.rdt.core.IRubyScript[] unitsToLookInside;
		if (unitToLookInside != null) {
			int wcLength = workingCopies == null ? 0 : workingCopies.length;
			if (wcLength == 0) {
				unitsToLookInside = new org.rubypeople.rdt.core.IRubyScript[] {unitToLookInside};
			} else {
				unitsToLookInside = new org.rubypeople.rdt.core.IRubyScript[wcLength+1];
				unitsToLookInside[0] = unitToLookInside;
				System.arraycopy(workingCopies, 0, unitsToLookInside, 1, wcLength);
			}
		} else {
			unitsToLookInside = workingCopies;
		}
		if (project != null) {
//			SearchableEnvironment searchableEnvironment = project.newSearchableNameEnvironment(unitsToLookInside);
//			this.nameLookup = searchableEnvironment.nameLookup;
			this.hierarchyResolver =
				new HierarchyResolver(
//					searchableEnvironment,
					project.getOptions(true),
					this
					);
//					new DefaultProblemFactory());
		}
		this.infoToHandle = new HashMap(5);
		this.focusQualifiedName = focusType == null ? null : focusType.getFullyQualifiedName();
	}
	
	public abstract void build(boolean computeSubtypes)
		throws RubyModelException, CoreException;
	
	/**
	 * Connect the supplied type to its superclass & superinterfaces.
	 * The superclass & superinterfaces are the identical binary or source types as
	 * supplied by the name environment.
	 */
	public void connect(
		IType typeHandle,
		IType superclassHandle,
		IType[] superinterfaceHandles) {

		/*
		 * Temporary workaround for 1G2O5WK: ITPJCORE:WINNT - NullPointerException when selecting "Show in Type Hierarchy" for a inner class
		 */
		if (typeHandle == null)
			return;
		if (TypeHierarchy.DEBUG) {
			System.out.println(
				"Connecting: " + ((RubyElement) typeHandle).toStringWithAncestors()); //$NON-NLS-1$
			System.out.println(
				"  to superclass: " //$NON-NLS-1$
					+ (superclassHandle == null
						? "<None>" //$NON-NLS-1$
						: ((RubyElement) superclassHandle).toStringWithAncestors()));
			System.out.print("  and superinterfaces:"); //$NON-NLS-1$
			if (superinterfaceHandles == null || superinterfaceHandles.length == 0) {
				System.out.println(" <None>"); //$NON-NLS-1$
			} else {
				System.out.println();
				for (int i = 0, length = superinterfaceHandles.length; i < length; i++) {
					if (superinterfaceHandles[i] == null) continue;
					System.out.println(
						"    " + ((RubyElement) superinterfaceHandles[i]).toStringWithAncestors()); //$NON-NLS-1$
				}
			}
		}
		// now do the caching
		if (typeHandle.isModule()) {
			this.hierarchy.addModule(typeHandle);
		} else {
			if (superclassHandle == null) {
				this.hierarchy.addRootClass(typeHandle);
			} else {
				this.hierarchy.cacheSuperclass(typeHandle, superclassHandle);
			}
		}
		if (superinterfaceHandles == null) {
			superinterfaceHandles = TypeHierarchy.NO_TYPE;
		}
		this.hierarchy.cacheSuperModules(typeHandle, superinterfaceHandles);
		 
		// record flags
		this.hierarchy.cacheFlags(typeHandle, /*type.getModifiers()*/ 0 );
	}
	
	protected IType getType() {
		return this.hierarchy.getType();
	}
	
	
	/**
	 * Configure this type hierarchy by computing the supertypes only.
	 */
	protected void buildSupertypes() {
		IType focusType = this.getType();
		if (focusType == null)
			return;
		// get generic type from focus type
//		IGenericType type;
//		try {
//			type = (IGenericType) ((JavaElement) focusType).getElementInfo();
//		} catch (JavaModelException e) {
//			// if the focus type is not present, or if cannot get workbench path
//			// we cannot create the hierarchy
//			return;
//		}
		//NB: no need to set focus type on hierarchy resolver since no other type is injected
		//    in the hierarchy resolver, thus there is no need to check that a type is 
		//    a sub or super type of the focus type.
		this.hierarchyResolver.resolve(focusType);

		// Add focus if not already in (case of a type with no explicit super type)
		if (!this.hierarchy.contains(focusType)) {
			this.hierarchy.addRootClass(focusType);
		}
	}
	
	protected void worked(IProgressMonitor monitor, int work) {
		if (monitor != null) {
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			} else {
				monitor.worked(work);
			}
		}
	}
}
