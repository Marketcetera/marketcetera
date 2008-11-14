/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;


public abstract class NamedMember extends Member {

	/*
	 * This element's name, or an empty <code> String </code> if this element
	 * does not have a name.
	 */
	protected String name;

	public NamedMember(RubyElement parent, String name) {
		super(parent);
		this.name = name;
	}

	public String getElementName() {
		return this.name;
	}
	
	public String getTypeQualifiedName(String enclosingTypeSeparator, boolean showParameters) throws RubyModelException {
		NamedMember declaringType;
		switch (this.parent.getElementType()) {
			case IRubyElement.SCRIPT:
				return this.name;
			case IRubyElement.TYPE:
				declaringType = (NamedMember) this.parent;
				break;
			case IRubyElement.FIELD:
			case IRubyElement.METHOD:
				declaringType = (NamedMember) ((IMember) this.parent).getDeclaringType();
				break;
			default:
				return null;
		}
		StringBuffer buffer = new StringBuffer(declaringType.getTypeQualifiedName(enclosingTypeSeparator, showParameters));
		buffer.append(enclosingTypeSeparator);
		String simpleName = this.name.length() == 0 ? Integer.toString(this.occurrenceCount) : this.name;
		buffer.append(simpleName);
		return buffer.toString();
	}
}
