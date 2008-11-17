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
package org.rubypeople.rdt.internal.ui.typehierarchy;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.MethodOverrideTester;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.viewsupport.SourcePositionSorter;
import org.rubypeople.rdt.ui.RubyElementSorter;

/**
  */
public abstract class AbstractHierarchyViewerSorter extends ViewerSorter {
	
	private static final int OTHER= 1;
	private static final int CLASS= 2;
	private static final int MODULE= 3;
	private static final int ANONYM= 4;
	
	private RubyElementSorter fNormalSorter;
	private SourcePositionSorter fSourcePositonSorter;
	
	public AbstractHierarchyViewerSorter() {
		fNormalSorter= new RubyElementSorter();
		fSourcePositonSorter= new SourcePositionSorter();
	}
	
	protected abstract ITypeHierarchy getHierarchy(IType type);
	public abstract boolean isSortByDefiningType();
	public abstract boolean isSortAlphabetically();
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	 */
	public int category(Object element) {
		if (element instanceof IType) {
			IType type= (IType) element;
			if (type.getElementName().length() == 0) {
				return ANONYM;
			}
			if (type.isModule()) {
				return MODULE;
			} else {
				return CLASS;
			}
		}
		return OTHER;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#compare(null, null, null)
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (!isSortAlphabetically() && !isSortByDefiningType()) {
			return fSourcePositonSorter.compare(viewer, e1, e2);
		}
		
		int cat1= category(e1);
		int cat2= category(e2);

		if (cat1 != cat2)
			return cat1 - cat2;
		
		if (cat1 == OTHER) { // method or field
			if (isSortByDefiningType()) {
				try {
					IType def1= (e1 instanceof IMethod) ? getDefiningType((IMethod) e1) : null;
					IType def2= (e2 instanceof IMethod) ? getDefiningType((IMethod) e2) : null;
					if (def1 != null) {
						if (def2 != null) {
							if (!def2.equals(def1)) {
								return compareInHierarchy(def1, def2);
							}
						} else {
							return -1;						
						}					
					} else {
						if (def2 != null) {
							return 1;
						}	
					}
				} catch (RubyModelException e) {
					// ignore, default to normal comparison
				}
			}
			if (isSortAlphabetically()) {
				return fNormalSorter.compare(viewer, e1, e2); // use appearance pref page settings
			}
			return 0;
		} else if (cat1 == ANONYM) {
			return 0;
		} else if (isSortAlphabetically()) {
			String name1= ((IType) e1).getFullyQualifiedName();
			String name2= ((IType) e2).getFullyQualifiedName();
			return getCollator().compare(name1, name2);
		}
		return 0;
	}
	
	private IType getDefiningType(IMethod method) throws RubyModelException {
		if (method.getVisibility() == IMethod.PRIVATE || method.isSingleton() || method.isConstructor()) {
			return null;
		}
	
		IType declaringType= method.getDeclaringType();
		MethodOverrideTester tester= new MethodOverrideTester(declaringType, getHierarchy(declaringType));
		IMethod res= tester.findDeclaringMethod(method, true);
		if (res == null) {
			return null;
		}
		return res.getDeclaringType();
	}
	

	private int compareInHierarchy(IType def1, IType def2) {
		if (RubyModelUtil.isSuperType(getHierarchy(def1), def2, def1)) {
			return 1;
		} else if (RubyModelUtil.isSuperType(getHierarchy(def2), def1, def2)) {
			return -1;
		}
		// modules after classes
		if (def1.isModule()) {
			if (!def2.isModule()) {
				return 1;
			}
		} else if (def2.isModule()) {
			return -1;
		}
		String name1= def1.getElementName();
		String name2= def2.getElementName();
		
		return getCollator().compare(name1, name2);
	}

}
