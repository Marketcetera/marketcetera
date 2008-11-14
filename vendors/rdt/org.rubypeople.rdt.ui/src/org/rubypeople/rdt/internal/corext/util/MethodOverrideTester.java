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

package org.rubypeople.rdt.internal.corext.util;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;


public class MethodOverrideTester {
	private static class Substitutions {
		
		public static final Substitutions EMPTY_SUBST= new Substitutions();
		
		private HashMap fMap;
		
		public Substitutions() {
			fMap= null;
		}
		
		public void addSubstitution(String typeVariable, String substitution, String erasure) {
			if (fMap == null) {
				fMap= new HashMap(3);
			}
			fMap.put(typeVariable, new String[] { substitution, erasure });
		}
		
		private String[] getSubstArray(String typeVariable) {
			if (fMap != null) {
				return (String[]) fMap.get(typeVariable);
			}
			return null;
		}
						
		public String getSubstitution(String typeVariable) {
			String[] subst= getSubstArray(typeVariable);
			if (subst != null) {
				return subst[0];
			}
			return null;
		}
		
		public String getErasure(String typeVariable) {
			String[] subst= getSubstArray(typeVariable);
			if (subst != null) {
				return subst[1];
			}
			return null;
		}
	}	
	
	private final IType fFocusType;
	private final ITypeHierarchy fHierarchy;
	
	private Map /* <IMethod, Substitutions> */ fMethodSubstitutions;
	private Map /* <IType, Substitutions> */ fTypeVariableSubstitutions;
			
	public MethodOverrideTester(IType focusType, ITypeHierarchy hierarchy) {
		fFocusType= focusType;
		fHierarchy= hierarchy;
		fTypeVariableSubstitutions= null;
		fMethodSubstitutions= null;
	}
	
	public IType getFocusType() {
		return fFocusType;
	}
	
	public ITypeHierarchy getTypeHierarchy() {
		return fHierarchy;
	}
	
	/**
	 * Finds the method that declares the given method. A declaring method is the 'original' method declaration that does
	 * not override nor implement a method. <code>null</code> is returned it the given method does not override
	 * a method. When searching, super class are examined before implemented interfaces.
	 * @param testVisibility If true the result is tested on visibility. Null is returned if the method is not visible.
	 * @throws RubyModelException
	 */
	public IMethod findDeclaringMethod(IMethod overriding, boolean testVisibility) throws RubyModelException {
		IMethod result= null;
		IMethod overridden= findOverriddenMethod(overriding, testVisibility);
		while (overridden != null) {
			result= overridden;
			overridden= findOverriddenMethod(result, testVisibility);
		}
		return result;
	}
	
	/**
	 * Finds the method that is overridden by the given method.
	 * First the super class is examined and then the implemented interfaces.
	 * @param testVisibility If true the result is tested on visibility. Null is returned if the method is not visible.
	 * @throws RubyModelException
	 */
	public IMethod findOverriddenMethod(IMethod overriding, boolean testVisibility) throws RubyModelException {
		if (overriding.getVisibility() == IMethod.PRIVATE || overriding.isSingleton() || overriding.isConstructor()) {
			return null;
		}
		
		IType type= overriding.getDeclaringType();
		IType superClass= fHierarchy.getSuperclass(type);
		if (superClass != null) {
			IMethod res= findOverriddenMethodInHierarchy(superClass, overriding);
			if (res != null && res.getVisibility() != IMethod.PRIVATE) {
				if (!testVisibility || RubyModelUtil.isVisibleInHierarchy(res, type.getSourceFolder())) {
					return res;
				}
			}
		}
		if (!overriding.isConstructor()) {
			IType[] interfaces= fHierarchy.getSuperModules(type);
			for (int i= 0; i < interfaces.length; i++) {
				IMethod res= findOverriddenMethodInHierarchy(interfaces[i], overriding);
				if (res != null) {
					return res; // methods from interfaces are always public and therefore visible
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds the directly overridden method in a type and its super types. First the super class is examined and then the implemented interfaces.
	 * With generics it is possible that 2 methods in the same type are overidden at the same time. In that case, the first overridden method found is returned. 
	 * 	@param type The type to find methods in
	 * @param overriding The overriding method
	 * @return The first overridden method or <code>null</code> if no method is overridden
	 * @throws RubyModelException
	 */
	public IMethod findOverriddenMethodInHierarchy(IType type, IMethod overriding) throws RubyModelException {
		IMethod method= findOverriddenMethodInType(type, overriding);
		if (method != null) {
			return method;
		}
		IType superClass= fHierarchy.getSuperclass(type);
		if (superClass != null) {
			IMethod res=  findOverriddenMethodInHierarchy(superClass, overriding);
			if (res != null) {
				return res;
			}
		}
		if (!overriding.isConstructor()) {
			IType[] superInterfaces= fHierarchy.getSuperModules(type);
			for (int i= 0; i < superInterfaces.length; i++) {
				IMethod res= findOverriddenMethodInHierarchy(superInterfaces[i], overriding);
				if (res != null) {
					return res;
				}
			}
		}
		return method;		
	}
	
	/**
	 * Finds an overridden method in a type. WWith generics it is possible that 2 methods in the same type are overidden at the same time.
	 * In that case the first overridden method found is returned.
	 * @param overriddenType The type to find methods in
	 * @param overriding The overriding method
	 * @return The first overridden method or <code>null</code> if no method is overridden
	 * @throws RubyModelException
	 */
	public IMethod findOverriddenMethodInType(IType overriddenType, IMethod overriding) throws RubyModelException {
		IMethod[] overriddenMethods= overriddenType.getMethods();
		for (int i= 0; i < overriddenMethods.length; i++) {
			if (isSubsignature(overriding, overriddenMethods[i])) {
				return overriddenMethods[i];
			}
		}
		return null;
	}
	
	/**
	 * Finds an overriding method in a type.
	 * @param overridingType The type to find methods in
	 * @param overridden The overridden method
	 * @return The overriding method or <code>null</code> if no method is overriding.
	 * @throws RubyModelException
	 */
	public IMethod findOverridingMethodInType(IType overridingType, IMethod overridden) throws RubyModelException {
		IMethod[] overridingMethods= overridingType.getMethods();
		for (int i= 0; i < overridingMethods.length; i++) {
			if (isSubsignature(overridingMethods[i], overridden)) {
				return overridingMethods[i];
			}
		}
		return null;
	}
	
	/**
	 * Tests if a method is a subsignature of another method.
	 * @param overriding overriding method (m1)
	 * @param overridden overridden method (m2)
	 * @return <code>true</code> iff the method <code>m1</code> is a subsignature of the method <code>m2</code>.
	 * 		This is one of the requirements for m1 to override m2.
	 * 		Accessibility and return types are not taken into account.
	 * 		Note that subsignature is <em>not</em> symmetric!
	 * @throws RubyModelException
	 */
	public boolean isSubsignature(IMethod overriding, IMethod overridden) throws RubyModelException {
		if (!overridden.getElementName().equals(overriding.getElementName())) {
			return false;
		}
		int nParameters= overridden.getNumberOfParameters();
		if (nParameters != overriding.getNumberOfParameters()) {
			return false;
		}
		
		return nParameters == 0;
	}
			
}
