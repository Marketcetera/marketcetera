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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.MethodOverrideTester;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.ui.IWorkingCopyProvider;

/**
 * Base class for content providers for type hierarchy viewers.
 * Implementors must override 'getTypesInHierarchy'.
 * Ruby delta processing is also performed by the content provider
 */
public abstract class TypeHierarchyContentProvider implements ITreeContentProvider, IWorkingCopyProvider {
	protected static final Object[] NO_ELEMENTS= new Object[0];
	
	protected TypeHierarchyLifeCycle fTypeHierarchy;
	protected IMember[] fMemberFilter;
	
	protected TreeViewer fViewer;

	private ViewerFilter fWorkingSetFilter;
	private MethodOverrideTester fMethodOverrideTester;
	private ITypeHierarchyLifeCycleListener fTypeHierarchyLifeCycleListener;
	
	
	public TypeHierarchyContentProvider(TypeHierarchyLifeCycle lifecycle) {
		fTypeHierarchy= lifecycle;
		fMemberFilter= null;
		fWorkingSetFilter= null;
		fMethodOverrideTester= null;
		fTypeHierarchyLifeCycleListener= new ITypeHierarchyLifeCycleListener() {
			public void typeHierarchyChanged(TypeHierarchyLifeCycle typeHierarchyProvider, IType[] changedTypes) {
				if (changedTypes == null) {
					fMethodOverrideTester= null;
				}
			}
		};
		lifecycle.addChangedListener(fTypeHierarchyLifeCycleListener);
	}
	
	/**
	 * Sets members to filter the hierarchy for. Set to <code>null</code> to disable member filtering.
	 * When member filtering is enabled, the hierarchy contains only types that contain
	 * an implementation of one of the filter members and the members themself.
	 * The hierarchy can be empty as well.
	 */
	public final void setMemberFilter(IMember[] memberFilter) {
		fMemberFilter= memberFilter;
	}	

	private boolean initializeMethodOverrideTester(IMethod filterMethod, IType typeToFindIn) {
		IType filterType= filterMethod.getDeclaringType();
		ITypeHierarchy hierarchy= fTypeHierarchy.getHierarchy();
		
		boolean filterOverrides= RubyModelUtil.isSuperType(hierarchy, typeToFindIn, filterType);
		IType focusType= filterOverrides ? filterType : typeToFindIn;
		
		if (fMethodOverrideTester == null || !fMethodOverrideTester.getFocusType().equals(focusType)) {
			fMethodOverrideTester= new MethodOverrideTester(focusType, hierarchy);
		}
		return filterOverrides;
	}
	
	private void addCompatibleMethods(IMethod filterMethod, IType typeToFindIn, List children) throws RubyModelException {
		boolean filterMethodOverrides= initializeMethodOverrideTester(filterMethod, typeToFindIn);
		IMethod[] methods= typeToFindIn.getMethods();
		for (int i= 0; i < methods.length; i++) {
			IMethod curr= methods[i];
			if (isCompatibleMethod(filterMethod, curr, filterMethodOverrides) && !children.contains(curr)) {
				children.add(curr);
			}
		}
	}
	
