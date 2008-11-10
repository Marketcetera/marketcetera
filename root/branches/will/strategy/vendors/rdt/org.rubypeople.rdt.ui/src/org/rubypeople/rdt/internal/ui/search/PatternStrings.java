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
package org.rubypeople.rdt.internal.ui.search;

import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.ui.RubyElementLabels;

public class PatternStrings {

	public static String getSignature(IRubyElement element) {
		if (element == null)
			return null;
		else
			switch (element.getElementType()) {
				case IRubyElement.METHOD:
					return getMethodSignature((IMethod)element);
				case IRubyElement.TYPE:
					return getTypeSignature((IType) element);
				case IRubyElement.FIELD:
					return getFieldSignature((IField) element);
				default:
					return element.getElementName();
			}
	}
	
	public static String getMethodSignature(IMethod method) {
		
		StringBuffer buffer= new StringBuffer();
		if (method.isSingleton() || method.isConstructor()) {
			buffer.append(RubyElementLabels.getElementLabel(method.getDeclaringType(), RubyElementLabels.USE_RESOLVED));
			buffer.append('.');
		}
		boolean isConstructor= method.isConstructor();
		if (!isConstructor) {
			buffer.append(getUnqualifiedMethodSignature(method, !isConstructor));
		} else {
			buffer.append("new");
		}
			
		return buffer.toString();
	}
	
	private static String getUnqualifiedMethodSignature(IMethod method, boolean isNotConstructor) {
		StringBuffer buffer= new StringBuffer();
		if (isNotConstructor) {
			buffer.append(method.getElementName());
		}
//		buffer.append('(');
		// TODO Add parameter names, or arity?
//		buffer.append(')');
		
		return buffer.toString();
	}

	public static String getUnqualifiedMethodSignature(IMethod method) {
		return getUnqualifiedMethodSignature(method, true);
	}

	public static String getTypeSignature(IType field) {
		return RubyElementLabels.getElementLabel(field, RubyElementLabels.USE_RESOLVED);
	}	
	
	public static String getFieldSignature(IField field) {
		return RubyElementLabels.getElementLabel(field, 0);
	}	
}