	private boolean hasCompatibleMethod(IMethod filterMethod, IType typeToFindIn) throws RubyModelException {
		boolean filterMethodOverrides= initializeMethodOverrideTester(filterMethod, typeToFindIn);
		IMethod[] methods= typeToFindIn.getMethods();
		for (int i= 0; i < methods.length; i++) {
			if (isCompatibleMethod(filterMethod, methods[i], filterMethodOverrides)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isCompatibleMethod(IMethod filterMethod, IMethod method, boolean filterOverrides) throws RubyModelException {
		if (filterOverrides) {
			return fMethodOverrideTester.isSubsignature(filterMethod, method);
		} else {
			return fMethodOverrideTester.isSubsignature(method, filterMethod);
		}
	}

	/**
	 * The members to filter or <code>null</code> if member filtering is disabled.
	 */
	public IMember[] getMemberFilter() {
		return fMemberFilter;
	}
	
	/**
	 * Sets a filter representing a working set or <code>null</code> if working sets are disabled.
	 */
	public void setWorkingSetFilter(ViewerFilter filter) {
		fWorkingSetFilter= filter;
	}
		
	
	protected final ITypeHierarchy getHierarchy() {
		return fTypeHierarchy.getHierarchy();
	}
	
	
	/* (non-Javadoc)
	 * @see IReconciled#providesWorkingCopies()
	 */
	public boolean providesWorkingCopies() {
		return true;
	}		
	
	
	/*
	 * Called for the root element
	 * @see IStructuredContentProvider#getElements	 
	 */
	public Object[] getElements(Object parent) {
		ArrayList types= new ArrayList();
		getRootTypes(types);
		for (int i= types.size() - 1; i >= 0; i--) {
			IType curr= (IType) types.get(i);
			try {
				if (!isInTree(curr)) {
					types.remove(i);
				}
			} catch (RubyModelException e) {
				// ignore
			}
		}
		return types.toArray();
	}
	
	protected void getRootTypes(List res) {
		ITypeHierarchy hierarchy= getHierarchy();
		if (hierarchy != null) {
			IType input= hierarchy.getType();
			if (input != null) {
				res.add(input);
			}
			// opened on a region: dont show
		}
	}
	
	/**
	 * Hook to overwrite. Filter will be applied on the returned types
	 */	
	protected abstract void getTypesInHierarchy(IType type, List res);
	
	/**
	 * Hook to overwrite. Return null if parent is ambiguous.
	 */	
	protected abstract IType getParentType(IType type);	
	
	
	private boolean isInScope(IType type) {
		if (fWorkingSetFilter != null && !fWorkingSetFilter.select(null, null, type)) {
			return false;
		}
		
		IRubyElement input= fTypeHierarchy.getInputElement();
		int inputType= input.getElementType();
		if (inputType ==  IRubyElement.TYPE) {
			return true;
		}
		
		IRubyElement parent= type.getAncestor(input.getElementType());
		if (inputType == IRubyElement.SOURCE_FOLDER) {
			if (parent == null || parent.getElementName().equals(input.getElementName())) {
				return true;
			}
		} else if (input.equals(parent)) {
			return true;
		}
		return false;
	}
	
	/*
	 * Called for the tree children.
	 * @see ITreeContentProvider#getChildren
	 */	
	public Object[] getChildren(Object element) {
		if (element instanceof IType) {
			try {
				IType type= (IType)element;
	
				List children= new ArrayList();
				if (fMemberFilter != null) {
					addFilteredMemberChildren(type, children);
				}
	
				addTypeChildren(type, children);
				
				return children.toArray();
			} catch (RubyModelException e) {
				// ignore
			}
		}
		return NO_ELEMENTS;
	}
	
	/*
	 * @see ITreeContentProvider#hasChildren
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof IType) {
			try {
				IType type= (IType) element;
				return hasTypeChildren(type) || (fMemberFilter != null && hasMemberFilterChildren(type));
			} catch (RubyModelException e) {
				return false;
			}			
		}
		return false;
	}	
	
	private void addFilteredMemberChildren(IType parent, List children) throws RubyModelException {
		for (int i= 0; i < fMemberFilter.length; i++) {
			IMember member= fMemberFilter[i];
			if (parent.equals(member.getDeclaringType())) {
				if (!children.contains(member)) {
					children.add(member);
				}
			} else if (member instanceof IMethod) {
				addCompatibleMethods((IMethod) member, parent, children);
			}
		}		
	}
		
	private void addTypeChildren(IType type, List children) throws RubyModelException {
		ArrayList types= new ArrayList();
		getTypesInHierarchy(type, types);
		int len= types.size();
		for (int i= 0; i < len; i++) {
			IType curr= (IType) types.get(i);
			if (isInTree(curr)) {
				children.add(curr);
			}
		}
	}
	
	protected final boolean isInTree(IType type) throws RubyModelException {
		if (isInScope(type)) {
			if (fMemberFilter != null) {
				return hasMemberFilterChildren(type) || hasTypeChildren(type);
			} else {
				return true;
			}
		}
		return hasTypeChildren(type);
	}
	
	private boolean hasMemberFilterChildren(IType type) throws RubyModelException {
		for (int i= 0; i < fMemberFilter.length; i++) {
			IMember member= fMemberFilter[i];
			if (type.equals(member.getDeclaringType())) {
				return true;
			} else if (member instanceof IMethod) {
				if (hasCompatibleMethod((IMethod) member, type)) {
					return true;
				}
			}
		}
		return false;
	}
		
	private boolean hasTypeChildren(IType type) throws RubyModelException {
		ArrayList types= new ArrayList();
		getTypesInHierarchy(type, types);
		int len= types.size();
		for (int i= 0; i < len; i++) {
			IType curr= (IType) types.get(i);
			if (isInTree(curr)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * @see IContentProvider#inputChanged
	 */
	public void inputChanged(Viewer part, Object oldInput, Object newInput) {
		Assert.isTrue(part instanceof TreeViewer);
		fViewer= (TreeViewer)part;
	}
	
	/*
	 * @see IContentProvider#dispose
	 */	
	public void dispose() {
		fTypeHierarchy.removeChangedListener(fTypeHierarchyLifeCycleListener);
		
	}
	
	/*
	 * @see ITreeContentProvider#getParent
	 */
	public Object getParent(Object element) {
		if (element instanceof IMember) {
			IMember member= (IMember) element;
			if (member.getElementType() == IRubyElement.TYPE) {
				return getParentType((IType)member);
			}
			return member.getDeclaringType();
		}
		return null;
	}
	
	protected final boolean isAnonymous(IType type) {
		return type.getElementName().length() == 0;
	}
	
	protected final boolean isAnonymousFromInterface(IType type) {
		return isAnonymous(type) && fTypeHierarchy.getHierarchy().getSuperModules(type).length != 0;
	}
	
	protected final boolean isObject(IType type) {
		return "Object".equals(type.getElementName()) && type.getDeclaringType() == null && "java.lang".equals(type.getSourceFolder().getElementName());  //$NON-NLS-1$//$NON-NLS-2$
	}
	
}
